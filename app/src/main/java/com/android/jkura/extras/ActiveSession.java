package com.android.jkura.extras;

public class ActiveSession {

    private String school;
    private String department;
    private String position;
    private String id;

    public ActiveSession() {
    }

    public ActiveSession(String school, String department, String position, String id) {
        this.school = school;
        this.department = department;
        this.position = position;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
