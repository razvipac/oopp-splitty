package commons.dto;

public class ExpenseDTO {

    private int price;
    private String item;
    private ParticipantDTO paidBy;

    public ExpenseDTO() {
    }

    public ExpenseDTO(int price, String item, ParticipantDTO paidBy) {
        this.price = price;
        this.item = item;
        this.paidBy = paidBy;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public ParticipantDTO getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(ParticipantDTO paidBy) {
        this.paidBy = paidBy;
    }
}
