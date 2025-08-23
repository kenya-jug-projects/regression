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
import com.kenyajug.regression.controllers.DatasourceController;
import com.kenyajug.regression.entities.Application;
import com.kenyajug.regression.entities.LogsDataSource;
import com.kenyajug.regression.entities.User;
import com.kenyajug.regression.repository.ApplicationsRepository;
import com.kenyajug.regression.repository.LogsDataSourceRepository;
import com.kenyajug.regression.repository.UserRepository;
import com.kenyajug.regression.security.SecurityHelper;
import com.kenyajug.regression.security.SecurityUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
@AutoConfigureMockMvc
@SpringBootTest
@TestPropertySource(locations = "classpath:application-noliquibase-test.properties")
public class DataSourceUITest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private SecurityHelper securityHelper;
    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private ApplicationsRepository applicationsRepository;
    @MockitoBean
    private LogsDataSourceRepository dataSourceRepository;
    @Autowired
    private DatasourceController datasourceController;
    @Test
    @DisplayName("Should display data source form successfully")
    public void shouldDisplayDataSourceFormTest() throws Exception {
        var timestamp = LocalDateTime.of(2000,11,8,17,34,50);
        var apps = List.of(
                new Application("2097f3c8-e08d-4499-b536-753e9f4aded3","Gimp","1.0","JVM","",timestamp),
                new Application("2097f3c8-e08d-4499-b536-753e9f4aded3","Chrome","1.0","V8","",timestamp),
                new Application("2097f3c8-e08d-4499-b536-753e9f4aded3","Xcode","1.0","macOs","",timestamp)
        );
        var securityUser = new SecurityUser("vladimir@ru.com","$2a$10$D1r0ghp70r...aC7pS3Ozi3IM", List.of());
        var user = new User("2ae29319-c7cb-4964-a7ab-4905715f5105",
                "vladimir@ru.com",
                "$2a$10$D1r0ghp70r...aC7pS3Ozi3IM",
                "",
                LocalDateTime.now());
        when(securityHelper.findAuthenticatedUser()).thenReturn(securityUser);
        when(userRepository.findByUsername(securityUser.getUsername())).thenReturn(Optional.of(user));
        when(applicationsRepository.findByOwner(user)).thenReturn(apps);
        mockMvc.perform(get("/add/data/source")
                .with(user("mike").roles("ADMIN"))
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("data-source-form"))
                .andExpect(model().attributeExists("datasource"))
                .andExpect(model().attributeExists("applications"));

    }
    @Test
    @DisplayName("Should save new data source successfully")
    public void shouldSaveNewDatasourceTest() throws Exception {
        var entity = new LogsDataSource(
                "b6ac8cf2-5497-47c4-93b9-c8090737d410",
                "Chrome Memory Dump Logs",
                "V8 Runtime",
                "996233d5-1df5-409f-942e-05feb417f90e",
                LocalDateTime.now(),
                "/var/app/v8/logs/v8_logs.log");
        mockMvc.perform(post("/add/data/source")
                .with(user("dan").roles("ADMIN"))
                .with(csrf())
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("uuid","b6ac8cf2-5497-47c4-93b9-c8090737d410")
                .param("name","Chrome Memory Dump Logs")
                .param("sourceType","V8 Runtime")
                .param("applicationId","996233d5-1df5-409f-942e-05feb417f90e")
                .param("createdAt",entity.createdAt().toString())
                .param("logFilePath","/var/app/v8/logs/v8_logs.log"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
        verify(dataSourceRepository, times(1)).save(entity);
    }
    @Test
    @DisplayName("Should not save data source with empty required fields")
    public void shouldNot_SaveWithEmptyFieldsTest() throws Exception {
        var entity = new LogsDataSource(
                "b6ac8cf2-5497-47c4-93b9-c8090737d410",
                "",
                "",
                "",
                LocalDateTime.now(),
                "");
        mockMvc.perform(post("/add/data/source")
                .with(user("dan").roles("ADMIN"))
                .with(csrf())
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("uuid","b6ac8cf2-5497-47c4-93b9-c8090737d410")
                .param("name","")
                .param("sourceType","")
                .param("applicationId","")
                .param("createdAt",entity.createdAt().toString())
                .param("logFilePath",""))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("data-source-form"));
        verify(dataSourceRepository, times(0)).save(entity);
    }
    @Test
    @DisplayName("Throw security exception if principle user is not persisted")
    public void shouldThrowSecurityException_Case2_Test()  {
        var securityUser = new SecurityUser("vladimir@ru.com","$2a$10$D1r0ghp70r...aC7pS3Ozi3IM", List.of());
        when(securityHelper.findAuthenticatedUser()).thenReturn(securityUser);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        assertThrows(SecurityException.class, () -> datasourceController.datasourceForm(null));
    }
    @Test
    @DisplayName("Should display the data source list successfully")
    public void shouldDisplayDataSourceListTest() throws Exception {
        var datasourceList = List.of(
                new LogsDataSource(
                        "UUID1",
                        "Apache Tomcat 10 (Linux Ubuntu 20.04.01)",
                        "local",
                        "App_UUID1",
                        LocalDateTime.of(2000, 11, 5, 21, 15, 0),
                        "/var/log/mysql/error.log"
                ),
                new LogsDataSource(
                        "UUID2",
                        "Apache Tomcat 10 (Linux Ubuntu 20.04.01)",
                        "local",
                        "App_UUID1",
                        LocalDateTime.of(2001, 11, 5, 21, 15, 0),
                        "/var/log/tomcat/catalina.out"
                )
        );
        when(dataSourceRepository.findAll()).thenReturn(datasourceList);
        mockMvc.perform(get("/data/sources")
                .with(user("maxwell").roles("USER"))
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("datasource-list"))
                .andExpect(model().attributeExists("logsDatasourceList"))
                .andExpect(model().attribute("logsDatasourceList",datasourceList));
    }
}
