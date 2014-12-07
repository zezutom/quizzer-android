package org.zezutom.capstone.android.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.zezutom.capstone.android.R;
import org.zezutom.capstone.android.adapter.NavigationItemAdapter;
import org.zezutom.capstone.android.model.NavigationItem;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {

    private GridView mTopicsGridView;

    private GridView mDifficultyGridView;

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private SettingsMenuCallbacks mCallbacks;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        mTopicsGridView = (GridView) view.findViewById(R.id.topics);
        mTopicsGridView.setAdapter(new NavigationItemAdapter(getActivity(), getTopics(), R.layout.row_settings));
        mTopicsGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mCallbacks != null) {
                    mCallbacks.onSettingsMenuItemSelected(position);
                }
            }
        });

        mDifficultyGridView = (GridView) view.findViewById(R.id.difficulty);
        mDifficultyGridView.setAdapter(new NavigationItemAdapter(getActivity(), getDifficultyLevels(), R.layout.row_settings));
        mDifficultyGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mCallbacks != null) {
                    mCallbacks.onSettingsMenuItemSelected(position);
                }
            }
        });
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (SettingsMenuCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement SettingsMenuCallbacks.");
        }
    }

    private List<NavigationItem> getTopics() {
        List<NavigationItem> items = new ArrayList<>();
        items.add(createNavigationItem(R.string.title_settings_topic_java));
        items.add(createNavigationItem(R.string.title_settings_topic_android));
        items.add(createNavigationItem(R.string.title_settings_topic_javascript));
        items.add(createNavigationItem(R.string.title_settings_topic_html5));
        items.add(createNavigationItem(R.string.title_settings_all));

        return items;
    }

    private List<NavigationItem> getDifficultyLevels() {
        List<NavigationItem> items = new ArrayList<>();
        items.add(createNavigationItem(R.string.title_settings_difficulty_easy));
        items.add(createNavigationItem(R.string.title_settings_difficulty_medium));
        items.add(createNavigationItem(R.string.title_settings_difficulty_hard));
        items.add(createNavigationItem(R.string.title_settings_all));

        return items;
    }

    private NavigationItem createNavigationItem(int itemId) {
        return new NavigationItem(itemId, getString(itemId), R.drawable.ic_action_tick);
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface SettingsMenuCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onSettingsMenuItemSelected(int position);
    }

}
