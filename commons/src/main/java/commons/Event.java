package commons;

import jakarta.persistence.*;

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
                .sorted(Comparator.comparing(Event::getLastActivity))
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
     * Settles the debts within an event.
     * @param allParticipants Represents all the participants from the server
     * @param event The event to be taken into consideration
     * @param expenses The list of expenses within an event
     * @return Returns a list of debts to be settled
     */
    public static List<Debt> settleDebts(List<Participant> allParticipants,
                                         Event event, List<Expense> expenses) {
        Map<Participant, Double> debtMap = new HashMap<>();
        for (Expense expense : expenses) {
            List<Participant> participants = expense.getParticipants();
            double totalExpense = expense.getPrice();
            double individualShare = totalExpense / participants.size();

            for (Participant participant : participants) {
                double currentDebt = debtMap.getOrDefault(participant, 0.0);
                debtMap.put(participant, currentDebt + individualShare);
            }
        }

        List<Debt> debts = new ArrayList<>();
        for (Map.Entry<Participant, Double> entry : debtMap.entrySet()) {
            Participant debtor = entry.getKey();
            double amount = entry.getValue();
            debts.add(new Debt(debtor, null, amount)); // Leave the creditor null for now
        }

        return debts;
    }

    /**
     * Settles the debts within an event.
     * @param allParticipants Represents all the participants from the entire server
     * @param event The event to be taken into consideration
     * @return Returns the list of participants who are present within one specific event
     */
    private static List<Participant> basicGetParticipants(List<Participant> allParticipants,
                                                          Event event) {
        List<Participant> participants = new ArrayList<>();
        for(Participant participant : allParticipants)
            if(participant.getEvent().equals(event))
                participants.add(participant);
        return participants;
    }

    /**
     * Retrieves the participants within a specific expense.
     * @param expense The expense to be taken into consideration
     * @return Returns the list of people who participated in this event
     */
    private static List<Participant> advancedGetParticipants(Expense expense) {
        return new ArrayList<>(expense.getParticipants());
    }

    /**
     * Retrieves the debtors within a specific expense.
     *
     * @param allParticipants A list of all participants involved.
     * @param expense         The expense for which debtors are to be retrieved.
     * @return A set of participants who are debtors within the expense.
     */
    public Set<Participant> getDebtorsWithinExpense(List<Participant> allParticipants,
                                                    Expense expense) {
        Set<Participant> debtors = new HashSet<>();
        Participant creditor = expense.getPaidBy();
        // The participant who paid for the expense is the creditor

        for (Debt debt : settleDebts(allParticipants,
                expense.getPaidBy().getEvent(),
                List.of(expense))) {
            if (!debt.getCreditor().equals(creditor)) {
                debtors.add(debt.getDebtor());
            }
        }

        return debtors;
    }

}
