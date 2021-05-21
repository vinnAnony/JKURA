package com.android.jkura.extras;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SessionManager {

    private final SharedPreferences userSession;
    private final SharedPreferences.Editor editor;

    public static final String KEY_STUDENT_EMAIL = "email";
    public static final String KEY_STUDENT_REG_NO = "reg_no";
    public static final String KEY_STUDENT_PASSWORD= "password";
    public static final String KEY_STUDENT_NAME = "name";
    public static final String KEY_STUDENT_SCHOOL = "school";
    public static final String KEY_STUDENT_DEPERTMENT = "depertment";
    public static final String KEY_STUDENT_DELEGATE_VOTE = "delegate_vote";
    public static final String KEY_STUDENT_REP_VOTE = "rep_vote";
    public static final String KEY_STUDENT_COURSE = "course";
    public static final String KEY_STUDENT_REPLACED_MAIL = "course";

    public static final String KEY_LOGGED_IN = "logged_in";

    private final String TAG = "Session Manager";

    private FirebaseAuth auth;

    public SessionManager(Context context){

        auth = FirebaseAuth.getInstance();
        userSession = context.getSharedPreferences("userLoginSession", Context.MODE_PRIVATE);
        editor = userSession.edit();

    }

    public boolean checkLoggedIn(){
        Log.d(TAG, "checkLoggedIn: " + userSession.getBoolean(KEY_LOGGED_IN, false));
        return userSession.getBoolean(KEY_LOGGED_IN, false);
    }

    public void setLoginValue(boolean value){
        editor.putBoolean(KEY_LOGGED_IN, value);
        editor.commit();
    }

    public void saveStudentDetails(StudentModel studentModel){

        Log.d(TAG, "saveStudentDetails: " + studentModel);
        editor.putString(KEY_STUDENT_REG_NO, studentModel.getStudentRegNo());
        editor.putString(KEY_STUDENT_EMAIL, studentModel.getStudentRegNo());
        editor.putString(KEY_STUDENT_PASSWORD, studentModel.getPassword());
        editor.putString(KEY_STUDENT_NAME, studentModel.getStudentName());
        editor.putString(KEY_STUDENT_SCHOOL, studentModel.getStudentSchool());
        editor.putString(KEY_STUDENT_DEPERTMENT, studentModel.getStudentDepartment());
        editor.putInt(KEY_STUDENT_DELEGATE_VOTE, studentModel.getDlgtVoted());
        editor.putInt(KEY_STUDENT_REP_VOTE, studentModel.getSchRepVoted());
        editor.putString(KEY_STUDENT_COURSE, studentModel.getStudentCourse());
        editor.commit();
    }

    public void setReplacedMail(String replacedMail){
        editor.putString(KEY_STUDENT_REPLACED_MAIL, replacedMail);
        editor.commit();
    }

    public StudentModel getStudentDetails(){

        StudentModel studentModel = new StudentModel();
        studentModel.setDlgtVoted(userSession.getInt(KEY_STUDENT_DELEGATE_VOTE, 0));
        studentModel.setSchRepVoted(userSession.getInt(KEY_STUDENT_REP_VOTE, 0));
        studentModel.setPassword(userSession.getString(KEY_STUDENT_PASSWORD, null));
        studentModel.setStudentCourse(userSession.getString(KEY_STUDENT_COURSE, null));
        studentModel.setStudentEmail(userSession.getString(KEY_STUDENT_EMAIL, null));
        studentModel.setStudentName(userSession.getString(KEY_STUDENT_NAME, null));
        studentModel.setStudentDepartment(userSession.getString(KEY_STUDENT_DEPERTMENT, null));
        studentModel.setStudentSchool(userSession.getString(KEY_STUDENT_SCHOOL, null));
        studentModel.setStudentRegNo(userSession.getString(KEY_STUDENT_REG_NO, null));

        Log.d(TAG, "getStudentDetails: Student " + studentModel);
        return studentModel;

    }


    public void resetData(){
        editor.putString(KEY_STUDENT_REG_NO, null);
        editor.putString(KEY_STUDENT_EMAIL, null);
        auth.signOut();
        editor.commit();
    }

    public String getRegNo(){
        return userSession.getString(KEY_STUDENT_REG_NO, null);
    }

    public String getEmail(){
        return userSession.getString(KEY_STUDENT_EMAIL, null);
    }

    public void setRegNo(String regNo){
        editor.putString(KEY_STUDENT_REG_NO, regNo);
        editor.commit();
    }

    public void setEmailDetails(String emailDetails){
        editor.putString(KEY_STUDENT_EMAIL, emailDetails);
        editor.commit();
    }


}
