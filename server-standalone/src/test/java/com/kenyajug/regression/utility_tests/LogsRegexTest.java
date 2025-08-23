package com.kenyajug.regression.utility_tests;
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
import com.kenyajug.regression.utils.Constants;
import org.junit.jupiter.api.Test;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.junit.jupiter.api.Assertions.*;
public class LogsRegexTest {
    @Test
    void testClientIpAddress() {
        String input = "Client IP: 192.168.1.1 connected";
        Matcher m = Pattern.compile(Constants.clientIpAddress).matcher(input);
        assertTrue(m.find());
        assertEquals("192.168.1.1", m.group(1));
    }
    @Test
    void testRequestLatencyMs() {
        String input = "timeTaken=123ms";
        Matcher m = Pattern.compile(Constants.requestLatencyMs).matcher(input);
        assertTrue(m.find());
        assertEquals("123", m.group(1));
        assertEquals("ms", m.group(2));
    }
    @Test
    void testConfiguredPort() {
        String input = "port = 8080";
        Matcher m = Pattern.compile(Constants.configuredPort).matcher(input);
        assertTrue(m.find());
        assertEquals("8080", m.group(1));
    }

    @Test
    void testTomcatStartupPort() {
        String input = "Tomcat started on port(s): 8080";
        Matcher m = Pattern.compile(Constants.tomcatStartupPort).matcher(input);
        assertTrue(m.find());
        assertEquals("8080", m.group(1));
    }

    @Test
    void testSessionId() {
        String input = "Cookie: JSESSIONID=ABC123DEF456";
        Matcher m = Pattern.compile(Constants.sessionId).matcher(input);
        assertTrue(m.find());
        assertEquals("ABC123DEF456", m.group(1));
    }

    @Test
    void testExceptionClassAndMessage() {
        String input = "java.lang.NullPointerException: Something went wrong";
        Matcher m = Pattern.compile(Constants.exceptionClassAndMessage).matcher(input);
        assertTrue(m.find());
        assertEquals("java.lang.NullPointerException", m.group(1));
        assertEquals("Something went wrong", m.group(2));
    }

    @Test
    void testExceptionClass() {
        String input = "Caught MyCustomException in handler";
        Matcher m = Pattern.compile(Constants.exceptionClass).matcher(input);
        assertTrue(m.find());
        assertEquals("MyCustomException", m.group(1));
    }
    @Test
    void testTraceIdentifier() {
        String input = "traceId=abc123-def456";
        Matcher m = Pattern.compile(Constants.traceIdentifier).matcher(input);
        assertTrue(m.find());
        assertEquals("abc123-def456", m.group(1));
    }
    @Test
    void testThreadName() {
        String input = "[main] INFO starting up";
        Matcher m = Pattern.compile(Constants.threadName).matcher(input);
        assertTrue(m.find());
        assertEquals("main", m.group(1));
    }
    @Test
    void testLogSeverityLevel() {
        String input = "ERROR Something bad happened";
        Matcher m = Pattern.compile(Constants.logSeverityLevel).matcher(input);
        assertTrue(m.find());
        assertEquals("ERROR", m.group(1));
    }
    @Test
    void testSourceLoggerClass() {
        String input = "INFO com.example.MyClass - Message";
        Matcher m = Pattern.compile(Constants.sourceLoggerClass).matcher(input);
        assertTrue(m.find());
        assertEquals("com.example.MyClass", m.group(1));
    }
    @Test
    void testProcessId() {
        String input = "pid=1234";
        Matcher m = Pattern.compile(Constants.processId).matcher(input);
        assertTrue(m.find());
        assertEquals("1234", m.group(1));
    }
    @Test
    void testHttpQueryParameters() {
        String input = "GET /search?q=test&page=2 HTTP/1.1";
        Matcher m = Pattern.compile(Constants.httpQueryParameters).matcher(input);
        assertTrue(m.find());
        assertEquals("q=test&page=2", m.group(1));
    }
    @Test
    void testHttpHostHeader() {
        String input = "Host: example.com";
        Matcher m = Pattern.compile(Constants.httpHostHeader).matcher(input);
        assertTrue(m.find());
        assertEquals("example.com", m.group(1));
    }
    @Test
    void testActiveProfiles() {
        String input = "The following profiles are active: dev, staging";
        Matcher m = Pattern.compile(Constants.activeProfiles).matcher(input);
        assertTrue(m.find());
        assertEquals("dev, staging", m.group(1));
    }
    @Test
    void testUserAgentHeader() {
        String input = "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64)";
        Matcher m = Pattern.compile(Constants.userAgentHeader).matcher(input);
        assertTrue(m.find());
        assertEquals("Mozilla/5.0 (Windows NT 10.0; Win64; x64)", m.group(1));
    }
    @Test
    void testHttpRequestMethodAndPath() {
        String input = "\"GET /api/v1/items?id=42 HTTP/1.1\"";
        Matcher m = Pattern.compile(Constants.httpRequestMethodAndPath).matcher(input);
        assertTrue(m.find());
        assertEquals("GET", m.group(1));
        assertEquals("/api/v1/items?id=42", m.group(2));
    }
    @Test
    void testHttpRequestPath() {
        String input = "\"GET /api/v1/items?id=42 HTTP/1.1\"";
        Matcher m = Pattern.compile(Constants.httpRequestPath).matcher(input);
        assertTrue(m.find());
        assertEquals("/api/v1/items", m.group(1));
    }
    @Test
    void testHttpResponseStatusCode() {
        String input = "\" 200 OK\"";
        Matcher m = Pattern.compile(Constants.httpResponseStatusCode).matcher(input);
        assertTrue(m.find());
        assertEquals("200", m.group(1));
    }
    @Test
    void shouldCreateConstantsTest(){
        var constant = new Constants();
        assertNotNull(constant);
    }
}

