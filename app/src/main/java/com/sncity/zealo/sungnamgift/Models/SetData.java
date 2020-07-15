package com.sncity.zealo.sungnamgift.Models;

import java.io.Serializable;

/**
 * Created by zealo on 2017-09-26.
 */

public class SetData implements Serializable {

    private String setMyStore;
    private String setMyText;
    private String setMyNick;
    private float setMyScore;

    public SetData(String setMyStore, String setMyText, String setMyNick, float setMyScore) {
        this.setMyStore = setMyStore;
        this.setMyText = setMyText;
        this.setMyNick = setMyNick;
        this.setMyScore = setMyScore;
    }

    public String getSetMyStore() {
        return setMyStore;
    }

    public String getSetMyText() {
        return setMyText;
    }

    public String getSetMyNick() {
        return setMyNick;
    }

    public float getSetMyScore() {
        return setMyScore;
    }
}
