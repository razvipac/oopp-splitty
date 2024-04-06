package server.database;

import server.entities.participant.Participant;
import server.entities.participant.ParticipantId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface ParticipantRepository extends CrudRepository<Participant, ParticipantId> {

    /**
     * Fetches all participants in a given Event
     *
     * @param eventCode code of the Event from which to fetch Participants
     * @return A collection of fetched participants
     */
    @Query("SELECT p FROM Participant p WHERE p.pkey.event.code = :eventCode")
    Collection<Participant> findAllParticipantsInEvent(@Param("eventCode") String eventCode);

    /**
     * Fetches a participant given their name and an event's code
     *
     * @param name participant's name
     * @param eventCode event's code
     * @return An Optional containing the fetched Participant, or empty if not found
     */
    @Query("SELECT p FROM Participant p " +
            "WHERE p.pkey.name = :name " +
            "AND p.pkey.event.code = :eventCode")
    Optional<Participant> findParticipantByEventCodeAndName(@Param("name") String name,
                                                            @Param("eventCode") String eventCode);


}