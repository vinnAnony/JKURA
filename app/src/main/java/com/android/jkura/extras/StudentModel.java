package com.android.jkura.extras;

import android.os.Parcel;
import android.os.Parcelable;

public class StudentModel implements Parcelable {
    private String studentCourse;
    private String password;
    private String studentName;
    private String studentRegNo;
    private String studentEmail;
    private String studentSchool;
    private String studentDepartment;


    public StudentModel(){

    }

    public StudentModel(String course, String password, String name, String reg_no, String email, String school, String department) {
        this.studentCourse = course;
        this.password = password;
        this.studentName = name;
        this.studentRegNo = reg_no;
        this.studentEmail = email;
        this.studentSchool = school;
        this.studentDepartment = department;
    }

    protected StudentModel(Parcel in) {
        studentCourse = in.readString();
        password = in.readString();
        studentName = in.readString();
        studentRegNo = in.readString();
        studentEmail = in.readString();
        studentSchool = in.readString();
        studentDepartment = in.readString();
    }

    public static final Creator<StudentModel> CREATOR = new Creator<StudentModel>() {
        @Override
        public StudentModel createFromParcel(Parcel in) {
            return new StudentModel(in);
        }

        @Override
        public StudentModel[] newArray(int size) {
            return new StudentModel[size];
        }
    };

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(studentCourse);
        dest.writeString(password);
        dest.writeString(studentName);
        dest.writeString(studentRegNo);
        dest.writeString(studentEmail);
        dest.writeString(studentSchool);
        dest.writeString(studentDepartment);
    }
}
