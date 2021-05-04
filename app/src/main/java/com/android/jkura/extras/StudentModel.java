package com.android.jkura.extras;

public class StudentModel {
    private String studentCourse;
    private String password;
    private String studentName;
    private String studentRegNo;
    private String studentEmail;
    private String studentSchool;
    private String studentDepartment;

    public StudentModel(String course, String password, String name, String reg_no, String email, String school, String department) {
        this.studentCourse = course;
        this.password = password;
        this.studentName = name;
        this.studentRegNo = reg_no;
        this.studentEmail = email;
        this.studentSchool = school;
        this.studentDepartment = department;
    }

    public String getStudentCourse() {
        return studentCourse;
    }

    public void setStudentCourse(String studentCourse) {
        this.studentCourse = studentCourse;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentRegNo() {
        return studentRegNo;
    }

    public void setStudentRegNo(String studentRegNo) {
        this.studentRegNo = studentRegNo;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getStudentSchool() {
        return studentSchool;
    }

    public void setStudentSchool(String studentSchool) {
        this.studentSchool = studentSchool;
    }

    public String getStudentDepartment() {
        return studentDepartment;
    }

    public void setStudentDepartment(String studentDepartment) {
        this.studentDepartment = studentDepartment;
    }


}
