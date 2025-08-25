package com.kenyajug.feature;
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
import com.kenyajug.core.DatabaseManager;
import com.kenyajug.core.DateTimeManager;
import com.kenyajug.core.OpResult;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class FeatureQAResultRepository {
    public OpResult save(FeatureQAResult qaResult) throws IOException, SQLException {
        var url = DatabaseManager.fetchDatabaseUrl();
        try(Connection connection = DriverManager.getConnection(url)) {
            var sql = """
                    INSERT INTO FeatureQAResult
                    VALUES(?,?,?,?,?,?,?)
                    """;
            var statement = connection.prepareStatement(sql);
            statement.setString(1,qaResult.uuid());
            statement.setString(2,qaResult.qaPromptId());
            statement.setString(3,qaResult.platformId());
            statement.setString(4,qaResult.releaseCandidateId());
            statement.setInt(5,qaResult.result());
            statement.setString(6,qaResult.testNotes());
            statement.setString(7, DateTimeManager.timestampToUTCText(qaResult.createdUTCDate()));
            statement.execute();
            return new OpResult(true,"New QA Result saved successfully!");
        }
    }
    public List<FeatureQAResult> findAll() throws IOException,SQLException{
        var url = DatabaseManager.fetchDatabaseUrl();
        try(Connection connection = DriverManager.getConnection(url)){
            var sql = """
                    SELECT * FROM FeatureQAResult
                    """;
            var statement = connection.prepareStatement(sql);
            var result = statement.executeQuery();
            List<FeatureQAResult> platforms = new ArrayList<>();
            while (result.next()){
                var qaResult = new FeatureQAResult(
                        result.getString(1),
                        result.getString(2),
                        result.getString(3),
                        result.getString(4),
                        result.getInt(5),
                        result.getString(6),
                        DateTimeManager.dateTextToUTCTimestamp(result.getString(7)));
                platforms.add(qaResult);
            }
            return platforms;
        }
    }
    public OpResult update(FeatureQAResult updatedQAResult) throws IOException,SQLException {
        var url = DatabaseManager.fetchDatabaseUrl();
        try(Connection connection = DriverManager.getConnection(url)) {
            var checkSql = """
                    SELECT COUNT(*) FROM FeatureQAResult
                    WHERE
                    uuid = ?
                    """;
            var countStatement = connection.prepareStatement(checkSql);
            countStatement.setString(1,updatedQAResult.uuid());
            var countResult = countStatement.executeQuery();
            if (countResult.next()){
                var isNotPresent = countResult.getLong(1) < 1;
                if (isNotPresent)
                    return new OpResult(
                            false,
                            "Failed to update, QA Result with this id does not exist");
            }
            var sql = """
                    UPDATE FeatureQAResult SET
                    qaPromptId = ?,
                    platformId = ?,
                    releaseCandidateId = ?,
                    result = ?,
                    testNotes = ?,
                    createdUTCDate = ?
                    WHERE
                    uuid = ?
                    """;
            var updateStatement = connection.prepareStatement(sql);
            updateStatement.setString(1,updatedQAResult.qaPromptId());
            updateStatement.setString(2,updatedQAResult.platformId());
            updateStatement.setString(3,updatedQAResult.releaseCandidateId());
            updateStatement.setInt(4,updatedQAResult.result());
            updateStatement.setString(5,updatedQAResult.testNotes());
            updateStatement.setString(6,
                    DateTimeManager.timestampToUTCText(updatedQAResult.createdUTCDate()));
            updateStatement.execute();
            updateStatement.setString(7,updatedQAResult.uuid());
            updateStatement.execute();
            return new OpResult(true,"QA Result updated successfully!");
        }
    }
    public OpResult delete(String uuid) throws IOException, SQLException {
        var url = DatabaseManager.fetchDatabaseUrl();
        try(Connection connection = DriverManager.getConnection(url)) {
            var checkSql = """
                    SELECT COUNT(*) FROM FeatureQAResult
                    WHERE
                    uuid = ?
                    """;
            var countStatement = connection.prepareStatement(checkSql);
            countStatement.setString(1,uuid);
            var countResult = countStatement.executeQuery();
            if (countResult.next()){
                var isNotPresent = countResult.getLong(1) < 1;
                if (isNotPresent)
                    return new OpResult(
                            false,
                            "Failed to delete, QA Result with this id does not exist");
            }
            var sql = """
                    DELETE FROM FeatureQAResult
                    WHERE
                    uuid = ?
                    """;
            var deleteStatement = connection.prepareStatement(sql);
            deleteStatement.setString(1,uuid);
            deleteStatement.execute();
            return new OpResult(true,"QA Result is permanently deleted!");
        }
    }
}
