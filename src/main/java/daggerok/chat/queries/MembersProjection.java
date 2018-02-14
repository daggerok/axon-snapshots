package daggerok.chat.queries;

import daggerok.chat.LeftRoomEvent;
import daggerok.chat.RoomEnteredEvent;
import daggerok.chat.queries.members.MemberQueryModel;
import daggerok.chat.queries.members.MemberQueryRepository;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.Timestamp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Collections;

import static java.util.Collections.*;
import static java.util.Optional.ofNullable;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping(produces = APPLICATION_JSON_UTF8_VALUE)
public class MembersProjection {

  final MemberQueryRepository repository;

  @EventHandler
  public void on(final RoomEnteredEvent event, @Timestamp Instant createdAt) {
    repository.save(new MemberQueryModel().setMemberId(event.getMemberId())
                                          .setRoomId(event.getRoomId())
                                          .setOnline(true)
                                          .setCreatedAt(createdAt));
  }

  @EventHandler
  public void on(final LeftRoomEvent event, @Timestamp Instant createdAt) {
    ofNullable(repository.findOne(event.getMemberId()))
        .ifPresent(member -> repository.save(member.setOnline(false)));
  }

  @GetMapping("/api/member")
  public Page<MemberQueryModel> queryAll(final Pageable pageable) {

    return Try.of(() -> repository.findAll(pageable))
              .getOrElseGet(throwable -> new PageImpl<MemberQueryModel>(EMPTY_LIST));
  }

  @GetMapping("/api/member/room/{roomId}")
  public Page<MemberQueryModel> queryRoomMembers(final @PathVariable String roomId,
                                                 final Pageable pageable) {

    return Try.of(() -> repository.findAllByRoomId(roomId, pageable))
              .getOrElseGet(throwable -> new PageImpl<MemberQueryModel>(EMPTY_LIST));
  }

  @GetMapping("/api/member/{memberId}")
  public MemberQueryModel queryOne(final @PathVariable String memberId) {
    return Try.of(() -> repository.findOne(memberId))
              .getOrNull();
  }
}
