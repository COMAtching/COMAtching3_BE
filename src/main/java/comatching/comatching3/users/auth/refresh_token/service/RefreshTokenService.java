package comatching.comatching3.users.auth.refresh_token.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static comatching.comatching3.users.auth.jwt.JwtUtil.REFRESH_TOKEN_EXPIRATION;


@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, Object> redisTemplate;


    public void saveRefreshToken(String socialId, String refreshToken) {
        redisTemplate.opsForValue().set(socialId, refreshToken, REFRESH_TOKEN_EXPIRATION, TimeUnit.MILLISECONDS);
    }

    public String getRefreshToken(String socialId) {
        return (String) redisTemplate.opsForValue().get(socialId);
    }

    public void deleteRefreshToken(String socialId) {
        redisTemplate.delete(socialId);
    }
}
