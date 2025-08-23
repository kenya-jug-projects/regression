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
import com.kenyajug.regression.entities.AppLog;
import com.kenyajug.regression.entities.Application;
import com.kenyajug.regression.entities.LogsDataSource;
import com.kenyajug.regression.entities.LogsMetadata;
import com.kenyajug.regression.repository.AppLogRepository;
import com.kenyajug.regression.repository.ApplicationsRepository;
import com.kenyajug.regression.repository.LogsDataSourceRepository;
import com.kenyajug.regression.repository.LogsMetadataRepository;
import com.kenyajug.regression.resources.ApplicationResource;
import com.kenyajug.regression.resources.DatasourceResource;
import com.kenyajug.regression.resources.LogResource;
import com.kenyajug.regression.services.RetrievalService;
import com.kenyajug.regression.utils.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
@Slf4j
@ExtendWith(MockitoExtension.class)
public class RetrievalServiceTest {
    @InjectMocks
    private RetrievalService retrievalService;
    @Mock
    private ApplicationsRepository applicationsRepository;
    @Mock
    private LogsDataSourceRepository logsDataSourceRepository;
    @Mock
    private AppLogRepository logRepository;
    @Mock
    private LogsMetadataRepository metadataRepository;
    @Test
    public void shouldRetrieveLogsTest(){
        var filterDate = LocalDate.of(2001,11,5);
        var severity = "";
        var appId = "2b38887b-5afe-4d87-b34a-d2f67db5a211";
        var datasourceId = "bc8de955-a2d5-48d4-96b5-c49e7774fa01";
        var application = new Application(
                appId,
                "Instagram",
                "12.05.11.43787463Beta",
                "staging-ci",
                "owner_uuid",
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2001-11-05 11:09:22 UTC")
        );
        var datasourceList = List.of(
                new LogsDataSource(
                        datasourceId,
                        "Apache Tomcat 10 (Linux Ubuntu 20.04.01)",
                        "local",
                        application.uuid(),
                        LocalDateTime.of(2000, 11, 5, 21, 15, 0),
                        "/var/log/mysql/error.log"
                )
        );
        var expectedLogs = List.of(
                new AppLog(
                        "UUID1",
                        DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2001-11-05 11:09:22 UTC"),
                        "WARN",
                        appId, datasourceId,
                        "Object not found exception"
                ),
                new AppLog(
                        "UUID2",
                        DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2001-11-05 11:09:22 UTC"),
                        "WARN",
                        appId, datasourceId,
                        "Object not found exception"
                )
        );
        when(applicationsRepository.findById(appId)).thenReturn(Optional.of(application));
        when(logsDataSourceRepository.findByApplicationId(appId)).thenReturn(datasourceList);
        when(logRepository.findByApplicationAndDatasource(appId,datasourceId)).thenReturn(expectedLogs);
        var logs = retrievalService.listLogs(filterDate,severity,appId,datasourceId);
        assertThat(logs).isNotEmpty();
    }
    @Test
    public void shouldRetrieveLogs_Case2_Test(){
        var filterDate = LocalDate.of(20001,11,5);
        var severity = "";
        var appId = "2b38887b-5afe-4d87-b34a-d2f67db5a211";
        var datasourceId = "bc8de955-a2d5-48d4-96b5-c49e7774fa01";
        when(applicationsRepository.findById(appId)).thenReturn(Optional.empty());
        var logs = retrievalService.listLogs(filterDate,severity,appId,datasourceId);
        assertThat(logs).isEmpty();
    }
    @Test
    public void shouldFilterBySeverityIfAvailableTest(){
        var filterDate = LocalDate.of(2001,11,5);
        var severity = "INFO";
        var appId = "2b38887b-5afe-4d87-b34a-d2f67db5a211";
        var datasourceId = "bc8de955-a2d5-48d4-96b5-c49e7774fa01";
        var application = new Application(
                appId,
                "Instagram",
                "12.05.11.43787463Beta",
                "staging-ci",
                "owner_uuid",
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2001-11-05 11:09:22 UTC")
        );
        var datasourceList = List.of(
                new LogsDataSource(
                        datasourceId,
                        "Apache Tomcat 10 (Linux Ubuntu 20.04.01)",
                        "local",
                        application.uuid(),
                        LocalDateTime.of(2001, 11, 5, 21, 15, 0),
                        "/var/log/mysql/error.log"
                )
        );
        var expectedLogs = List.of(
                new AppLog(
                        "UUID1",
                        DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2001-11-05 11:09:22 UTC"),
                        "WARN",
                        appId, datasourceId,
                        "Object not found exception"
                ),
                new AppLog(
                        "UUID2",
                        DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2001-11-05 11:09:22 UTC"),
                        "INFO",
                        appId, datasourceId,
                        "Object not found exception"
                )
        );
        when(applicationsRepository.findById(appId)).thenReturn(Optional.of(application));
        when(logsDataSourceRepository.findByApplicationId(appId)).thenReturn(datasourceList);
        when(logRepository.findByApplicationAndDatasource(appId,datasourceId)).thenReturn(expectedLogs);
        var logs = retrievalService.listLogs(filterDate,severity,appId,datasourceId);
        assertThat(logs).isNotEmpty();
        var log = logs.getFirst();
        assertThat(log).isNotNull();
        assertThat(log.severity()).isEqualTo(severity);
        assertThat(log.logId()).isEqualTo("UUID2");
    }
    @Test
    public void shouldReturnAllLogsIfSeverityIsEmptyTest(){
        var filterDate = LocalDate.of(2001,11,5);
        var severity = "";
        var appId = "2b38887b-5afe-4d87-b34a-d2f67db5a211";
        var datasourceId = "bc8de955-a2d5-48d4-96b5-c49e7774fa01";
        var application = new Application(
                appId,
                "Instagram",
                "12.05.11.43787463Beta",
                "staging-ci",
                "owner_uuid",
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-08-11 11:09:22 UTC")
        );
        var datasourceList = List.of(
                new LogsDataSource(
                        datasourceId,
                        "Apache Tomcat 10 (Linux Ubuntu 20.04.01)",
                        "local",
                        application.uuid(),
                        LocalDateTime.of(2001, 11, 5, 21, 15, 0),
                        "/var/log/mysql/error.log"
                )
        );
        var expectedLogs = List.of(
                new AppLog(
                        "UUID1",
                        DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2001-11-05 11:09:22 UTC"),
                        "WARN",
                        appId, datasourceId,
                        "Object not found exception"
                ),
                new AppLog(
                        "UUID2",
                        DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2001-11-05 11:09:22 UTC"),
                        "INFO",
                        appId, datasourceId,
                        "Object not found exception"
                )
        );
        when(applicationsRepository.findById(appId)).thenReturn(Optional.of(application));
        when(logsDataSourceRepository.findByApplicationId(appId)).thenReturn(datasourceList);
        when(logRepository.findByApplicationAndDatasource(appId,datasourceId)).thenReturn(expectedLogs);
        var logs = retrievalService.listLogs(filterDate,severity,appId,datasourceId);
        assertThat(logs).isNotEmpty();
        assertThat(logs.size()).isEqualTo(2);
    }
    @Test
    public void shouldReturnAllLogsIfSeverity_IsUsing_All_Flag_Test(){
        var filterDate = LocalDate.of(2001,11,5);
        var severity = "All";
        var appId = "2b38887b-5afe-4d87-b34a-d2f67db5a211";
        var datasourceId = "bc8de955-a2d5-48d4-96b5-c49e7774fa01";
        var application = new Application(
                appId,
                "Instagram",
                "12.05.11.43787463Beta",
                "staging-ci",
                "owner_uuid",
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-08-11 11:09:22 UTC")
        );
        var datasourceList = List.of(
                new LogsDataSource(
                        datasourceId,
                        "Apache Tomcat 10 (Linux Ubuntu 20.04.01)",
                        "local",
                        application.uuid(),
                        LocalDateTime.of(2001, 11, 5, 21, 15, 0),
                        "/var/log/mysql/error.log"
                )
        );
        var expectedLogs = List.of(
                new AppLog(
                        "UUID1",
                        DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2001-11-05 11:09:22 UTC"),
                        "WARN",
                        appId, datasourceId,
                        "Object not found exception"
                ),
                new AppLog(
                        "UUID2",
                        DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2001-11-05 11:09:22 UTC"),
                        "INFO",
                        appId, datasourceId,
                        "Object not found exception"
                )
        );
        when(applicationsRepository.findById(appId)).thenReturn(Optional.of(application));
        when(logsDataSourceRepository.findByApplicationId(appId)).thenReturn(datasourceList);
        when(logRepository.findByApplicationAndDatasource(appId,datasourceId)).thenReturn(expectedLogs);
        var logs = retrievalService.listLogs(filterDate,severity,appId,datasourceId);
        assertThat(logs).isNotEmpty();
        assertThat(logs.size()).isEqualTo(2);
    }
    @Test
    public void shouldListAllTodayLogsTest(){
        var todayDateTime = LocalDateTime.now();
        var appId = "2b38887b-5afe-4d87-b34a-d2f67db5a211";
        var datasourceId = "bc8de955-a2d5-48d4-96b5-c49e7774fa01";
        var application = new Application(
                appId,
                "Instagram",
                "12.05.11.43787463Beta",
                "staging-ci",
                "owner_uuid",
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-08-11 11:09:22 UTC")
        );
        var datasource = new LogsDataSource(
                datasourceId,
                "Apache Tomcat 10 (Linux Ubuntu 20.04.01)",
                "local",
                application.uuid(),
                LocalDateTime.of(2000, 11, 5, 21, 15, 0),
                "/var/log/mysql/error.log"
        );
        var expectedLogs = List.of(
                new AppLog(
                        "UUID1",
                        DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-08-11 11:09:22 UTC"),
                        "WARN",
                        appId, datasourceId,
                        "Object not found exception"
                ),
                new AppLog(
                        "UUID2",
                        todayDateTime,
                        "INFO",
                        appId, datasourceId,
                        "Object not found exception"
                ),
                new AppLog(
                        "UUID3",
                        todayDateTime,
                        "INFO",
                        appId, datasourceId,
                        "Object not found exception"
                )
        );
        when(applicationsRepository.findById(appId)).thenReturn(Optional.of(application));
        when(logsDataSourceRepository.findById(datasourceId)).thenReturn(Optional.of(datasource));
        when(logRepository.findAll()).thenReturn(expectedLogs);
        var logs = retrievalService.listAllTodayLogs();
        assertThat(logs).isNotEmpty();
        assertThat(logs.size()).isEqualTo(2);
    }
    @Test
    public void shouldListAllTodayLogs_Case1_Test(){
        var todayDateTime = LocalDateTime.now();
        var appId = "2b38887b-5afe-4d87-b34a-d2f67db5a211";
        var datasourceId = "bc8de955-a2d5-48d4-96b5-c49e7774fa01";
        var expectedLogs = List.of(
                new AppLog(
                        "UUID1",
                        DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-08-11 11:09:22 UTC"),
                        "WARN",
                        appId, datasourceId,
                        "Object not found exception"
                ),
                new AppLog(
                        "UUID2",
                        todayDateTime,
                        "INFO",
                        appId, datasourceId,
                        "Object not found exception"
                ),
                new AppLog(
                        "UUID3",
                        todayDateTime,
                        "INFO",
                        appId, datasourceId,
                        "Object not found exception"
                )
        );
        when(logsDataSourceRepository.findById(datasourceId)).thenReturn(Optional.empty());
        when(logRepository.findAll()).thenReturn(expectedLogs);
        var logs = retrievalService.listAllTodayLogs();
        assertThat(logs).isEmpty();
    }
    @Test
    public void shouldListAllTodayLogs_Case3_Test(){
        var todayDateTime = LocalDateTime.now();
        var appId = "2b38887b-5afe-4d87-b34a-d2f67db5a211";
        var datasourceId = "bc8de955-a2d5-48d4-96b5-c49e7774fa01";
        var application = new Application(
                appId,
                "Instagram",
                "12.05.11.43787463Beta",
                "staging-ci",
                "owner_uuid",
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-08-11 11:09:22 UTC")
        );
        var datasource = new LogsDataSource(
                datasourceId,
                "Apache Tomcat 10 (Linux Ubuntu 20.04.01)",
                "local",
                application.uuid(),
                LocalDateTime.of(2000, 11, 5, 21, 15, 0),
                "/var/log/mysql/error.log"
        );
        var expectedLogs = List.of(
                new AppLog(
                        "UUID1",
                        DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-08-11 11:09:22 UTC"),
                        "WARN",
                        appId, datasourceId,
                        "Object not found exception"
                ),
                new AppLog(
                        "UUID2",
                        todayDateTime,
                        "INFO",
                        appId, datasourceId,
                        "Object not found exception"
                ),
                new AppLog(
                        "UUID3",
                        todayDateTime,
                        "INFO",
                        appId, datasourceId,
                        "Object not found exception"
                )
        );
        when(applicationsRepository.findById(appId)).thenReturn(Optional.empty());
        when(logsDataSourceRepository.findById(datasourceId)).thenReturn(Optional.of(datasource));
        when(logRepository.findAll()).thenReturn(expectedLogs);
        var logs = retrievalService.listAllTodayLogs();
        assertThat(logs).isEmpty();
    }
    @Test
    public void shouldListAllApplicationsTest(){
        var applications = List.of(
                new Application("af3b3361-2735-4d63-b273-7d6d02e0dcb8","Chromium","9.0.1","V8","0b85a7a5-1bfb-4a4a-b83b-ebe5c1136f85", LocalDateTime.now()),
                new Application("5c1cce9b-34ec-43d9-bb08-1bff71982ea5","Tally App","11.44","JVM","0b85a7a5-1bfb-4a4a-b83b-ebe5c1136f85",LocalDateTime.now()),
                new Application("e34092ef-72d5-4727-929d-ca2868801109","Tally Core","3.0","JVM","0b85a7a5-1bfb-4a4a-b83b-ebe5c1136f85",LocalDateTime.now()),
                new Application("efe5ff8d-c07f-4f82-a46e-59f8106f2abf","Crib","1.0.0","Docker","0b85a7a5-1bfb-4a4a-b83b-ebe5c1136f85",LocalDateTime.now())
        );
        var expectedResources = applications
                .stream()
                .map(e -> new ApplicationResource(e.uuid(),e.name(),e.runtimeEnvironment()))
                .toList();
        when(applicationsRepository.findAll()).thenReturn(applications);
        var actualResources = retrievalService.listAllApplications();
        assertThat(applications).isNotEmpty();
        assertThat(expectedResources.size()).isEqualTo(actualResources.size());
    }
    @Test
    public void shouldListAllDataSourcesTest(){
        var expectedDataSources = List.of(
                new DatasourceResource("Local MySQL Logs","4791111d-354b-4c19-86b2-a066e8cca138"),
                new DatasourceResource("Tomcat Logs","cd1ae6ef-363c-43f9-a521-de6c674298d2"),
                new DatasourceResource("Docker Logs Dump","61b70be0-8bc9-4994-b2d7-55efcc772d58")
        );
        var entities = List.of(
                new LogsDataSource("4791111d-354b-4c19-86b2-a066e8cca138","Local MySQL Logs","","",LocalDateTime.now(),""),
                new LogsDataSource("cd1ae6ef-363c-43f9-a521-de6c674298d2","Tomcat Logs","","",LocalDateTime.now(),""),
                new LogsDataSource("61b70be0-8bc9-4994-b2d7-55efcc772d58","Docker Logs Dump","","",LocalDateTime.now(),"")
        );
        when(logsDataSourceRepository.findAll()).thenReturn(entities);
        var actualDataSources = retrievalService.listAllDataSources();
        assertThat(actualDataSources).isNotEmpty();
        assertThat(actualDataSources.size()).isEqualTo(expectedDataSources.size());
    }
    @Test
    public void shouldComposeChartDataBySeverityTest(){
        var severity = "WARN";
        var logsDate = LocalDate.of(2001,10,18);
        var expectedHourlyData = List.of(
                0,  // 00:00
                0,  // 01:00
                0,  // 02:00
                1,  // 03:00
                0,  // 04:00
                0,  // 05:00
                2,  // 06:00
                0,  // 07:00
                0,  // 08:00
                3,  // 09:00
                0,  // 10:00
                0,  // 11:00
                5,  // 12:00
                4,  // 13:00
                0,  // 14:00
                0,  // 15:00
                6,  // 16:00
                0,  // 17:00
                0,  // 18:00
                1,  // 19:00
                0,  // 20:00
                0,  // 21:00
                0,  // 22:00
                2   // 23:00
        );
        var logs = List.of(
                // 03:00 — 1 log
                new AppLog("UUID-0300-1",
                        DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2001-10-18 03:15:00 UTC"),
                        severity, "", "Chrome LTS version 132.0.6834.223", "Sample error at 03:00"),

                // 06:00 — 2 logs
                new AppLog("UUID-0600-1",
                        DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2001-10-18 06:05:00 UTC"),
                        severity, "", "Chrome LTS version 132.0.6834.223", "Sample error at 06:00"),
                new AppLog("UUID-0600-2",
                        DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2001-10-18 06:45:00 UTC"),
                        severity, "", "Chrome LTS version 132.0.6834.223", "Sample error at 06:00"),

                // 09:00 — 3 logs
                new AppLog("UUID-0900-1",
                        DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2001-10-18 09:10:00 UTC"),
                        severity, "", "Chrome LTS version 132.0.6834.223", "Sample error at 09:00"),
                new AppLog("UUID-0900-2",
                        DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2001-10-18 09:20:00 UTC"),
                        severity, "", "Chrome LTS version 132.0.6834.223", "Sample error at 09:00"),
                new AppLog("UUID-0900-3",
                        DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2001-10-18 09:50:00 UTC"),
                        severity, "", "Chrome LTS version 132.0.6834.223", "Sample error at 09:00"),

                // 12:00 — 5 logs
                new AppLog("UUID-1200-1",
                        DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2001-10-18 12:01:00 UTC"),
                        severity, "", "Chrome LTS version 132.0.6834.223", "Sample error at 12:00"),
                new AppLog("UUID-1200-2",
                        DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2001-10-18 12:15:00 UTC"),
                        severity, "", "Chrome LTS version 132.0.6834.223", "Sample error at 12:00"),
                new AppLog("UUID-1200-3",
                        DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2001-10-18 12:30:00 UTC"),
                        severity, "", "Chrome LTS version 132.0.6834.223", "Sample error at 12:00"),
                new AppLog("UUID-1200-4",
                        DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2001-10-18 12:45:00 UTC"),
                        severity, "", "Chrome LTS version 132.0.6834.223", "Sample error at 12:00"),
                new AppLog("UUID-1200-5",
                        DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2001-10-18 12:55:00 UTC"),
                        severity, "", "Chrome LTS version 132.0.6834.223", "Sample error at 12:00"),

                // 13:00 — 4 logs
                new AppLog("UUID-1300-1",
                        DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2001-10-18 13:00:00 UTC"),
                        severity, "", "Chrome LTS version 132.0.6834.223", "Sample error at 13:00"),
                new AppLog("UUID-1300-2",
                        DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2001-10-18 13:20:00 UTC"),
                        severity, "", "Chrome LTS version 132.0.6834.223", "Sample error at 13:00"),
                new AppLog("UUID-1300-3",
                        DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2001-10-18 13:40:00 UTC"),
                        severity, "", "Chrome LTS version 132.0.6834.223", "Sample error at 13:00"),
                new AppLog("UUID-1300-4",
                        DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2001-10-18 13:50:00 UTC"),
                        severity, "", "Chrome LTS version 132.0.6834.223", "Sample error at 13:00"),

                // 16:00 — 6 logs
                new AppLog("UUID-1600-1",
                        DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2001-10-18 16:05:00 UTC"),
                        severity, "", "Chrome LTS version 132.0.6834.223", "Sample error at 16:00"),
                new AppLog("UUID-1600-2",
                        DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2001-10-18 16:10:00 UTC"),
                        severity, "", "Chrome LTS version 132.0.6834.223", "Sample error at 16:00"),
                new AppLog("UUID-1600-3",
                        DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2001-10-18 16:15:00 UTC"),
                        severity, "", "Chrome LTS version 132.0.6834.223", "Sample error at 16:00"),
                new AppLog("UUID-1600-4",
                        DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2001-10-18 16:25:00 UTC"),
                        severity, "", "Chrome LTS version 132.0.6834.223", "Sample error at 16:00"),
                new AppLog("UUID-1600-5",
                        DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2001-10-18 16:35:00 UTC"),
                        severity, "", "Chrome LTS version 132.0.6834.223", "Sample error at 16:00"),
                new AppLog("UUID-1600-6",
                        DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2001-10-18 16:45:00 UTC"),
                        severity, "", "Chrome LTS version 132.0.6834.223", "Sample error at 16:00"),

                // 19:00 — 1 log
                new AppLog("UUID-1900-1",
                        DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2001-10-18 19:15:00 UTC"),
                        severity, "", "Chrome LTS version 132.0.6834.223", "Sample error at 19:00"),

                // 23:00 — 2 logs
                new AppLog("UUID-2300-1",
                        DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2001-10-18 23:05:00 UTC"),
                        severity, "", "Chrome LTS version 132.0.6834.223", "Sample error at 23:00"),
                new AppLog("UUID-2300-2",
                        DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2001-10-18 23:30:00 UTC"),
                        severity, "", "Chrome LTS version 132.0.6834.223", "Sample error at 23:00")
        );

        when(logRepository.findBySeverityAndDate(severity,logsDate))
                .thenReturn(logs);
        var actualHourlyData = retrievalService.composeChartDataBySeverity(logsDate,severity);
        assertThat(actualHourlyData).isNotEmpty();
        assertThat(actualHourlyData.size()).isEqualTo(expectedHourlyData.size());
        log.info("{}",actualHourlyData);
    }
    @Test
    public void shouldFindLogsByIdTest(){
        var logId = "97a0d9d6-434b-4ad1-9a4a-e7dd7beb8fab";
        var appId = "6d9bfbad-f591-4a13-95ec-8f584381b7eb";
        var datasourceId = "a3604879-6a0a-4747-9292-02857ba1a06f";
        var application = new Application(
                appId,
                "Instagram",
                "12.05.11.43787463Beta",
                "staging-ci",
                "owner_uuid",
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-08-11 11:09:22 UTC")
        );
        var applicationResource = new ApplicationResource(
                "ae548aea-8493-40dc-9427-ff104a528f53",
                "3o10",
                "JVM");
        var datasource = new LogsDataSource(
                datasourceId,
                "Apache Tomcat 10 (Linux Ubuntu 20.04.01)",
                "local",
                application.uuid(),
                LocalDateTime.of(2000, 11, 5, 21, 15, 0),
                "/var/log/mysql/error.log"
        );
        var datasourceResource = new DatasourceResource(
                "Local MySQL Logs",
                "4791111d-354b-4c19-86b2-a066e8cca138");
        var expectedLog = new AppLog(
                logId,
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2001-11-05 11:09:22 UTC"),
                "WARN",
                application.uuid(),
                datasource.uuid(),
                "Object not found exception"
        );
        var expectedLogResource = new LogResource(
                DateTimeUtils.convertDateToString(LocalDate.now()),
                "WARN",
                applicationResource,
                datasourceResource,
                logId,
                expectedLog.message());
        when(applicationsRepository.findById(application.uuid())).thenReturn(Optional.of(application));
        when(logsDataSourceRepository.findById(datasourceId)).thenReturn(Optional.of(datasource));
        when(logRepository.findById(logId)).thenReturn(Optional.of(expectedLog));
        var optionalActualLog = retrievalService.findLogsById(logId);
        assertThat(optionalActualLog).isNotEmpty();
        var actualLog = optionalActualLog.get();
        assertThat(actualLog).isNotNull();
        assertThat(actualLog.application().name()).isEqualTo(application.name());
        assertThat(actualLog.source().name()).isEqualTo(datasource.name());
        assertThat(actualLog.severity()).isEqualTo(expectedLogResource.severity());
        assertThat(actualLog.logId()).isEqualTo(expectedLogResource.logId());
    }
    @Test
    public void shouldFindLogsById_Case1_Test(){
        var logId = "97a0d9d6-434b-4ad1-9a4a-e7dd7beb8fab";
        when(logRepository.findById(logId)).thenReturn(Optional.empty());
        var optionalActualLog = retrievalService.findLogsById(logId);
        assertThat(optionalActualLog).isEmpty();
    }
    @Test
    public void shouldFindLogsById_Case3_Test(){
        var logId = "97a0d9d6-434b-4ad1-9a4a-e7dd7beb8fab";
        var appId = "6d9bfbad-f591-4a13-95ec-8f584381b7eb";
        var datasourceId = "a3604879-6a0a-4747-9292-02857ba1a06f";
        var application = new Application(
                appId,
                "Instagram",
                "12.05.11.43787463Beta",
                "staging-ci",
                "owner_uuid",
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-08-11 11:09:22 UTC")
        );
        var datasource = new LogsDataSource(
                datasourceId,
                "Apache Tomcat 10 (Linux Ubuntu 20.04.01)",
                "local",
                application.uuid(),
                LocalDateTime.of(2000, 11, 5, 21, 15, 0),
                "/var/log/mysql/error.log"
        );
        var expectedLog = new AppLog(
                logId,
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2001-11-05 11:09:22 UTC"),
                "WARN",
                application.uuid(),
                datasource.uuid(),
                "Object not found exception"
        );
        when(logsDataSourceRepository.findById(datasourceId)).thenReturn(Optional.empty());
        when(logRepository.findById(logId)).thenReturn(Optional.of(expectedLog));
        var optionalActualLog = retrievalService.findLogsById(logId);
        assertThat(optionalActualLog).isEmpty();
    }
    @Test
    public void shouldFindLogsById_Case4_Test(){
        var logId = "97a0d9d6-434b-4ad1-9a4a-e7dd7beb8fab";
        var appId = "6d9bfbad-f591-4a13-95ec-8f584381b7eb";
        var datasourceId = "a3604879-6a0a-4747-9292-02857ba1a06f";
        var application = new Application(
                appId,
                "Instagram",
                "12.05.11.43787463Beta",
                "staging-ci",
                "owner_uuid",
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-08-11 11:09:22 UTC")
        );
        var datasource = new LogsDataSource(
                datasourceId,
                "Apache Tomcat 10 (Linux Ubuntu 20.04.01)",
                "local",
                application.uuid(),
                LocalDateTime.of(2000, 11, 5, 21, 15, 0),
                "/var/log/mysql/error.log"
        );
        var expectedLog = new AppLog(
                logId,
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2001-11-05 11:09:22 UTC"),
                "WARN",
                application.uuid(),
                datasource.uuid(),
                "Object not found exception"
        );
        when(applicationsRepository.findById(application.uuid())).thenReturn(Optional.empty());
        when(logsDataSourceRepository.findById(datasourceId)).thenReturn(Optional.of(datasource));
        when(logRepository.findById(logId)).thenReturn(Optional.of(expectedLog));
        var optionalActualLog = retrievalService.findLogsById(logId);
        assertThat(optionalActualLog).isEmpty();
    }
    @Test
    public void shouldFindMetadataByLogIdTest(){
        var logId = "97a0d9d6-434b-4ad1-9a4a-e7dd7beb8fab";
        var metadataList = List.of(
                new LogsMetadata(
                        "UUID1",
                        logId,
                        "OS",
                        "Ubuntu Desktop 24.04.2 LTS"
                ),
                new LogsMetadata(
                        "UUID2",
                        logId,
                        "OS",
                        "Ubuntu Desktop 24.04.2 LTS"
                )
        );
        when(metadataRepository.findByRootLogId(logId))
                .thenReturn(metadataList);
        var actualMetadataList = retrievalService.findMetadataByLogId(logId);
        assertThat(actualMetadataList).isNotEmpty();
    }
}
