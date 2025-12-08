package cs209a.finalproject_demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Comment {
    private Owner owner;
    private Boolean edited;
    private Integer score;

    @JsonProperty("creation_date")
    private Long creationDate;

    @JsonProperty("post_id")
    private Long postId;

    @JsonProperty("comment_id")
    private Long commentId;

    @JsonProperty("content_license")
    private String contentLicense;

    private String body;

    // Getters and Setters
    public Owner getOwner() { return owner; }
    public void setOwner(Owner owner) { this.owner = owner; }
    public Boolean getEdited() { return edited; }
    public void setEdited(Boolean edited) { this.edited = edited; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public Long getCreationDate() { return creationDate; }
    public void setCreationDate(Long creationDate) { this.creationDate = creationDate; }

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }

    public Long getCommentId() { return commentId; }
    public void setCommentId(Long commentId) { this.commentId = commentId; }

    public String getContentLicense() { return contentLicense; }
    public void setContentLicense(String contentLicense) { this.contentLicense = contentLicense; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
}