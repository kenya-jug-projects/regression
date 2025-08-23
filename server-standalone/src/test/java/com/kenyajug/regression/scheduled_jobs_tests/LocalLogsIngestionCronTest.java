package com.kenyajug.regression.scheduled_jobs_tests;
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
import com.kenyajug.regression.scheduled.LocalLogsIngestionCron;
import com.kenyajug.regression.services.IngestionService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
@SpringBootTest
@TestPropertySource(locations = "classpath:application-noliquibase-test.properties")
public class LocalLogsIngestionCronTest {
    @MockitoSpyBean
    private LocalLogsIngestionCron logsIngestionCron;
    @MockitoBean
    private IngestionService ingestionService;
    @Test
    public void shouldScheduleAndRunLogIngestionCronTest() throws InterruptedException, IOException {
        TimeUnit.SECONDS.sleep(10);
        verify(logsIngestionCron, atLeastOnce()).runIngestionJob();
    }
}
