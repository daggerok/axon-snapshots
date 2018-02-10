package daggerok.chat.queries;

import daggerok.chat.RoomCreatedEvent;
import daggerok.chat.queries.rooms.RoomQueryModel;
import daggerok.chat.queries.rooms.RoomQueryRepository;
import lombok.RequiredArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.Timestamp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping(produces = APPLICATION_JSON_UTF8_VALUE)
public class RoomsProjection {

  final RoomQueryRepository repository;

  @EventHandler
  public void on(final RoomCreatedEvent event, @Timestamp Instant createdAt) {
    repository.save(new RoomQueryModel().setRoomId(event.getRoomId())
                                        .setCreatedAt(createdAt));
  }

  @GetMapping("/api/room")
  public Page<RoomQueryModel> queryAll(final Pageable pageable) {
    return repository.findAll(pageable);
  }

  @GetMapping("/api/room/{roomId}")
  public RoomQueryModel queryOne(final @PathVariable String roomId) {
    return repository.findOne(roomId);
  }
}
