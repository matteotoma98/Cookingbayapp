package it.tpt.cookingbayapp.recipeObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//Per le classi da utilizzare con FireStore serve sempre un costruttore senza argomenti disponibile
public class Recipe implements Serializable{
    private String title;
    private String previewUrl;
    private String profilePicUrl;
    private String time;
    private String author;
    private ArrayList<Ingredient> ingredients;
    private ArrayList<Section> sections;

    public Recipe(){ }

    public Recipe(String title, String previewUrl, String profilePicUrl, String time, String author, ArrayList<Ingredient> ingredients, ArrayList<Section> sections) {
        this.title = title;
        this.previewUrl = previewUrl;
        this.profilePicUrl = profilePicUrl;
        this.time = time;
        this.author = author;
        this.ingredients = ingredients;
        this.sections = sections;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public ArrayList<Section> getSections() {
        return sections;
    }

    public void setSections(ArrayList<Section> sections) {
        this.sections = sections;
    }
}
