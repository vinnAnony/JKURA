package com.android.jkura.extras;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class RegNameManager {

    private final SharedPreferences session;
    private final SharedPreferences.Editor editor;
    private String TAG = "REG_NAME_MANAGER";

    public RegNameManager(Context context){
        session = context.getSharedPreferences("regNumberSession", Context.MODE_PRIVATE);
        editor = session.edit();
    }

    public void setRegName(String reg, String name){
        editor.putString(reg, name);
        editor.commit();
        Log.d(TAG, "setRegName: SET" + reg + "name "+ name);
    }

    public String getRegName(String reg){
        Log.d(TAG, "getRegName: get" + session.getString(reg, null));
        return session.getString(reg, null);
    }


}
