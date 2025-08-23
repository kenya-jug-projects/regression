package com.kenyajug.regression.persistence_tests;
/*
 * MIT License
 *
 * Copyright (c) 2025 Kenya JUG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
import com.kenyajug.regression.entities.LogsDataSource;
import com.kenyajug.regression.repository.LogsDataSourceRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Comparator;
import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class LogsDataSourceRepositoryTest {
    @Autowired
    private JdbcClient jdbcClient;
    @Autowired
    private LogsDataSourceRepository repository;
    @AfterEach
    public void cleanUp(){
        var clearTable = """
                DELETE FROM logs_data_source;
                """;
        jdbcClient.sql(clearTable).update();
    }
    /**
     * Unit test to verify that a {@link LogsDataSource} entity can be successfully saved and retrieved
     * from the repository, ensuring all fields are correctly persisted.
     */
    @DisplayName("Should save and retrieve LogsDataSource correctly")
    @Test
    public void shouldSaveObjectTest() {
        var entity = new LogsDataSource(
                "UUID1",
                "Apache Tomcat 10 (Linux Ubuntu 20.04.01)",
                "local",
                "App_UUID1",
                LocalDateTime.of(2000, 11, 5, 21, 15, 0),
                "/var/log/mysql/error.log"
        );
        repository.save(entity);
        var optionalEntity = repository.findById(entity.uuid());
        assertThat(optionalEntity).isNotEmpty();
        var persisted = optionalEntity.get();
        assertThat(persisted).isNotNull();
        assertThat(persisted.uuid()).isEqualTo(entity.uuid());
        assertThat(persisted.name()).isEqualTo(entity.name());
        assertThat(persisted.sourceType()).isEqualTo(entity.sourceType());
        assertThat(persisted.applicationId()).isEqualTo(entity.applicationId());
        assertThat(persisted.createdAt().isEqual(entity.createdAt())).isTrue();
        assertThat(persisted.logFilePath()).isEqualTo(entity.logFilePath());
    }
    /**
     * Unit test to verify that all {@link LogsDataSource} entities can be retrieved from the repository.
     * Ensures that multiple entities are persisted and the attributes of the first (sorted) entry match expected values.
     */
    @DisplayName("Should retrieve all LogsDataSource entities and validate first entry")
    @Test
    public void shouldFindAllObjectsTest() {
        var entity1 = new LogsDataSource(
                "UUID1",
                "Apache Tomcat 10 (Linux Ubuntu 20.04.01)",
                "local",
                "App_UUID1",
                LocalDateTime.of(2000, 11, 5, 21, 15, 0),
                "/var/log/mysql/error.log"
        );
        var entity2 = new LogsDataSource(
                "UUID2",
                "Apache Tomcat 9 (Linux Ubuntu 20.04.01)",
                "local",
                "App_UUID2",
                LocalDateTime.of(2000, 11, 5, 21, 15, 0),
                "/var/log/mysql/error.log"
        );

        repository.save(entity1);
        repository.save(entity2);

        var users = repository.findAll();
        assertThat(users).isNotEmpty();
        var persistedOptional = users.stream()
                .sorted(Comparator.comparing(LogsDataSource::uuid))
                .findFirst();
        assertThat(persistedOptional).isNotEmpty();
        var persisted = persistedOptional.get();
        assertThat(persisted).isNotNull();
        assertThat(persisted.uuid()).isEqualTo("UUID1");
        assertThat(persisted.name()).isEqualTo(entity1.name());
        assertThat(persisted.sourceType()).isEqualTo(entity1.sourceType());
        assertThat(persisted.applicationId()).isEqualTo(entity1.applicationId());
        assertThat(persisted.createdAt().isEqual(entity1.createdAt())).isTrue();
        assertThat(persisted.logFilePath()).isEqualTo(entity1.logFilePath());
    }
    /**
     * Unit test to verify that a {@link LogsDataSource} entity can be deleted from the repository by its ID.
     * Ensures the entity is persisted first, and then properly removed with no remaining trace upon retrieval.
     */
    @DisplayName("Should delete LogsDataSource entity by ID successfully")
    @Test
    public void shouldDeleteByIdTest(){
        var entity = new LogsDataSource(
                "UUID1",
                "Apache Tomcat 10 (Linux Ubuntu 20.04.01)",
                "local",
                "App_UUID1",
                LocalDateTime.of(2000,11,5,21,15,0),
                "/var/log/mysql/error.log"
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
    /**
     * Unit test to verify that all {@link LogsDataSource} entities can be successfully deleted from the repository.
     * Ensures multiple entities are saved, validated, and then completely removed using the deleteAll() operation.
     */
    @DisplayName("Should delete all LogsDataSource entities from the repository")
    @Test
    public void shouldDeleteAllObjectsTest(){
        var entity1 = new LogsDataSource(
                "UUID1",
                "Apache Tomcat 10 (Linux Ubuntu 20.04.01)",
                "local",
                "App_UUID1",
                LocalDateTime.of(2000,11,5,21,15,0),
                "/var/log/mysql/error.log"
        );
        var entity2 = new LogsDataSource(
                "UUID2",
                "Apache Tomcat 10 (Linux Ubuntu 20.04.01)",
                "local",
                "App_UUID1",
                LocalDateTime.of(2000,11,5,21,15,0),
                "/var/log/mysql/error.log"
        );
        repository.save(entity1);
        repository.save(entity2);
        var entities = repository.findAll();
        assertThat(entities).isNotEmpty();
        var persistedOptional = entities.stream()
                .sorted(Comparator.comparing(LogsDataSource::uuid))
                .findFirst();
        assertThat(persistedOptional).isNotEmpty();
        var persisted = persistedOptional.get();
        assertThat(persisted).isNotNull();
        assertThat(persisted.uuid()).isEqualTo("UUID1");
        repository.deleteAll();
        entities = repository.findAll();
        assertThat(entities).isEmpty();
    }
    /**
     * Unit test to verify the existence check functionality of the repository.
     * Ensures that {@link LogsDataSource} presence can be confirmed by ID after saving,
     * and correctly returns false after deletion.
     */
    @DisplayName("Should verify existence of LogsDataSource by ID before and after deletion")
    @Test
    public void shouldCheckObjectExistenceById_Test(){
        var entity = new LogsDataSource(
                "UUID1",
                "Apache Tomcat 10 (Linux Ubuntu 20.04.01)",
                "local",
                "App_UUID1",
                LocalDateTime.of(2000,11,5,21,15,0),
                "/var/log/mysql/error.log"
        );
        repository.save(entity);
        var exists = repository.existsById(entity.uuid());
        assertThat(exists).isTrue();
        repository.deleteAll();
        exists = repository.existsById(entity.uuid());
        assertThat(exists).isFalse();
    }
    /**
     * Unit test to verify that a {@link LogsDataSource} entity can be successfully updated in the repository.
     * The test ensures the entity is first saved, then updated by ID, and that all fields reflect the new state after update.
     */
    @DisplayName("Should update LogsDataSource entity by ID and verify updated values")
    @Test
    public void shouldUpdateObjectTest(){
        var entity = new LogsDataSource(
                "UUID1",
                "Apache Tomcat 10 (Linux Ubuntu 20.04.01)",
                "local",
                "App_UUID1",
                LocalDateTime.of(2000,11,5,21,15,0),
                "/var/log/mysql/error.log"
        );
        repository.save(entity);
        var exists = repository.existsById(entity.uuid());
        assertThat(exists).isTrue();
        var entityUpdated = new LogsDataSource(
                "UUID1",
                "Apache Tomcat 10 (Linux Ubuntu 20.04.01)",
                "local",
                "App_UUID1",
                LocalDateTime.of(2000,11,5,21,15,0),
                "/var/log/update/mysql/error.log"
        );
        repository.updateById(entity.uuid(),entityUpdated);
        var updatedOptional = repository.findById(entity.uuid());
        assertThat(updatedOptional).isNotEmpty();
        var persisted = updatedOptional.get();
        assertThat(persisted).isNotNull();
        assertThat(persisted.uuid()).isEqualTo(entityUpdated.uuid());
        assertThat(persisted.name()).isEqualTo(entityUpdated.name());
        assertThat(persisted.sourceType()).isEqualTo(entityUpdated.sourceType());
        assertThat(persisted.applicationId()).isEqualTo(entityUpdated.applicationId());
        assertThat(persisted.createdAt().isEqual(entityUpdated.createdAt())).isTrue();
        assertThat(persisted.logFilePath()).isEqualTo(entityUpdated.logFilePath());
    }
    /**
     * Unit test to verify that {@link LogsDataSource} entities can be correctly retrieved by their associated application ID.
     * Ensures that only entities matching the given application ID are returned and that their data is accurate.
     */
    @DisplayName("Should find LogsDataSource by application ID")
    @Test
    public void shouldFindLogsDataSourceByParentApplicationTest(){
        var entity1 = new LogsDataSource(
                "UUID1",
                "Apache Tomcat 10 (Linux Ubuntu 20.04.01)",
                "local",
                "App_UUID1",
                LocalDateTime.of(2000,11,5,21,15,0),
                "/var/log/mysql/error.log"
        );
        var entity2 = new LogsDataSource(
                "UUID2",
                "Apache Tomcat 10 (Linux Ubuntu 20.04.01)",
                "local",
                "App_UUID2",
                LocalDateTime.of(2000,11,5,21,15,0),
                "/var/log/mysql/error.log"
        );
        repository.save(entity1);
        repository.save(entity2);
        var entities = repository.findAll();
        assertThat(entities).isNotEmpty();
        var matching = repository.findByApplicationId("App_UUID1");
        assertThat(matching).isNotEmpty();
        assertThat(matching.size()).isEqualTo(1);
        var match = matching.getFirst();
        assertThat(match).isNotNull();
        assertThat(match.uuid()).isEqualTo(entity1.uuid());
        assertThat(match.uuid()).isEqualTo(entity1.uuid());
        assertThat(match.name()).isEqualTo(entity1.name());
        assertThat(match.sourceType()).isEqualTo(entity1.sourceType());
        assertThat(match.applicationId()).isEqualTo(entity1.applicationId());
        assertThat(match.createdAt().isEqual(entity1.createdAt())).isTrue();
        assertThat(match.logFilePath()).isEqualTo(entity1.logFilePath());
    }
    @Test
    public void shouldFindBySourceTypeTest(){
        var entity1 = new LogsDataSource(
                "UUID1",
                "Apache Tomcat 10 (Linux Ubuntu 20.04.01)",
                "API",
                "App_UUID1",
                LocalDateTime.of(2000,11,5,21,15,0),
                ""
        );
        var entity2 = new LogsDataSource(
                "UUID2",
                "Apache Tomcat 10 (Linux Ubuntu 20.04.01)",
                "local",
                "App_UUID2",
                LocalDateTime.of(2000,11,5,21,15,0),
                "/var/log/mysql/error.log"
        );
        repository.save(entity1);
        repository.save(entity2);
        var entities = repository.findAll();
        assertThat(entities).isNotEmpty();
        var matching = repository.findBySourceType("local");
        assertThat(matching).isNotEmpty();
        assertThat(matching.size()).isEqualTo(1);
        var match = matching.getFirst();
        assertThat(match).isNotNull();
        assertThat(match.uuid()).isEqualTo(entity2.uuid());
        assertThat(match.uuid()).isEqualTo(entity2.uuid());
        assertThat(match.name()).isEqualTo(entity2.name());
        assertThat(match.sourceType()).isEqualTo(entity2.sourceType());
        assertThat(match.applicationId()).isEqualTo(entity2.applicationId());
        assertThat(match.createdAt().isEqual(entity2.createdAt())).isTrue();
        assertThat(match.logFilePath()).isEqualTo(entity2.logFilePath());
    }
}
