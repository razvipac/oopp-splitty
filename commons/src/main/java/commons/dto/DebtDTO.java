package commons.dto;

public class DebtDTO {

    private ParticipantDTO debtor;
    private ParticipantDTO creditor;
    private double amount;
    private boolean received;

    public DebtDTO() {
    }

    public DebtDTO(ParticipantDTO debtor, ParticipantDTO creditor, double amount, boolean received) {
        this.debtor = debtor;
        this.creditor = creditor;
        this.amount = amount;
        this.received = received;
    }

    public ParticipantDTO getDebtor() {
        return debtor;
    }

    public void setDebtor(ParticipantDTO debtor) {
        this.debtor = debtor;
    }

    public ParticipantDTO getCreditor() {
        return creditor;
    }

    public void setCreditor(ParticipantDTO creditor) {
        this.creditor = creditor;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isReceived() {
        return received;
    }

    public void setReceived(boolean received) {
        this.received = received;
    }
}
