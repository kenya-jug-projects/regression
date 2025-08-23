package com.kenyajug.regression.service_tests;
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
import com.kenyajug.regression.TestData;
import com.kenyajug.regression.entities.AppLog;
import com.kenyajug.regression.entities.LogsDataSource;
import com.kenyajug.regression.entities.LogsMetadata;
import com.kenyajug.regression.models.InstantTraceGroup;
import com.kenyajug.regression.repository.AppLogRepository;
import com.kenyajug.regression.repository.LogsDataSourceRepository;
import com.kenyajug.regression.repository.LogsMetadataRepository;
import com.kenyajug.regression.services.IngestionService;
import com.kenyajug.regression.utils.Constants;
import com.kenyajug.regression.utils.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
@Slf4j
@ExtendWith(MockitoExtension.class)
public class IngestionServiceTests {
    @InjectMocks
    private IngestionService ingestionService;
    private Path logsFilePath = Path.of("mock_log.log");
    @Mock
    private LogsDataSourceRepository dataSourceRepository;
    @Mock
    private AppLogRepository appLogRepository;
    @Mock
    private LogsMetadataRepository metadataRepository;
    @BeforeEach
    public void setUp() throws Exception {
        Files.deleteIfExists(logsFilePath);
        Files.createFile(logsFilePath);
    }
    @AfterEach
    public void cleanUp() throws Exception {
        Files.delete(logsFilePath);
    }
    @Test
    @DisplayName("Verify that Regression collects raw logs from local platform log files")
    public void shouldCollectRawLogsTest() throws Exception {
        Files.writeString(logsFilePath, TestData.rawTomcatLogs());
        var datasource = new LogsDataSource(
                "39a34b57-c25d-477c-bd7e-358d2635636a",
                "Apache Tomcat 10 (Linux Ubuntu 20.04.01)",
                "local",
                "App_UUID1",
                LocalDateTime.of(2000, 11, 5, 21, 15, 0),
                "mock_log.log"
        );
        var rawLogs = ingestionService.collectRawLogs(datasource);
        assertThat(rawLogs).isNotNull();
        assertThat(rawLogs).isNotEmpty();
        assertThat(rawLogs.length()).isGreaterThan(0);
        assertThat(rawLogs).isEqualTo(TestData.rawTomcatLogs());
    }
    @Test
    @DisplayName("Verify that Regression divides raw logs into chunks based on timestamp")
    public void shouldComposeRawLogsTraceGroupTest() {
        var rawLogs = """
                15-May-2025 14:32:10.213 INFO [main] org.apache.catalina.startup.Catalina.start Server startup in [5452] milliseconds
                15-May-2025 14:32:15.120 INFO [http-nio-8080-exec-1] org.apache.coyote.http11.Http11Processor.service Request processed: GET /app/home
                15-May-2025 14:32:15.134 INFO [http-nio-8080-exec-1] com.example.service.UserService.getUserById Fetching user with ID 42
                15-May-2025 14:32:15.138 WARNING [http-nio-8080-exec-1] com.example.dao.UserDao.getUserById User not found: 42
                15-May-2025 14:32:20.500 SEVERE [http-nio-8080-exec-2] com.example.controller.LoginController.login Failed to authenticate user
                java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "username" is null
                    at com.example.service.AuthService.authenticate(AuthService.java:45)
                    at com.example.controller.LoginController.login(LoginController.java:33)
                    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
                    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
                    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
                    at java.lang.reflect.Method.invoke(Method.java:566)
                    at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:227)
                """;
        var expectedGroups = List.of(
                new InstantTraceGroup(
                        LocalDateTime.of(2025, 5, 15, 14, 32, 10, 213_000_000),
                        "15-May-2025 14:32:10.213 INFO [main] org.apache.catalina.startup.Catalina.start Server startup in [5452] milliseconds".length(),
                        "15-May-2025 14:32:10.213 INFO [main] org.apache.catalina.startup.Catalina.start Server startup in [5452] milliseconds"
                ),
                new InstantTraceGroup(
                        LocalDateTime.of(2025, 5, 15, 14, 32, 15, 120_000_000),
                        "15-May-2025 14:32:15.120 INFO [http-nio-8080-exec-1] org.apache.coyote.http11.Http11Processor.service Request processed: GET /app/home".length(),
                        "15-May-2025 14:32:15.120 INFO [http-nio-8080-exec-1] org.apache.coyote.http11.Http11Processor.service Request processed: GET /app/home"
                ),
                new InstantTraceGroup(
                        LocalDateTime.of(2025, 5, 15, 14, 32, 15, 134_000_000),
                        "15-May-2025 14:32:15.134 INFO [http-nio-8080-exec-1] com.example.service.UserService.getUserById Fetching user with ID 42".length(),
                        "15-May-2025 14:32:15.134 INFO [http-nio-8080-exec-1] com.example.service.UserService.getUserById Fetching user with ID 42"
                ),
                new InstantTraceGroup(
                        LocalDateTime.of(2025, 5, 15, 14, 32, 15, 138_000_000),
                        "15-May-2025 14:32:15.138 WARNING [http-nio-8080-exec-1] com.example.dao.UserDao.getUserById User not found: 42".length(),
                        "15-May-2025 14:32:15.138 WARNING [http-nio-8080-exec-1] com.example.dao.UserDao.getUserById User not found: 42"
                ),
                new InstantTraceGroup(
                        LocalDateTime.of(2025, 5, 15, 14, 32, 20, 500_000_000),
                        """
                                15-May-2025 14:32:20.500 SEVERE [http-nio-8080-exec-2] com.example.controller.LoginController.login Failed to authenticate user
                                java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "username" is null
                                    at com.example.service.AuthService.authenticate(AuthService.java:45)
                                    at com.example.controller.LoginController.login(LoginController.java:33)
                                    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
                                    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
                                    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
                                    at java.lang.reflect.Method.invoke(Method.java:566)
                                    at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:227)
                                """.length(),
                        """
                                15-May-2025 14:32:20.500 SEVERE [http-nio-8080-exec-2] com.example.controller.LoginController.login Failed to authenticate user
                                java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "username" is null
                                    at com.example.service.AuthService.authenticate(AuthService.java:45)
                                    at com.example.controller.LoginController.login(LoginController.java:33)
                                    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
                                    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
                                    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
                                    at java.lang.reflect.Method.invoke(Method.java:566)
                                    at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:227)
                                """
                )
        );
        var actualGroups = ingestionService.composeRawLogsTraceGroup(rawLogs);
        assertThat(actualGroups).isNotEmpty();
        assertThat(actualGroups.size()).isEqualTo(expectedGroups.size());
        assertThat(actualGroups.getFirst()).isEqualTo(expectedGroups.getFirst());
        var lastInstantChunk = actualGroups.getLast();
        System.out.println("LastInstantChunk\n" + lastInstantChunk.chunk());
        assertThat(lastInstantChunk.timestamp().isEqual(LocalDateTime.of(2025, 5, 15, 14, 32, 20, 500_000_000))).isTrue();
        assertThat(lastInstantChunk.chunk().isEmpty()).isFalse();
    }
    @Test
    @DisplayName("Verify that Regression divides raw logs into chunks based on timestamp")
    public void shouldComposeRawLogsTraceGroup_Case2_Test() {
        var rawLogs = """
                INFO [main] org.apache.catalina.startup.Catalina.start Server startup in [5452] milliseconds
                INFO [http-nio-8080-exec-1] org.apache.coyote.http11.Http11Processor.service Request processed: GET /app/home
                INFO [http-nio-8080-exec-1] com.example.service.UserService.getUserById Fetching user with ID 42
                WARNING [http-nio-8080-exec-1] com.example.dao.UserDao.getUserById User not found: 42
                SEVERE [http-nio-8080-exec-2] com.example.controller.LoginController.login Failed to authenticate user
                java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "username" is null
                    at com.example.service.AuthService.authenticate(AuthService.java:45)
                    at com.example.controller.LoginController.login(LoginController.java:33)
                    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
                    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
                    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
                    at java.lang.reflect.Method.invoke(Method.java:566)
                    at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:227)
                """;
        var expectedGroups = List.of(
                new InstantTraceGroup(
                        LocalDateTime.of(2025, 5, 15, 14, 32, 10, 213_000_000),
                        "INFO [main] org.apache.catalina.startup.Catalina.start Server startup in [5452] milliseconds".length(),
                        "INFO [main] org.apache.catalina.startup.Catalina.start Server startup in [5452] milliseconds"
                ),
                new InstantTraceGroup(
                        LocalDateTime.of(2025, 5, 15, 14, 32, 15, 120_000_000),
                        "INFO [http-nio-8080-exec-1] org.apache.coyote.http11.Http11Processor.service Request processed: GET /app/home".length(),
                        "INFO [http-nio-8080-exec-1] org.apache.coyote.http11.Http11Processor.service Request processed: GET /app/home"
                ),
                new InstantTraceGroup(
                        LocalDateTime.of(2025, 5, 15, 14, 32, 15, 134_000_000),
                        "INFO [http-nio-8080-exec-1] com.example.service.UserService.getUserById Fetching user with ID 42".length(),
                        "INFO [http-nio-8080-exec-1] com.example.service.UserService.getUserById Fetching user with ID 42"
                ),
                new InstantTraceGroup(
                        LocalDateTime.of(2025, 5, 15, 14, 32, 15, 138_000_000),
                        "WARNING [http-nio-8080-exec-1] com.example.dao.UserDao.getUserById User not found: 42".length(),
                        "WARNING [http-nio-8080-exec-1] com.example.dao.UserDao.getUserById User not found: 42"
                ),
                new InstantTraceGroup(
                        LocalDateTime.of(2025, 5, 15, 14, 32, 20, 500_000_000),
                        """
                                SEVERE [http-nio-8080-exec-2] com.example.controller.LoginController.login Failed to authenticate user
                                java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "username" is null
                                    at com.example.service.AuthService.authenticate(AuthService.java:45)
                                    at com.example.controller.LoginController.login(LoginController.java:33)
                                    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
                                    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
                                    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
                                    at java.lang.reflect.Method.invoke(Method.java:566)
                                    at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:227)
                                """.length(),
                        """
                                SEVERE [http-nio-8080-exec-2] com.example.controller.LoginController.login Failed to authenticate user
                                java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "username" is null
                                    at com.example.service.AuthService.authenticate(AuthService.java:45)
                                    at com.example.controller.LoginController.login(LoginController.java:33)
                                    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
                                    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
                                    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
                                    at java.lang.reflect.Method.invoke(Method.java:566)
                                    at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:227)
                                """
                )
        );
        assertThatThrownBy(() -> ingestionService.composeRawLogsTraceGroup(rawLogs))
                .hasMessage("Failed to process logs, invalid instant group, missing timestamp");
    }
    @Test
    @DisplayName("Should extract timestamp from valid log line")
    public void shouldExtractTimestampsTest() {
        var line1 = "15-May-2025 14:32:10.213 INFO [main] org.apache.catalina.startup.Catalina.start Server startup in [5452] milliseconds";
        var line2 = "java.lang.NullPointerException: Cannot invoke String.equals(Object) because username is null";
        var line3 = "2025-05-16T09:31:14.088Z ERROR 880 --- [nio-8081-exec-2] o.l.h.api.aop.ControllersErrorHandler    : Internal server error occurred";
        var actualTimestamp1 = ingestionService.extractTimestamp(line1);
        var actualTimestamp2 = ingestionService.extractTimestamp(line2);
        var actualTimestamp3 = ingestionService.extractTimestamp(line3);
        assertThat(actualTimestamp1).isNotEmpty();
        assertThat(actualTimestamp1.get().isEqual(LocalDateTime.of(2025, 5, 15, 14, 32, 10, 213_000_000))).isTrue();
        assertThat(actualTimestamp2).isEmpty();
        assertThat(actualTimestamp3).isNotEmpty();
        assertThat(actualTimestamp3.get().isEqual(LocalDateTime.of(2025, 5, 16, 9, 31, 14, 88_000_000))).isTrue();
    }
    @Test
    public void shouldComposeLogsAndMetadataTest() {
        var datasource = new LogsDataSource(
                "39a34b57-c25d-477c-bd7e-358d2635636a",
                "Apache Tomcat 10 (Linux Ubuntu 20.04.01)",
                "local",
                "App_UUID1",
                LocalDateTime.of(2000, 11, 5, 21, 15, 0),
                "mock_log.log"
        );
        var instantTraceGroups = List.of(
                new InstantTraceGroup(
                        LocalDateTime.of(2025, 5, 15, 14, 32, 10, 213_000_000),
                        "15-May-2025 14:32:10.213 INFO [main] org.apache.catalina.startup.Catalina.start Server startup in [5452] milliseconds".length(),
                        "15-May-2025 14:32:10.213 INFO [main] org.apache.catalina.startup.Catalina.start Server startup in [5452] milliseconds"
                ),
                new InstantTraceGroup(
                        LocalDateTime.of(2025, 5, 15, 14, 32, 15, 120_000_000),
                        "15-May-2025 14:32:15.120 INFO [http-nio-8080-exec-1] org.apache.coyote.http11.Http11Processor.service Request processed: GET /app/home".length(),
                        "15-May-2025 14:32:15.120 INFO [http-nio-8080-exec-1] org.apache.coyote.http11.Http11Processor.service Request processed: GET /app/home"
                )
        );
        var logsAndMetadata = ingestionService.composeLogsAndMetadata(datasource, instantTraceGroups);
        assertThat(logsAndMetadata).isNotEmpty();
        assertThat(logsAndMetadata.keySet()).isNotEmpty();
        assertThat(logsAndMetadata.entrySet()).isNotEmpty();
        log.info("Logs Result\n{}", logsAndMetadata);
        log.info("Logs Result App Logs\n{}", logsAndMetadata.keySet());
        log.info("Logs Result App Logs.Metadata\n{}", logsAndMetadata.entrySet());
    }
    @Test
    public void shouldExtractLogLevelTest(){
        var traceGroup = new InstantTraceGroup(
                LocalDateTime.of(2025, 5, 15, 14, 32, 10, 213_000_000),
                "15-May-2025 14:32:10.213 INFO [main] org.apache.catalina.startup.Catalina.start Server startup in [5452] milliseconds".length(),
                "15-May-2025 14:32:10.213 INFO [main] org.apache.catalina.startup.Catalina.start Server startup in [5452] milliseconds"
        );
        var optionalLogLevel = ingestionService.extractLogLevel(traceGroup);
        assertThat(optionalLogLevel).isNotEmpty();
        var logLevel = optionalLogLevel.get();
        assertThat(logLevel).isNotEmpty();
        assertThat(logLevel).isEqualTo("INFO");
    }
    @Test
    public void shouldExtractLogLevel_Case2_Test(){
        var traceGroup = new InstantTraceGroup(
                LocalDateTime.of(2025, 5, 15, 14, 32, 10, 213_000_000),
                "15-May-2025 14:32:10.213 _ [main] org.apache.catalina.startup.Catalina.start Server startup in [5452] milliseconds".length(),
                "15-May-2025 14:32:10.213 _ [main] org.apache.catalina.startup.Catalina.start Server startup in [5452] milliseconds"
        );
        var optionalLogLevel = ingestionService.extractLogLevel(traceGroup);
        assertThat(optionalLogLevel).isEmpty();
    }
    @Test
    public void shouldExtractMetadataByRegexTest(){
        var appLog = new AppLog(
                "UUID1",
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-08-11 11:09:22 UTC"),
                "WARN",
                "aa53818c-5ebc-442e-b0c9-5e668bcfe85b",
                "Chrome LTS  version 132.0.6834.223",
                "Object not found exception"
        );
        var traceGroup = new InstantTraceGroup(
                LocalDateTime.of(2025, 5, 15, 14, 32, 10, 213_000_000),
                0,
                """
                        2025-05-16 14:45:22.456 ERROR 12345 --- [http-nio-8080-exec-5] com.example.api.UserController           : Failed to fetch user details
                        
                        java.lang.NullPointerException: Cannot invoke "User.getEmail()" because "user" is null
                            at com.example.api.UserController.getUser(UserController.java:45)
                            at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
                            at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
                        """
        );
        var errorClassRegexWithMessage = new Constants.Tuple("exceptionClassAndMessage",Constants.exceptionClassAndMessage);
        var optionalMetadata = ingestionService.extractMetadataByRegex(appLog,traceGroup, errorClassRegexWithMessage);
        assertThat(optionalMetadata).isNotEmpty();
        var exception = optionalMetadata.get();
        assertThat(exception.metadataValue()).isNotEmpty();
        assertThat(exception.metadataType()).isNotEmpty();
        assertThat(exception.metadataValue()).isEqualTo("java.lang.NullPointerException: Cannot invoke \"User.getEmail()\" because \"user\" is null");
        var errorClassOnlyRegex = new Constants.Tuple("exceptionClass",Constants.exceptionClass);//Constants.exceptionClass;
        optionalMetadata = ingestionService.extractMetadataByRegex(appLog,traceGroup,errorClassOnlyRegex);
        exception = optionalMetadata.get();
        assertThat(exception.metadataValue()).isNotEmpty();
        assertThat(exception.metadataType()).isNotEmpty();
        assertThat(exception.metadataValue()).isEqualTo("java.lang.NullPointerException");
    }
    @Test
    public void shouldProcessLocalLogsTest() throws Exception {
        when(dataSourceRepository.findBySourceType("local"))
                .thenReturn(List.of( new LogsDataSource(
                        "UUID1",
                        "Apache Tomcat 10 (Linux Ubuntu 20.04.01)",
                        "local",
                        "App_UUID1",
                        LocalDateTime.of(2000, 11, 5, 21, 15, 0),
                        logsFilePath.getFileName().toString()
                )));
        when(appLogRepository.existsByTimestampApplicationAndSource(any(LocalDateTime.class),anyString(),anyString())).thenReturn(false);
        Files.writeString(logsFilePath, """
                2025-05-16T09:31:14.088Z ERROR 880 --- [nio-8081-exec-2] o.l.h.api.aop.ControllersErrorHandler    : Internal server error occurred
                
                java.lang.NullPointerException: Cannot invoke "User.getEmail()" because "user" is null
                    at com.example.api.UserController.getUser(UserController.java:45)
                    at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
                    at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
                """);
        var result = ingestionService.processLocalLogs();
        verify(appLogRepository, atLeastOnce()).save(any(AppLog.class));
        verify(metadataRepository, atLeastOnce()).save(any(LogsMetadata.class));
        assertThat(result).isTrue();
    }
    @Test
    public void shouldProcessLocalLogs_Case2_Test() throws Exception {
        when(dataSourceRepository.findBySourceType("local"))
                .thenReturn(List.of( new LogsDataSource(
                        "UUID1",
                        "Apache Tomcat 10 (Linux Ubuntu 20.04.01)",
                        "local",
                        "App_UUID1",
                        LocalDateTime.of(2000, 11, 5, 21, 15, 0),
                        logsFilePath.getFileName().toString()
                )));
        Files.writeString(logsFilePath, "");
        verify(appLogRepository, times(0)).save(any(AppLog.class));
        verify(metadataRepository, times(0)).save(any(LogsMetadata.class));
        var result = ingestionService.processLocalLogs();
        assertThat(result).isFalse();
    }
    @Test
    public void shouldProcessLocalLogs_Case3_Test() throws Exception {
        when(dataSourceRepository.findBySourceType("local"))
                .thenReturn(List.of( new LogsDataSource(
                        "UUID1",
                        "Apache Tomcat 10 (Linux Ubuntu 20.04.01)",
                        "local",
                        "App_UUID1",
                        LocalDateTime.of(2000, 11, 5, 21, 15, 0),
                        logsFilePath.getFileName().toString()
                )));
        when(appLogRepository.existsByTimestampApplicationAndSource(any(LocalDateTime.class),anyString(),anyString()))
                .thenReturn(true);
        Files.writeString(logsFilePath, """
                2025-05-16T09:31:14.088Z ERROR 880 --- [nio-8081-exec-2] o.l.h.api.aop.ControllersErrorHandler    : Internal server error occurred
                
                java.lang.NullPointerException: Cannot invoke "User.getEmail()" because "user" is null
                    at com.example.api.UserController.getUser(UserController.java:45)
                    at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
                    at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
                """);
        var result = ingestionService.processLocalLogs();
        verify(appLogRepository, times(0)).save(any(AppLog.class));
        verify(metadataRepository, times(0)).save(any(LogsMetadata.class));
        assertThat(result).isFalse();
    }
    @Test
    public void shouldProcessLocalLogs_Case4_Test() throws Exception {
        when(dataSourceRepository.findBySourceType("local"))
                .thenReturn(List.of( new LogsDataSource(
                        "UUID1",
                        "Apache Tomcat 10 (Linux Ubuntu 20.04.01)",
                        "local",
                        "App_UUID1",
                        LocalDateTime.of(2000, 11, 5, 21, 15, 0),
                        "invalid_path.log"
                )));
        assertThatThrownBy(() -> ingestionService.processLocalLogs());
        verify(appLogRepository, times(0)).save(any(AppLog.class));
        verify(metadataRepository, times(0)).save(any(LogsMetadata.class));
    }
    @Test
    public void shouldSaveNewLogsTest(){
        var appLog = new AppLog(
                "UUID1",
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-08-11 11:09:22 UTC"),
                "WARN",
                "App_UUID1",
                "Chrome LTS  version 132.0.6834.223",
                "Object not found exception"
        );
        var metadata = List.of(
                new LogsMetadata(
                        "UUID1",
                        appLog.uuid(),
                        "OS",
                        "Ubuntu Desktop 24.04.2 LTS"
                )
        );
        Map<AppLog,List<LogsMetadata>> appLogListMap = new HashMap<>();
        appLogListMap.put(appLog,metadata);
        ingestionService.saveNewLogs(appLog,appLogListMap);
        verify(appLogRepository, atLeastOnce()).save(any(AppLog.class));
        verify(metadataRepository, atLeastOnce()).save(any(LogsMetadata.class));
    }
}
