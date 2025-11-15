package org.example.model;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FriendshipTest {
    private String testId;
    private Long creatorId;
    private String CreatorName;
    private List<Question> questions;
    private Map<Long, TestResult> results;
    private long createAt;

    public FriendshipTest() {
        this.results = new ConcurrentHashMap<>();
        this.createAt = System.currentTimeMillis();
    }

    public String getTestId() {
        return testId;
    }
    public void setTestId(String testId) {
        this.testId = testId;
    }

    public Long getCreatorId() {
        return creatorId;
    }
    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorName() {
        return CreatorName;
    }
    public void setCreatorName(String CreatorName) {
        this.CreatorName = CreatorName;
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