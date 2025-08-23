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
import com.kenyajug.regression.entities.AppLog;
import com.kenyajug.regression.repository.AppLogRepository;
import com.kenyajug.regression.utils.DateTimeUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class AppLogRepositoryTest {
    @Autowired
    private JdbcClient jdbcClient;
    @Autowired
    private AppLogRepository repository;
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
        var entity = new AppLog(
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
        var entity1 = new AppLog(
                "UUID1",
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-08-11 11:09:22 UTC"),
                "WARN",
                appId,
                "Chrome LTS  version 132.0.6834.223",
                "Object not found exception"
        );
        var entity2 = new AppLog(
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
                .sorted(Comparator.comparing(AppLog::uuid))
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
        var entity = new AppLog(
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
        var entity1 = new AppLog(
                "UUID1",
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-08-11 11:09:22 UTC"),
                "WARN",
                appId,
                "Chrome LTS  version 132.0.6834.223",
                "Object not found exception"
        );
        var entity2 = new AppLog(
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
                .sorted(Comparator.comparing(AppLog::uuid))
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
        var entity = new AppLog(
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
        var entity = new AppLog(
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
        var entityUpdated = new AppLog(
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
    @Test
    public void shouldCheck_existsByTimestampApplicationAndSource_Test(){
        var entity = new AppLog(
                "UUID1",
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-08-11 11:09:22 UTC"),
                "WARN",
                appId,
                "data_source_uuid",
                "Object not found exception"
        );
        repository.save(entity);
        var exists = repository.existsById(entity.uuid());
        assertThat(exists).isTrue();
        exists = repository.existsByTimestampApplicationAndSource(entity.timestamp(),entity.applicationId(),entity.logSource());
        assertThat(exists).isTrue();
    }
    @Test
    public void shouldCheck_existsByTimestampApplicationAndSource_Case2_Test(){
        var timestamp = DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-08-11 11:09:22 UTC");
        var exists = repository.existsByTimestampApplicationAndSource(timestamp,appId,"data_source_uuid");
        assertThat(exists).isFalse();
    }
    @Test
    public void shouldFindLogsByApplicationAndDatasourceTest(){
        var applicationId = "8e468a74-8d2d-4784-981e-5f195071b50a";
        var datasourceId = "5066eb2e-06dd-4e82-a0ba-9d771132ca09";
        var entity = new AppLog(
                "UUID1",
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-08-11 11:09:22 UTC"),
                "WARN",
                applicationId,
                datasourceId,
                "Object not found exception"
        );
        repository.save(entity);
        var exists = repository.existsById(entity.uuid());
        assertThat(exists).isTrue();
        var logs = repository.findByApplicationAndDatasource(applicationId,datasourceId);
        assertThat(logs).isNotEmpty();
        assertThat(logs.size()).isEqualTo(1);
        var appLog = logs.getFirst();
        assertThat(appLog).isNotNull();
        assertThat(appLog.uuid()).isEqualTo(entity.uuid());
        assertThat(DateTimeUtils.localDateTimeToUTCTime(appLog.timestamp())).isEqualTo("2025-08-11 11:09:22 UTC");
        assertThat(appLog.severity()).isEqualTo("WARN");
        assertThat(appLog.applicationId()).isEqualTo(applicationId);
        assertThat(appLog.logSource()).isEqualTo(datasourceId);
        assertThat(appLog.message()).isEqualTo("Object not found exception");
    }
    @Test
    @DisplayName("Should find logs by severity and date")
    public void shouldFindBySeverityAndDateTest(){
        var entity1 = new AppLog(
                "UUID1",
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("1990-11-22 11:09:22 UTC"),
                "WARN",
                appId,
                "Chrome LTS  version 132.0.6834.223",
                "Object not found exception"
        );
        var entity2 = new AppLog(
                "UUID2",
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("1990-11-22 10:49:50 UTC"),
                "WARN",
                appId,
                "Chrome LTS  version 132.0.6834.223",
                "Object not found exception"
        );
        repository.save(entity1);
        repository.save(entity2);
        var entities = repository.findAll();
        assertThat(entities).isNotEmpty();
        var date = LocalDate.of(1990,11,22);
        var filtered = repository.findBySeverityAndDate("WARN",date);
        assertThat(filtered).isNotEmpty();
        assertThat(filtered.size()).isEqualTo(2);
    }
}
