# API Design Document
###  Regression

## Overview

This document defines the REST API specification for the Regression backend system, including endpoints for log ingestion, retrieval, AI analysis, and metrics.

---

## Authentication

- **Auth Method**: JWT-based Authentication
- **Headers**: `Authorization: Bearer <token>`
- **Roles**:
  - `admin`: Full access
  - `viewer`: Read-only access to logs and analysis
  - `datasource`: Write-only access to submit logs and analysis

---

## 1. Log Ingestion

### `POST /logs`

**Description**: Ingest an application error log into the system.

**Auth Required**: Yes (`admin`, `datasource`)

**Request Body** (JSON):

```json
{
  "timestamp": "2025-05-08T12:00:00Z",
  "severity": "ERROR",
  "applicationId": "app-123",
  "applicationVersion": "v2.3.1",
  "environment": "production",
  "source": "service-A",
  "host": "ip-10-0-1-23.ec2.internal",
  "thread": "http-nio-8080-exec-7",
  "logger": "com.example.user.UserService",
  "message": "NullPointerException in UserService",
  "exception": {
    "type": "java.lang.NullPointerException",
    "stackTrace": [
      "com.example.user.UserService.getUser(UserService.java:45)",
      "com.example.api.UserController.getUser(UserController.java:22)"
    ],
    "cause": "User object was null"
  },
  "metrics": {
    "cpuUsage": 0.75,
    "memoryUsageMB": 1536,
    "diskSpaceRemainingMB": 20480
  },
  "context": {
    "userId": "u789",
    "sessionId": "sess-456",
    "transactionId": "txn-001122",
    "featureFlag": "new-login-flow"
  },
  "tags": ["login", "user-service", "high-priority"],
  "customAttributes": {
    "requestId": "req-998877",
    "deploymentRegion": "us-east-1"
  }
}
````

**Required Fields**:

* `timestamp`
* `severity`
* `applicationId`
* `message`

**Response**:

* `201 Created`
* On error: `400 Bad Request`, `401 Unauthorized`

---

## 2. Log Retrieval

### `GET /logs`

**Description**: Retrieve logs with filtering and pagination support.

**Auth Required**: Yes (`admin`, `viewer`)

**Query Parameters**:

| Param         | Type     | Description                       |
|---------------|----------|-----------------------------------|
| startTime     | ISO Date | Filter logs after this timestamp  |
| endTime       | ISO Date | Filter logs before this timestamp |
| severity      | String   | e.g., `ERROR`, `WARN`, `INFO`     |
| applicationId | String   | Filter by application ID          |
| keyword       | String   | Keyword search in messages        |
| limit         | Int      | Max number of logs to return      |
| offset        | Int      | For pagination                    |

**Example**:

```http
GET /logs?severity=ERROR&startTime=2025-05-01T00:00:00Z&limit=50
```

```bash
curl -X GET "https://api-domain.com/logs?severity=ERROR&startTime=2025-05-01T00:00:00Z&limit=50" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Accept: application/json"

```

**Response** (`200 OK`):

```json
[
  {
    "id": "log-001",
    "timestamp": "2025-05-08T12:00:00Z",
    "severity": "ERROR",
    "applicationId": "app-123",
    "source": "service-A",
    "message": "NullPointerException in UserService",
    "context": { "userId": "u789" }
  }, 
   {
    "id": "log-002",
    "timestamp": "2025-06-11T12:00:00Z",
    "severity": "ERROR",
    "applicationId": "app-123",
    "source": "service-A",
    "message": "SecurityException in UserService",
    "context": { "userId": "u789" }
  }
]
```

---

## 3. AI-Powered Analysis

### `GET /logs/analysis/anomalies/{appId}`

**Description**: Retrieve logs identified as anomalies by the AI engine by app id.

**Auth Required**: Yes (`admin`, `viewer`)

**Query Parameters**:

* Same as `/logs` endpoint (filters)

**Response**:

```json
[
  {
    "id": "log-301",
    "message": "OutOfMemoryError in PaymentService",
    "anomalyScore": 0.95
  }
]
```


### Fetch Regression Logs with AI Analysis

### `GET /logs/analysis/regressions/{appId}`

**Description**: Fetch logs identified as regressions by the AI model. A regression is defined as an error that reappears in a pattern after a fix was applied.

**Auth Required**: Yes (`admin`, `viewer`)
   
**Query Parameters**:

| Param         | Type     | Description                       |
|---------------|----------|-----------------------------------|
| startTime     | ISO Date | Filter logs after this timestamp  |
| endTime       | ISO Date | Filter logs before this timestamp |
| severity      | String   | e.g., `ERROR`, `WARN`, `INFO`     |
| applicationId | String   | Filter by application ID          |
| keyword       | String   | Keyword search in messages        |
| limit         | Int      | Max number of logs to return      |
| offset        | Int      | For pagination                    |


**Example**:

```bash
   GET /logs/analysis/regressions?severity=ERROR&regressionScore=0.85&limit=50
