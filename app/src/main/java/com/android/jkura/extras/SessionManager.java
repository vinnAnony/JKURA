package com.android.jkura.extras;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private final SharedPreferences userSession;
    private final SharedPreferences.Editor editor;

    public static final String KEY_STUDENT_EMAIL = "email";

    public SessionManager(Context context){

        userSession = context.getSharedPreferences("userLoginSession", Context.MODE_PRIVATE);
        editor = userSession.edit();
        editor.apply();

    }

    public String getEmail(){
        return userSession.getString(KEY_STUDENT_EMAIL, null);
    }

    private void setEmailDetails(String emailDetails){
        editor.putString(KEY_STUDENT_EMAIL, emailDetails);
        editor.commit();
    }

    public boolean checkIfFirstTime(){

        if (getEmail() != null){
            return getEmail().equals("");
        } else {
            return true;
        }

    }
}
