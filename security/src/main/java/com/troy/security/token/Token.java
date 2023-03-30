package com.troy.security.token;

import com.troy.security.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import static com.troy.security.token.TokenType.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(value = "token")
public class Token {

    @Id
    public String id;

   // @Indexed(unique = true)
    public String token;

    public TokenType tokenType = BEARER;

    public boolean revoked;

    public boolean expired;

   // @DocumentReference(lazy = true, lookup = "{ 'token' : ?#{#self._id} }")
   // @ReadOnlyProperty
    //public User user;

    public String userId;

}
