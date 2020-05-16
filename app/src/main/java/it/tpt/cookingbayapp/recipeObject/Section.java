package it.tpt.cookingbayapp.recipeObject;

import java.io.Serializable;

/*Classe utilizzata per gli step della ricetta
  Verrà utilizzata in una lista nella classe Recipe
 */
public class Section implements Serializable {
    private String text;
    private String imageUrl;
    private int timer;

    public Section() {
    }

    public Section(String text, String imageUrl, int timer) {
        this.text = text;
        this.imageUrl = imageUrl;
        this.timer = timer;
    }

    public String getText() {
        return text;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getTimer() {
        return timer;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }
}
