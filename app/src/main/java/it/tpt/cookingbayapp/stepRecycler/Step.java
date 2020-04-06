package it.tpt.cookingbayapp.stepRecycler;

import android.net.Uri;

import java.io.Serializable;

public class Step implements Serializable {

    private String text;
    private String url;
    private int hours;
    private int minutes;
    private Uri stepUri;
    private boolean hasPicture;

    public Step(){

    }

    public Step(String text, Uri stepUri) {
        this.text = text;
        this.stepUri = stepUri;
        this.url = "";
        this.hasPicture = false;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public boolean getHasPicture() {
        return hasPicture;
    }

    public void setHasPicture(boolean hasPicture) {
        this.hasPicture = hasPicture;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Uri getStepUri() {
        return stepUri;
    }

    public void setStepUri(Uri stepUri) {
        this.stepUri = stepUri;
    }

}
