package comatching.comatching3.users.auth.refresh_token.service;

import static comatching.comatching3.users.auth.jwt.JwtExpirationConst.*;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Deprecated
@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

	private static final String REFRESH_TOKEN_PREFIX = "refreshToken:";
	private final RedisTemplate<String, Object> redisTemplate;

	@Transactional
	public void saveRefreshTokenInRedis(String uuid, String refreshToken) {
		String key = REFRESH_TOKEN_PREFIX + uuid;
		redisTemplate.opsForValue().set(key, refreshToken, REFRESH_TOKEN_EXPIRATION, TimeUnit.MILLISECONDS);
	}

	public String getRefreshToken(String uuid) {
		String key = REFRESH_TOKEN_PREFIX + uuid;
		return (String)redisTemplate.opsForValue().get(key);
	}

	@Transactional
	public void deleteRefreshToken(String uuid) {
		String key = REFRESH_TOKEN_PREFIX + uuid;
		redisTemplate.delete(key);
	}
}
