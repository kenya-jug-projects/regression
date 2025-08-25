package com.kenyajug.release;
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
import com.kenyajug.core.OpResult;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class ReleaseCandidateRepository {
    public OpResult save(ReleaseCandidate releaseCandidate) throws IOException, SQLException {
        var url = DatabaseManager.fetchDatabaseUrl();
        try(Connection connection = DriverManager.getConnection(url)) {
            var sql = """
                    INSERT INTO ReleaseCandidate VALUES(?,?,?,?)
                    """;
            var statement = connection.prepareStatement(sql);
            statement.setString(1,releaseCandidate.uuid());
            statement.setString(2,releaseCandidate.platformUuid());
            statement.setString(3,releaseCandidate.versionLabel());
            statement.setString(4,releaseCandidate.releaseType());
            statement.execute();
            return new OpResult(true,"New release candidate saved successfully!");
        }
    }
    public List<ReleaseCandidate> findAll() throws IOException,SQLException{
        var url = DatabaseManager.fetchDatabaseUrl();
        try(Connection connection = DriverManager.getConnection(url)){
            var sql = """
                    SELECT * FROM ReleaseCandidate
                    """;
            var statement = connection.prepareStatement(sql);
            var result = statement.executeQuery();
            List<ReleaseCandidate> platforms = new ArrayList<>();
            while (result.next()){
                var releaseCandidate = new ReleaseCandidate(
                        result.getString(1),
                        result.getString(2),
                        result.getString(3),
                        result.getString(4));
                platforms.add(releaseCandidate);
            }
            return platforms;
        }
    }
    public OpResult update(ReleaseCandidate updatedPlatform) throws IOException,SQLException {
        var url = DatabaseManager.fetchDatabaseUrl();
        try(Connection connection = DriverManager.getConnection(url)) {
            var checkSql = """
                    SELECT COUNT(*) FROM ReleaseCandidate WHERE uuid = ?
                    """;
            var countStatement = connection.prepareStatement(checkSql);
            countStatement.setString(1,updatedPlatform.uuid());
            var countResult = countStatement.executeQuery();
            if (countResult.next()){
                var isNotPresent = countResult.getLong(1) < 1;
                if (isNotPresent)
                    return new OpResult(
                            false,
                            "Failed to update, A release candidate with this id does not exist");
            }
            var sql = """
                    UPDATE ReleaseCandidate SET
                    platformUuid = ?,
                    versionLabel = ?,
                    releaseType = ?
                    WHERE uuid = ?
                    """;
            var updateStatement = connection.prepareStatement(sql);
            updateStatement.setString(1,updatedPlatform.platformUuid());
            updateStatement.setString(2,updatedPlatform.versionLabel());
            updateStatement.setString(3,updatedPlatform.releaseType());
            updateStatement.setString(4,updatedPlatform.uuid());
            updateStatement.execute();
            return new OpResult(true,"Release candidate updated successfully!");
        }
    }
    public OpResult delete(String uuid) throws IOException, SQLException {
        var url = DatabaseManager.fetchDatabaseUrl();
        try(Connection connection = DriverManager.getConnection(url)) {
            var checkSql = """
                    SELECT COUNT(*) FROM ReleaseCandidate WHERE uuid = ?
                    """;
            var countStatement = connection.prepareStatement(checkSql);
            countStatement.setString(1,uuid);
            var countResult = countStatement.executeQuery();
            if (countResult.next()){
                var isNotPresent = countResult.getLong(1) < 1;
                if (isNotPresent)
                    return new OpResult(
                            false,
                            "Failed to delete.A release candidate with this id does not exist");
            }
            var sql = """
                    DELETE FROM ReleaseCandidate WHERE uuid = ?
                    """;
            var deleteStatement = connection.prepareStatement(sql);
            deleteStatement.setString(1,uuid);
            deleteStatement.execute();
            return new OpResult(true,"Release candidate is permanently deleted!");
        }
    }
    public List<ReleaseCandidate> findByPlatformId(String platformUuid) throws IOException, SQLException {
        var url = DatabaseManager.fetchDatabaseUrl();
        try(Connection connection = DriverManager.getConnection(url)){
            var byPlatformIdSql = """
                    SELECT * FROM ReleaseCandidate
                    WHERE
                    platformUuid = ?
                    """;
            var statement = connection.prepareStatement(byPlatformIdSql);
            statement.setString(1,platformUuid);
            var result = statement.executeQuery();
            List<ReleaseCandidate> releases = new ArrayList<>();
            while (result.next()){
                var release = new ReleaseCandidate(
                        result.getString(1),
                        result.getString(2),
                        result.getString(3),
                        result.getString(4)
                );
                releases.add(release);
            }
            return releases;
        }
    }
}
