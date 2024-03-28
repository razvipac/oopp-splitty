package commons.dto;

/**
 * Data Transfer Object (DTO) representing a participant.
 */
public class ParticipantDTO {

    private String name;
    private EventDTO event;
    private String email;
    private String iban;
    private String bic;

    /**
     * Default constructor for ParticipantDTO.
     */
    public ParticipantDTO() {
    }

    /**
     * Constructs a ParticipantDTO with the specified name, associated event, email, IBAN, and BIC.
     *
     * @param name  The name of the participant.
     * @param event The event associated with the participant.
     * @param email The email address of the participant.
     * @param iban  The IBAN (International Bank Account Number) of the participant.
     * @param bic   The BIC (Bank Identifier Code) of the participant.
     */
    public ParticipantDTO(String name, EventDTO event, String email, String iban, String bic) {
        this.name = name;
        this.event = event;
        this.email = email;
        this.iban = iban;
        this.bic = bic;
    }

    /**
     * Retrieves the name of the participant.
     *
     * @return The name of the participant.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the participant.
     *
     * @param name The name of the participant.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the event associated with the participant.
     *
     * @return The event associated with the participant.
     */
    public EventDTO getEvent() {
        return event;
    }

    /**
     * Sets the event associated with the participant.
     *
     * @param event The event associated with the participant.
     */
    public void setEvent(EventDTO event) {
        this.event = event;
    }

    /**
     * Retrieves the email address of the participant.
     *
     * @return The email address of the participant.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the participant.
     *
     * @param email The email address of the participant.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Retrieves the IBAN (International Bank Account Number) of the participant.
     *
     * @return The IBAN of the participant.
     */
    public String getIban() {
        return iban;
    }

    /**
     * Sets the IBAN (International Bank Account Number) of the participant.
     *
     * @param iban The IBAN of the participant.
     */
    public void setIban(String iban) {
        this.iban = iban;
    }

    /**
     * Retrieves the BIC (Bank Identifier Code) of the participant.
     *
     * @return The BIC of the participant.
     */
    public String getBic() {
        return bic;
    }

    /**
     * Sets the BIC (Bank Identifier Code) of the participant.
     *
     * @param bic The BIC of the participant.
     */
    public void setBic(String bic) {
        this.bic = bic;
    }
}