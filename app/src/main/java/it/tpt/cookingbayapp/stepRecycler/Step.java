package it.tpt.cookingbayapp.stepRecycler;

import java.io.Serializable;

public class Step implements Serializable {
    private String stepnumber;

    public Step(){

    }
    public Step(String stepnumber) {
        this.stepnumber = stepnumber;
    }
    public String getStepnumber() {
        return stepnumber;
    }

    public void setStepnumber(String stepnumber) {
        this.stepnumber = stepnumber;
    }
}
