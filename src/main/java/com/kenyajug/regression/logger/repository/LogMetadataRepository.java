package com.kenyajug.regression.logger.repository;
import com.kenyajug.regression.common.CrudRepository;
import com.kenyajug.regression.logger.model.LogMetadata;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
@Repository
public class LogMetadataRepository implements CrudRepository<LogMetadata>{
    private final JdbcClient jdbcClient;
    private final TransactionTemplate transactionTemplate;
    public LogMetadataRepository(JdbcClient jdbcClient, TransactionTemplate transactionTemplate) {
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
    public void save(LogMetadata entity) {
        transactionTemplate.executeWithoutResult(status -> {
            var insertSql = """
                 INSERT INTO logs_metadata (
                     uuid,
                     log_uuid,
                     metadata_type,
                     metadata_value
                 ) VALUES (
                     :uuid,
                     :log_uuid,
                     :metadata_type,
                     :metadata_value
                 );
                 
                 """;
            jdbcClient
                    .sql(insertSql)
                    .param("uuid", entity.uuid())
                    .param("log_uuid", entity.logId())
                    .param("metadata_type", entity.metadataType())
                    .param("metadata_value", entity.metadataValue())
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
    public Optional<LogMetadata> findById(String uuid) {
        var selectSql = """
                SELECT * FROM logs_metadata
                WHERE uuid = :uuid
                ;
                """;
        return jdbcClient.sql(selectSql)
                .param("uuid",uuid)
                .query((resultSet, row) -> new LogMetadata(
                        resultSet.getString("uuid"),
                        resultSet.getString("log_uuid"),
                        resultSet.getString("metadata_type"),
                        resultSet.getString("metadata_value")
                ))
                .optional();
    }
    /**
     * Retrieves all entities of type {@code T} from the database.
     *
     * @return a list of all entities; never {@code null}, but may be empty
     */
    @Override
    public List<LogMetadata> findAll() {
        var selectSql = """
                SELECT * FROM logs_metadata
                ;
                """;
        return jdbcClient.sql(selectSql)
                .query((resultSet, row) -> new LogMetadata(
                        resultSet.getString("uuid"),
                        resultSet.getString("log_uuid"),
                        resultSet.getString("metadata_type"),
                        resultSet.getString("metadata_value")
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
                DELETE FROM logs_metadata
                WHERE
                uuid = :uuid
                """;
        jdbcClient.sql(deleteSql)
                .param("uuid",uuid)
                .update();
    }
    /**
     * Deletes all entities of type {@code T} from the database.
     * Use with caution in production environments.
     */
    @Override
    public void deleteAll() {
        var deleteSql = """
                DELETE FROM logs_metadata;
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
                SELECT COUNT(*) FROM logs_metadata
                WHERE
                uuid = :uuid
                """;
        var count = jdbcClient.sql(countSql)
                .param("uuid",uuid)
                .query((resultSet,row) -> resultSet.getLong(1))
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
    public void updateById(String uuid, LogMetadata entity) throws NoSuchElementException {
        var updateSql = """
                UPDATE logs_metadata
                SET log_uuid = :log_uuid,
                    metadata_type = :metadata_type,
                    metadata_value = :metadata_value
                WHERE uuid = :uuid;
                ;
                """;
        jdbcClient.sql(updateSql)
                .param("log_uuid", entity.logId())
                .param("metadata_type", entity.metadataType())
                .param("metadata_value", entity.metadataValue())
                .param("uuid",uuid)
                .update();
    }
    public List<LogMetadata> findByRootLogId(String parentLogId) {
        var selectSql = """
                SELECT * FROM logs_metadata
                WHERE log_uuid = :log_uuid
                ;
                """;
        return jdbcClient.sql(selectSql)
                .param("log_uuid",parentLogId)
                .query((resultSet, row) -> new LogMetadata(
                        resultSet.getString("uuid"),
                        resultSet.getString("log_uuid"),
                        resultSet.getString("metadata_type"),
                        resultSet.getString("metadata_value")
                ))
                .list();
    }
}
