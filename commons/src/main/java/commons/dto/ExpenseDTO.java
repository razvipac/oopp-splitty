package commons.dto;

public class ExpenseDTO {

    private int price;
    private String item;
    private String paidByName;

    public ExpenseDTO() {
    }

    public ExpenseDTO(int price, String item, String paidByName) {
        this.price = price;
        this.item = item;
        this.paidByName = paidByName;
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

    public String getPaidByName() {
        return paidByName;
    }

    public void setPaidByName(String paidByName) {
        this.paidByName = paidByName;
    }
}