```

```bash
   curl -X GET "https://api-domain.com/logs/analysis/regressions?severity=ERROR&regressionScore=0.85&limit=50" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Accept: application/json"
```

***Response**

```json
{
  "regressions": [
    {
      "id": "log-301",
      "timestamp": "2025-05-08T12:00:00Z",
      "severity": "ERROR",
      "applicationId": "app-123",
      "source": "service-A",
      "message": "OutOfMemoryError in PaymentService",
      "anomalyScore": 0.92,
      "regressionScore": 0.88,
      "relatedLogs": [
        "log-100",
        "log-102",
        "log-104"
      ],
      "rootCauseSuggestions": [
        "Memory leak in PaymentProcessor",
        "Insufficient server resources"
      ],
      "affectedAppVersions": [
        "v2.1.0",
        "v2.2.3",
        "v2.3.1"
      ]
    },
    {
      "id": "log-302",
      "timestamp": "2025-05-08T14:00:00Z",
      "severity": "ERROR",
      "applicationId": "app-456",
      "source": "service-B",
      "message": "NullPointerException in UserService",
      "anomalyScore": 0.90,
      "regressionScore": 0.86,
      "relatedLogs": [
        "log-150",
        "log-155",
        "log-160"
      ],
      "rootCauseSuggestions": [
        "Issue with UserService initialization",
        "Recent deployment bug"
      ],
      "affectedAppVersions": [
        "v1.4.2",
        "v1.5.0"
      ]
    }
  ]
}

