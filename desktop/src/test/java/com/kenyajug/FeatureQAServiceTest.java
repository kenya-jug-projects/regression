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
import com.kenyajug.core.OpDataResult;
import com.kenyajug.core.OpResult;
import com.kenyajug.feature.*;
import com.kenyajug.platform.TargetPlatform;
import com.kenyajug.platform.TargetPlatformRepository;
import com.kenyajug.release.ReleaseCandidate;
import com.kenyajug.release.ReleaseCandidateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
public class FeatureQAServiceTest {
    @InjectMocks
    private FeatureQAService service;
    @Mock
    private TargetPlatformRepository platformRepository;
    @Mock
    private ReleaseCandidateRepository releasesRepository;
    @Mock
    private FeatureQAPromptRepository promptRepository;
    @Mock
    private FeatureQAResultRepository resultRepository;
    @Test
    public void shouldListTargetPlatformsTest() throws SQLException, IOException {
        var expectedList = List.of(
                new TargetPlatform(
                        "22fe7bf5-8294-4222-8538-e8a755c19ffd",
                        "Linux Distro"
                ),
                new TargetPlatform(
                        "9f004927-ce46-4f66-ae6d-82602f80d161",
                        "Windows Distro"
                )
        );
        when(platformRepository.findAll()).thenReturn(expectedList);
        var platforms = service.listTargetPlatforms();
        assertThat(platforms).isNotEmpty();
        assertThat(platforms.size()).isEqualTo(2);
    }
    @Test
    public void shouldListReleaseCandidatesTest() throws SQLException, IOException {
        var platformId = "06d191bb-90e3-4dcd-a729-4ac7f8dd1074";
        var release = new ReleaseCandidate(
                "e72b0918-1b02-49e3-ba33-b3b03e01f323",
                platformId,
                "1.1.43453.beta",
                "beta");
        when(releasesRepository.findByPlatformId(platformId))
                .thenReturn(Collections.singletonList(release));
        var releases = service.listReleaseCandidates(platformId);
        assertThat(releases).isNotEmpty();
        assertThat(releases.size()).isEqualTo(1);
        assertThat(releases.getFirst()).isNotNull();
        assertThat(releases.getFirst().uuid()).isEqualTo(release.uuid());
        assertThat(releases.getFirst().platformUuid()).isEqualTo(platformId);
        assertThat(releases.getFirst().versionLabel()).isEqualTo(release.versionLabel());
        assertThat(releases.getFirst().releaseType()).isEqualTo(release.releaseType());
    }
    @Test
    public void shouldListQAPromptsTest() throws SQLException, IOException {
        var platformId = "06d191bb-90e3-4dcd-a729-4ac7f8dd1074";
        var qaPrompt = new FeatureQAPrompt(
                "1d44f331-a0bf-4357-bc11-94340f5a4914",
                "Test Onboarding Initial Setup",
                platformId,
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
        when(promptRepository.findByPlatformId(platformId))
                .thenReturn(Collections.singletonList(qaPrompt));
        var prompts = service.listQAPrompts(platformId);
        assertThat(prompts).isNotEmpty();
        assertThat(prompts.size()).isEqualTo(1);
        assertThat(prompts.getFirst()).isNotNull();
        assertThat(prompts.getFirst().name()).isEqualTo(qaPrompt.name());
        assertThat(prompts.getFirst().platformUuid()).isEqualTo(qaPrompt.platformUuid());
        assertThat(prompts.getFirst().prompt()).isEqualTo(qaPrompt.prompt());
        assertThat(prompts.getFirst().testInstructions()).isEqualTo(qaPrompt.testInstructions());
        assertThat(prompts.getFirst().testOrder()).isEqualTo(qaPrompt.testOrder());
    }
    @Test
    public void shouldSaveOrUpdateQAResultTest() throws SQLException, IOException {
        var result = new FeatureQAResult(
                "22fe7bf5-8294-4222-8538-e8a755c19ffd",
                "6d136998-215f-48f4-ad53-ee926090dc2b",
                "7cbcdb9c-4d88-4a3c-a2ef-097f13d7a166",
                "13b08276-8e47-49e2-881b-029f1e648cb9",
                1,
                "Found some intermittent problems! Working on reproducing!",
                LocalDateTime.now()
        );
        when(resultRepository.exists(result.uuid()))
                .thenReturn(new OpDataResult<>(true, "QA Result does not yet exists", false));
        when(resultRepository.save(result))
                .thenReturn(new OpResult(true,"New QA Result saved successfully!"));
        var opResult = service.saveOrUpdateQAResult(result);
        assertThat(opResult).isNotNull();
        assertThat(opResult.isSuccess()).isTrue();
        assertThat(opResult.message()).isEqualTo("New QA Result saved successfully!");
    }
    @Test
    public void shouldSaveOrUpdateQAResult_Case1_ResultsExist_Test() throws SQLException, IOException {
        var result = new FeatureQAResult(
                "22fe7bf5-8294-4222-8538-e8a755c19ffd",
                "6d136998-215f-48f4-ad53-ee926090dc2b",
                "7cbcdb9c-4d88-4a3c-a2ef-097f13d7a166",
                "13b08276-8e47-49e2-881b-029f1e648cb9",
                1,
                "Found some intermittent problems! Working on reproducing!",
                LocalDateTime.now()
        );
        when(resultRepository.exists(result.uuid()))
                .thenReturn(new OpDataResult<>(true,
                        "Feature QA Result with this id already exist",
                        true));
        when(resultRepository.update(result))
                .thenReturn(new OpResult(true,"QA Result updated successfully!"));
        var opResult = service.saveOrUpdateQAResult(result);
        assertThat(opResult).isNotNull();
        assertThat(opResult.isSuccess()).isTrue();
        assertThat(opResult.message()).isEqualTo("QA Result updated successfully!");
    }
    @Test
    public void shouldListQAResultsTest() throws SQLException, IOException {
        var releaseId = "eb5284e0-59f8-45e7-a29a-414f63ddffed";
        var platformId = "7cbcdb9c-4d88-4a3c-a2ef-097f13d7a166";
        var includeCompleted = true;
        var timestamp = LocalDateTime.now();
        List<FeatureQAResult> results = List.of(
                new FeatureQAResult(
                        "22fe7bf5-8294-4222-8538-e8a755c19ffd",
                        "6d136998-215f-48f4-ad53-ee926090dc2b",
                        platformId,
                        releaseId,
                        1,
                        "Found some intermittent problems! Working on reproducing!",
                        timestamp),
                new FeatureQAResult(
                        "98fd36d1-fd5f-4393-8234-64193b802bce",
                        "6d136998-215f-48f4-ad53-ee926090dc2b",
                        platformId,
                        releaseId,
                        0,
                        "Found some intermittent problems! Working on reproducing!",
                        LocalDateTime.now())
        );
        when(resultRepository.listByReleaseCandidate(releaseId,includeCompleted))
                .thenReturn(results);
        var actualResults = service.listQAResults(releaseId,includeCompleted);
        assertThat(actualResults).isNotEmpty();
        assertThat(actualResults.size()).isEqualTo(2);
        assertThat(actualResults.getFirst()).isNotNull();
        assertThat(actualResults.getFirst().qaPromptId()).isEqualTo("6d136998-215f-48f4-ad53-ee926090dc2b");
        assertThat(actualResults.getFirst().platformId()).isEqualTo(platformId);
        assertThat(actualResults.getFirst().releaseCandidateId()).isEqualTo(releaseId);
        assertThat(actualResults.getFirst().result()).isEqualTo(1);
        assertThat(actualResults.getFirst().testNotes()).isEqualTo("Found some intermittent problems! Working on reproducing!");
        assertThat(actualResults.getFirst().createdUTCDate().isEqual(timestamp)).isTrue();
    }
    @Test
    public void shouldListQAResults_Case1_Test() throws SQLException, IOException {
        var releaseId = "eb5284e0-59f8-45e7-a29a-414f63ddffed";
        var platformId = "7cbcdb9c-4d88-4a3c-a2ef-097f13d7a166";
        var includeCompleted = false;
        var timestamp = LocalDateTime.now();
        List<FeatureQAResult> results = List.of(
                new FeatureQAResult(
                        "98fd36d1-fd5f-4393-8234-64193b802bce",
                        "6d136998-215f-48f4-ad53-ee926090dc2b",
                        platformId,
                        releaseId,
                        0,
                        "Found some intermittent problems!",
                        timestamp)
        );
        when(resultRepository.listByReleaseCandidate(releaseId,includeCompleted))
                .thenReturn(results);
        var actualResults = service.listQAResults(releaseId,includeCompleted);
        assertThat(actualResults).isNotEmpty();
        assertThat(actualResults).isNotEmpty();
        assertThat(actualResults.size()).isEqualTo(1);
        assertThat(actualResults.getFirst()).isNotNull();
        assertThat(actualResults.getFirst().qaPromptId()).isEqualTo("6d136998-215f-48f4-ad53-ee926090dc2b");
        assertThat(actualResults.getFirst().platformId()).isEqualTo(platformId);
        assertThat(actualResults.getFirst().releaseCandidateId()).isEqualTo(releaseId);
        assertThat(actualResults.getFirst().result()).isEqualTo(0);
        assertThat(actualResults.getFirst().testNotes()).isEqualTo("Found some intermittent problems!");
        assertThat(actualResults.getFirst().createdUTCDate().isEqual(timestamp)).isTrue();
    }
    @Test
    public void shouldCalculateReleaseQAProgressTest() throws SQLException, IOException {
        var releaseId = "eb5284e0-59f8-45e7-a29a-414f63ddffed";
        List<FeatureQAResult> results = List.of(
                new FeatureQAResult(
                        "uuid-1",
                        "qaPrompt-1",
                        "platform-1",
                        releaseId,
                        1,
                        "All tests passed",
                        LocalDateTime.now()
                ),
                new FeatureQAResult(
                        "uuid-2",
                        "qaPrompt-2",
                        "platform-2",
                        releaseId,
                        0,
                        "Login test failed",
                        LocalDateTime.now().minusHours(2)
                ),
                new FeatureQAResult(
                        "uuid-3",
                        "qaPrompt-3",
                        "platform-3",
                        releaseId,
                        1,
                        "Minor warnings, but acceptable",
                        LocalDateTime.now().minusDays(1)
                )
        );
        var includeCompleted = true;
        when(resultRepository.listByReleaseCandidate(releaseId,includeCompleted))
                .thenReturn(results);
        var expectedCompletedPercentage = 66;
        var actualCompletedPercentage = service.calculateReleaseQAProgress(releaseId);
        assertThat(expectedCompletedPercentage).isEqualTo(actualCompletedPercentage);
    }
}
