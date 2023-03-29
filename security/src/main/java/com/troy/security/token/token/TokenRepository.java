package com.troy.security.token.token;

import com.troy.security.token.Token;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends MongoRepository<Token, String> {

    @Query("{'user' : ?0, " +
            "'$and expired':  false, " +
            "'$or revoked': false}" +
            "")
    List<Token> findAllValidTokenByUser(String id);

    @Query("{'token' :  ?0}")
    Optional<Token> findByToken(String token);
}
