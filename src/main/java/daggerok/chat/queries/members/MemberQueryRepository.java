package daggerok.chat.queries.members;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface MemberQueryRepository extends MongoRepository<MemberQueryModel, String> {}
