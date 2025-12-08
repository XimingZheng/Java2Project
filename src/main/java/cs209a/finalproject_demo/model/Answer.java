package cs209a.finalproject_demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Answer {
    private Owner owner;
    @JsonProperty("is_accepted")
    private Boolean isAccepted;

    private Integer score;

    @JsonProperty("last_activity_date")
    private Long lastActivityDate;

    @JsonProperty("creation_date")
    private Long creationDate;

    @JsonProperty("answer_id")
    private Long answerId;

    @JsonProperty("question_id")
    private Long questionId;

    @JsonProperty("content_license")
    private String contentLicense;

    private String body;

    // Getters and Setters
    public Owner getOwner() { return owner; }
    public void setOwner(Owner owner) { this.owner = owner; }

    public Boolean getIsAccepted() { return isAccepted; }
    public void setIsAccepted(Boolean isAccepted) { this.isAccepted = isAccepted; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public Long getLastActivityDate() { return lastActivityDate; }
    public void setLastActivityDate(Long lastActivityDate) { this.lastActivityDate = lastActivityDate; }

    public Long getCreationDate() { return creationDate; }
    public void setCreationDate(Long creationDate) { this.creationDate = creationDate; }

    public Long getAnswerId() { return answerId; }
    public void setAnswerId(Long answerId) { this.answerId = answerId; }

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public String getContentLicense() { return contentLicense; }
    public void setContentLicense(String contentLicense) { this.contentLicense = contentLicense; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
}
