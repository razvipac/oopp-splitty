package commons.dto;

/**
 * Data Transfer Object (DTO) representing a debt between participants.
 */
public class DebtDTO {

    private ParticipantDTO debtor;
    private ParticipantDTO creditor;
    private double amount;
    private boolean received;

    /**
     * Default constructor for DebtDTO.
     */
    public DebtDTO() {
    }

    /**
     * Constructs a DebtDTO with specified debtor, creditor, amount, and received status.
     * @param debtor The participant who owes the debt.
     * @param creditor The participant who is owed the debt.
     * @param amount The amount of the debt.
     * @param received Indicates whether the debt has been received or not.
     */
    public DebtDTO(ParticipantDTO debtor, ParticipantDTO creditor,
                   double amount, boolean received) {
        this.debtor = debtor;
        this.creditor = creditor;
        this.amount = amount;
        this.received = received;
    }

    /**
     * Retrieves the participant who owes the debt.
     * @return The debtor participant.
     */
    public ParticipantDTO getDebtor() {
        return debtor;
    }

    /**
     * Sets the participant who owes the debt.
     * @param debtor The debtor participant.
     */
    public void setDebtor(ParticipantDTO debtor) {
        this.debtor = debtor;
    }

    /**
     * Retrieves the participant who is owed the debt.
     * @return The creditor participant.
     */
    public ParticipantDTO getCreditor() {
        return creditor;
    }

    /**
     * Sets the participant who is owed the debt.
     * @param creditor The creditor participant.
     */
    public void setCreditor(ParticipantDTO creditor) {
        this.creditor = creditor;
    }

    /**
     * Retrieves the amount of the debt.
     * @return The amount of the debt.
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Sets the amount of the debt.
     * @param amount The amount of the debt.
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * Checks whether the debt has been received.
     * @return True if the debt has been received, false otherwise.
     */
    public boolean isReceived() {
        return received;
    }

    /**
     * Sets the status of whether the debt has been received.
     * @param received True if the debt has been received, false otherwise.
     */
    public void setReceived(boolean received) {
        this.received = received;
    }
}