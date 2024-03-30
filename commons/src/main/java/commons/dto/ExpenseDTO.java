package commons.dto;

/**
 * Data Transfer Object (DTO) representing an expense.
 */
public class ExpenseDTO {

    private int price;
    private String item;
    private ParticipantDTO paidBy;

    /**
     * Default constructor for ExpenseDTO.
     */
    public ExpenseDTO() {
    }

    /**
     * Constructs an ExpenseDTO with the specified price, item, and participant who paid for it.
     *
     * @param price   The price of the expense.
     * @param item    The item or description of the expense.
     * @param paidBy  The participant who paid for the expense.
     */
    public ExpenseDTO(int price, String item, ParticipantDTO paidBy) {
        this.price = price;
        this.item = item;
        this.paidBy = paidBy;
    }

    /**
     * Retrieves the price of the expense.
     *
     * @return The price of the expense.
     */
    public int getPrice() {
        return price;
    }

    /**
     * Sets the price of the expense.
     *
     * @param price The price of the expense.
     */
    public void setPrice(int price) {
        this.price = price;
    }

    /**
     * Retrieves the item or description of the expense.
     *
     * @return The item or description of the expense.
     */
    public String getItem() {
        return item;
    }

    /**
     * Sets the item or description of the expense.
     *
     * @param item The item or description of the expense.
     */
    public void setItem(String item) {
        this.item = item;
    }

    /**
     * Retrieves the participant who paid for the expense.
     *
     * @return The participant who paid for the expense.
     */
    public ParticipantDTO getPaidBy() {
        return paidBy;
    }

    /**
     * Sets the participant who paid for the expense.
     *
     * @param paidBy The participant who paid for the expense.
     */
    public void setPaidBy(ParticipantDTO paidBy) {
        this.paidBy = paidBy;
    }
}