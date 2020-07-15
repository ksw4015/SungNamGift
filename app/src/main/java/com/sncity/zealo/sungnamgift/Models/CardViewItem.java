package com.sncity.zealo.sungnamgift.Models;

/**
 * Created by zealo on 2017-09-20.
 */

public class CardViewItem {

    private int ImgID;
    private String text;
    private String nick;
    private String date;
    private float star;

    public CardViewItem(int imgID, String text, String nick, String date, float star) {
        ImgID = imgID;
        this.text = text;
        this.nick = nick;
        this.date = date;
        this.star = star;
    }

    public int getImgID() {
        return ImgID;
    }

    public String getText() {
        return text;
    }

    public String getNick() {
        return nick;
    }

    public String getDate() {
        return date;
    }

    public float getStar() {
        return star;
    }
}
