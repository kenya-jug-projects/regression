package com.kenyajug.regression;
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
public class TestData {
    public static String rawTomcatLogs(){
        return """
                15-May-2025 10:02:13.821 INFO [main] org.apache.catalina.startup.VersionLoggerListener.log Server version: Apache Tomcat/9.0.65
                15-May-2025 10:02:13.823 INFO [main] org.apache.catalina.startup.VersionLoggerListener.log Server built:   Apr 25 2025 10:20:59 UTC
                15-May-2025 10:02:13.823 INFO [main] org.apache.catalina.startup.VersionLoggerListener.log Server number:  9.0.65.0
                15-May-2025 10:02:13.824 INFO [main] org.apache.catalina.startup.VersionLoggerListener.log OS Name:        Linux
                15-May-2025 10:02:13.824 INFO [main] org.apache.catalina.startup.VersionLoggerListener.log Java Home:      /usr/lib/jvm/java-17-openjdk
                15-May-2025 10:02:14.345 INFO [main] org.apache.coyote.AbstractProtocol.init Initializing ProtocolHandler ["http-nio-8080"]
                15-May-2025 10:02:14.427 INFO [main] org.apache.catalina.startup.Catalina.load Server initialization in [1532] milliseconds
                15-May-2025 10:02:14.670 INFO [main] org.springframework.boot.StartupInfoLogger.log Starting Application on server.local with PID 4219
                15-May-2025 10:02:15.015 INFO [main] org.springframework.boot.autoconfigure.logging.ConditionEvaluationReportLoggingListener.log
                
                ============================
                CONDITION EVALUATION REPORT
                ============================
                
                Positive matches:
                -----------------
                   WebMvcAutoConfiguration matched
                
                Negative matches:
                -----------------
                   JmxAutoConfiguration did not match
                
                15-May-2025 10:02:15.680 INFO [main] org.hibernate.Version.log HHH000412: Hibernate ORM core version 6.2.5.Final
                15-May-2025 10:02:16.313 INFO [main] org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl.configure HHH10001002: Using built-in connection pool (not for production use!)
                15-May-2025 10:02:17.045 INFO [main] com.myapp.Application - Application started in 3.59 seconds (JVM running for 4.82)
                15-May-2025 10:02:18.992 INFO [http-nio-8080-exec-2] com.myapp.controllers.UserController - Handling login request for user 'admin'
                
                15-May-2025 10:02:19.004 ERROR [http-nio-8080-exec-2] com.myapp.services.UserService - Invalid credentials for user 'admin'
                org.springframework.security.authentication.BadCredentialsException: Bad credentials
                    at org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider.authenticate(AbstractUserDetailsAuthenticationProvider.java:142)
                    at com.myapp.services.UserService.login(UserService.java:47)
                
                15-May-2025 10:02:21.123 INFO [http-nio-8080-exec-4] com.myapp.controllers.AppController - Listing all applications (15 found)
                
                15-May-2025 10:05:00.001 INFO [taskScheduler-1] com.myapp.jobs.HeartbeatJob - 🔄 Heartbeat check passed at 2025-05-15T10:05:00
                
                15-May-2025 10:07:42.812 WARN [http-nio-8080-exec-9] org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver - Resolved [org.springframework.web.HttpRequestMethodNotSupportedException: Request method 'DELETE' not supported]
                
                15-May-2025 10:08:02.103 INFO [http-nio-8080-exec-3] com.myapp.controllers.LogsController - Received new logs from API source [sourceId=api-123]
                
                15-May-2025 10:08:02.657 INFO [http-nio-8080-exec-3] com.myapp.services.LogIngestionService - Successfully parsed 328 log entries
                
                15-May-2025 10:09:10.443 ERROR [http-nio-8080-exec-5] com.myapp.controllers.AdminController - Password update failed: passwords do not match
                
                15-May-2025 10:12:44.892 ERROR [http-nio-8080-exec-7] com.myapp.controllers.UserController - Unexpected error while retrieving user profile
                java.lang.NullPointerException: Cannot invoke "User.getName()" because "user" is null
                	at com.myapp.controllers.UserController.getProfile(UserController.java:42)
                	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
                	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
                
                15-May-2025 10:13:15.201 WARN [http-nio-8080-exec-3] org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver - Resolved [org.springframework.http.converter.HttpMessageNotReadableException: JSON parse error: Unexpected character ('}' (code 125)); nested exception is com.fasterxml.jackson.core.JsonParseException: Unexpected character ('}' (code 125))
                	at [Source: (PushbackInputStream); line: 1, column: 57]
                
                15-May-2025 10:14:02.742 ERROR [http-nio-8080-exec-5] org.springframework.orm.jpa.JpaSystemException - could not execute statement; SQL [n/a]; constraint [UK_username]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement
                org.springframework.dao.DataIntegrityViolationException: could not execute statement
                	at org.springframework.orm.jpa.vendor.HibernateJpaDialect.convertHibernateAccessException(HibernateJpaDialect.java:259)
                	at org.hibernate.engine.jdbc.spi.SqlExceptionHelper.logExceptions(SqlExceptionHelper.java:137)
                
                15-May-2025 10:14:36.108 ERROR [http-nio-8080-exec-6] com.myapp.controllers.RegistrationController - Validation failed during registration
                org.springframework.web.bind.MethodArgumentNotValidException: Validation failed for argument [0] in public org.springframework.http.ResponseEntity<?> com.myapp.controllers.RegistrationController.register(UserDto):
                [Field error in object 'userDto' on field 'email': rejected value [admin@]; codes [Email.userDto.email,Email.email,Email.java.lang.String,Email]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [userDto.email,email]; arguments []; default message [email]]; default message [must be a well-formed email address]]
                	at org.springframework.web.method.annotation.ModelAttributeMethodProcessor.resolveArgument(ModelAttributeMethodProcessor.java:167)
                
                15-May-2025 10:15:10.544 ERROR [http-nio-8080-exec-2] com.myapp.services.LoggingService - Failed to write logs to disk
                java.nio.file.AccessDeniedException: /var/log/myapp/logs.json
                	at java.base/sun.nio.fs.UnixException.translateToIOException(UnixException.java:90)
                	at java.base/sun.nio.fs.UnixException.rethrowAsIOException(UnixException.java:111)
                	at java.base/sun.nio.fs.UnixFileSystemProvider.newByteChannel(UnixFileSystemProvider.java:219)
                	at java.base/java.nio.file.Files.newByteChannel(Files.java:378)
                
                15-May-2025 10:16:21.770 WARN [http-nio-8080-exec-1] org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver - Resolved [javax.validation.ConstraintViolationException: name: must not be blank]
                
                15-May-2025 10:18:03.231 INFO  [http-nio-8080-exec-6] com.myapp.services.UserService - User registered successfully: admin@example.com
                
                15-May-2025 10:18:04.552 INFO  [http-nio-8080-exec-7] com.myapp.controllers.LogsController - Received logs submission:
                {
                  "source": "API",
                  "applicationId": "app-2231",
                  "logLevel": "INFO",
                  "entries": [
                    {"timestamp": "2025-05-15T10:17:54", "message": "Startup completed."},
                    {"timestamp": "2025-05-15T10:18:02", "message": "Heartbeat check passed."}
                  ]
                }
                
                15-May-2025 10:18:04.955 INFO  [http-nio-8080-exec-7] com.myapp.services.LogIngestionService - Processed 2 log entries for applicationId=app-2231
                
                15-May-2025 10:18:06.148 ERROR [http-nio-8080-exec-3] com.myapp.services.ApiClient - Failed to fetch logs from external API
                org.springframework.web.client.HttpServerErrorException$InternalServerError: 500 : "Internal Server Error"
                	at org.springframework.web.client.DefaultResponseErrorHandler.handleError(DefaultResponseErrorHandler.java:94)
                	at com.myapp.services.ApiClient.fetchLogs(ApiClient.java:72)
                
                15-May-2025 10:18:08.017 INFO  [http-nio-8080-exec-5] com.myapp.controllers.AdminController - Password updated successfully for userId=admin-001
                
                15-May-2025 10:18:10.905 WARN  [http-nio-8080-exec-8] com.myapp.controllers.RegistrationController - Registration failed: email already exists
                
                15-May-2025 10:18:11.318 ERROR [http-nio-8080-exec-4] com.myapp.controllers.LogsController - Invalid payload received
                Request Body:
                {
                  "source": "",
                  "applicationId": "",
                  "entries": []
                }
                org.springframework.web.bind.MethodArgumentNotValidException: Validation failed for argument [0] in method: public ResponseEntity logs(LogRequest request)
                
                15-May-2025 10:18:14.221 INFO  [taskScheduler-1] com.myapp.jobs.HeartbeatJob - 🔄 Heartbeat task executed at 2025-05-15T10:18:14
                
                15-May-2025 10:18:15.541 INFO  [http-nio-8080-exec-9] com.myapp.controllers.FileUploadController - Log file uploaded:
                {
                  "filename": "logs-2025-05-15.txt",
                  "size": "28KB",
                  "contentType": "text/plain"
                }
                
                15-May-2025 10:18:16.674 ERROR [http-nio-8080-exec-10] com.myapp.controllers.AppController - Failed to save application due to validation error
                org.springframework.web.bind.MethodArgumentNotValidException: Field 'runtimeEnvironment' must not be blank
                
                15-May-2025 10:18:17.411 INFO  [http-nio-8080-exec-2] com.myapp.controllers.AppController - New application saved:
                {
                  "uuid": "a8f5f167f44f4964e6c998dee827110c",
                  "name": "Audit Service",
                  "runtimeEnvironment": "Linux"
                }
                
                """;
    }
}
