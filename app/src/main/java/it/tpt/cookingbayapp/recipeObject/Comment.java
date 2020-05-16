package it.tpt.cookingbayapp.recipeObject;

import java.io.Serializable;

/*Classe utilizzata per i commenti della ricetta
  Verrà utilizzata in una lista nella classe Recipe
 */
public class Comment implements Serializable {

    private String userId; //Id dell'utente
    private String content; //Testo del commento

    public Comment() {

    }

    public Comment(String userId, String content) {
        this.userId = userId;
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
