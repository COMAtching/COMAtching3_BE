package comatching.comatching3.users.auth.refresh_token.service;

import comatching.comatching3.admin.entity.Admin;
import comatching.comatching3.admin.repository.AdminRepository;
import comatching.comatching3.exception.BusinessException;
import comatching.comatching3.users.entity.UserAiFeature;
import comatching.comatching3.users.repository.UserAiFeatureRepository;
import comatching.comatching3.users.repository.UsersRepository;
import comatching.comatching3.util.ResponseCode;
import comatching.comatching3.util.UUIDUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static comatching.comatching3.users.auth.jwt.JwtExpirationConst.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String REFRESH_TOKEN_PREFIX = "refreshToken:";

    @Transactional
    public void saveRefreshTokenInRedis(String uuid, String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + uuid;
        redisTemplate.opsForValue().set(key, refreshToken, REFRESH_TOKEN_EXPIRATION, TimeUnit.MILLISECONDS);
    }

    public String getRefreshToken(String uuid) {
        String key = REFRESH_TOKEN_PREFIX + uuid;
        return (String) redisTemplate.opsForValue().get(key);
    }

    public void deleteRefreshToken(String uuid) {
        String key = REFRESH_TOKEN_PREFIX + uuid;
        redisTemplate.delete(key);
    }
}
