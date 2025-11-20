package org.example.model;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FriendshipTest {
    private final String testId;
    private final Long creatorId;
    private String creatorName;
    private List<Question> questions;
    private Map<Long, TestResult> results;
    private long createAt;

    public FriendshipTest(String testId, Long creatorId, String creatorName) {
        this.testId = testId;
        this.creatorId = creatorId;
        this.creatorName = creatorName;
        this.results = new ConcurrentHashMap<>();
        this.createAt = System.currentTimeMillis();
    }

    public String getTestId() {
        return testId;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }
    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public List<Question> getQuestions(){
        return questions;
    }
    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public Map<Long, TestResult> getResults() {
        return results;
    }
    public void setResults(Map<Long, TestResult> results) {
        this.results = results;
    }

    public long getCreateAt() {
        return createAt;
    }
    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }
}