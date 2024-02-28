package server.api.request_bodies;

public record ParticipantBody(
        String name,
        String email,
        String iban,
        String bic
) {
}
