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

public class HomeFragment extends Fragment {

    private GridView mHomeMenuGridView;

    /**x
     * A pointer to the current callbacks instance (the Activity).
     */
    private HomeMenuCallbacks mCallbacks;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mHomeMenuGridView = (GridView) view.findViewById(R.id.home_menu);
        mHomeMenuGridView.setAdapter(new NavigationItemAdapter(getActivity(), getMenuItems(), R.layout.row_home_menu));
        mHomeMenuGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mCallbacks != null) {
                    mCallbacks.onHomeMenuItemSelected(position);
                }
            }
        });
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (HomeMenuCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement HomeMenuCallbacks.");
        }
    }

    private List<NavigationItem> getMenuItems() {
        List<NavigationItem> items = new ArrayList<>();
        items.add(createNavigationItem(R.string.title_play_single, R.drawable.ic_action_play_single));
        items.add(createNavigationItem(R.string.title_play_challenge, R.drawable.ic_action_playoff));
        items.add(createNavigationItem(R.string.title_stats_score, R.drawable.ic_action_score));
        items.add(createNavigationItem(R.string.title_stats_rating, R.drawable.ic_action_rating));

        return items;
    }

    private NavigationItem createNavigationItem(int itemId, int imageId) {
        return new NavigationItem(itemId, getString(itemId), imageId);
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface HomeMenuCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onHomeMenuItemSelected(int position);
    }
}
