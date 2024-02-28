package server.database;

import commons.Participant;
import commons.ParticipantId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantRepository extends CrudRepository<Participant, ParticipantId> {
}
