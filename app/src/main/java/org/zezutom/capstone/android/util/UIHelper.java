package org.zezutom.capstone.android.util;

import android.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

/**
 * Keeps references to all UI elements, just to keep the fragment code clean and tidy.
 */
public class UIHelper {

    private Map<Integer, View> viewMap;

    private View mainView;

    private FragmentManager fragmentManager;

    private LayoutInflater inflater;

    public UIHelper(View mainView) {
        this.mainView = mainView;
        this.viewMap = new HashMap<>();
    }

    public View getMainView() {
        return mainView;
    }

    public UIHelper addView(int viewId) {
        viewMap.put(viewId, mainView.findViewById(viewId));
        return this;
    }

    public <T extends View> T getView(int viewId) {
        return (T) viewMap.get(viewId);
    }
}
