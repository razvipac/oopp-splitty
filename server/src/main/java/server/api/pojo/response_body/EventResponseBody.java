package server.api.pojo.response_body;

import commons.Event;

import java.util.List;

public record EventResponseBody(
        Event event,
        List<ParticipantResponseBody> participants,
        List<ExpenseResponseBody> expenses,
        List<DebtResponseBody> debts
) { }
