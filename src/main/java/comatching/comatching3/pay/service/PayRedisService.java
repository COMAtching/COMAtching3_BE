package comatching.comatching3.pay.service;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PayRedisService {

	private final RedisTemplate<String, String> redisTemplate;

	public boolean isIdempotent(String idempotencyKey, String orderUUID) {
		String redisKey = "idempotency-key:" + idempotencyKey;

		// Redis에서 멱등성 검사
		Boolean isSet = redisTemplate.opsForValue().setIfAbsent(redisKey, orderUUID, Duration.ofMinutes(5));
		if (isSet == null || !isSet) {
			return false;
		}
		return true;
	}
}
