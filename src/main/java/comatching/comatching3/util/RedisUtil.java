package comatching.comatching3.util;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

@Service
public class RedisUtil {
	private final RedisTemplate<String, Object> redisTemplate;
	private final ObjectMapper objectMapper;

	public RedisUtil(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
		this.redisTemplate = redisTemplate;
		this.objectMapper = objectMapper;
	}

	@Transactional
	public void putRedisValue(String key, Object putData) throws JsonProcessingException {
		redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(putData));
	}

	@Transactional
	public void putRedisValue(String key, Object putData, Integer seconds) throws JsonProcessingException {
		redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(putData), seconds, TimeUnit.SECONDS);
	}

	@Transactional
	public <T> T getRedisValue(String key,Class<T> classType) throws JsonProcessingException {
		String redisValue = (String) redisTemplate.opsForValue().get(key);
		if (redisValue.isEmpty()) {
			return null;
		}else{
			return objectMapper.readValue(redisValue,classType);
		}
	}
}
