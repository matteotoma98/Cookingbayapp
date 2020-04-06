package it.tpt.cookingbayapp.stepRecycler;

import android.net.Uri;

import java.io.Serializable;

public class Step implements Serializable {

    private String text;
    private String url;
    private String hours;
    private String minutes;
    private Uri stepUri;
    private boolean hasPicture;

    public Step(){

    }

    public Step(String text, Uri stepUri) {
        this.text = text;
        this.stepUri = stepUri;
        this.url = "";
        this.hours = "";
        this.minutes = "";
        this.hasPicture = false;
    }

    public Step(String text, String url, String hours, String minutes) {
        this.text = text;
        this.url = url;
        this.hours = hours;
        this.stepUri = Uri.parse("");
        this.minutes = minutes;
        this.hasPicture = true;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getMinutes() {
        return minutes;
    }

    public void setMinutes(String minutes) {
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
