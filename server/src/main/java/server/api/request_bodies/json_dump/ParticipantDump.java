package server.api.request_bodies.json_dump;

public record ParticipantDump (
        String name,
        String email,
        String iban,
        String bic
) { }
