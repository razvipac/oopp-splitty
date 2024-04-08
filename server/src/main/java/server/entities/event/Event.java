package server.entities.event;

import jakarta.persistence.*;
import server.entities.expense.Expense;
import server.entities.participant.Participant;

import java.text.DecimalFormat;
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
     * Getter for participants
     * @return list of participants of the event
     */
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
    public static double sumOfAllExpenses(List<Expense> expenses) {
        return expenses.stream()
                .mapToDouble(Expense::getPrice)
                .sum();
    }

    /**
     * Calculate the open debts among participants within the event based on expenses
     *
     * @param expenses A map where each expense is associated with its participants
     * @return A map representing the total debts between participants
     */
    public static Map<Participant, Map<Participant, Double>> settleDebts(Map<Expense, List<Participant>> expenses) {
        Map<Participant, Map<Participant, Double>> debts = new HashMap<>();
        DecimalFormat df = new DecimalFormat("#.##");

        // Now we are analyzing each expense and for each, its participants in that specific expense
        for (Map.Entry<Expense, List<Participant>> entry : expenses.entrySet()) {
            Expense expense = entry.getKey();
            Participant paidBy = expense.getPaidBy();
            // People who are participating in that expense
            List<Participant> participants = entry.getValue();

            // Calculate the share per participant (it is split equally among every participant)
            double share = (double) expense.getPrice() / participants.size();
            share = Double.parseDouble(df.format(share));

            for (Participant debtor : participants) {
                if (!debtor.equals(paidBy)) {
                    // Update the debt between participants

                    double amount = debts.getOrDefault(debtor, new HashMap<>()).getOrDefault(paidBy, 0.0);
                    amount += share;
                    Map<Participant, Double> debtorDebts = debts.computeIfAbsent(debtor, k -> new HashMap<>());
                    debtorDebts.put(paidBy, amount);
                }
            }
        }

        return debts;
    }
}
