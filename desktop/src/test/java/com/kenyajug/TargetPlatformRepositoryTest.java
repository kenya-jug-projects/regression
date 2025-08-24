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
import com.kenyajug.platform.TargetPlatform;
import com.kenyajug.platform.TargetPlatformRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.sql.SQLException;
import static org.assertj.core.api.Assertions.assertThat;
public class TargetPlatformRepositoryTest {
    @BeforeEach
    public void setUp() throws SQLException, IOException {
        DatabaseManager.initSchema();
    }
    @Test
    public void shouldSavePlatformTest() throws IOException, SQLException {
        var repository = new TargetPlatformRepository();
        var platform = new TargetPlatform(
                "22fe7bf5-8294-4222-8538-e8a755c19ffd",
                "Linux Distro"
        );
        var savedResult = repository.save(platform);
        assertThat(savedResult.isSuccess()).isTrue();
        assertThat(savedResult.message()).isEqualTo("New platform saved successfully!");
    }
    @Test
    public void shouldListAllPlatformsTest() throws SQLException, IOException {
        var repository = new TargetPlatformRepository();
        var platform = new TargetPlatform(
                "22fe7bf5-8294-4222-8538-e8a755c19ffd",
                "Linux Distro"
        );
        var platform2 = new TargetPlatform(
                "0df41d7a-0ca7-41a4-9294-04232f77fd07",
                "Windows Distro"
        );
        var savedResult = repository.save(platform);
        var savedResult2 = repository.save(platform2);
        assertThat(savedResult.isSuccess()).isTrue();
        assertThat(savedResult.message()).isEqualTo("New platform saved successfully!");
        assertThat(savedResult2.isSuccess()).isTrue();
        assertThat(savedResult2.message()).isEqualTo("New platform saved successfully!");
        var platforms = repository.findAll();
        assertThat(platforms).isNotEmpty();
        assertThat(platforms.size()).isEqualTo(2);
    }
    @Test
    public void shouldUpdatePlatformTest() throws SQLException, IOException {
        var repository = new TargetPlatformRepository();
        var platform = new TargetPlatform(
                "22fe7bf5-8294-4222-8538-e8a755c19ffd",
                "Linux Distro"
        );
        var savedResult = repository.save(platform);
        assertThat(savedResult.isSuccess()).isTrue();
        var updatedPlatform = new TargetPlatform(platform.uuid(),"Linux Distro_Ubuntu");
        var updateResult = repository.update(updatedPlatform);
        assertThat(updateResult.isSuccess()).isTrue();
        assertThat(updateResult.message()).isEqualTo("Platform updated successfully!");
        var updatedPlatforms = repository.findAll();
        assertThat(updatedPlatforms).isNotEmpty();
        assertThat(updatedPlatforms.size()).isEqualTo(1);
        var updatedEntity = updatedPlatforms.getFirst();
        assertThat(updatedEntity).isNotNull();
        assertThat(updatedEntity.uuid()).isEqualTo(platform.uuid());
        assertThat(updatedEntity.name()).isEqualTo(updatedPlatform.name());
    }
    @Test
    public void shouldNotUpdatePlatform_IfNotExists_Test() throws SQLException, IOException {
        var repository = new TargetPlatformRepository();
        var platform = new TargetPlatform(
                "22fe7bf5-8294-4222-8538-e8a755c19ffd",
                "Linux Distro"
        );
        var stalePlatform = new TargetPlatform(
                "3394266b-4a0f-4250-a4c1-743585fd1ef8",
                "Linux Distro"
        );
        var savedResult = repository.save(platform);
        assertThat(savedResult.isSuccess()).isTrue();
        var updatedPlatform = new TargetPlatform(stalePlatform.uuid(),"Linux Distro_Ubuntu");
        var updateResult = repository.update(updatedPlatform);
        assertThat(updateResult.isSuccess()).isFalse();
        assertThat(updateResult.message()).isEqualTo("Failed to update, Platform with this id does not exist");
        var updatedPlatforms = repository.findAll();
        assertThat(updatedPlatforms).isNotEmpty();
        assertThat(updatedPlatforms.size()).isEqualTo(1);
        var updatedEntity = updatedPlatforms.getFirst();
        assertThat(updatedEntity).isNotNull();
        assertThat(updatedEntity.uuid()).isEqualTo(platform.uuid());
        assertThat(updatedEntity.name()).isEqualTo(platform.name());
    }
    @Test
    public void shouldDeletePlatformTest() throws IOException, SQLException {
        var repository = new TargetPlatformRepository();
        var uuid = "e72b0918-1b02-49e3-ba33-b3b03e01f323";
        var platform = new TargetPlatform(
                uuid,
                "Linux Distro"
        );
        var savedResult = repository.save(platform);
        assertThat(savedResult.isSuccess()).isTrue();
        var deleteResult = repository.delete(uuid);
        assertThat(deleteResult).isNotNull();
        assertThat(deleteResult.isSuccess()).isTrue();
        assertThat(deleteResult.message()).isEqualTo("Target Platform is permanently deleted!");
    }
    @Test
    public void shouldNotDeletePlatform_IfNotExists_Test() throws IOException, SQLException {
        var repository = new TargetPlatformRepository();
        var uuid = "e72b0918-1b02-49e3-ba33-b3b03e01f323";
        var incorrectUuid = "weird-evil-uuid";
        var platform = new TargetPlatform(
                uuid,
                "Linux Distro"
        );
        var savedResult = repository.save(platform);
        assertThat(savedResult.isSuccess()).isTrue();
        var deleteResult = repository.delete(incorrectUuid);
        assertThat(deleteResult).isNotNull();
        assertThat(deleteResult.isSuccess()).isFalse();
        assertThat(deleteResult.message()).isEqualTo("Failed to delete, Platform with this id does not exist");
    }
    @AfterEach
    public void cleanUp() throws SQLException, IOException {
        DatabaseManager.clearDatabase();
    }
}
