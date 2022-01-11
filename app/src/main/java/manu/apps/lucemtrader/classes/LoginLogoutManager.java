package manu.apps.lucemtrader.classes;

import android.content.Context;
import android.content.SharedPreferences;

import manu.apps.lucemtrader.R;

public class LoginLogoutManager {

    Context context;
    private SharedPreferences sharedPreferences;

    public LoginLogoutManager(Context context) {
        this.context = context;
        getSharedPreferences();
    }

    private void getSharedPreferences() {
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.admin_preference), Context.MODE_PRIVATE);
    }

    public void writePreference() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.admin_preference_key), "INIT_OK");
        editor.apply();

    }

    public boolean checkPreference() {
        boolean status;

        status = !sharedPreferences.getString(context.getString(R.string.admin_preference_key), "null").equals("null");

        return status;
    }

    public void clearPreference() {

        sharedPreferences.edit().clear().apply();

    }


}
