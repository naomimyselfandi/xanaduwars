package io.github.naomimyselfandi.xanaduwars.integration;

import io.github.naomimyselfandi.xanaduwars.testing.AbstractSpringTest;
import io.github.naomimyselfandi.xanaduwars.util.AbstractEntity;
import io.github.naomimyselfandi.xanaduwars.util.Id;
import org.junit.jupiter.api.AfterEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Rollback
@Transactional
@SpringBootTest
public abstract class AbstractIntegrationTest extends AbstractSpringTest {

    private final List<Runnable> teardown = new ArrayList<>();

    @AfterEach
    void executeTeardownCallbacks() {
        if (teardown.isEmpty()) return;
        if (!TestTransaction.isActive()) TestTransaction.start();
        for (var it : teardown) it.run();
        TestTransaction.flagForCommit();
        TestTransaction.end();
    }

    protected void startNewTransaction() {
        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();
    }

    protected <T extends AbstractEntity<T>> T save(CrudRepository<T, Id<T>> repository, T entity) {
        var saved = repository.save(entity);
        teardown.add(() -> repository.delete(saved));
        return saved;
    }

}
