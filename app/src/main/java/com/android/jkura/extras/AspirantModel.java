package com.android.jkura.extras;

import com.google.firebase.database.Exclude;

public class AspirantModel {
    private String aspirantName;
    private String aspirantImageURL;
    private String aspirantPosition;
    private String aspirantSchool;
    private String aspirantDepartment;
    private String aspirantRegNo;
    private String aspirantEmail;
    private String key;
    private int position;

    public AspirantModel() {
    }

    public AspirantModel(int position) {
        this.position = position;
    }

    public AspirantModel(String aspirantName, String aspirantImageURL, String aspirantPosition, String aspirantSchool, String aspirantDepartment, String aspirantEmail, String aspirantRegNo) {
        this.aspirantName = aspirantName;
        this.aspirantImageURL = aspirantImageURL;
        this.aspirantPosition = aspirantPosition;
        this.aspirantSchool = aspirantSchool;
        this.aspirantDepartment = aspirantDepartment;
        this.aspirantEmail = aspirantEmail;
        this.aspirantRegNo = aspirantRegNo;
    }

    public String getAspirantRegNo() {
        return aspirantRegNo;
    }

    public void setAspirantRegNo(String aspirantRegNo) {
        this.aspirantRegNo = aspirantRegNo;
    }

    public String getAspirantEmail() {
        return aspirantEmail;
    }

    public void setAspirantEmail(String aspirantEmail) {
        this.aspirantEmail = aspirantEmail;
    }

    public String getAspirantName() {
        return aspirantName;
    }

    public void setAspirantName(String aspirantName) {
        this.aspirantName = aspirantName;
    }

    public String getAspirantImageURL() {
        return aspirantImageURL;
    }

    public void setAspirantImageURL(String aspirantImageURL) {
        this.aspirantImageURL = aspirantImageURL;
    }

    public String getAspirantPosition() {
        return aspirantPosition;
    }

    public void setAspirantPosition(String aspirantPosition) {
        this.aspirantPosition = aspirantPosition;
    }

    public String getAspirantSchool() {
        return aspirantSchool;
    }

    public void setAspirantSchool(String aspirantSchool) {
        this.aspirantSchool = aspirantSchool;
    }

    public String getAspirantDepartment() {
        return aspirantDepartment;
    }

    public void setAspirantDepartment(String aspirantDepartment) {
        this.aspirantDepartment = aspirantDepartment;
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
