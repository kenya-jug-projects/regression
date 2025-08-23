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
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MigrationTest {
    @Autowired
    private DataSource dataSource;
    @Test
    @Order(1)
    void databaseShouldHaveUsersTable() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            ResultSet rs = conn.getMetaData().getTables(null, null, "users", null);
            Assertions.assertTrue(rs.next(), "Expected 'users' table to exist");
        }
    }
    @Test
    @Order(2)
    void databaseShouldHaveApplicationsTable() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            ResultSet rs = conn.getMetaData().getTables(null, null, "applications", null);
            Assertions.assertTrue(rs.next(), "Expected 'applications' table to exist");
        }
    }
    @Test
    @Order(3)
    void databaseShouldHaveAppLogsTable() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            ResultSet rs = conn.getMetaData().getTables(null, null, "app_logs", null);
            Assertions.assertTrue(rs.next(), "Expected 'app_logs' table to exist");
        }
    }
    @Test
    @Order(4)
    void databaseShouldHaveLogsMetadataTable() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            ResultSet rs = conn.getMetaData().getTables(null, null, "logs_metadata", null);
            Assertions.assertTrue(rs.next(), "Expected 'logs_metadata' table to exist");
        }
    }
}