```
---

### `GET /logs/analysis/clusters/{appId}`

**Description**: Group logs by similarity to identify patterns.

**Response**:

```json
[
  {
    "clusterId": "c1",
    "representativeMessage": "NullPointerException in UserService",
    "logIds": ["log-101", "log-102", "log-107"]
  },
   {
    "clusterId": "c2",
    "representativeMessage": "SecurityException in UserService",
    "logIds": ["log-333", "log-444", "log-555"]
  },
   {
    "clusterId": "c3",
    "representativeMessage": "StackoverflowException in LogisticsService",
    "logIds": ["log-101", "log-1122", "log-3322"]
  }
]
```

---

## 4. Metrics

### `GET /metrics/{appId}`

**Description**: Expose system-level metrics.

**Auth Required**: Yes (`admin` only)

**Response**:

```json
{
   "logIngestionRate": 200,
   "errorCountBySeverity": {
      "ERROR": 120,
      "WARN": 45,
      "INFO": 300
   },
   "activeApplications": 5,
   "highestIncidentTimeOfDay": "Afternoon",
   "highestIncidentDayOfWeek": "Tuesday",
   "mostRecurringMessages": {
      "ERROR": "NullPointerException in UserService",
      "WARN": "Deprecated API usage in NotificationService",
      "INFO": "User login successful"
   },
   "appVersionWithMostErrors": {
      "applicationId": "app-123",
      "version": "v2.3.1",
      "errorCount": 47
   }
}
```
---

## 5. Security and Error Handling

### Common Response Codes:

* `200 OK`: Successful operation
* `201 Created`: Resource created
* `400 Bad Request`: Validation error
* `401 Unauthorized`: Missing/invalid token
* `403 Forbidden`: Insufficient permissions
* `404 Not Found`: Resource doesn't exist
* `500 Internal Server Error`: Unexpected failure

---

## OpenAPI / Swagger

* API is documented via Swagger (`/swagger-ui.html`).
* OpenAPI 3.0 spec is generated with Springdoc.

---

## Future Enhancements

* Support for bulk log ingestion (`POST /logs/bulk`)
* WebSocket-based live log streaming
* Rate limiting and API usage tracking

---

## Appendix: How AI Log Grouping and Anomaly Detection Works

### Overview
The Regression backend leverages AI/ML techniques to automatically analyze incoming logs for grouping similar errors and detecting anomalies. This enables proactive identification of issues, root cause analysis, and trend detection.

### Log Grouping (Clustering)
- **Technique:** The system uses unsupervised machine learning algorithms (such as k-means, DBSCAN, or embedding-based clustering) to group logs with similar messages, stack traces, or metadata.
- **Features Used:**
  - Log message text (vectorized using NLP techniques)
  - Exception types and stack traces
  - Application and source metadata
- **Purpose:**
  - Identify recurring error patterns
  - Reduce noise by grouping duplicate or similar logs
  - Surface representative errors for faster triage

### Anomaly Detection
- **Technique:** The system applies statistical and machine learning models (e.g., Isolation Forest, autoencoders, or time-series analysis) to detect outliers in log data.
- **Features Used:**
  - Frequency and rate of log events
  - Severity and error type distribution
  - Unusual patterns in log content or metadata
- **Purpose:**
  - Alert on rare or unexpected errors
  - Detect spikes or drops in error rates
  - Highlight logs with high anomaly scores for investigation

### Example Workflow
1. **Ingestion:** Logs are ingested and preprocessed (tokenization, normalization).
2. **Feature Extraction:** Relevant features are extracted from each log entry.
3. **Clustering:** Logs are grouped into clusters based on similarity.
4. **Anomaly Scoring:** Each log is assigned an anomaly score; high scores indicate potential issues.
5. **API Exposure:** Results are available via `/logs/analysis/anomalies` and `/logs/analysis/clusters` endpoints.

### Benefits
- Reduces manual effort in log analysis
- Surfaces actionable insights for developers and operators
- Enables early detection of regressions and critical incidents

---

## Implementation Guidelines for AI Log Grouping and Anomaly Detection

### Overview
This section provides guidelines and references for implementing the AI log grouping and anomaly detection features in the Regression backend.

### Potential APIs, Libraries, and Tools

- **Machine Learning Libraries:**
  - [scikit-learn](https://scikit-learn.org/): For clustering (k-means, DBSCAN) and anomaly detection (Isolation Forest).
  - [TensorFlow](https://www.tensorflow.org/) or [PyTorch](https://pytorch.org/): For deep learning models if needed.
  - [Hugging Face Transformers](https://huggingface.co/transformers/): For NLP tasks and text vectorization.

- **Data Processing:**
  - [Pandas](https://pandas.pydata.org/): For data manipulation and feature extraction.
  - [NumPy](https://numpy.org/): For numerical operations and array handling.

- **Log Parsing and Analysis:**
  - [Logstash](https://www.elastic.co/logstash) or [Fluentd](https://www.fluentd.org/): For log ingestion and preprocessing.
  - [Elasticsearch](https://www.elastic.co/elasticsearch/): For log storage and search capabilities.

- **Visualization (Optional):**
  - [Matplotlib](https://matplotlib.org/) or [Seaborn](https://seaborn.pydata.org/): For creating diagrams and visualizations of log clusters and anomalies.
  - [Plotly](https://plotly.com/): For interactive visualizations.

### Implementation Steps

1. **Log Ingestion:**
   - Use Logstash or Fluentd to collect and preprocess logs.
   - Normalize log formats and extract relevant features.

2. **Feature Extraction:**
   - Use NLP techniques (e.g., TF-IDF, word embeddings) to vectorize log messages.
   - Extract metadata (e.g., timestamp, severity, application ID) for additional features.

3. **Clustering:**
   - Implement clustering algorithms (e.g., k-means, DBSCAN) using scikit-learn.
   - Group similar logs based on message content and metadata.

4. **Anomaly Detection:**
   - Use statistical methods or machine learning models (e.g., Isolation Forest) to detect anomalies.
   - Assign anomaly scores to logs and flag those with high scores.

5. **API Exposure:**
   - Expose results via REST endpoints (e.g., `/logs/analysis/anomalies`, `/logs/analysis/clusters`).
   - Use Spring Boot to integrate with the existing backend.

### Optional Diagrams
Consider creating diagrams to illustrate the workflow, such as:
- A flowchart showing the log ingestion, processing, and analysis pipeline.
- A scatter plot or heatmap visualizing log clusters and anomalies.

---

## Diagram: AI Log Grouping and Anomaly Detection Workflow

Below is a conceptual diagram illustrating the workflow for AI log grouping and anomaly detection in the Regression backend:

```
+------------------+     +------------------+     +------------------+
|                  |     |                  |     |                  |
|  Log Ingestion   | --> | Feature Extraction| --> | Clustering       |
|                  |     |                  |     |                  |
+------------------+     +------------------+     +------------------+
        |                        |                        |
        v                        v                        v
