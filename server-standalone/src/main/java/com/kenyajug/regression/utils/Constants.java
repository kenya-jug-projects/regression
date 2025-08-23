package com.kenyajug.regression.utils;
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
import java.util.List;
public class Constants {
    public record Tuple(String logName, String regex) {}
    public static List<Tuple> regexTuples = List.of(
            new Tuple("clientIpAddress", "(\\d{1,3}(?:\\.\\d{1,3}){3})"),
            new Tuple("requestLatencyMs", "time(?:Taken)?[=: ]+(\\d+)(ms)?"),
            new Tuple("configuredPort", "\\bport\\s*=\\s*(\\d{1,5})"),
            new Tuple("tomcatStartupPort", "Tomcat started on port\\(s\\): (\\d+)"),
            new Tuple("sessionId", "JSESSIONID=([A-Fa-f0-9]+)"),
            new Tuple("exceptionClassAndMessage", "([a-zA-Z0-9_.]+Exception): (.+)"),
            new Tuple("exceptionClass", "\\b([a-zA-Z0-9_.]+Exception)\\b"),
            new Tuple("traceIdentifier", "traceId[=: ]?([a-f0-9\\-]+)"),
            new Tuple("threadName", "\\[(.*?)\\]"),
            new Tuple("logSeverityLevel", "(INFO|DEBUG|ERROR|WARN)"),
            new Tuple("sourceLoggerClass", "(?:INFO|DEBUG|ERROR|WARN)\\s+([a-zA-Z0-9_.]+)\\s*-"),
            new Tuple("processId", "\\bpid[=: ]?(\\d+)"),
            new Tuple("httpQueryParameters", "\\?([^\\s]+)"),
            new Tuple("httpHostHeader", "Host:\\s*([^\\s]+)"),
            new Tuple("activeProfiles", "The following profiles are active: ([\\w,\\s]+)"),
            new Tuple("userAgentHeader", "User-Agent:\\s*([^\\r\\n]+)"),
            new Tuple("httpRequestMethodAndPath", "\\\"(GET|POST|PUT|DELETE|PATCH|OPTIONS|HEAD)\\s+([^\\s]+)"),
            new Tuple("httpRequestPath", "\"(?:GET|POST|PUT|DELETE|PATCH|OPTIONS|HEAD)\\s+([^\\s\\?]+)"),
            new Tuple("httpResponseStatusCode", "\\\"\\s(\\d{3})\\s")
    );
    public static final String clientIpAddress = "(\\d{1,3}(?:\\.\\d{1,3}){3})";
    public static final String requestLatencyMs = "time(?:Taken)?[=: ]+(\\d+)(ms)?";
    public static final String configuredPort = "\\bport\\s*=\\s*(\\d{1,5})";
    public static final String tomcatStartupPort = "Tomcat started on port\\(s\\): (\\d+)";
    public static final String sessionId = "JSESSIONID=([A-Fa-f0-9]+)";
    public static final String exceptionClassAndMessage = "([a-zA-Z0-9_.]+Exception): (.+)";
    public static final String exceptionClass = "\\b([a-zA-Z0-9_.]+Exception)\\b";
    public static final String traceIdentifier = "traceId[=: ]?([a-f0-9\\-]+)";
    public static final String threadName = "\\[(.*?)\\]";
    public static final String logSeverityLevel = "(INFO|DEBUG|ERROR|WARN)";
    public static final String sourceLoggerClass = "(?:INFO|DEBUG|ERROR|WARN)\\s+([a-zA-Z0-9_.]+)\\s*-";
    public static final String processId = "\\bpid[=: ]?(\\d+)";
    public static final String httpQueryParameters = "\\?([^\\s]+)";
    public static final String httpHostHeader = "Host:\\s*([^\\s]+)";
    public static final String activeProfiles = "The following profiles are active: ([\\w,\\s]+)";
    public static final String userAgentHeader = "User-Agent:\\s*([^\\r\\n]+)";
    public static final String httpRequestMethodAndPath = "\\\"(GET|POST|PUT|DELETE|PATCH|OPTIONS|HEAD)\\s+([^\\s]+)";
    public static final String httpRequestPath = "\"(?:GET|POST|PUT|DELETE|PATCH|OPTIONS|HEAD)\\s+([^\\s\\?]+)";
    public static final String httpResponseStatusCode = "\\\"\\s(\\d{3})\\s";
}

