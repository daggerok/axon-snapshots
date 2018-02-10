package daggerok.chat.queries.messages;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

public interface ChatMessageQueryModelRepository extends MongoRepository<ChatMessageQueryModel, String> {

  Page<ChatMessageQueryModel> findAllByRoomId(@Param("roomId") final String roomId, final Pageable pageable);

  Page<ChatMessageQueryModel> findAllByMemberId(@Param("memberId") final String memberId, final Pageable pageable);

  Page<ChatMessageQueryModel> findAllByRoomIdAndMemberId(@Param("roomId") final String roomId,
                                                         @Param("memberId") final String memberId,
                                                         final Pageable pageable);
}
