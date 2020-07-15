package com.sncity.zealo.sungnamgift.Models;

/**
 * Created by zealo on 2017-09-22.
 */

public class ReviewData {

    private String nickName;
    private String reviewText;
    private String reviewDate;
    private String reviewScore;

    public ReviewData(String nickName, String reviewText, String reviewDate, String reviewScore) {
        this.nickName = nickName;
        this.reviewText = reviewText;
        this.reviewDate = reviewDate;
        this.reviewScore = reviewScore;
    }

    public String getNickName() {
        return nickName;
    }

    public String getReviewText() {
        return reviewText;
    }

    public String getReviewDate() {
        return reviewDate;
    }

    public String getReviewScore() {
        return reviewScore;
    }
}
