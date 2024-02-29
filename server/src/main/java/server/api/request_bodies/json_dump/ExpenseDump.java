package server.api.request_bodies.json_dump;

public record ExpenseDump (
       Long id,
       String paidBy,
       Integer price,
       String item
) { }
