package com.sncity.zealo.sungnamgift.Models;

/**
 * Created by USER on 2017-08-29.
 */
public class MyReviewData {
    private String reviewStore;
    private String reviewDate;
    private String reviewText;
    private String reviewScore;
    private String nickName;

    public MyReviewData(String reviewStore, String reviewDate, String reviewText, String reviewScore, String nickName) {
        this.reviewStore = reviewStore;
        this.reviewDate = reviewDate;
        this.reviewText = reviewText;
        this.reviewScore = reviewScore;
        this.nickName = nickName;
    }

    public String getReviewStore() {
        return reviewStore;
    }

    public void setReviewStore(String reviewStore) {
        this.reviewStore = reviewStore;
    }

    public String getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(String reviewDate) {
        this.reviewDate = reviewDate;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public String getReviewScore() {
        return reviewScore;
    }

    public void setReviewScore(String reviewScore) {
        this.reviewScore = reviewScore;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
