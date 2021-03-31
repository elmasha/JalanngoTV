package el.com.jalangotv.models;

import java.util.Date;

public class Comments {
    private String userImage,userName,comment;
    private long likeCount;
    private Date timestamp;

    public Comments() {
    //empty constructor
    }

    public Comments(String userImage, String userName,
                    String comment, long likeCount, Date timestamp) {
        this.userImage = userImage;
        this.userName = userName;
        this.comment = comment;
        this.likeCount = likeCount;
        this.timestamp = timestamp;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
