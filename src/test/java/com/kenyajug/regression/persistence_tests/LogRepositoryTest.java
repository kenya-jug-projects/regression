package com.kenyajug.regression.persistence_tests;
import com.kenyajug.regression.logger.model.Log;
import com.kenyajug.regression.logger.repository.LogRepository;
import com.kenyajug.regression.utils.DateTimeUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import java.util.Comparator;
import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class LogRepositoryTest {
    @Autowired
    private JdbcClient jdbcClient;
    @Autowired
    private LogRepository repository;
    private final String appId = "APP_UUID1";
    @AfterEach
    public void cleanUp(){
        var clearTable = """
                DELETE FROM app_logs;
                """;
        jdbcClient.sql(clearTable).update();
    }
    @Test
    public void shouldSaveObjectTest(){
        var entity = new Log(
                "UUID1",
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-08-11 11:09:22 UTC"),
                "WARN",
                appId,
                "Chrome LTS  version 132.0.6834.223",
                "Object not found exception"
        );
        repository.save(entity);
        var optionalEntity = repository.findById(entity.uuid());
        assertThat(optionalEntity).isNotEmpty();
        var persisted = optionalEntity.get();
        assertThat(persisted).isNotNull();
        assertThat(persisted.uuid()).isEqualTo(entity.uuid());
        assertThat(persisted.timestamp()).isEqualTo(entity.timestamp());
        assertThat(persisted.severity()).isEqualTo(entity.severity());
        assertThat(persisted.applicationId()).isEqualTo(entity.applicationId());
        assertThat(persisted.logSource()).isEqualTo(entity.logSource());
        assertThat(persisted.message()).isEqualTo(entity.message());
    }
    @Test
    public void shouldFindAllObjectsTest(){
        var entity1 = new Log(
                "UUID1",
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-08-11 11:09:22 UTC"),
                "WARN",
                appId,
                "Chrome LTS  version 132.0.6834.223",
                "Object not found exception"
        );
        var entity2 = new Log(
                "UUID2",
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-08-11 11:09:22 UTC"),
                "WARN",
                appId,
                "Chrome LTS  version 132.0.6834.223",
                "Object not found exception"
        );
        repository.save(entity1);
        repository.save(entity2);
        var users = repository.findAll();
        assertThat(users).isNotEmpty();
        var persistedOptional = users.stream()
                .sorted(Comparator.comparing(Log::uuid))
                .findFirst();
        assertThat(persistedOptional).isNotEmpty();
        var persisted = persistedOptional.get();
        assertThat(persisted).isNotNull();
        assertThat(persisted.uuid()).isEqualTo("UUID1");
        assertThat(persisted.timestamp()).isEqualTo(entity1.timestamp());
        assertThat(persisted.severity()).isEqualTo(entity1.severity());
        assertThat(persisted.applicationId()).isEqualTo(entity1.applicationId());
        assertThat(persisted.logSource()).isEqualTo(entity1.logSource());
        assertThat(persisted.message()).isEqualTo(entity1.message());
    }
    @Test
    public void shouldDeleteByIdTest(){
        var entity = new Log(
                "UUID1",
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-08-11 11:09:22 UTC"),
                "WARN",
                appId,
                "Chrome LTS  version 132.0.6834.223",
                "Object not found exception"
        );
        repository.save(entity);
        var optionalEntity = repository.findById(entity.uuid());
        assertThat(optionalEntity).isNotEmpty();
        var persisted = optionalEntity.get();
        assertThat(persisted).isNotNull();
        assertThat(persisted.uuid()).isEqualTo(entity.uuid());
        repository.deleteById(entity.uuid());
        optionalEntity = repository.findById(entity.uuid());
        assertThat(optionalEntity).isEmpty();
    }
    @Test
    public void shouldDeleteAllObjectsTest(){
        var entity1 = new Log(
                "UUID1",
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-08-11 11:09:22 UTC"),
                "WARN",
                appId,
                "Chrome LTS  version 132.0.6834.223",
                "Object not found exception"
        );
        var entity2 = new Log(
                "UUID2",
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-08-11 11:09:22 UTC"),
                "WARN",
                appId,
                "Chrome LTS  version 132.0.6834.223",
                "Object not found exception"
        );
        repository.save(entity1);
        repository.save(entity2);
        var entities = repository.findAll();
        assertThat(entities).isNotEmpty();
        var persistedOptional = entities.stream()
                .sorted(Comparator.comparing(Log::uuid))
                .findFirst();
        assertThat(persistedOptional).isNotEmpty();
        var persisted = persistedOptional.get();
        assertThat(persisted).isNotNull();
        assertThat(persisted.uuid()).isEqualTo("UUID1");
        repository.deleteAll();
        entities = repository.findAll();
        assertThat(entities).isEmpty();
    }
    @Test
    public void shouldCheckObjectExistenceById_Test(){
        var entity = new Log(
                "UUID1",
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-08-11 11:09:22 UTC"),
                "WARN",
                appId,
                "Chrome LTS  version 132.0.6834.223",
                "Object not found exception"
        );
        repository.save(entity);
        var exists = repository.existsById(entity.uuid());
        assertThat(exists).isTrue();
        repository.deleteAll();
        exists = repository.existsById(entity.uuid());
        assertThat(exists).isFalse();
    }
    @Test
    public void shouldUpdateObjectTest(){
        var entity = new Log(
                "UUID1",
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-08-11 11:09:22 UTC"),
                "WARN",
                appId,
                "Chrome LTS  version 132.0.6834.223",
                "Object not found exception"
        );
        repository.save(entity);
        var exists = repository.existsById(entity.uuid());
        assertThat(exists).isTrue();
        var entityUpdated = new Log(
                "UUID1",
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-08-11 11:09:22 UTC"),
                "ERROR",
                appId,
                "Chrome LTS  version 132.0.6834.223Beta",
                "Object not found exception"
        );
        repository.updateById(entity.uuid(),entityUpdated);
        var updatedOptional = repository.findById(entity.uuid());
        assertThat(updatedOptional).isNotEmpty();
        var persisted = updatedOptional.get();
        assertThat(persisted).isNotNull();
        assertThat(persisted.uuid()).isEqualTo(entity.uuid());
        assertThat(DateTimeUtils.localDateTimeToUTCTime(persisted.timestamp())).isEqualTo("2025-08-11 11:09:22 UTC");
        assertThat(persisted.severity()).isEqualTo("ERROR");
        assertThat(persisted.applicationId()).isEqualTo(appId);
        assertThat(persisted.logSource()).isEqualTo("Chrome LTS  version 132.0.6834.223Beta");
        assertThat(persisted.message()).isEqualTo("Object not found exception");
    }
}
