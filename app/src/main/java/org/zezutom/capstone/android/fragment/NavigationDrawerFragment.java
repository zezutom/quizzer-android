package org.zezutom.capstone.android.fragment;


import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import org.zezutom.capstone.android.R;
import org.zezutom.capstone.android.adapter.NavigationItemAdapter;
import org.zezutom.capstone.android.model.NavigationItem;
import org.zezutom.capstone.android.model.UserProfile;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

    /**
     * Remember the currently selected position.
     */
    public static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    public static final String STATE_SIGNED_IN = "is_signed_in";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    public static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**x
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks callbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle drawerToggle;

    /**
     * Fixed menu items.
     */
    private List<NavigationItem> navigationItems;

    private DrawerLayout drawerLayout;

    private View drawerView;

    private View userProfileView;

    private ListView drawerListView;

    private View fragmentContainerView;

    private int selectedPosition;

    private boolean isUserSignedIn;

    private boolean fromSavedInstanceState;

    private boolean hasUserLearnedDrawer;

    private RequestQueue requestQueue;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigationItems = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(getActivity());
        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        hasUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            selectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            isUserSignedIn = savedInstanceState.getBoolean(STATE_SIGNED_IN);
            fromSavedInstanceState = true;
        }
    }

    private void initNavigationItems(UserProfile userProfile) {
        navigationItems = new ArrayList<>();
        navigationItems.add(createNavigationItem(R.string.label_home, R.drawable.ic_action_home));
        navigationItems.add(createNavigationItem(R.string.label_play_single, R.drawable.ic_action_play_single));
        navigationItems.add(createNavigationItem(R.string.label_stats_score, R.drawable.ic_action_score));
        navigationItems.add(createNavigationItem(R.string.label_game_results, R.drawable.ic_action_calendar));
        navigationItems.add(createNavigationItem(R.string.label_stats_rating, R.drawable.ic_action_rating));

        initUserProfileView(userProfile);
        isUserSignedIn = (userProfile != null);
        if (isUserSignedIn) {
            navigationItems.add(navigationItems.size(), createNavigationItem(R.string.label_sign_out, R.drawable.ic_action_google_plus));
        } else {
            navigationItems.add(0, createNavigationItem(R.string.label_sign_in, R.drawable.ic_action_google_plus));
        }
        drawerListView.setAdapter(new NavigationItemAdapter(this.getActivity(), navigationItems, R.layout.row_navigation));
    }

    private void initUserProfileView(UserProfile userProfile) {
        if (userProfile == null) {
            userProfileView.setVisibility(View.GONE);
        } else {
            userProfileView.setVisibility(View.VISIBLE);

            final ImageView imageView = (ImageView) userProfileView.findViewById(R.id.image);
            ImageRequest imageRequest = new ImageRequest(userProfile.getImageUrl(), new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap bitmap) {
                    imageView.setImageBitmap(bitmap);
                }
            }, 0, 0, null, null);
            requestQueue.add(imageRequest);

            final TextView fullNameView = (TextView) userProfileView.findViewById(R.id.full_name);
            fullNameView.setText(userProfile.getFullName());

            final TextView emailView = (TextView) userProfileView.findViewById(R.id.email);
            emailView.setText(userProfile.getEmail());
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        drawerView = inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);

        drawerListView = (ListView) drawerView.findViewById(R.id.drawer_list);
        drawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        userProfileView = drawerView.findViewById(R.id.user_profile);

        return drawerView;
    }

    public void setSignedInView(UserProfile userProfile) {
        boolean userSignedIn = isUserSignedIn;
        initNavigationItems(userProfile);
        selectMenuItem(userSignedIn);
    }

    public void setSignedOutView() {
        setSignedInView(null);
    }

    private void selectMenuItem(boolean userSignedIn) {
        if (userSignedIn != isUserSignedIn) {
            selectHomeItem();
        } else {
            selectItem(selectedPosition);
        }
    }

    private NavigationItem createNavigationItem(int itemId, int imageId) {
        return new NavigationItem(itemId, getString(itemId), imageId);
    }

    public NavigationItem getNavigationItem(int position) {
        if (navigationItems == null) {
            throw new IllegalStateException("The drawer is empty!");
        }

        if (navigationItems.size() <= position) {
            position = navigationItems.size() - 1;
        }
        return navigationItems.get(position);
    }

    public int getNavigationItemPosition(int itemId) {
        int position = 0;

        for (int i = 0; i < navigationItems.size(); i++) {
            if (navigationItems.get(i).getId() == itemId) {
                position = i;
                break;
            }
        }
        return position;
    }

    public boolean isDrawerOpen() {
        return drawerLayout != null && drawerLayout.isDrawerOpen(fragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        fragmentContainerView = getActivity().findViewById(fragmentId);
        this.drawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        this.drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        drawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                NavigationDrawerFragment.this.drawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!hasUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    hasUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!hasUserLearnedDrawer && !fromSavedInstanceState) {
            this.drawerLayout.openDrawer(fragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        this.drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                drawerToggle.syncState();
            }
        });

        this.drawerLayout.setDrawerListener(drawerToggle);
    }

    public void selectHomeItem() {
        selectItem(userProfileView.getVisibility() == View.VISIBLE ? 0 : 1);
    }

    public void selectItem(int position) {
        selectedPosition = position;
        if (drawerListView != null) {
            drawerListView.setItemChecked(position, true);
        }
        if (drawerLayout != null) {
            drawerLayout.closeDrawer(fragmentContainerView);
        }
        if (callbacks != null) {
            callbacks.onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            callbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, selectedPosition);
        outState.putBoolean(STATE_SIGNED_IN, isUserSignedIn);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (drawerLayout != null && isDrawerOpen()) {
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return getActivity().getActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }
}
