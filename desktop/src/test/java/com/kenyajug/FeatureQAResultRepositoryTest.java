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
import com.kenyajug.core.DatabaseManager;
import com.kenyajug.feature.FeatureQAResult;
import com.kenyajug.feature.FeatureQAResultRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;
public class FeatureQAResultRepositoryTest {
    @BeforeEach
    public void setUp() throws SQLException, IOException {
        DatabaseManager.initSchema();
    }
    @Test
    public void shouldSavePlatformTest() throws IOException, SQLException {
        var repository = new FeatureQAResultRepository();
        var platform = new FeatureQAResult(
                "22fe7bf5-8294-4222-8538-e8a755c19ffd",
                "6d136998-215f-48f4-ad53-ee926090dc2b",
                "7cbcdb9c-4d88-4a3c-a2ef-097f13d7a166",
                "13b08276-8e47-49e2-881b-029f1e648cb9",
                1,
                "Found some intermittent problems! Working on reproducing!",
                LocalDateTime.now()
        );
        var savedResult = repository.save(platform);
        assertThat(savedResult.isSuccess()).isTrue();
        assertThat(savedResult.message()).isEqualTo("New QA Result saved successfully!");
    }
    @Test
    public void shouldListAllPlatformsTest() throws SQLException, IOException {
        var repository = new FeatureQAResultRepository();
        var platform = new FeatureQAResult(
                "22fe7bf5-8294-4222-8538-e8a755c19ffd",
                "6d136998-215f-48f4-ad53-ee926090dc2b",
                "7cbcdb9c-4d88-4a3c-a2ef-097f13d7a166",
                "13b08276-8e47-49e2-881b-029f1e648cb9",
                1,
                "Found some intermittent problems! Working on reproducing!",
                LocalDateTime.now()
        );
        var platform2 = new FeatureQAResult(
                "702688a4-c3bd-41b8-b531-2e0d99271b25",
                "6d136998-215f-48f4-ad53-ee926090dc2b",
                "7cbcdb9c-4d88-4a3c-a2ef-097f13d7a166",
                "13b08276-8e47-49e2-881b-029f1e648cb9",
                1,
                "Found some intermittent problems! Working on reproducing!",
                LocalDateTime.now()
        );
        var savedResult = repository.save(platform);
        var savedResult2 = repository.save(platform2);
        assertThat(savedResult.isSuccess()).isTrue();
        assertThat(savedResult.message()).isEqualTo("New QA Result saved successfully!");
        assertThat(savedResult2.isSuccess()).isTrue();
        assertThat(savedResult2.message()).isEqualTo("New QA Result saved successfully!");
        var platforms = repository.findAll();
        assertThat(platforms).isNotEmpty();
        assertThat(platforms.size()).isEqualTo(2);
    }
    @Test
    public void shouldUpdatePlatformTest() throws SQLException, IOException {
        var repository = new FeatureQAResultRepository();
        var qAResult = new FeatureQAResult(
                "702688a4-c3bd-41b8-b531-2e0d99271b25",
                "6d136998-215f-48f4-ad53-ee926090dc2b",
                "7cbcdb9c-4d88-4a3c-a2ef-097f13d7a166",
                "13b08276-8e47-49e2-881b-029f1e648cb9",
                1,
                "Found some intermittent problems! Working on reproducing!",
                LocalDateTime.now()
        );
        var savedResult = repository.save(qAResult);
        assertThat(savedResult.isSuccess()).isTrue();
        var updatedPlatform = new FeatureQAResult(
                qAResult.uuid(),
                qAResult.qaPromptId(),
                qAResult.platformId(),
                qAResult.releaseCandidateId(),
                qAResult.result(),
                "I have found the intermittent bug, it actual fixed in v2.1",
                qAResult.createdUTCDate()
        );
        var updateResult = repository.update(updatedPlatform);
        assertThat(updateResult.isSuccess()).isTrue();
        assertThat(updateResult.message()).isEqualTo("QA Result updated successfully!");
        var updatedPlatforms = repository.findAll();
        assertThat(updatedPlatforms).isNotEmpty();
        assertThat(updatedPlatforms.size()).isEqualTo(1);
        var updatedEntity = updatedPlatforms.getFirst();
        assertThat(updatedEntity).isNotNull();
        assertThat(updatedEntity.uuid()).isEqualTo(qAResult.uuid());
        assertThat(updatedEntity.qaPromptId()).isEqualTo(updatedPlatform.qaPromptId());
        assertThat(updatedEntity.platformId()).isEqualTo(updatedPlatform.platformId());
        assertThat(updatedEntity.releaseCandidateId()).isEqualTo(updatedPlatform.releaseCandidateId());
        assertThat(updatedEntity.result()).isEqualTo(updatedPlatform.result());
        assertThat(updatedEntity.testNotes()).isEqualTo(updatedPlatform.testNotes());
        assertThat(updatedEntity.createdUTCDate().isEqual(updatedPlatform.createdUTCDate()));
    }
    @Test
    public void shouldNotUpdatePlatform_IfNotExists_Test() throws SQLException, IOException {
        var repository = new FeatureQAResultRepository();
        var qAResult = new FeatureQAResult(
                "702688a4-c3bd-41b8-b531-2e0d99271b25",
                "6d136998-215f-48f4-ad53-ee926090dc2b",
                "7cbcdb9c-4d88-4a3c-a2ef-097f13d7a166",
                "13b08276-8e47-49e2-881b-029f1e648cb9",
                1,
                "Found some intermittent problems! Working on reproducing!",
                LocalDateTime.now()
        );
        var stalePlatform = new FeatureQAResult(
                "984b82f4-54d4-487f-9c5a-81ffb8c53692",
                "6d136998-215f-48f4-ad53-ee926090dc2b",
                "7cbcdb9c-4d88-4a3c-a2ef-097f13d7a166",
                "13b08276-8e47-49e2-881b-029f1e648cb9",
                1,
                "Found some intermittent problems! Working on reproducing!",
                LocalDateTime.now()
        );
        var savedResult = repository.save(qAResult);
        assertThat(savedResult.isSuccess()).isTrue();
        var updatedPlatform = new FeatureQAResult(
                stalePlatform.uuid(),
                "6d136998-215f-48f4-ad53-ee926090dc2b",
                "7cbcdb9c-4d88-4a3c-a2ef-097f13d7a166",
                "13b08276-8e47-49e2-881b-029f1e648cb9",
                1,
                "Found some intermittent problems! Working on reproducing!",
                LocalDateTime.now()
        );
        var updateResult = repository.update(updatedPlatform);
        assertThat(updateResult.isSuccess()).isFalse();
        assertThat(updateResult.message()).isEqualTo("Failed to update, QA Result with this id does not exist");
        var updatedPlatforms = repository.findAll();
        assertThat(updatedPlatforms).isNotEmpty();
        assertThat(updatedPlatforms.size()).isEqualTo(1);
        var updatedEntity = updatedPlatforms.getFirst();
        assertThat(updatedEntity).isNotNull();
        assertThat(updatedEntity.uuid()).isEqualTo(qAResult.uuid());
        assertThat(updatedEntity.qaPromptId()).isEqualTo(qAResult.qaPromptId());
        assertThat(updatedEntity.platformId()).isEqualTo(qAResult.platformId());
        assertThat(updatedEntity.releaseCandidateId()).isEqualTo(qAResult.releaseCandidateId());
        assertThat(updatedEntity.result()).isEqualTo(qAResult.result());
        assertThat(updatedEntity.testNotes()).isEqualTo(qAResult.testNotes());
        assertThat(updatedEntity.createdUTCDate().isEqual(qAResult.createdUTCDate()));
    }
    @Test
    public void shouldDeletePlatformTest() throws IOException, SQLException {
        var repository = new FeatureQAResultRepository();
        var uuid = "e72b0918-1b02-49e3-ba33-b3b03e01f323";
        var qAResult = new FeatureQAResult(
                uuid,
                "6d136998-215f-48f4-ad53-ee926090dc2b",
                "7cbcdb9c-4d88-4a3c-a2ef-097f13d7a166",
                "13b08276-8e47-49e2-881b-029f1e648cb9",
                1,
                "Found some intermittent problems! Working on reproducing!",
                LocalDateTime.now()
        );
        var savedResult = repository.save(qAResult);
        assertThat(savedResult.isSuccess()).isTrue();
        var deleteResult = repository.delete(uuid);
        assertThat(deleteResult).isNotNull();
        assertThat(deleteResult.isSuccess()).isTrue();
        assertThat(deleteResult.message()).isEqualTo("QA Result is permanently deleted!");
    }
    @Test
    public void shouldNotDeletePlatform_IfNotExists_Test() throws IOException, SQLException {
        var repository = new FeatureQAResultRepository();
        var uuid = "e72b0918-1b02-49e3-ba33-b3b03e01f323";
        var incorrectUuid = "weird-evil-uuid";
        var qAResult = new FeatureQAResult(
                uuid,
                "6d136998-215f-48f4-ad53-ee926090dc2b",
                "7cbcdb9c-4d88-4a3c-a2ef-097f13d7a166",
                "13b08276-8e47-49e2-881b-029f1e648cb9",
                1,
                "Found some intermittent problems! Working on reproducing!",
                LocalDateTime.now()
        );
        var savedResult = repository.save(qAResult);
        assertThat(savedResult.isSuccess()).isTrue();
        var deleteResult = repository.delete(incorrectUuid);
        assertThat(deleteResult).isNotNull();
        assertThat(deleteResult.isSuccess()).isFalse();
        assertThat(deleteResult.message()).isEqualTo("Failed to delete, QA Result with this id does not exist");
    }
    @Test
    public void shouldCheckExistsTest() throws SQLException, IOException {
        var repository = new FeatureQAResultRepository();
        var resultId = "76066f68-9906-4475-b922-9409a0fb7aff";
        var qAResult = new FeatureQAResult(
                resultId,
                "6d136998-215f-48f4-ad53-ee926090dc2b",
                "7cbcdb9c-4d88-4a3c-a2ef-097f13d7a166",
                "13b08276-8e47-49e2-881b-029f1e648cb9",
                1,
                "Found some intermittent problems! Working on reproducing!",
                LocalDateTime.now()
        );
        var savedResult = repository.save(qAResult);
        assertThat(savedResult.isSuccess()).isTrue();
        var existsResult = repository.exists(resultId);
        assertThat(existsResult).isNotNull();
        assertThat(existsResult.isSuccess()).isTrue();
        assertThat(existsResult.message()).isEqualTo("Feature QA Result with this id already exist");
        assertThat(existsResult.data()).isTrue();
    }
    @Test
    public void shouldCheckExists_Case1_DoesNotExist_Test() throws SQLException, IOException {
        var repository = new FeatureQAResultRepository();
        var resultId = "76066f68-9906-4475-b922-9409a0fb7aff";
        var qAResult = new FeatureQAResult(
                resultId,
                "6d136998-215f-48f4-ad53-ee926090dc2b",
                "7cbcdb9c-4d88-4a3c-a2ef-097f13d7a166",
                "13b08276-8e47-49e2-881b-029f1e648cb9",
                1,
                "Found some intermittent problems! Working on reproducing!",
                LocalDateTime.now()
        );
        var savedResult = repository.save(qAResult);
        assertThat(savedResult.isSuccess()).isTrue();
        var anotherId = "6ca9b718-566b-40f3-96a4-bf57d024bed9";
        var existsResult = repository.exists(anotherId);
        assertThat(existsResult).isNotNull();
        assertThat(existsResult.isSuccess()).isTrue();
        assertThat(existsResult.data()).isFalse();
        assertThat(existsResult.message()).isEqualTo("Feature QA Result with this id does not exist");
    }
    @Test
    public void shouldListByReleaseCandidatesTest() throws SQLException, IOException {
        var repository = new FeatureQAResultRepository();
        var releaseId = "6e1ddf7e-4450-439c-b41f-ea3b0823b9df";
        var qAResult = new FeatureQAResult(
                "ac42b9cd-ad93-426f-b2c7-4036bdede12a",
                "6d136998-215f-48f4-ad53-ee926090dc2b",
                "7cbcdb9c-4d88-4a3c-a2ef-097f13d7a166",
                releaseId,
                1,
                "Found some intermittent problems! Working on reproducing!",
                LocalDateTime.now()
        );
        var savedResult = repository.save(qAResult);
        assertThat(savedResult.isSuccess()).isTrue();
        var includeCompleted = true;
        var results = repository.listByReleaseCandidate(releaseId,includeCompleted);
        assertThat(results).isNotEmpty();
        assertThat(results.getFirst()).isNotNull();
        assertThat(results.getFirst().qaPromptId()).isEqualTo(qAResult.qaPromptId());
        assertThat(results.getFirst().platformId()).isEqualTo(qAResult.platformId());
        assertThat(results.getFirst().releaseCandidateId()).isEqualTo(qAResult.releaseCandidateId());
        assertThat(results.getFirst().result()).isEqualTo(qAResult.result());
        assertThat(results.getFirst().testNotes()).isEqualTo(qAResult.testNotes());
    }
    @Test
    public void shouldListByReleaseCandidates_Case1_Test() throws SQLException, IOException {
        var repository = new FeatureQAResultRepository();
        var releaseId = "6e1ddf7e-4450-439c-b41f-ea3b0823b9df";
        var qAResult = new FeatureQAResult(
                "ac42b9cd-ad93-426f-b2c7-4036bdede12a",
                "6d136998-215f-48f4-ad53-ee926090dc2b",
                "7cbcdb9c-4d88-4a3c-a2ef-097f13d7a166",
                releaseId,
                1,
                "Found some intermittent problems! Working on reproducing!",
                LocalDateTime.now()
        );
        var qAResultPending = new FeatureQAResult(
                "39cd134e-9b7c-48fc-bd7c-3bcccd5f2ad8",
                "6d136998-215f-48f4-ad53-ee926090dc2b",
                "7cbcdb9c-4d88-4a3c-a2ef-097f13d7a166",
                releaseId,
                0,
                "Working on reproducing!",
                LocalDateTime.now()
        );
        var savedResult = repository.save(qAResult);
        assertThat(savedResult.isSuccess()).isTrue();
        var savedResult2 = repository.save(qAResultPending);
        assertThat(savedResult2.isSuccess()).isTrue();
        var includeCompleted = false;
        var results = repository.listByReleaseCandidate(releaseId,includeCompleted);
        assertThat(results).isNotEmpty();
        assertThat(results.getFirst()).isNotNull();
        assertThat(results.getFirst().qaPromptId()).isEqualTo(qAResultPending.qaPromptId());
        assertThat(results.getFirst().platformId()).isEqualTo(qAResultPending.platformId());
        assertThat(results.getFirst().releaseCandidateId()).isEqualTo(qAResultPending.releaseCandidateId());
        assertThat(results.getFirst().result()).isEqualTo(qAResultPending.result());
        assertThat(results.getFirst().testNotes()).isEqualTo(qAResultPending.testNotes());
    }
    @Test
    public void shouldListByReleaseCandidates_Case2_Test() throws SQLException, IOException {
        var repository = new FeatureQAResultRepository();
        var releaseId = "6e1ddf7e-4450-439c-b41f-ea3b0823b9df";
        var qAResultCompleted = new FeatureQAResult(
                "ac42b9cd-ad93-426f-b2c7-4036bdede12a",
                "6d136998-215f-48f4-ad53-ee926090dc2b",
                "7cbcdb9c-4d88-4a3c-a2ef-097f13d7a166",
                releaseId,
                1,
                "Found some intermittent problems! Working on reproducing!",
                LocalDateTime.now()
        );
        var qAResultPending = new FeatureQAResult(
                "39cd134e-9b7c-48fc-bd7c-3bcccd5f2ad8",
                "6d136998-215f-48f4-ad53-ee926090dc2b",
                "7cbcdb9c-4d88-4a3c-a2ef-097f13d7a166",
                releaseId,
                0,
                "Working on reproducing!",
                LocalDateTime.now()
        );
        var savedResult = repository.save(qAResultCompleted);
        assertThat(savedResult.isSuccess()).isTrue();
        var savedResult2 = repository.save(qAResultPending);
        assertThat(savedResult2.isSuccess()).isTrue();
        var includeCompleted = true;
        var results = repository.listByReleaseCandidate(releaseId,includeCompleted);
        assertThat(results).isNotEmpty();
        assertThat(results.getFirst()).isNotNull();
        assertThat(results.getFirst().qaPromptId()).isEqualTo(qAResultCompleted.qaPromptId());
        assertThat(results.getFirst().platformId()).isEqualTo(qAResultCompleted.platformId());
        assertThat(results.getFirst().releaseCandidateId()).isEqualTo(qAResultCompleted.releaseCandidateId());
        assertThat(results.getFirst().result()).isEqualTo(qAResultCompleted.result());
        assertThat(results.getFirst().testNotes()).isEqualTo(qAResultCompleted.testNotes());
    }
    @AfterEach
    public void cleanUp() throws SQLException, IOException {
        DatabaseManager.clearDatabase();
    }
}
