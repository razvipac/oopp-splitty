package server.database;

import commons.Participant;
import commons.ParticipantId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ParticipantRepository extends CrudRepository<Participant, ParticipantId> {

    @Query("SELECT p FROM Participant p WHERE p.pkey.event.code = :eventCode")
    Collection<Participant> getAllParticipantsInEvent(@Param("eventCode") String eventCode);

}
