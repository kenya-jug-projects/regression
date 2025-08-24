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
import com.kenyajug.release.ReleaseCandidate;
import com.kenyajug.release.ReleaseCandidateRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.sql.SQLException;
import static org.assertj.core.api.Assertions.assertThat;
public class ReleaseCandidateRepositoryTest {
    @BeforeEach
    public void setUp() throws SQLException, IOException {
        DatabaseManager.initSchema();
    }
    @Test
    public void shouldSaveReleaseCandidateTest() throws IOException, SQLException {
        var repository = new ReleaseCandidateRepository();
        var platform = new ReleaseCandidate(
                "22fe7bf5-8294-4222-8538-e8a755c19ffd",
                "ea80e600-d902-4e5b-bf40-f51263ad8744",
                "1.1.43453.beta",
                "beta");
        var savedResult = repository.save(platform);
        assertThat(savedResult.isSuccess()).isTrue();
        assertThat(savedResult.message()).isEqualTo("New release candidate saved successfully!");
    }
    @Test
    public void shouldListAllCandidatesTest() throws SQLException, IOException {
        var repository = new ReleaseCandidateRepository();
        var releaseCandidate = new ReleaseCandidate(
                "22fe7bf5-8294-4222-8538-e8a755c19ffd",
                "ea80e600-d902-4e5b-bf40-f51263ad8744",
                "1.1.43453.beta",
                "beta");
        var releaseCandidate1 = new ReleaseCandidate(
                "f960ee1c-c651-4b2a-91ee-53823fe512dd",
                "ea80e600-d902-4e5b-bf40-f51263ad8744",
                "2.1.99982231.beta",
                "beta");
        var savedResult = repository.save(releaseCandidate);
        var savedResult2 = repository.save(releaseCandidate1);
        assertThat(savedResult.isSuccess()).isTrue();
        assertThat(savedResult.message()).isEqualTo("New release candidate saved successfully!");
        assertThat(savedResult2.isSuccess()).isTrue();
        assertThat(savedResult2.message()).isEqualTo("New release candidate saved successfully!");
        var platforms = repository.findAll();
        assertThat(platforms).isNotEmpty();
        assertThat(platforms.size()).isEqualTo(2);
    }
    @Test
    public void shouldUpdateCandidateTest() throws SQLException, IOException {
        var repository = new ReleaseCandidateRepository();
        var releaseCandidate = new ReleaseCandidate(
                "22fe7bf5-8294-4222-8538-e8a755c19ffd",
                "ea80e600-d902-4e5b-bf40-f51263ad8744",
                "1.1.43453.beta",
                "beta");
        var savedResult = repository.save(releaseCandidate);
        assertThat(savedResult.isSuccess()).isTrue();
        var updatedCandidate = new ReleaseCandidate(
                "22fe7bf5-8294-4222-8538-e8a755c19ffd",
                "ea80e600-d902-4e5b-bf40-f51263ad8744",
                "3.4.43453.staging",
                "staging");
        var updateResult = repository.update(updatedCandidate);
        assertThat(updateResult.isSuccess()).isTrue();
        assertThat(updateResult.message()).isEqualTo("Release candidate updated successfully!");
        var updatedCandidates = repository.findAll();
        assertThat(updatedCandidates).isNotEmpty();
        assertThat(updatedCandidates.size()).isEqualTo(1);
        var updatedEntity = updatedCandidates.getFirst();
        assertThat(updatedEntity).isNotNull();
        assertThat(updatedEntity.uuid()).isEqualTo(releaseCandidate.uuid());
        assertThat(updatedEntity.platformUuid()).isEqualTo(updatedCandidate.platformUuid());
        assertThat(updatedEntity.versionLabel()).isEqualTo(updatedCandidate.versionLabel());
        assertThat(updatedEntity.releaseType()).isEqualTo(updatedCandidate.releaseType());
    }
    @Test
    public void shouldNotUpdateCandidate_IfNotExists_Test() throws SQLException, IOException {
        var repository = new ReleaseCandidateRepository();
        var platform = new ReleaseCandidate(
                "22fe7bf5-8294-4222-8538-e8a755c19ffd",
                "ea80e600-d902-4e5b-bf40-f51263ad8744",
                "1.1.43453.beta",
                "beta");
        var staleCandidate = new ReleaseCandidate(
                "06c28a87-c99f-4413-ba98-4d09733f96f1",
                "ea80e600-d902-4e5b-bf40-f51263ad8744",
                "1.1.43453.beta",
                "beta");
        var savedResult = repository.save(platform);
        assertThat(savedResult.isSuccess()).isTrue();
        var updatedCandidate = new ReleaseCandidate(staleCandidate.uuid(),
                "ea80e600-d902-4e5b-bf40-f51263ad8744",
                "1.1.43453.beta",
                "beta");
        var updateResult = repository.update(updatedCandidate);
        assertThat(updateResult.isSuccess()).isFalse();
        assertThat(updateResult.message()).isEqualTo("Failed to update, A release candidate with this id does not exist");
        var updatedCandidates = repository.findAll();
        assertThat(updatedCandidates).isNotEmpty();
        assertThat(updatedCandidates.size()).isEqualTo(1);
        var updatedEntity = updatedCandidates.getFirst();
        assertThat(updatedEntity).isNotNull();
        assertThat(updatedEntity.uuid()).isEqualTo(platform.uuid());
        assertThat(updatedEntity.platformUuid()).isEqualTo(platform.platformUuid());
        assertThat(updatedEntity.versionLabel()).isEqualTo(platform.versionLabel());
        assertThat(updatedEntity.releaseType()).isEqualTo(platform.releaseType());
    }
    @Test
    public void shouldDeleteCandidateTest() throws IOException, SQLException {
        var repository = new ReleaseCandidateRepository();
        var uuid = "e72b0918-1b02-49e3-ba33-b3b03e01f323";
        var platform = new ReleaseCandidate(
                uuid,
                "ea80e600-d902-4e5b-bf40-f51263ad8744",
                "1.1.43453.beta",
                "beta");
        var savedResult = repository.save(platform);
        assertThat(savedResult.isSuccess()).isTrue();
        var deleteResult = repository.delete(uuid);
        assertThat(deleteResult).isNotNull();
        assertThat(deleteResult.isSuccess()).isTrue();
        assertThat(deleteResult.message()).isEqualTo("Release candidate is permanently deleted!");
    }
    @Test
    public void shouldNotDeleteCandidate_IfNotExists_Test() throws IOException, SQLException {
        var repository = new ReleaseCandidateRepository();
        var uuid = "e72b0918-1b02-49e3-ba33-b3b03e01f323";
        var incorrectUuid = "weird-evil-uuid";
        var platform = new ReleaseCandidate(
                uuid,
                "ea80e600-d902-4e5b-bf40-f51263ad8744",
                "1.1.43453.beta",
                "beta");
        var savedResult = repository.save(platform);
        assertThat(savedResult.isSuccess()).isTrue();
        var deleteResult = repository.delete(incorrectUuid);
        assertThat(deleteResult).isNotNull();
        assertThat(deleteResult.isSuccess()).isFalse();
        assertThat(deleteResult.message()).isEqualTo("Failed to delete.A release candidate with this id does not exist");
    }
    @AfterEach
    public void cleanUp() throws SQLException, IOException {
        DatabaseManager.clearDatabase();
    }
}
