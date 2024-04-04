package server.entities.event;

import jakarta.persistence.*;
import server.entities.expense.Expense;
import server.entities.participant.Participant;
import server.entities.debt.Debt;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name="event")
public class Event {

    private String name;

    @Id
    private String code;
    private LocalDateTime creationDate;
    private LocalDateTime lastActivity;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "pkey.event")
    private List<Participant> participants;


    /**
     * Initializing an empty Event
     */
    public Event() {
        this.lastActivity = LocalDateTime.now();
    }

    /**
     * Initializing an Event with proper attributes
     *
     * @param name         The name of the respective event
     * @param code         The code of the respective event
     * @param creationDate The creation date of the respective event
     */
    public Event(String name, String code, LocalDateTime creationDate) {
        this.name = name;
        this.code = code;
        this.lastActivity = LocalDateTime.now();
        this.creationDate = creationDate;
    }

    /**
     * @return the name of an instance of type Event
     */
    public String getName() {
        return name;
    }

    /**
     * @return the code of an instance of type Event
     */
    public String getCode() {
        return code;
    }

    /**
     * @return the creation date of an instance of type Event
     */
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    /**
     * @return the last activity of an instance of type Event
     */
    public LocalDateTime getLastActivity() {
        return lastActivity;
    }

    /**
     * Changing the value of the name
     *
     * @param name The name of an event
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Changing the value of the code
     *
     * @param code The code of an event
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Changing the value of the creation date
     *
     * @param creationDate The creation date of an event
     */
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Changing the value of the last activity
     *
     * @param lastActivity The last activity of an event
     */
    public void setLastActivity(LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
    }

    public List<Participant> getParticipants(){
        return participants;
    }

    /**
     * A proper equals method for the class Event
     *
     * @param o another object with which we compare
     * @return whether the two objects are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || this.getClass() != o.getClass())
            return false;
        Event event = (Event) o;
        return code.equals(event.code)
                && name.equals(event.name)
                && creationDate.equals(event.creationDate)
                && lastActivity.equals(event.lastActivity);
    }

    /**
     * A proper hashCode for the class Event
     *
     * @return the hashCode of an instance of this class
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, code, creationDate, lastActivity);
    }

    /**
     * @return the format in which the events shall appear
     */
    @Override
    public String toString() {
        return "Event " + name + ":" +
                "\t- code = " + code +
                "\t- creationDate = " + creationDate.toString() +
                "\t- lastActivity = " + lastActivity.toString();
    }

    /**
     *
     * @param events Represents our list of present events
     * @return Returns the list of present events ordered alphabetically
     */
    public static List<Event> orderByTitle(List<Event> events)
    {
        return events.stream()
                .sorted(Comparator.comparing(Event::getName))
                .collect(Collectors.toList());
    }

    /**
     *
     * @param events Represents our list of present events
     * @return Returns the list of present events but ordered by means of date of creation
     */
    public static List<Event> orderByCreationDate(List<Event> events) {
        return events.stream()
                .sorted(Comparator.comparing(Event::getCreationDate))
                .collect(Collectors.toList());
    }

    /**
     *
     * @param events Represents our list of present events
     * @return Returns the list of present events but ordered by means of last activity
     */
    public static List<Event> orderByLastActivity(List<Event> events) {
        return events.stream()
                .sorted(Comparator.comparing(Event::getLastActivity).reversed())
                .collect(Collectors.toList());
    }

    /**
     *
     * @param expenses The list of present expenses regarding one event
     * @return returns the total cost of these expenses
     */
    public static int sumOfAllExpenses(List<Expense> expenses) {
        return expenses.stream()
                .mapToInt(Expense::getPrice)
                .sum();
    }

    /**
     * Calculates and settles debts among participants based on expenses.
     *
     * @param participants List of participants involved in the expenses.
     * @param expenses     List of expenses incurred by participants.
     * @return List of debts to settle within the group.
     */
    public static List<Debt> settleDebts(List<Participant> participants, List<Expense> expenses) {
        if (participants.isEmpty() || expenses.isEmpty()) {
            return Collections.emptyList();
        }

        // Calculate total expenses and individual shares
        Map<Participant, Double> totalExpensesByParticipant = calculateTotalExpenses(expenses);
        Map<Participant, Double> individualShareByParticipant =
                calculateIndividualShare(participants, expenses);

        // Calculate debts between each pair of participants
        List<Debt> debts = new ArrayList<>();
        for (Participant debtor : participants) {
            for (Participant creditor : participants) {
                if (!debtor.equals(creditor)) {
                    double debtAmount = calculateDebtAmount(debtor, creditor,
                            totalExpensesByParticipant, individualShareByParticipant);
                    if (debtAmount > 0) {
                        debts.add(new Debt(debtor, creditor, debtAmount));
                    }
                }
            }
        }

        return debts;
    }

    /**
     * Calculates the total expenses incurred by each participant.
     *
     * @param expenses List of expenses incurred by participants.
     * @return Map containing total expenses by participant.
     */
    public static Map<Participant, Double> calculateTotalExpenses(List<Expense> expenses) {
        Map<Participant, Double> totalExpensesByParticipant = new HashMap<>();
        for (Expense expense : expenses) {
            Participant paidBy = expense.getPaidBy();
            double totalExpense = expense.getPrice();
            totalExpensesByParticipant.put(paidBy, totalExpensesByParticipant
                    .getOrDefault(paidBy, 0.0) + totalExpense);
        }
        return totalExpensesByParticipant;
    }

    /**
     * Calculates the individual share of expenses for each participant.
     *
     * @param participants List of participants involved in the expenses.
     * @param expenses     List of expenses incurred by participants.
     * @return Map containing individual share of expenses by participant.
     */
    public static Map<Participant, Double> calculateIndividualShare(List<Participant> participants,
                                                                     List<Expense> expenses) {
        Map<Participant, Double> individualShareByParticipant = new HashMap<>();
        for (Expense expense : expenses) {
            double individualShare = (double) expense.getPrice() / participants.size();
            for (Participant participant : participants) {
                if (!participant.equals(expense.getPaidBy())) {
                    individualShareByParticipant.put(participant,
                            individualShareByParticipant
                                    .getOrDefault(participant, 0.0) + individualShare);
                }
            }
        }
        return individualShareByParticipant;
    }

    /**
     * Calculates the amount of debt between a debtor and a creditor.
     *
     * @param debtor                      Participant who owes the debt.
     * @param creditor                    Participant to whom the debt is owed.
     * @param totalExpensesByParticipant Map of total expenses by participant.
     * @param individualShareByParticipant Map of individual share of expenses by participant.
     * @return Amount of debt between the debtor and the creditor.
     */
    public static double calculateDebtAmount(Participant debtor, Participant creditor,
                                              Map<Participant, Double>
                                                      totalExpensesByParticipant,
                                              Map<Participant, Double>
                                                      individualShareByParticipant) {
        double debt = totalExpensesByParticipant.getOrDefault(creditor, 0.0)
                - totalExpensesByParticipant.getOrDefault(debtor, 0.0);
        double individualShare = individualShareByParticipant.getOrDefault(creditor, 0.0);
        return Math.max(debt, individualShare);
    }

    /*/**
     *
     * @param expenses The list of expenses within an event
     * @return Returns a list of debts to be settled
     */
    /*
    public static List<Debt> settleDebts(List<Participant> participants, List<Expense> expenses) {
        Map<Participant, Double> debtMap = new HashMap<>();

        for (Expense expense : expenses) {
            Participant paidBy = expense.getPaidBy();
            double totalExpense = expense.getPrice();

            double individualShare = totalExpense / participants.size();

            for (Participant participant : participants) {
                if (!participant.equals(paidBy)) {
                    double currentDebt = debtMap.getOrDefault(participant, 0.0);
                    debtMap.put(participant, currentDebt + individualShare);
                }
            }
        }

        List<Debt> debts = new ArrayList<>();
        for (Map.Entry<Participant, Double> entry : debtMap.entrySet()) {
            Participant debtor = entry.getKey();
            double amount = entry.getValue();
            debts.add(new Debt(debtor, null, amount)); // Leave the creditor null for now
        }

        return debts;
    }*/


    /**
     * Retrieves the debtors within a specific expense.
     *
     * @param allParticipants A list of all participants involved in the expense.
     * @param expense         The expense for which debtors are to be retrieved.
     * @return A set of participants who are debtors within the expense.
     */
    public Set<Participant> getDebtorsWithinExpense(List<Participant> allParticipants,
                                                    Expense expense) {
        Set<Participant> debtors = new HashSet<>();
        Participant creditor = expense.getPaidBy();

        for (Participant participant : allParticipants) {
            if (!participant.equals(creditor)) {
                debtors.add(participant);
            }
        }

        return debtors;
    }
}
