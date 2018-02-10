package daggerok.chat.queries;

import daggerok.chat.MessageSentEvent;
import daggerok.chat.queries.messages.ChatMessageQueryModel;
import daggerok.chat.queries.messages.ChatMessageQueryModelRepository;
import lombok.RequiredArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.Timestamp;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping(produces = APPLICATION_JSON_VALUE)
public class MessagesProjection {

  final EventStore eventStore;
  final ChatMessageQueryModelRepository repository;

  @EventHandler
  public void on(final MessageSentEvent event, @Timestamp final Instant createdAt) {
    repository.save(new ChatMessageQueryModel().setRoomId(event.getRoomId())
                                               .setMemberId(event.getMemberId())
                                               .setMessageBody(event.getMessage())
                                               .setCreatedAt(createdAt));
  }

  @GetMapping("/api/message")
  public Page<ChatMessageQueryModel> queryMessages(final Pageable pageable) {
    return repository.findAll(pageable);
  }

  @GetMapping("/api/message/room/{roomId}")
  public Page<ChatMessageQueryModel> queryRoomMessages(final Pageable pageable, @PathVariable final String roomId) {
    return repository.findAllByRoomId(roomId, pageable);
  }

  @GetMapping("/api/message/member/{memberId}")
  public Page<ChatMessageQueryModel> queryMemberMessages(final Pageable pageable, @PathVariable final String memberId) {
    return repository.findAllByMemberId(memberId, pageable);
  }

  @GetMapping("/api/message/{roomId}/{memberId}")
  public Page<ChatMessageQueryModel> queryRoomMemberMessages(final Pageable pageable,
                                                             @PathVariable final String roomId,
                                                             @PathVariable final String memberId) {

    return repository.findAllByRoomIdAndMemberId(roomId, memberId, pageable);
  }
}
