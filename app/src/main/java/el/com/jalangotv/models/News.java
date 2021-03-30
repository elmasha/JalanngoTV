package el.com.jalangotv.models;

import com.google.firebase.firestore.FieldValue;

import java.util.Date;

public class News {
private String  Headline,Story,category,search,News_image,Doc_ID;
private long likesCount,shareCount,commentCount,viewsCount;
private Date timestamp;

    public News() {
    }

    public News(String headline, String story, String category, String search,
                String news_image, String doc_ID, long likesCount, long shareCount, long commentCount, long viewsCount, Date timestamp) {
        Headline = headline;
        Story = story;
        this.category = category;
        this.search = search;
        News_image = news_image;
        Doc_ID = doc_ID;
        this.likesCount = likesCount;
        this.shareCount = shareCount;
        this.commentCount = commentCount;
        this.viewsCount = viewsCount;
        this.timestamp = timestamp;
    }

    public String getHeadline() {
        return Headline;
    }

    public void setHeadline(String headline) {
        Headline = headline;
    }

    public String getStory() {
        return Story;
    }

    public void setStory(String story) {
        Story = story;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getNews_image() {
        return News_image;
    }

    public void setNews_image(String news_image) {
        News_image = news_image;
    }

    public String getDoc_ID() {
        return Doc_ID;
    }

    public void setDoc_ID(String doc_ID) {
        Doc_ID = doc_ID;
    }

    public long getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(long likesCount) {
        this.likesCount = likesCount;
    }

    public long getShareCount() {
        return shareCount;
    }

    public void setShareCount(long shareCount) {
        this.shareCount = shareCount;
    }

    public long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(long commentCount) {
        this.commentCount = commentCount;
    }

    public long getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(long viewsCount) {
        this.viewsCount = viewsCount;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
