package it.tpt.cookingbayapp.stepRecycler;

import java.io.Serializable;

public class Step implements Serializable {
    private String stepnumber;
    private String text;

    public Step(){

    }

    public Step(String stepnumber, String text) {
        this.stepnumber = stepnumber;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getStepnumber() {
        return stepnumber;
    }

    public void setStepnumber(String stepnumber) {
        this.stepnumber = stepnumber;
    }
}
