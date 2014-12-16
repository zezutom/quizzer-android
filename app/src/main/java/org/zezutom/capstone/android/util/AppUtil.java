package org.zezutom.capstone.android.util;

import android.app.Dialog;
import android.app.DialogFragment;

public class AppUtil {

    public static final String NUMBER_FORMAT = "%01d";

    private AppUtil() {}

    public static void closeDialog(DialogFragment dialogFragment) {
        if (dialogFragment != null) {
            dialogFragment.dismiss();
        }
    }

    public static String numberToString(int value) {
        return String.format(NUMBER_FORMAT, value);
    }
}
