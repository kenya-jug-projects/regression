package com.kenyajug;
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
import static org.assertj.core.api.Assertions.assertThat;
import com.kenyajug.core.DatabaseManager;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.sql.SQLException;
public class DatabaseManagerTest {
    @Test
    public void shouldInitializeDatabaseSchemaTest() throws SQLException, IOException {
        var initResult = DatabaseManager.initSchema();
        assertThat(initResult).isTrue();
    }
    @Test
    public void shouldClearDatabaseTest() throws SQLException, IOException {
        var isCleared = DatabaseManager.clearDatabase();
        assertThat(isCleared).isTrue();
    }
    @Test
    public void shouldFetchDatabaseUrlTest() throws IOException {
        var url = DatabaseManager.fetchDatabaseUrl();
        assertThat(url).isNotEmpty();
        assertThat(url).isEqualTo("jdbc:sqlite:regression_test_db");
    }
    @Test
    public void shouldExtractSqlTableNameTest(){
        var schema = """
                -- Table: FeatureQAPrompt
                CREATE TABLE IF NOT EXISTS FeatureQAPrompt (
                    uuid TEXT PRIMARY KEY,
                    name TEXT NOT NULL,
                    platformUuid TEXT NOT NULL,
                    prompt TEXT NOT NULL,
                    testInstructions TEXT NOT NULL,
                    testOrder INTEGER,
                    createdDateUTC TEXT NOT NULL -- store DATETIME as ISO-8601 string
                );
                -- Table: FeatureQAResult
                CREATE TABLE IF NOT EXISTS FeatureQAResult (
                    uuid TEXT PRIMARY KEY,
                    qaPromptId TEXT NOT NULL,
                    platformId TEXT NOT NULL,
                    releaseCandidateId TEXT NOT NULL,
                    result INTEGER,
                    testNotes TEXT,
                    createdUTCDate TEXT -- store DATETIME as ISO-8601 string
                );
                """;
        var tables = DatabaseManager.extractSqlTableName(schema);
        assertThat(tables).isNotEmpty();
        assertThat(tables.size()).isEqualTo(2);
        var firstTable = tables.getFirst();
        assertThat(firstTable).isNotNull();
        assertThat(firstTable).isEqualTo("FeatureQAPrompt");
        var secondTable = tables.getLast();
        assertThat(secondTable).isNotNull();
        assertThat(secondTable).isNotEmpty();
        assertThat(secondTable).isEqualTo("FeatureQAResult");
    }
}
