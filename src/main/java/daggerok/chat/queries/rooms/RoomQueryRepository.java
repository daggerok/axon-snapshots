package daggerok.chat.queries.rooms;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomQueryRepository extends MongoRepository<RoomQueryModel, String> {}
