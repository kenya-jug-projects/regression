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
import com.kenyajug.core.OpResult;
import com.kenyajug.platform.TargetPlatform;
import com.kenyajug.platform.TargetPlatformRepository;
import com.kenyajug.release.ReleaseCandidate;
import com.kenyajug.release.ReleaseCandidateRepository;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

public class FeatureQAService {
    private final TargetPlatformRepository platformRepository;
    private final ReleaseCandidateRepository releasesRepository;
    private final FeatureQAPromptRepository promptRepository;
    private final FeatureQAResultRepository resultRepository;
    public FeatureQAService(
            TargetPlatformRepository platformRepository,
            ReleaseCandidateRepository releasesRepository,
            FeatureQAPromptRepository promptRepository,
            FeatureQAResultRepository resultRepository
    ) {
        this.platformRepository = platformRepository;
        this.releasesRepository = releasesRepository;
        this.promptRepository = promptRepository;
        this.resultRepository = resultRepository;
    }
    public List<TargetPlatform> listTargetPlatforms() throws SQLException, IOException {
        return platformRepository.findAll();
    }
    public List<ReleaseCandidate> listReleaseCandidates(String platformUuid) throws SQLException, IOException {
        return releasesRepository.findByPlatformId(platformUuid);
    }
    public List<FeatureQAPrompt> listQAPrompts(String platformId) throws SQLException, IOException {
        return promptRepository.findByPlatformId(platformId);
    }
    public OpResult saveOrUpdateQAResult(FeatureQAResult featureQAResult) throws SQLException, IOException {
        var existResult = resultRepository.exists(featureQAResult.uuid());
        if (!existResult.isSuccess()) return new OpResult(false,"Failed to save feature QA result");
        if (existResult.data() == true){
            return resultRepository.update(featureQAResult);
        } else {
            return resultRepository.save(featureQAResult);
        }
    }
    public int calculateReleaseQAProgress(String releaseId) throws SQLException, IOException {
        var includeCompleted = true;
        var results = resultRepository.listByReleaseCandidate(releaseId,includeCompleted)
                .stream()
                .map(FeatureQAResult::result)
                .toList();
        double completed = results.stream().filter(e -> e == 1).count();
        double total = results.size();
        var pp = (completed / total) * 100;
        return (int) pp;
    }
    public List<FeatureQAResult> listQAResults(String releaseId, boolean includeCompleted) throws SQLException, IOException {
        return resultRepository.listByReleaseCandidate(releaseId,includeCompleted);
    }
}
