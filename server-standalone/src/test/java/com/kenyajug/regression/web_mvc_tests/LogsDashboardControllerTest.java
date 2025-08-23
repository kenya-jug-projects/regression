package com.kenyajug.regression.web_mvc_tests;
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
import com.kenyajug.regression.resources.ApplicationResource;
import com.kenyajug.regression.resources.DatasourceResource;
import com.kenyajug.regression.resources.LogResource;
import com.kenyajug.regression.services.RetrievalService;
import com.kenyajug.regression.utils.DateTimeUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource(locations = "classpath:application-noliquibase-test.properties")
public class LogsDashboardControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private RetrievalService retrievalService;
    @Test
    public void shouldLoadLogsDashboardTest() throws Exception {
        var applicationId = "2b38887b-5afe-4d87-b34a-d2f67db5a211";
        var datasourceId = "bc8de955-a2d5-48d4-96b5-c49e7774fa01";
        var todayDateTime = LocalDateTime.now();
        var application = new ApplicationResource(applicationId,"Chromium","V8");
        var datasource = new DatasourceResource("local",datasourceId);
        var expectedLogs = List.of(
                new LogResource(
                        DateTimeUtils.localTimeString(todayDateTime.toLocalTime()),
                        "INFO",
                        application,
                        datasource,
                        "1e0b7f87-0a91-4e52-8783-602810831bf8",""),
                new LogResource(
                        DateTimeUtils.localTimeString(todayDateTime.toLocalTime()),
                        "WARN",
                        application,
                        datasource,
                        "7dab2c83-b034-45ef-9745-23cbfffcf5e0",""),
                new LogResource(
                        DateTimeUtils.localTimeString(todayDateTime.toLocalTime()),
                        "ERROR",
                        application,
                        datasource,
                        "8e4f1ffe-f22b-4687-9e3e-7a6555e5d6b4","")

        );
        var expectedDataSources = List.of(
                new DatasourceResource("Local MySQL Logs","4791111d-354b-4c19-86b2-a066e8cca138"),
                new DatasourceResource("Tomcat Logs","cd1ae6ef-363c-43f9-a521-de6c674298d2"),
                new DatasourceResource("Docker Logs Dump","61b70be0-8bc9-4994-b2d7-55efcc772d58")
        );
        when(retrievalService.listAllTodayLogs()).thenReturn(expectedLogs);
        when(retrievalService.listAllApplications()).thenReturn(List.of(
                new ApplicationResource("af3b3361-2735-4d63-b273-7d6d02e0dcb8","Chromium","V8"),
                new ApplicationResource("ae548aea-8493-40dc-9427-ff104a528f53","3o10","JVM"),
                new ApplicationResource("e9440ec2-cf14-40cb-b1db-8b2dc61805e2","Thunderbolt","Gro")
                ));
        when(retrievalService.listAllDataSources()).thenReturn(expectedDataSources);
        mockMvc.perform(get("/logs")
                .with(user("gina").roles("USER"))
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("logs-list"))
                .andExpect(model().attributeExists("logs"))
                .andExpect(model().attributeExists("selectedSeverity"))
                .andExpect(model().attributeExists("applications"))
                .andExpect(model().attributeExists("selectedDate"))
                .andExpect(model().attributeExists("selectedAppId"))
                .andExpect(model().attributeExists("dataSources"))
                .andExpect(model().attributeExists("logsFilter"))
                .andExpect(model().attributeExists("logChartData"))
                .andExpect(model().attribute("logs",expectedLogs));
    }
    @Test
    public void shouldListFilteredLogsTest() throws Exception {
        var applicationId = "2b38887b-5afe-4d87-b34a-d2f67db5a211";
        var datasourceId = "bc8de955-a2d5-48d4-96b5-c49e7774fa01";
        var todayDateTime = LocalDateTime.now();
        var application = new ApplicationResource(applicationId,"Chromium","V8");
        var datasource = new DatasourceResource("local",datasourceId);
        var expectedLogs = List.of(
                new LogResource(
                        DateTimeUtils.localTimeString(todayDateTime.toLocalTime()),
                        "INFO",
                        application,
                        datasource,
                        "1e0b7f87-0a91-4e52-8783-602810831bf8",""),
                new LogResource(
                        DateTimeUtils.localTimeString(todayDateTime.toLocalTime()),
                        "WARN",
                        application,
                        datasource,
                        "7dab2c83-b034-45ef-9745-23cbfffcf5e0",""),
                new LogResource(
                        DateTimeUtils.localTimeString(todayDateTime.toLocalTime()),
                        "ERROR",
                        application,
                        datasource,
                        "8e4f1ffe-f22b-4687-9e3e-7a6555e5d6b4","")

        );
        var expectedDataSources = List.of(
                new DatasourceResource("Local MySQL Logs","4791111d-354b-4c19-86b2-a066e8cca138"),
                new DatasourceResource("Tomcat Logs","cd1ae6ef-363c-43f9-a521-de6c674298d2"),
                new DatasourceResource("Docker Logs Dump","61b70be0-8bc9-4994-b2d7-55efcc772d58")
        );
        when(retrievalService.listAllTodayLogs()).thenReturn(expectedLogs);
        when(retrievalService.listAllApplications()).thenReturn(List.of(
                new ApplicationResource("af3b3361-2735-4d63-b273-7d6d02e0dcb8","Chromium","V8"),
                new ApplicationResource("ae548aea-8493-40dc-9427-ff104a528f53","3o10","JVM"),
                new ApplicationResource("e9440ec2-cf14-40cb-b1db-8b2dc61805e2","Thunderbolt","Gro")
        ));
        when(retrievalService.listAllDataSources()).thenReturn(expectedDataSources);
        var filterDate = LocalDate.of(2025,11,20);
        when(retrievalService.listLogs(filterDate,"INFO","af3b3361-2735-4d63-b273-7d6d02e0dcb8","ae548aea-8493-40dc-9427-ff104a528f53"))
                .thenReturn(expectedLogs);
        mockMvc.perform(get("/logs/filtered")
                        .with(user("gina").roles("USER"))
                        .with(csrf())
                        .param("selectedDate","2025-11-20")
                        .param("selectedSeverity","INFO")
                        .param("selectedAppId","af3b3361-2735-4d63-b273-7d6d02e0dcb8")
                        .param("selectedSourceId","ae548aea-8493-40dc-9427-ff104a528f53"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("logs"))
                .andExpect(model().attributeExists("selectedSeverity"))
                .andExpect(model().attributeExists("applications"))
                .andExpect(model().attributeExists("selectedDate"))
                .andExpect(model().attributeExists("selectedAppId"))
                .andExpect(model().attributeExists("dataSources"))
                .andExpect(model().attributeExists("logChartData"));
    }
    @Test
    public void shouldListFilteredLogs_Case2_WarnFilterTest() throws Exception {
        var applicationId = "2b38887b-5afe-4d87-b34a-d2f67db5a211";
        var datasourceId = "bc8de955-a2d5-48d4-96b5-c49e7774fa01";
        var todayDateTime = LocalDateTime.now();
        var application = new ApplicationResource(applicationId,"Chromium","V8");
        var datasource = new DatasourceResource("local",datasourceId);
        var expectedLogs = List.of(
                new LogResource(
                        DateTimeUtils.localTimeString(todayDateTime.toLocalTime()),
                        "INFO",
                        application,
                        datasource,
                        "1e0b7f87-0a91-4e52-8783-602810831bf8",""),
                new LogResource(
                        DateTimeUtils.localTimeString(todayDateTime.toLocalTime()),
                        "WARN",
                        application,
                        datasource,
                        "7dab2c83-b034-45ef-9745-23cbfffcf5e0",""),
                new LogResource(
                        DateTimeUtils.localTimeString(todayDateTime.toLocalTime()),
                        "ERROR",
                        application,
                        datasource,
                        "8e4f1ffe-f22b-4687-9e3e-7a6555e5d6b4","")

        );
        var expectedDataSources = List.of(
                new DatasourceResource("Local MySQL Logs","4791111d-354b-4c19-86b2-a066e8cca138"),
                new DatasourceResource("Tomcat Logs","cd1ae6ef-363c-43f9-a521-de6c674298d2"),
                new DatasourceResource("Docker Logs Dump","61b70be0-8bc9-4994-b2d7-55efcc772d58")
        );
        when(retrievalService.listAllTodayLogs()).thenReturn(expectedLogs);
        when(retrievalService.listAllApplications()).thenReturn(List.of(
                new ApplicationResource("af3b3361-2735-4d63-b273-7d6d02e0dcb8","Chromium","V8"),
                new ApplicationResource("ae548aea-8493-40dc-9427-ff104a528f53","3o10","JVM"),
                new ApplicationResource("e9440ec2-cf14-40cb-b1db-8b2dc61805e2","Thunderbolt","Gro")
        ));
        when(retrievalService.listAllDataSources()).thenReturn(expectedDataSources);
        var filterDate = LocalDate.of(2025,11,20);
        when(retrievalService.listLogs(filterDate,"WARN","af3b3361-2735-4d63-b273-7d6d02e0dcb8","ae548aea-8493-40dc-9427-ff104a528f53"))
                .thenReturn(expectedLogs);
        mockMvc.perform(get("/logs/filtered")
                        .with(user("gina").roles("USER"))
                        .with(csrf())
                        .param("selectedDate","2025-11-20")
                        .param("selectedSeverity","WARN")
                        .param("selectedAppId","af3b3361-2735-4d63-b273-7d6d02e0dcb8")
                        .param("selectedSourceId","ae548aea-8493-40dc-9427-ff104a528f53"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("logs"))
                .andExpect(model().attributeExists("selectedSeverity"))
                .andExpect(model().attributeExists("applications"))
                .andExpect(model().attributeExists("selectedDate"))
                .andExpect(model().attributeExists("selectedAppId"))
                .andExpect(model().attributeExists("dataSources"))
                .andExpect(model().attributeExists("logChartData"));
    }
    @Test
    public void shouldListFilteredLogs_Case3_ErrorFilterTest() throws Exception {
        var applicationId = "2b38887b-5afe-4d87-b34a-d2f67db5a211";
        var datasourceId = "bc8de955-a2d5-48d4-96b5-c49e7774fa01";
        var todayDateTime = LocalDateTime.now();
        var application = new ApplicationResource(applicationId,"Chromium","V8");
        var datasource = new DatasourceResource("local",datasourceId);
        var expectedLogs = List.of(
                new LogResource(
                        DateTimeUtils.localTimeString(todayDateTime.toLocalTime()),
                        "INFO",
                        application,
                        datasource,
                        "1e0b7f87-0a91-4e52-8783-602810831bf8",""),
                new LogResource(
                        DateTimeUtils.localTimeString(todayDateTime.toLocalTime()),
                        "WARN",
                        application,
                        datasource,
                        "7dab2c83-b034-45ef-9745-23cbfffcf5e0",""),
                new LogResource(
                        DateTimeUtils.localTimeString(todayDateTime.toLocalTime()),
                        "ERROR",
                        application,
                        datasource,
                        "8e4f1ffe-f22b-4687-9e3e-7a6555e5d6b4","")

        );
        var expectedDataSources = List.of(
                new DatasourceResource("Local MySQL Logs","4791111d-354b-4c19-86b2-a066e8cca138"),
                new DatasourceResource("Tomcat Logs","cd1ae6ef-363c-43f9-a521-de6c674298d2"),
                new DatasourceResource("Docker Logs Dump","61b70be0-8bc9-4994-b2d7-55efcc772d58")
        );
        when(retrievalService.listAllTodayLogs()).thenReturn(expectedLogs);
        when(retrievalService.listAllApplications()).thenReturn(List.of(
                new ApplicationResource("af3b3361-2735-4d63-b273-7d6d02e0dcb8","Chromium","V8"),
                new ApplicationResource("ae548aea-8493-40dc-9427-ff104a528f53","3o10","JVM"),
                new ApplicationResource("e9440ec2-cf14-40cb-b1db-8b2dc61805e2","Thunderbolt","Gro")
        ));
        when(retrievalService.listAllDataSources()).thenReturn(expectedDataSources);
        var filterDate = LocalDate.of(2025,11,20);
        when(retrievalService.listLogs(filterDate,"ERROR","af3b3361-2735-4d63-b273-7d6d02e0dcb8","ae548aea-8493-40dc-9427-ff104a528f53"))
                .thenReturn(expectedLogs);
        mockMvc.perform(get("/logs/filtered")
                        .with(user("gina").roles("USER"))
                        .with(csrf())
                        .param("selectedDate","2025-11-20")
                        .param("selectedSeverity","ERROR")
                        .param("selectedAppId","af3b3361-2735-4d63-b273-7d6d02e0dcb8")
                        .param("selectedSourceId","ae548aea-8493-40dc-9427-ff104a528f53"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("logs"))
                .andExpect(model().attributeExists("selectedSeverity"))
                .andExpect(model().attributeExists("applications"))
                .andExpect(model().attributeExists("selectedDate"))
                .andExpect(model().attributeExists("selectedAppId"))
                .andExpect(model().attributeExists("dataSources"))
                .andExpect(model().attributeExists("logChartData"));
    }
    @Test
    public void shouldListFilteredLogs_Case4_AllFilterTest() throws Exception {
        var applicationId = "2b38887b-5afe-4d87-b34a-d2f67db5a211";
        var datasourceId = "bc8de955-a2d5-48d4-96b5-c49e7774fa01";
        var todayDateTime = LocalDateTime.now();
        var application = new ApplicationResource(applicationId,"Chromium","V8");
        var datasource = new DatasourceResource("local",datasourceId);
        var expectedLogs = List.of(
                new LogResource(
                        DateTimeUtils.localTimeString(todayDateTime.toLocalTime()),
                        "INFO",
                        application,
                        datasource,
                        "1e0b7f87-0a91-4e52-8783-602810831bf8",""),
                new LogResource(
                        DateTimeUtils.localTimeString(todayDateTime.toLocalTime()),
                        "WARN",
                        application,
                        datasource,
                        "7dab2c83-b034-45ef-9745-23cbfffcf5e0",""),
                new LogResource(
                        DateTimeUtils.localTimeString(todayDateTime.toLocalTime()),
                        "ERROR",
                        application,
                        datasource,
                        "8e4f1ffe-f22b-4687-9e3e-7a6555e5d6b4","")

        );
        var expectedDataSources = List.of(
                new DatasourceResource("Local MySQL Logs","4791111d-354b-4c19-86b2-a066e8cca138"),
                new DatasourceResource("Tomcat Logs","cd1ae6ef-363c-43f9-a521-de6c674298d2"),
                new DatasourceResource("Docker Logs Dump","61b70be0-8bc9-4994-b2d7-55efcc772d58")
        );
        when(retrievalService.listAllTodayLogs()).thenReturn(expectedLogs);
        when(retrievalService.listAllApplications()).thenReturn(List.of(
                new ApplicationResource("af3b3361-2735-4d63-b273-7d6d02e0dcb8","Chromium","V8"),
                new ApplicationResource("ae548aea-8493-40dc-9427-ff104a528f53","3o10","JVM"),
                new ApplicationResource("e9440ec2-cf14-40cb-b1db-8b2dc61805e2","Thunderbolt","Gro")
        ));
        when(retrievalService.listAllDataSources()).thenReturn(expectedDataSources);
        var filterDate = LocalDate.of(2025,11,20);
        when(retrievalService.listLogs(filterDate,"ERROR","af3b3361-2735-4d63-b273-7d6d02e0dcb8","ae548aea-8493-40dc-9427-ff104a528f53"))
                .thenReturn(expectedLogs);
        mockMvc.perform(get("/logs/filtered")
                        .with(user("gina").roles("USER"))
                        .with(csrf())
                        .param("selectedDate","2025-11-20")
                        .param("selectedSeverity","All")
                        .param("selectedAppId","af3b3361-2735-4d63-b273-7d6d02e0dcb8")
                        .param("selectedSourceId","ae548aea-8493-40dc-9427-ff104a528f53"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("logs"))
                .andExpect(model().attributeExists("selectedSeverity"))
                .andExpect(model().attributeExists("applications"))
                .andExpect(model().attributeExists("selectedDate"))
                .andExpect(model().attributeExists("selectedAppId"))
                .andExpect(model().attributeExists("dataSources"))
                .andExpect(model().attributeExists("logChartData"));
    }
}
