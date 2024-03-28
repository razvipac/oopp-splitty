package commons.dto;

public class ParticipantDTO {

    private String name;
    private EventDTO event;
    private String email;
    private String iban;
    private String bic;

    public ParticipantDTO() {
    }

    public ParticipantDTO(String name, EventDTO event, String email, String iban, String bic) {
        this.name = name;
        this.event = event;
        this.email = email;
        this.iban = iban;
        this.bic = bic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EventDTO getEvent() {
        return event;
    }

    public void setEvent(EventDTO event) {
        this.event = event;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }
}
