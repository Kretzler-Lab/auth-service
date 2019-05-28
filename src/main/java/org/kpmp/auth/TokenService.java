package org.kpmp.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;
import users.User;

import java.io.IOException;
import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

@Service
public class TokenService {

    private SecurityConstants securityConstants;

    public TokenService(SecurityConstants securityConstants) {
        this.securityConstants = securityConstants;
    }

    public String buildTokenWithUser(User user) throws JsonProcessingException {
        return JWT.create().withSubject(user.getId())
                .withExpiresAt(new Date(System.currentTimeMillis() + securityConstants.EXPIRATION_TIME)).withClaim("user", user.toJson())
                .sign(HMAC512(securityConstants.getSecret()));

    }

    public DecodedJWT verifyToken(String token) {
        try {
            return JWT.require(Algorithm.HMAC512(securityConstants.getSecret()))
                        .build()
                        .verify(token);

        } catch (JWTVerificationException exception) {
            return null;
        }
    }

    public User getUserFromToken(DecodedJWT verifiedToken) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(verifiedToken.getClaim("user").asString(), User.class);
        } catch (IOException e) {
            return null;
        }
    }

}
