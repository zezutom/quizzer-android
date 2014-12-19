package org.zezutom.capstone.android.util;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthCache {

    public static final String AUTH_KEY = "auth";

    public static final String PREF_ACCOUNT_NAME = AUTH_KEY + ".prefAccountName";

    private SharedPreferences sharedPreferences;

    public AuthCache(Context context) {
        this.sharedPreferences = context.getSharedPreferences(AUTH_KEY, 0);
    }

    public void setSelectedAccountName(String accountName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_ACCOUNT_NAME, accountName);
        editor.commit();
    }

    public String getSelectedAccountName() {
        return sharedPreferences.getString(PREF_ACCOUNT_NAME, null);
    }

}
