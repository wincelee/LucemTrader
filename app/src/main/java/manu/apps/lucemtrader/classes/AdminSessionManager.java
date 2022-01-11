package manu.apps.lucemtrader.classes;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;


public class AdminSessionManager {

    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "ADMIN_SESSION_MANAGER";

    public static final String USERTYPE = "USERTYPE";

    public AdminSessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
        editor.apply();
        editor.commit();
    }


    public void createSession(String userType) {

        editor.putString(USERTYPE, userType);
        editor.apply();
    }


    public HashMap<String, String> getUserDetails() {

        HashMap<String, String> user = new HashMap<>();
        user.put(USERTYPE, sharedPreferences.getString(USERTYPE, null));

        return user;

    }

    public SharedPreferences returnSessionManagerSharedPreferences(){

        return sharedPreferences;

    }

}
