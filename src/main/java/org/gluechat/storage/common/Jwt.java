package org.gluechat.storage.common;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

public class Jwt {
    private final String secretKey;

    public Jwt(Dotenv dotenv) {
        this.secretKey = dotenv.get("JWT_SECRET_KEY");
        if (this.secretKey == null || this.secretKey.isBlank()) {
            throw new IllegalArgumentException("API_KEY cannot be empty!");
        }
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public boolean verifyToken(String token) {
        try {
            JwtParser parser = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build();

            parser.parseSignedClaims(token);
            return true;

        } catch (ExpiredJwtException | SignatureException e) {
            return false;
        } catch(JwtException e) {
            return false;
        } catch (Exception e) {
            System.err.println("Failed to verify token: " + e.getMessage());
            return false;
        }
    }
}