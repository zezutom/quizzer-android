package org.zezutom.capstone.android.util;

import android.app.DialogFragment;
import android.content.Context;

import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Scope;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppUtil {

    public static final String WEB_CLIENT_ID = "98684777677-2rsfk0ht84vn00pph1kjr87ud0587cfi.apps.googleusercontent.com";

    public static final String ANDROID_CLIENT_ID = "98684777677-4ev0ir13ce5ri23q8eupnl3jtvml5u4c.apps.googleusercontent.com";

    public static final String NUMBER_FORMAT = "%01d";

    private AppUtil() {}

    public static void closeDialog(DialogFragment dialogFragment) {
        if (dialogFragment != null) {
            dialogFragment.dismiss();
        }
    }

    public static GoogleAccountCredential getCredential(Context context) {
        final GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(context, "server:client_id:" + WEB_CLIENT_ID);
        final String accountName = new AuthCache(context).getSelectedAccountName();
        credential.setSelectedAccountName(accountName);
        return credential;
    }

    /*public static GoogleAccountCredential getCredential(Context context) {
        List<String> scopes = new ArrayList<>();
        scopes.add("https://www.googleapis.com/auth/userinfo.email");

        final GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(context, scopes);
        credential.setSelectedAccountName(new AuthCache(context).getSelectedAccountName());
        return credential;
    }*/

    /**
     * Generate the OAuth2 scope string accepted by the client libraries.
     *
     * @param scopes to be encoded in the OAuth2 string
     * @return OAuth2 scope string
     */
    public static String getOAuth2ScopeString(String[] scopes) {
        if (scopes==null || Array.getLength(scopes) < 1) {
            return null;
        }

        StringBuilder scopeString = null;
        for (String scope : scopes) {
            if (scopeString == null) {
                scopeString = new StringBuilder("oauth2: ").append(scope);
            } else {
                scopeString.append(" ")
                        .append(scope);
            }
        }

        return scopeString.toString();
    }

    public static String numberToString(int value) {
        return String.format(NUMBER_FORMAT, value);
    }
}
