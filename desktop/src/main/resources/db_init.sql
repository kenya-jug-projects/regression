-- Table: FeatureQAPrompt
CREATE TABLE IF NOT EXISTS FeatureQAPrompt (
    uuid TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    platformUuid TEXT NOT NULL,
    prompt TEXT NOT NULL,
    testInstructions TEXT NOT NULL,
    testOrder INTEGER,
    createdUTCDate TEXT NOT NULL -- store DATETIME as ISO-8601 string
);
-- Table: FeatureQAResult
CREATE TABLE IF NOT EXISTS FeatureQAResult (
    uuid TEXT PRIMARY KEY,
    qaPromptId TEXT NOT NULL,
    platformId TEXT NOT NULL,
    releaseCandidateId TEXT NOT NULL,
    result INTEGER,
    testNotes TEXT,
    createdUTCDate TEXT -- store DATETIME as ISO-8601 string
);
-- Table: TargetPlatform
CREATE TABLE IF NOT EXISTS TargetPlatform (
    uuid TEXT PRIMARY KEY,
    name TEXT NOT NULL
);
-- Table: ReleaseCandidate
CREATE TABLE IF NOT EXISTS ReleaseCandidate (
    uuid TEXT PRIMARY KEY,
    platformUuid TEXT NOT NULL,
    versionLabel TEXT NOT NULL,
    releaseType TEXT -- Can be beta, production, staging
);