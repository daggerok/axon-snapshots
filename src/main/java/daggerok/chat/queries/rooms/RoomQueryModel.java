package daggerok.chat.queries.rooms;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.Instant;

@Data
@Document
@NoArgsConstructor
@Accessors(chain = true)
public class RoomQueryModel implements Serializable {
  private static final long serialVersionUID = -6008284836583310598L;

  @Id String roomId;
  Instant createdAt;
}
