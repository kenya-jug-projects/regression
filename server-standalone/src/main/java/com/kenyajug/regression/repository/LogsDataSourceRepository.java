package com.kenyajug.regression.repository;
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
import com.kenyajug.regression.entities.LogsDataSource;
import com.kenyajug.regression.utils.DateTimeUtils;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
@Repository
public non-sealed class LogsDataSourceRepository implements CrudRepository<LogsDataSource> {
    private final JdbcClient jdbcClient;
    private final TransactionTemplate transactionTemplate;
    public LogsDataSourceRepository(JdbcClient jdbcClient, TransactionTemplate transactionTemplate) {
        this.jdbcClient = jdbcClient;
        this.transactionTemplate = transactionTemplate;
    }
    /**
     * Saves the given entity to the database.
     * If the entity already exists (e.g., same ID), it may update the record depending on implementation.
     *
     * @param entity the entity to save (must not be {@code null})
     */
    @Override
    public void save(LogsDataSource entity) {
        transactionTemplate.executeWithoutResult(status -> {
            var insertSql = """
                        INSERT INTO logs_data_source (
                            uuid,
                            name,
                            source_type,
                            application_id,
                            created_at,
                            log_file_path
                        ) VALUES (
                            :uuid,
                            :name,
                            :source_type,
                            :application_id,
                            :created_at,
                            :log_file_path
                        )
                    """;
            jdbcClient.sql(insertSql)
                    .param("uuid", entity.uuid())
                    .param("name", entity.name())
                    .param("source_type", entity.sourceType())
                    .param("application_id", entity.applicationId())
                    .param("created_at", DateTimeUtils.localDateTimeToUTCTime(entity.createdAt()))
                    .param("log_file_path", entity.logFilePath())
                    .update();

        });
    }
    /**
     * Finds an entity by its unique identifier.
     *
     * @param uuid the unique identifier of the entity
     * @return an {@link Optional} containing the found entity, or empty if not found
     */
    @Override
    public Optional<LogsDataSource> findById(String uuid) {
        var selectSql = """
                SELECT * FROM logs_data_source
                WHERE uuid = :uuid
                ;
                """;
        return jdbcClient.sql(selectSql)
                .param("uuid", uuid)
                .query((resultSet, row) -> new LogsDataSource(
                        resultSet.getString("uuid"),
                        resultSet.getString("name"),
                        resultSet.getString("source_type"),
                        resultSet.getString("application_id"),
                        DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime(resultSet.getString("created_at")),
                        resultSet.getString("log_file_path")
                ))
                .optional();
    }

    /**
     * Retrieves all entities of type {@code T} from the database.
     *
     * @return a list of all entities; never {@code null}, but may be empty
     */
    @Override
    public List<LogsDataSource> findAll() {
        var selectSql = """
                SELECT * FROM logs_data_source
                ;
                """;
        return jdbcClient.sql(selectSql)
                .query((resultSet, row) -> new LogsDataSource(
                        resultSet.getString("uuid"),
                        resultSet.getString("name"),
                        resultSet.getString("source_type"),
                        resultSet.getString("application_id"),
                        DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime(resultSet.getString("created_at")),
                        resultSet.getString("log_file_path")
                ))
                .list();
    }
    /**
     * Deletes the entity with the specified identifier from the database.
     * If no such entity exists, the operation is silently ignored.
     *
     * @param uuid the unique identifier of the entity to delete
     */
    @Override
    public void deleteById(String uuid) {
        var deleteSql = """
                DELETE FROM logs_data_source
                WHERE
                uuid = :uuid
                """;
        jdbcClient.sql(deleteSql)
                .param("uuid", uuid)
                .update();
    }
    /**
     * Deletes all entities of type {@code T} from the database.
     * Use with caution in production environments.
     */
    @Override
    public void deleteAll() {
        var deleteSql = """
                DELETE FROM logs_data_source;
                """;
        jdbcClient.sql(deleteSql)
                .update();
    }
    /**
     * Checks whether an entity with the given unique identifier exists in the data source.
     *
     * @param uuid the unique identifier of the entity to check (must not be {@code null})
     * @return {@code true} if an entity with the specified UUID exists, {@code false} otherwise
     */
    @Override
    public boolean existsById(String uuid) {
        var countSql = """
                SELECT COUNT(*) FROM logs_data_source
                WHERE
                uuid = :uuid
                """;
        var count = jdbcClient.sql(countSql)
                .param("uuid", uuid)
                .query((resultSet, row) -> resultSet.getLong(1))
                .single();
        return count > 0;
    }
    /**
     * Updates an existing entity identified by the given UUID with the provided new data.
     *
     * @param uuid   the unique identifier of the entity to update (must not be {@code null})
     * @param entity the updated entity data to apply (must not be {@code null});
     *               the UUID field inside the entity is typically ignored in favor of the provided {@code uuid}
     * @throws IllegalArgumentException if {@code uuid} or {@code entity} is {@code null}
     * @throws NoSuchElementException   if no entity with the given {@code uuid} exists in the data source
     */
    @Override
    public void updateById(String uuid, LogsDataSource entity) throws NoSuchElementException {
        var updateSql = """
                    UPDATE logs_data_source
                    SET
                        name = :name,
                        source_type = :source_type,
                        application_id = :application_id,
                        created_at = :created_at,
                        log_file_path = :log_file_path
                    WHERE
                        uuid = :uuid
                """;
        jdbcClient.sql(updateSql)
                .param("name", entity.name())
                .param("source_type", entity.sourceType())
                .param("application_id", entity.applicationId())
                .param("created_at", DateTimeUtils.localDateTimeToUTCTime(entity.createdAt()))
                .param("log_file_path", entity.logFilePath())
                .param("uuid", uuid)
                .update();

    }
    public List<LogsDataSource> findByApplicationId(String parentAppId) {
        var selectSql = """
                SELECT * FROM logs_data_source
                WHERE application_id = :application_id
                ;
                """;
        return jdbcClient.sql(selectSql)
                .param("application_id", parentAppId)
                .query((resultSet, row) -> new LogsDataSource(
                        resultSet.getString("uuid"),
                        resultSet.getString("name"),
                        resultSet.getString("source_type"),
                        resultSet.getString("application_id"),
                        DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime(resultSet.getString("created_at")),
                        resultSet.getString("log_file_path")
                ))
                .list();
    }
    /**
     * Retrieves a list of {@link LogsDataSource} entries that match the specified source type.
     * <p>
     * This method queries the underlying data store (e.g., database or in-memory collection) for
     * all log data sources whose type corresponds to the provided source type string.
     * </p>
     *
     * @param sourceType the type of log source to filter by (e.g., "local", "remote", "cloud").
     * @return a list of matching {@link LogsDataSource} objects; an empty list if no matches are found.
     */
    public List<LogsDataSource> findBySourceType(String sourceType) {
        var selectSql = """
                SELECT * FROM logs_data_source
                WHERE source_type = :source_type
                ;
                """;
        return jdbcClient.sql(selectSql)
                .param("source_type", sourceType)
                .query((resultSet, row) -> new LogsDataSource(
                        resultSet.getString("uuid"),
                        resultSet.getString("name"),
                        resultSet.getString("source_type"),
                        resultSet.getString("application_id"),
                        DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime(resultSet.getString("created_at")),
                        resultSet.getString("log_file_path")
                ))
                .list();
    }
}