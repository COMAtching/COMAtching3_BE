package comatching.comatching3.users.auth.jwt;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.concurrent.TimeUnit;


@Component
public class JwtUtil {

    private SecretKey secretKey;

    public static final long REFRESH_TOKEN_EXPIRATION = TimeUnit.DAYS.toMillis(1);
//    public static final long ACCESS_TOKEN_EXPIRATION = TimeUnit.HOURS.toMillis(1);
    public static final long ACCESS_TOKEN_EXPIRATION = TimeUnit.MINUTES.toMillis(1);


    public JwtUtil(@Value("${jwt.secret}") String secret) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(secret.getBytes(StandardCharsets.UTF_8));

            secretKey = new SecretKeySpec(hash, "HmacSHA256");

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found.", e);
        }
    }

    public String getSocialId(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("socialId", String.class);
    }

    public String getRole(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }

    public Boolean isExpired(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .before(new Date());
    }
    public String generateAccessToken(String socialId, String role) {
        return generateToken(socialId, role, ACCESS_TOKEN_EXPIRATION);
    }
    public String generateRefreshToken(String socialId, String role) {
        return generateToken(socialId, role, REFRESH_TOKEN_EXPIRATION);
    }

    public String generateToken(String socialId, String role, long expiredMs) {
        return Jwts.builder()
                .claim("socialId", socialId)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

}
