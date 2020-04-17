package it.tpt.cookingbayapp.recipeObject;

import android.net.Uri;

import java.io.Serializable;

/*Classe utilizzata per i commenti della ricetta
  Verrà utilizzata in una lista nella classe Recipe
 */
public class Comment implements Serializable {
    private String userId;
    private String userName;
    private String content;
    private Uri url;

    public Comment() {

    }

    public Comment(String userId, String userName, String content, Uri url) {
        this.userId = userId;
        this.userName = userName;
        this.content = content;
        this.url = url;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Uri getUrl() {
        return url;
    }

    public void setUrl(Uri url) {
        this.url = url;
    }
}
