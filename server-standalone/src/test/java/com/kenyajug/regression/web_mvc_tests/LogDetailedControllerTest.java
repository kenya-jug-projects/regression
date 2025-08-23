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
import com.kenyajug.regression.controllers.LogsController;
import com.kenyajug.regression.entities.LogsMetadata;
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
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource(locations = "classpath:application-noliquibase-test.properties")
public class LogDetailedControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private RetrievalService retrievalService;
    @Autowired
    private LogsController logsController;
    @Test
    public void shouldLaunchLogsDetailedViewTest() throws Exception {
        var logId = "d40475f4-2b8b-4e76-b97b-7deaddb73208";
        var applicationResource = new ApplicationResource(
                "ae548aea-8493-40dc-9427-ff104a528f53",
                "3o10",
                "JVM");
        var datasourceResource = new DatasourceResource(
                "Local MySQL Logs",
                "4791111d-354b-4c19-86b2-a066e8cca138");
        var log = new LogResource(
                DateTimeUtils.convertDateToString(LocalDate.now()),
                "WARN",
                applicationResource,
                datasourceResource,
                logId,"");
        var metadataList = List.of(
                new LogsMetadata(
                        "UUID1",
                        logId,
                        "OS",
                        "Ubuntu Desktop 24.04.2 LTS"
                ),
                new LogsMetadata(
                        "UUID1",
                        logId,
                        "OS",
                        "Ubuntu Desktop 24.04.2 LTS"
                )
        );
        when(retrievalService.findMetadataByLogId(logId))
                .thenReturn(metadataList);
        when(retrievalService.findLogsById(anyString()))
                .thenReturn(Optional.of(log));
        mockMvc.perform(get("/logs/" + logId)
                        .with(user("linus"))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("logs-detailed"))
                .andExpect(model().attributeExists("log"))
                .andExpect(model().attributeExists("metadata"));
    }
    @Test
    public void shouldThrowRuntimeExceptionTest()  {
        var logId = "d40475f4-2b8b-4e76-b97b-7deaddb73208";
        when(retrievalService.findLogsById(logId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> logsController.logsDetailed(logId,null));
    }
}
