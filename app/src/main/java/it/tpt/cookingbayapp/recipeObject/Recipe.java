package it.tpt.cookingbayapp.recipeObject;

import java.io.Serializable;
import java.util.ArrayList;


public class Recipe implements Serializable{
    private String title;
    private String previewUrl;
    private String profilePicUrl;
    private String time;
    private String type;
    private String authorId;
    private String authorName;
    private ArrayList<Ingredient> ingredients;
    private ArrayList<String> ingNames; //Field Ridondante per cercare le ricette in Ricerca
    private ArrayList<Section> sections;
    private ArrayList<Comment> comments;
    private long date;
    private int likes;
    private int dislikes;

    //Per le classi da utilizzare con FireStore serve sempre un costruttore senza argomenti disponibile
    public Recipe(){ }

    public Recipe(String title, String previewUrl, String profilePicUrl, String time, String type, String authorId, String authorName, ArrayList<Ingredient> ingredients, ArrayList<Section> sections, long date) {
        this.title = title;
        this.previewUrl = previewUrl;
        this.profilePicUrl = profilePicUrl;
        this.time = time;
        this.type = type;
        this.authorId = authorId;
        this.authorName = authorName;
        this.ingredients = ingredients;
        this.sections = sections;
        this.date = date;
        this.likes = 0;
        this.dislikes = 0;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public ArrayList<String> getIngNames() {
        return ingNames;
    }

    public void setIngNames(ArrayList<String> ingNames) {
        this.ingNames = ingNames;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public ArrayList<Section> getSections() {
        return sections;
    }

    public void setSections(ArrayList<Section> sections) {
        this.sections = sections;
    }
}
