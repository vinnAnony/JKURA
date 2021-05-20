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
    private final String TAG = "Session Manager";

    private FirebaseAuth auth;

    public SessionManager(Context context){

        auth = FirebaseAuth.getInstance();
        userSession = context.getSharedPreferences("userLoginSession", Context.MODE_PRIVATE);
        editor = userSession.edit();

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
