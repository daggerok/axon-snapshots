package daggerok.chat;

import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.callbacks.LoggingCallback;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

import static java.util.Collections.singletonMap;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping(produces = APPLICATION_JSON_VALUE)
public class ChatCommandsResource {

  final CommandGateway commandGateway;

  @ResponseStatus(CREATED)
  @PostMapping("/api/room")
  public Map<String, String> createRoom(@RequestBody final Map<String, String> request) {
    final String roomId = request.getOrDefault("roomId", UUID.randomUUID().toString());
    commandGateway.send(new CreateRoomCommand(roomId), LoggingCallback.INSTANCE);
    return singletonMap("roomId", roomId);
  }

  @ResponseStatus(ACCEPTED)
  @PutMapping("/api/room/{roomId}/{memberId}")
  public void enterRoom(@PathVariable final String roomId, @PathVariable final String memberId) {
    commandGateway.send(new EnterRoomCommand(roomId, memberId), LoggingCallback.INSTANCE);
  }

  @ResponseStatus(ACCEPTED)
  @DeleteMapping("/api/room/{roomId}/{memberId}")
  public void leaveRoom(@PathVariable final String roomId, @PathVariable final String memberId) {
    commandGateway.send(new LeaveRoomCommand(roomId, memberId), LoggingCallback.INSTANCE);
  }

  @ResponseStatus(CREATED)
  @PostMapping("/api/room/{roomId}/{memberId}")
  public void leaveRoom(@PathVariable final String roomId,
                        @PathVariable final String memberId,
                        @RequestBody final Map<String, String> message) {

    commandGateway.send(new PostMessageCommand(roomId, memberId, message.get("message")), LoggingCallback.INSTANCE);
  }
}
