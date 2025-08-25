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
import com.kenyajug.feature.FeatureQAPrompt;
import com.kenyajug.feature.FeatureQAPromptRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;
public class FeatureQAPromptRepositoryTest {
    @BeforeEach
    public void setUp() throws SQLException, IOException {
        DatabaseManager.initSchema();
    }
    @Test
    public void shouldSavePromptTest() throws IOException, SQLException {
        var repository = new FeatureQAPromptRepository();
        var platform = new FeatureQAPrompt(
                "22fe7bf5-8294-4222-8538-e8a755c19ffd",
                "Test Onboarding",
                "92e2d642-84f7-461c-9de2-6a58dd637fde",
                "Did the app crash during onboarding?",
                """
                        1. Tap on app from home screen
                        2. Wait for onboarding to launch
                        3. Tap on next
                        4. Wait for dashboard to load
                        """,
                2,
                LocalDateTime.now()
        );
        var savedResult = repository.save(platform);
        assertThat(savedResult.isSuccess()).isTrue();
        assertThat(savedResult.message()).isEqualTo("New prompt saved successfully!");
    }
    @Test
    public void shouldListAllPromptsTest() throws SQLException, IOException {
        var repository = new FeatureQAPromptRepository();
        var prompt = new FeatureQAPrompt(
                "22fe7bf5-8294-4222-8538-e8a755c19ffd",
                "Test Onboarding",
                "92e2d642-84f7-461c-9de2-6a58dd637fde",
                "Did the app crash during onboarding?",
                """
                        1. Tap on app from home screen
                        2. Wait for onboarding to launch
                        3. Tap on next
                        4. Wait for dashboard to load
                        """,
                2,
                LocalDateTime.now()
        );
        var prompt2 = new FeatureQAPrompt(
                "1d44f331-a0bf-4357-bc11-94340f5a4914",
                "Test Onboarding Initial Setup",
                "92e2d642-84f7-461c-9de2-6a58dd637fde",
                "Did the app crash during initial setup?",
                """
                        1. Tap on app from home screen
                        2. Wait for onboarding to launch
                        3. Tap on next
                        4. Wait for dashboard to load
                        5. Tap on 'initial setup' button
                        6. Tap on 'set defaults' button
                        """,
                3,
                LocalDateTime.now()
        );
        var savedResult = repository.save(prompt);
        var savedResult2 = repository.save(prompt2);
        assertThat(savedResult.isSuccess()).isTrue();
        assertThat(savedResult.message()).isEqualTo("New prompt saved successfully!");
        assertThat(savedResult2.isSuccess()).isTrue();
        assertThat(savedResult2.message()).isEqualTo("New prompt saved successfully!");
        var prompts = repository.findAll();
        assertThat(prompts).isNotEmpty();
        assertThat(prompts.size()).isEqualTo(2);
    }
    @Test
    public void shouldUpdatePromptTest() throws SQLException, IOException {
        var repository = new FeatureQAPromptRepository();
        var prompt = new FeatureQAPrompt(
                "22fe7bf5-8294-4222-8538-e8a755c19ffd",
                "Test Onboarding",
                "92e2d642-84f7-461c-9de2-6a58dd637fde",
                "Did the app crash during onboarding?",
                """
                        1. Tap on app from home screen
                        2. Wait for onboarding to launch
                        3. Tap on next
                        4. Wait for dashboard to load
                        """,
                2,
                LocalDateTime.now()
        );
        var savedResult = repository.save(prompt);
        assertThat(savedResult.isSuccess()).isTrue();
        var updatedPrompt = new FeatureQAPrompt(
                prompt.uuid(),
                "Test Desktop Onboarding",
                "92e2d642-84f7-461c-9de2-6a58dd637fde",
                "Did the app crash during onboarding?",
                """
                        1. Tap on app from home screen
                        2. Wait for onboarding to launch
                        3. Tap on next
                        4. Wait for dashboard to load
                        """,
                2,
                LocalDateTime.now()
        );
        var updateResult = repository.update(updatedPrompt);
        assertThat(updateResult.isSuccess()).isTrue();
        assertThat(updateResult.message()).isEqualTo("Prompt updated successfully!");
        var updatedPrompts = repository.findAll();
        assertThat(updatedPrompts).isNotEmpty();
        assertThat(updatedPrompts.size()).isEqualTo(1);
        var updatedEntity = updatedPrompts.getFirst();
        assertThat(updatedEntity).isNotNull();
        assertThat(updatedEntity.uuid()).isEqualTo(prompt.uuid());
        assertThat(updatedEntity.name()).isEqualTo(updatedPrompt.name());
    }
    @Test
    public void shouldNotUpdatePrompt_IfNotExists_Test() throws SQLException, IOException {
        var repository = new FeatureQAPromptRepository();
        var prompt = new FeatureQAPrompt(
                "22fe7bf5-8294-4222-8538-e8a755c19ffd",
                "Test Onboarding",
                "92e2d642-84f7-461c-9de2-6a58dd637fde",
                "Did the app crash during onboarding?",
                """
                        1. Tap on app from home screen
                        2. Wait for onboarding to launch
                        3. Tap on next
                        4. Wait for dashboard to load
                        """,
                2,
                LocalDateTime.now()
        );
        var stalePrompt = new FeatureQAPrompt(
                "10421432-9d44-4b42-9716-c8dee600e4fb",
                "Test Onboarding",
                "92e2d642-84f7-461c-9de2-6a58dd637fde",
                "Did the app crash during onboarding?",
                """
                        1. Tap on app from home screen
                        2. Wait for onboarding to launch
                        3. Tap on next
                        4. Wait for dashboard to load
                        """,
                2,
                LocalDateTime.now()
        );
        var savedResult = repository.save(prompt);
        assertThat(savedResult.isSuccess()).isTrue();
        var updatedPrompt = new FeatureQAPrompt(
                stalePrompt.uuid(),
                "Test Onboarding",
                "92e2d642-84f7-461c-9de2-6a58dd637fde",
                "Did the app crash during onboarding?",
                """
                        1. Tap on app from home screen
                        2. Wait for onboarding to launch
                        3. Tap on next
                        4. Wait for dashboard to load
                        """,
                2,
                LocalDateTime.now()
        );
        var updateResult = repository.update(updatedPrompt);
        assertThat(updateResult.isSuccess()).isFalse();
        assertThat(updateResult.message()).isEqualTo("Failed to update, Prompt with this id does not exist");
        var updatedPrompts = repository.findAll();
        assertThat(updatedPrompts).isNotEmpty();
        assertThat(updatedPrompts.size()).isEqualTo(1);
        var updatedEntity = updatedPrompts.getFirst();
        assertThat(updatedEntity).isNotNull();
        assertThat(updatedEntity.uuid()).isEqualTo(prompt.uuid());
        assertThat(updatedEntity.name()).isEqualTo(prompt.name());
    }
    @Test
    public void shouldDeletePromptTest() throws IOException, SQLException {
        var repository = new FeatureQAPromptRepository();
        var uuid = "e72b0918-1b02-49e3-ba33-b3b03e01f323";
        var prompt = new FeatureQAPrompt(
                uuid,
                "Test Onboarding",
                "92e2d642-84f7-461c-9de2-6a58dd637fde",
                "Did the app crash during onboarding?",
                """
                        1. Tap on app from home screen
                        2. Wait for onboarding to launch
                        3. Tap on next
                        4. Wait for dashboard to load
                        """,
                2,
                LocalDateTime.now()
        );
        var savedResult = repository.save(prompt);
        assertThat(savedResult.isSuccess()).isTrue();
        var deleteResult = repository.delete(uuid);
        assertThat(deleteResult).isNotNull();
        assertThat(deleteResult.isSuccess()).isTrue();
        assertThat(deleteResult.message()).isEqualTo("Prompt is permanently deleted!");
    }
    @Test
    public void shouldNotDeletePrompt_IfNotExists_Test() throws IOException, SQLException {
        var repository = new FeatureQAPromptRepository();
        var uuid = "e72b0918-1b02-49e3-ba33-b3b03e01f323";
        var incorrectUuid = "weird-evil-uuid";
        var prompt = new FeatureQAPrompt(
                uuid,
                "Test Onboarding",
                "92e2d642-84f7-461c-9de2-6a58dd637fde",
                "Did the app crash during onboarding?",
                """
                        1. Tap on app from home screen
                        2. Wait for onboarding to launch
                        3. Tap on next
                        4. Wait for dashboard to load
                        """,
                2,
                LocalDateTime.now()
        );
        var savedResult = repository.save(prompt);
        assertThat(savedResult.isSuccess()).isTrue();
        var deleteResult = repository.delete(incorrectUuid);
        assertThat(deleteResult).isNotNull();
        assertThat(deleteResult.isSuccess()).isFalse();
        assertThat(deleteResult.message()).isEqualTo("Failed to delete, Prompt with this id does not exist");
    }
    @AfterEach
    public void cleanUp() throws SQLException, IOException {
        DatabaseManager.clearDatabase();
    }
}
