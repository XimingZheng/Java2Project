package cs209a.finalproject_demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Question {
    private List<String> tags;
    private Owner owner;
    @JsonProperty("is_answered")
    private Boolean isAnswered;

    @JsonProperty("view_count")
    private Integer viewCount;

    @JsonProperty("answer_count")
    private Integer answerCount;

    private Integer score;

    @JsonProperty("last_activity_date")
    private Long lastActivityDate;

    @JsonProperty("creation_date")
    private Long creationDate;

    @JsonProperty("question_id")
    private Long questionId;

    @JsonProperty("content_license")
    private String contentLicense;

    private String link;
    private String title;
    private String body;

    // Getters and Setters
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public Owner getOwner() { return owner; }
    public void setOwner(Owner owner) { this.owner = owner; }

    public Boolean getIsAnswered() { return isAnswered; }
    public void setIsAnswered(Boolean isAnswered) { this.isAnswered = isAnswered; }

    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }

    public Integer getAnswerCount() { return answerCount; }
    public void setAnswerCount(Integer answerCount) { this.answerCount = answerCount; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public Long getLastActivityDate() { return lastActivityDate; }
    public void setLastActivityDate(Long lastActivityDate) { this.lastActivityDate = lastActivityDate; }

    public Long getCreationDate() { return creationDate; }
    public void setCreationDate(Long creationDate) { this.creationDate = creationDate; }

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public String getContentLicense() { return contentLicense; }
    public void setContentLicense(String contentLicense) { this.contentLicense = contentLicense; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
}
