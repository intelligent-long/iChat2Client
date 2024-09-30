package com.longx.intelligent.android.ichat2.data;

import java.util.Date;
import java.util.Objects;

/**
 * Created by LONG on 2024/9/23 at 6:41 AM.
 */
public class BroadcastComment {
    private String commentId;
    private String broadcastId;
    private String fromId;
    private String text;
    private String toCommentId;
    private Date commentTime;

    private BroadcastComment toComment;

    private String avatarHash;
    private String fromName;
    private String broadcastText;
    private Date broadcastTime;
    private String coverMediaId;
    private Boolean broadcastDeleted;
    private Boolean isNew;

    public BroadcastComment() {
    }

    public BroadcastComment(String commentId, String broadcastId, String fromId, String text, String toCommentId, Date commentTime, BroadcastComment toComment, String avatarHash, String fromName, String broadcastText, Date broadcastTime, String coverMediaId, Boolean broadcastDeleted, Boolean isNew) {
        this.commentId = commentId;
        this.broadcastId = broadcastId;
        this.fromId = fromId;
        this.text = text;
        this.toCommentId = toCommentId;
        this.commentTime = commentTime;
        this.toComment = toComment;
        this.avatarHash = avatarHash;
        this.fromName = fromName;
        this.broadcastText = broadcastText;
        this.broadcastTime = broadcastTime;
        this.coverMediaId = coverMediaId;
        this.broadcastDeleted = broadcastDeleted;
        this.isNew = isNew;
    }

    public String getCommentId() {
        return commentId;
    }

    public String getBroadcastId() {
        return broadcastId;
    }

    public String getFromId() {
        return fromId;
    }

    public String getText() {
        return text;
    }

    public String getToCommentId() {
        return toCommentId;
    }

    public Date getCommentTime() {
        return commentTime;
    }

    public BroadcastComment getToComment() {
        return toComment;
    }

    public String getAvatarHash() {
        return avatarHash;
    }

    public String getFromName() {
        return fromName;
    }

    public String getBroadcastText() {
        return broadcastText;
    }

    public Date getBroadcastTime() {
        return broadcastTime;
    }

    public String getCoverMediaId() {
        return coverMediaId;
    }

    public Boolean getBroadcastDeleted() {
        return broadcastDeleted;
    }

    public Boolean isNew() {
        return isNew;
    }

    public void setNew(Boolean isNew) {
        this.isNew = isNew;
    }

    public void setBroadcastText(String broadcastText) {
        this.broadcastText = broadcastText;
    }

    public void setCoverMediaId(String coverMediaId) {
        this.coverMediaId = coverMediaId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BroadcastComment that = (BroadcastComment) o;
        return Objects.equals(commentId, that.commentId) && Objects.equals(broadcastId, that.broadcastId) && Objects.equals(fromId, that.fromId) && Objects.equals(text, that.text) && Objects.equals(toCommentId, that.toCommentId) && Objects.equals(commentTime, that.commentTime) && Objects.equals(toComment, that.toComment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commentId, broadcastId, fromId, text, toCommentId, commentTime, toComment);
    }
}