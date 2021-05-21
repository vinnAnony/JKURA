package com.android.jkura.extras;

import android.content.Context;
import android.content.SharedPreferences;

public class RegNameManager {

    private final SharedPreferences session;
    private final SharedPreferences.Editor editor;

    public RegNameManager(Context context){
        session = context.getSharedPreferences("regNumberSession", Context.MODE_PRIVATE);
        editor = session.edit();
    }

    public void setRegName(String name, String reg){
        editor.putString(reg, name);
        editor.commit();
    }

    public String getRegName(String reg){
        return session.getString(reg, null);
    }


}
