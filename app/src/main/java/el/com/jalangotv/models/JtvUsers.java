package el.com.jalangotv.models;

import com.google.firebase.firestore.FieldValue;

import java.util.Date;

public class JtvUsers {
    private String UserName,Email,ProfileImage;
    private Date timestamp;

    public JtvUsers() {
        //empty constructor
    }

    public JtvUsers(String userName, String email, String profileImage, Date timestamp) {
        UserName = userName;
        Email = email;
        ProfileImage = profileImage;
        this.timestamp = timestamp;
    }


    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getProfileImage() {
        return ProfileImage;
    }

    public void setProfileImage(String profileImage) {
        ProfileImage = profileImage;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
