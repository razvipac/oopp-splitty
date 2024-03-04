package server.api.request_bodies.json_dump;

import commons.Event;

import java.util.List;

public record EventDump (
        Event event,
        List<ParticipantDump> participants,
        List<ExpenseDump> expenses
) { }
