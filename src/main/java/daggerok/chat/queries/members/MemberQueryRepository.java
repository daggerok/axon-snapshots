package daggerok.chat.queries.members;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

public interface MemberQueryRepository extends MongoRepository<MemberQueryModel, String> {
  Page<MemberQueryModel> findAllByRoomId(@Param("roomId") String roomId, Pageable pageable);
}
