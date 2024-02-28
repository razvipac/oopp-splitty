package server.database;

import commons.Participant;
import commons.ParticipantId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface ParticipantRepository extends CrudRepository<Participant, ParticipantId> {

    @Query("SELECT p FROM Participant p WHERE p.pkey.event.code = :eventCode")
    Collection<Participant> findAllParticipantsInEvent(@Param("eventCode") String eventCode);

    @Query("SELECT p FROM Participant p WHERE p.pkey.name = :name AND p.pkey.event.code = :eventcode")
    Optional<Participant> findParticipantByEventCodeAndName(@Param("name") String name, @Param("eventcode") String eventCode);

}