package org.zezutom.capstone.android.util;

import android.app.Dialog;
import android.app.DialogFragment;

public class AppUtil {

    private AppUtil() {}

    public static void closeDialog(DialogFragment dialogFragment) {
        if (dialogFragment != null) {
            dialogFragment.dismiss();
        }
    }

}
