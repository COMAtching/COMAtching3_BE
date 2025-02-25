package comatching.comatching3.testConfiguration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
abstract public class TestContainers {

    private static final DockerImageName MYSQL_IMAGE_NAME = DockerImageName.parse("mysql:8");
    private static final DockerImageName REDIS_IMAGE_NAME = DockerImageName.parse("redis:7");

    @Container
    private static final MySQLContainer<?> mySQLContainer = new MySQLContainer<>(MYSQL_IMAGE_NAME)
            .withReuse(true);

    @Container
    private static final GenericContainer<?> redisContainer = new GenericContainer<>(REDIS_IMAGE_NAME)
            .withExposedPorts(6379)
            .withReuse(true);

    @DynamicPropertySource
    private static void dynamicProperties(DynamicPropertyRegistry registry) {

        //✅ mysql
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.driver-class-name", mySQLContainer::getDriverClassName);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);

        //auto-ddl
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");  // 또는 "none", "create-drop" 등

        //✅ redis
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));
    }

}
