package com.kenyajug.regression.persistence_tests;
import com.kenyajug.regression.user.User;
import com.kenyajug.regression.user.UserRepository;
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
public class UserRepositoryTest {
    @Autowired
    private JdbcClient jdbcClient;
    @Autowired
    private UserRepository userRepository;
    @AfterEach
    public void cleanUp(){
        var clearTable = """
                DELETE FROM users;
                """;
        jdbcClient.sql(clearTable).update();
    }
    @Test
    public void shouldSaveObjectTest(){
        var entity = new User(
                "UUID1",
                "Elle Cordova",
                "encoded_password_",
                "{admin}",
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-08-11 11:09:22 UTC")
        );
        userRepository.save(entity);
        var optionalEntity = userRepository.findById(entity.uuid());
        assertThat(optionalEntity).isNotEmpty();
        var persisted = optionalEntity.get();
        assertThat(persisted).isNotNull();
        assertThat(persisted.uuid()).isEqualTo(entity.uuid());
        assertThat(persisted.username()).isEqualTo(entity.username());
        assertThat(persisted.password()).isEqualTo(entity.password());
        assertThat(persisted.roles_list_json()).isEqualTo(entity.roles_list_json());
        assertThat(persisted.created_at()).isEqualTo(entity.created_at());
    }
    @Test
    public void shouldFindAllObjectsTest(){
        var entity1 = new User(
                "UUID1",
                "Elle Cordova",
                "encoded_password_",
                "{admin}",
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-08-11 11:09:22 UTC")
        );
        var entity2 = new User(
                "UUID2",
                "Asimov",
                "encoded_password_",
                "{viewer}",
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-08-11 11:09:22 UTC")
        );
        userRepository.save(entity1);
        userRepository.save(entity2);
        var users = userRepository.findAll();
        assertThat(users).isNotEmpty();
        var persistedOptional = users.stream()
                .sorted(Comparator.comparing(User::uuid))
                .findFirst();
        assertThat(persistedOptional).isNotEmpty();
        var persisted = persistedOptional.get();
        assertThat(persisted).isNotNull();
        assertThat(persisted.uuid()).isEqualTo("UUID1");
        assertThat(persisted.username()).isEqualTo("Elle Cordova");
        assertThat(persisted.password()).isEqualTo("encoded_password_");
        assertThat(persisted.roles_list_json()).isEqualTo("{admin}");
        assertThat(DateTimeUtils.localDateTimeToUTCTime(persisted.created_at())).isEqualTo("2025-08-11 11:09:22 UTC");
    }
    @Test
    public void shouldDeleteByIdTest(){
        var entity = new User(
                "UUID1",
                "Elle Cordova",
                "encoded_password_",
                "{admin}",
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-08-11 11:09:22 UTC")
        );
        userRepository.save(entity);
        var optionalEntity = userRepository.findById(entity.uuid());
        assertThat(optionalEntity).isNotEmpty();
        var persisted = optionalEntity.get();
        assertThat(persisted).isNotNull();
        assertThat(persisted.uuid()).isEqualTo(entity.uuid());
        userRepository.deleteById(entity.uuid());
        optionalEntity = userRepository.findById(entity.uuid());
        assertThat(optionalEntity).isEmpty();
    }
    @Test
    public void shouldDeleteAllObjectsTest(){
        var entity1 = new User(
                "UUID1",
                "Elle Cordova",
                "encoded_password_",
                "{admin}",
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-08-11 11:09:22 UTC")
        );
        var entity2 = new User(
                "UUID2",
                "Asimov",
                "encoded_password_",
                "{viewer}",
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-08-11 11:09:22 UTC")
        );
        userRepository.save(entity1);
        userRepository.save(entity2);
        var entities = userRepository.findAll();
        assertThat(entities).isNotEmpty();
        var persistedOptional = entities.stream()
                .sorted(Comparator.comparing(User::uuid))
                .findFirst();
        assertThat(persistedOptional).isNotEmpty();
        var persisted = persistedOptional.get();
        assertThat(persisted).isNotNull();
        assertThat(persisted.uuid()).isEqualTo("UUID1");
        userRepository.deleteAll();
        entities = userRepository.findAll();
        assertThat(entities).isEmpty();
    }
    @Test
    public void shouldCheckObjectExistenceById_Test(){
        var entity = new User(
                "UUID1",
                "Elle Cordova",
                "encoded_password_",
                "{admin}",
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-08-11 11:09:22 UTC")
        );
        userRepository.save(entity);
        var exists = userRepository.existsById(entity.uuid());
        assertThat(exists).isTrue();
        userRepository.deleteAll();
        exists = userRepository.existsById(entity.uuid());
        assertThat(exists).isFalse();
    }
    @Test
    public void shouldUpdateObjectTest(){
        var entity = new User(
                "UUID1",
                "Elle Cordova",
                "encoded_password_",
                "{admin}",
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-08-11 11:09:22 UTC")
        );
        userRepository.save(entity);
        var exists = userRepository.existsById(entity.uuid());
        assertThat(exists).isTrue();
        var entityUpdated = new User(
                entity.uuid(),
                "Dave McAllister",
                "encoded_password_2",
                "{viewer}",
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2030-08-11 11:09:22 UTC")
        );
        userRepository.updateById(entity.uuid(),entityUpdated);
        var updatedOptional = userRepository.findById(entity.uuid());
        assertThat(updatedOptional).isNotEmpty();
        var persisted = updatedOptional.get();
        assertThat(persisted).isNotNull();
        assertThat(persisted.uuid()).isEqualTo(entity.uuid());
        assertThat(persisted.username()).isEqualTo("Dave McAllister");
        assertThat(persisted.password()).isEqualTo("encoded_password_2");
        assertThat(persisted.roles_list_json()).isEqualTo("{viewer}");
        assertThat(DateTimeUtils.localDateTimeToUTCTime(persisted.created_at())).isEqualTo("2030-08-11 11:09:22 UTC");
    }
}
