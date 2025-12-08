package cs209a.finalproject_demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Owner {
    @JsonProperty("account_id")
    private Long accountId;

    private Integer reputation;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("user_type")
    private String userType;

    @JsonProperty("profile_image")
    private String profileImage;

    @JsonProperty("display_name")
    private String displayName;

    private String link;

    // Getters and Setters
    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }

    public Integer getReputation() { return reputation; }
    public void setReputation(Integer reputation) { this.reputation = reputation; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
}