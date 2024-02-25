package server.api;

public record ExpenseBody(
        Integer price,
        String item,
        Long participantId
) {}
