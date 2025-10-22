package io.github.naomimyselfandi.xanaduwars.testing;

import io.github.naomimyselfandi.seededrandom.SeededRandomExtension;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ExtendWith(SeededRandomExtension.class)
public abstract class AbstractSpringTest {

    @SuppressWarnings("resource")
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("xanaduwars")
            .withUsername("test")
            .withPassword("test");

    @PersistenceContext
    protected EntityManager entityManager;

    protected SeededRng random;

    @BeforeAll
    static void startContainer() {
        POSTGRES.start();
    }

    @BeforeEach
    void setRandom(SeededRng random) {
        this.random = random;
    }

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

}
