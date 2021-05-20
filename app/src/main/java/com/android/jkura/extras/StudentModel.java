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
    private int dlgtVoted;
    private int schRepVoted;


    public StudentModel(){

    }

    public StudentModel(String studentCourse, String password, String studentName, String studentRegNo, String studentEmail, String studentSchool, String studentDepartment, int dlgtVoted, int schRepVoted) {
        this.studentCourse = studentCourse;
        this.password = password;
        this.studentName = studentName;
        this.studentRegNo = studentRegNo;
        this.studentEmail = studentEmail;
        this.studentSchool = studentSchool;
        this.studentDepartment = studentDepartment;
        this.dlgtVoted = dlgtVoted;
        this.schRepVoted = schRepVoted;
    }

    protected StudentModel(Parcel in) {
        studentCourse = in.readString();
        password = in.readString();
        studentName = in.readString();
        studentRegNo = in.readString();
        studentEmail = in.readString();
        studentSchool = in.readString();
        studentDepartment = in.readString();
        dlgtVoted = in.readInt();
        schRepVoted = in.readInt();
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

    public int getDlgtVoted() {
        return dlgtVoted;
    }

    public void setDlgtVoted(int dlgtVoted) {
        this.dlgtVoted = dlgtVoted;
    }

    public int getSchRepVoted() {
        return schRepVoted;
    }

    public void setSchRepVoted(int schRepVoted) {
        this.schRepVoted = schRepVoted;
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
        dest.writeInt(dlgtVoted);
        dest.writeInt(schRepVoted);
    }
}