+------------------+     +------------------+     +------------------+
|                  |     |                  |     |                  |
|  Anomaly Detection| --> | API Exposure     | --> | Visualization    |
|                  |     |                  |     |                  |
+------------------+     +------------------+     +------------------+
```

### Diagram Explanation

- **Log Ingestion:** Logs are collected and preprocessed using tools like Logstash or Fluentd.
- **Feature Extraction:** Relevant features are extracted from logs using NLP techniques and metadata.
- **Clustering:** Logs are grouped into clusters based on similarity using algorithms like k-means or DBSCAN.
- **Anomaly Detection:** Statistical or machine learning models detect anomalies and assign scores.
- **API Exposure:** Results are exposed via REST endpoints for further analysis.
- **Visualization:** Optional diagrams (e.g., scatter plots, heatmaps) visualize log clusters and anomalies.

---

## Potential Implementation Guidelines and Available Libraries/APIs

### Overview
This section outlines potential implementation guidelines and available libraries/APIs within the Java ecosystem that can be integrated to implement AI log grouping and anomaly detection in the Regression backend.

### Implementation Guidelines

1. **Log Ingestion and Preprocessing:**
   - Use [Logstash](https://www.elastic.co/logstash) or [Fluentd](https://www.fluentd.org/) for log collection and preprocessing.
   - Normalize log formats and extract relevant features (e.g., timestamp, severity, application ID).

2. **Feature Extraction:**
   - Use NLP techniques (e.g., TF-IDF, word embeddings) to vectorize log messages.
   - Extract metadata (e.g., stack traces, application version) for additional features.

3. **Clustering:**
   - Implement clustering algorithms (e.g., k-means, DBSCAN) using [Weka](https://www.cs.waikato.ac.nz/ml/weka/) or [Apache Commons Math](https://commons.apache.org/proper/commons-math/).
   - Group similar logs based on message content and metadata.

4. **Anomaly Detection:**
   - Use statistical methods or machine learning models (e.g., Isolation Forest) with [Weka](https://www.cs.waikato.ac.nz/ml/weka/) or [Apache Commons Math](https://commons.apache.org/proper/commons-math/).
   - Assign anomaly scores to logs and flag those with high scores.

5. **API Exposure:**
   - Expose results via REST endpoints (e.g., `/logs/analysis/anomalies`, `/logs/analysis/clusters`).
   - Use Spring Boot to integrate with the existing backend.

### Available Libraries and APIs

- **Machine Learning:**
  - [Weka](https://www.cs.waikato.ac.nz/ml/weka/): A collection of machine learning algorithms for data mining tasks.
  - [Apache Commons Math](https://commons.apache.org/proper/commons-math/): Provides algorithms for clustering and statistical analysis.

- **Log Parsing and Analysis:**
  - [Log4j](https://logging.apache.org/log4j/2.x/): For logging and log management.
  - [Elasticsearch](https://www.elastic.co/elasticsearch/): For log storage and search capabilities.

- **Data Processing:**
  - [Apache Spark](https://spark.apache.org/): For large-scale data processing and machine learning.
  - [Apache Flink](https://flink.apache.org/): For real-time data processing and analytics.

- **Visualization (Optional):**
  - [JFreeChart](https://www.jfree.org/jfreechart/): For creating charts and diagrams in Java.
  - [XChart](https://knowm.org/open-source/xchart/): For interactive visualizations.

### Integration Example

```java
// Example code snippet for log clustering using Weka
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;

// Load log data into Weka Instances
Instances data = loadLogData();

// Configure and build the clustering model
SimpleKMeans kmeans = new SimpleKMeans();
kmeans.setNumClusters(3);
kmeans.buildClusterer(data);

// Assign clusters to logs
for (int i = 0; i < data.numInstances(); i++) {
    int cluster = kmeans.clusterInstance(data.instance(i));
    System.out.println("Log " + i + " assigned to cluster " + cluster);
}
```

---
