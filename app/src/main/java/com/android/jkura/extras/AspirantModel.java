package com.android.jkura.extras;

import com.google.firebase.database.Exclude;

public class AspirantModel {
    private String aspirantName;
    private String aspirantEmail;
    private String aspirantImageURL;
    private String aspirantCourse;
    private String aspirantRegNo;
    private String key;
    private int position;

    public AspirantModel() {
    }

    public AspirantModel(int position) {
        this.position = position;
    }

    public AspirantModel(String aspirantName, String aspirantEmail, String aspirantImageURL, String aspirantCourse, String aspirantRegNo) {
        this.aspirantName = aspirantName;
        this.aspirantEmail = aspirantEmail;
        this.aspirantImageURL = aspirantImageURL;
        this.aspirantCourse = aspirantCourse;
        this.aspirantRegNo = aspirantRegNo;
    }

    public String getAspirantName() {
        return aspirantName;
    }

    public void setAspirantName(String aspirantName) {
        this.aspirantName = aspirantName;
    }

    public String getAspirantEmail() {
        return aspirantEmail;
    }

    public void setAspirantEmail(String aspirantEmail) {
        this.aspirantEmail = aspirantEmail;
    }

    public String getAspirantImageURL() {
        return aspirantImageURL;
    }

    public void setAspirantImageURL(String aspirantImageURL) {
        this.aspirantImageURL = aspirantImageURL;
    }

    public String getAspirantCourse() {
        return aspirantCourse;
    }

    public void setAspirantCourse(String aspirantCourse) {
        this.aspirantCourse = aspirantCourse;
    }

    public String getAspirantRegNo() {
        return aspirantRegNo;
    }

    public void setAspirantRegNo(String aspirantRegNo) {
        this.aspirantRegNo = aspirantRegNo;
    }

    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }
}
