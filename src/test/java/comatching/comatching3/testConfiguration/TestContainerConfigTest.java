package comatching.comatching3.testConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
public class TestContainerConfigTest extends TestContainers {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    @DisplayName("Redis Test Container 동작 확인")
    void testRedis() {
        redisTemplate.opsForValue().set("key", "value");
        String result = redisTemplate.opsForValue().get("key");

        assertThat(result).isEqualTo("value");
    }
}
