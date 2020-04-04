package it.tpt.cookingbayapp.stepRecycler;

import android.net.Uri;

import java.io.Serializable;

public class Step implements Serializable {
    private String stepnumber;
    private String text;
    private String url;
    private Uri stepUri;

    public Step(){

    }

    public Step(String stepnumber, String text, Uri stepUri) {
        this.stepnumber = stepnumber;
        this.text = text;
        this.stepUri = stepUri;
        this.url = "";
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

    public String getStepnumber() {
        return stepnumber;
    }

    public void setStepnumber(String stepnumber) {
        this.stepnumber = stepnumber;
    }
}
