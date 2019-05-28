package org.kpmp.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@Scope("singleton")
public class SecurityConstants {

    private final byte[] secret;
    public static final long EXPIRATION_TIME = 28_800_000;

    public SecurityConstants() {
        Random random = new Random();
        byte[] secret = new byte[32];
        random.nextBytes(secret);
        this.secret = secret;
    }

    public byte[] getSecret() {
        return secret;
    }

}
