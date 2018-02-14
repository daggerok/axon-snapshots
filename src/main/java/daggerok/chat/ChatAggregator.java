package daggerok.chat;

import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

@NoArgsConstructor
@Component("axonChatAggregator")
@Aggregate(repository = "axonChatRepository")
public class ChatAggregator implements Serializable {

  private static final transient long serialVersionUID = -3559510734736559985L;

  @AggregateIdentifier
  private String roomId;
  private Set<String> members = new HashSet<>();
  private Map<String, List<String>> messages = new HashMap<>();

  /* create room */
  @CommandHandler
  public ChatAggregator(final CreateRoomCommand cmd) {
    apply(new RoomCreatedEvent(
        cmd.getRoomId()
    ));
  }

  @EventSourcingHandler
  public void on(final RoomCreatedEvent event) {
    roomId = event.getRoomId();
  }

  /* enter room */
  @CommandHandler
  public void on(final EnterRoomCommand cmd) {

    if (members.contains(cmd.getMemberId())) return;

    apply(new RoomEnteredEvent(
        cmd.getRoomId(),
        cmd.getMemberId()
    ));
  }

  @EventSourcingHandler
  public void on(final RoomEnteredEvent event) {
    members.add(event.getMemberId());
  }

  /* leave room */
  @CommandHandler
  public void on(final LeaveRoomCommand cmd) {

    if (!members.contains(cmd.getMemberId())) return;

    apply(new LeftRoomEvent(
        cmd.getRoomId(),
        cmd.getMemberId()
    ));
  }

  @EventSourcingHandler
  public void on(final LeftRoomEvent event) {
    members.remove(event.getMemberId());
  }

  /* send message */
  @CommandHandler
  public void on(final PostMessageCommand cmd) {

    if (!members.contains(cmd.getMemberId()))
      throw new IllegalStateException("member have to enter room before posting message.");

    apply(new MessageSentEvent(
        cmd.getRoomId(),
        cmd.getMemberId(),
        cmd.getMessage()
    ));
  }

  @EventSourcingHandler
  public void on(final MessageSentEvent event) {

    final String memberId = event.getMemberId();
    messages.putIfAbsent(memberId, new ArrayList<>());

    final Stream<String> current = messages.get(memberId)
                                           .stream();
    final List<String> all = Stream.concat(current, Stream.of(event.getMessage()))
                                   .collect(toList());
    messages.put(memberId, all);
  }
}
