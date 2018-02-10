package daggerok.chat.queries.members;

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
public class MemberQueryModel implements Serializable {
  private static final long serialVersionUID = 3395320914636821041L;

  @Id String memberId;
  String roomId;
  boolean isOnline;
  Instant createdAt;
}
