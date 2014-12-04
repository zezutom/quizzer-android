package org.zezutom.capstone.android;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.zezutom.capstone.android.api.GameApi;
import org.zezutom.capstone.android.api.GameResultListener;
import org.zezutom.capstone.android.fragment.ChallengeAFriendFragment;
import org.zezutom.capstone.android.fragment.GameExitDialog;
import org.zezutom.capstone.android.fragment.GameFragment;
import org.zezutom.capstone.android.fragment.HomeFragment;
import org.zezutom.capstone.android.fragment.MyScoreFragment;
import org.zezutom.capstone.android.fragment.NavigationDrawerFragment;
import org.zezutom.capstone.android.fragment.QuizRatingFragment;
import org.zezutom.capstone.android.model.Game;
import org.zezutom.capstone.android.model.GameHistory;
import org.zezutom.capstone.android.model.NavigationItem;
import org.zezutom.capstone.android.model.UserProfile;
import org.zezutom.capstone.android.util.AppUtil;

import zezutom.org.gameService.model.GameResult;

public class MainActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        NavigationDrawerFragment.NavigationDrawerCallbacks,
        HomeFragment.HomeMenuCallbacks,
        View.OnClickListener,
        GameResultListener {

    public static final String TAG = "MainActivity";

    /**
     * A key to save the progress of signing in to the Google API.
     *
     * {@link #mIsInResolution}
     */
    public static final String KEY_IN_RESOLUTION = "is_in_resolution";

    /**
     * Indicates if a there is a game in progress (useful when one wants to load a different menu for ex.).
     *
     * {@link #mIsGameInProgress}
     */
    public static final String KEY_GAME_IN_PROGRESS = "is_game_in_progress";

    /**
     * Request code for auto Google Play Services error resolution.
     */
    public static final int REQUEST_CODE_RESOLUTION = 1;

    /**
     * Request code for signing a user in.
     */
    public static final int REQUEST_CODE_SIGN_IN = 2;

    /**
     * Profile picture image size in pixels.
     */
    public static final int PROFILE_IMAGE_SIZE = 200;

    /**
     * Google API client.
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * Google API latest connection result.
     * It's need for making an explicit login attempt.
     */
    private ConnectionResult mConnectionResult;

    /**
     * Determines if the client is in a resolution state, and
     * waiting for resolution intent to return.
     */
    private boolean mIsInResolution;

    /**
     * Determines if the client is waiting for sign-in intent to return.
     */
    private boolean mIsSignInProgress;

    /**
     * A flag indicating that a PendingIntent is in progress and prevents us
     * from starting further intents.
     */
    private boolean mIsIntentInProgress;

    /**
     * A flag indicating that there is a game in progress. If that's the case the user
     * is asked to take a decision on what to do with the game before she is allowed
     * to navigate away from it.
     */
    private boolean mIsGameInProgress;

    /**
     * Determines if the user has been successfully signed in.
     */
    private boolean mIsSignedIn;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Fragment managing a single game, incl. end-user interactions and presentation.
     */
    private GameFragment mGameFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    /**
     * Shown when the user is about to leave a game in progress.
     */
    private GameExitDialog mGameExitDialog;

    /**
     * An index of the currently selected menu item.
     */
    private int mMenuItemIndex;

    private GameApi mGameApi;

    /**
     * Called when the activity is starting. Restores the activity state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mIsInResolution = savedInstanceState.getBoolean(KEY_IN_RESOLUTION, false);
            mIsGameInProgress = savedInstanceState.getBoolean(KEY_GAME_IN_PROGRESS, false);
        }
        setContentView(R.layout.activity_main_navigation);
        mGameApi = new GameApi(this, this);
        mGameApi.setUp();

        mGameFragment = new GameFragment();
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);

        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGameApi.tearDown();
    }

    @Override
    public void onBackPressed() {
        if (mIsGameInProgress) {
            showGameExitDialog();
        } else {
            mNavigationDrawerFragment.selectHomeItem();
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if (isGameInProgress(position)) {
            mNavigationDrawerFragment.getNavigationItem(position);
            mMenuItemIndex = position;
            showGameExitDialog();
            return;
        }

        // update the main content by replacing fragments
        getFragmentManager().beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position))
                .commit();

        if (mNavigationDrawerFragment == null) {
            return;
        }

        // take an action
        final NavigationItem item = mNavigationDrawerFragment.getNavigationItem(position);
        switch (item.getId()) {
            case R.string.title_sign_in:
                signIn();
                break;
            case R.string.title_sign_out:
                signOut();
                break;
        }
    }

    private boolean isGameInProgress(int position) {
        if (!mIsGameInProgress || mNavigationDrawerFragment == null) {
            return false;
        }

        final NavigationItem item = mNavigationDrawerFragment.getNavigationItem(position);

        return item.getId() != R.string.title_play_single;
    }

    public void onSectionAttached(int position) {
        final NavigationItem item = mNavigationDrawerFragment.getNavigationItem(position);

        Fragment fragment = null;
        switch (item.getId()) {
            case R.string.title_home:
                mTitle = getString(R.string.title_home);
                fragment = new HomeFragment();
                break;
            case R.string.title_play_single:
                mTitle = getString(R.string.title_play_single);
                fragment = mGameFragment;
                mIsGameInProgress = true;
                break;
            case R.string.title_play_challenge:
                mTitle = getString(R.string.title_play_challenge);
                fragment = new ChallengeAFriendFragment();
                break;
            case R.string.title_stats_score:
                mTitle = getString(R.string.title_stats_score);
                fragment = new MyScoreFragment();
                break;
            case R.string.title_stats_rating:
                mTitle = getString(R.string.title_stats_rating);
                fragment = new QuizRatingFragment();
                break;
            case R.string.title_sign_in:
                mTitle = getString(R.string.title_sign_in);
                break;
            case R.string.title_sign_out:
                mTitle = getString(R.string.title_sign_out);
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment).commit();
            restoreActionBar();
        }
    }

    private void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main_activity, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when the Activity is made visible.
     * A connection to Play Services need to be initiated as
     * soon as the activity is visible. Registers {@code ConnectionCallbacks}
     * and {@code OnConnectionFailedListener} on the
     * activities itself.
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Plus.API)
                    .addScope(Plus.SCOPE_PLUS_LOGIN)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    /**
     * Called when activity gets invisible. Connection to Play Services needs to
     * be disconnected as soon as an activity is invisible.
     */
    @Override
    protected void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void onPause() {
        if (mGameExitDialog != null) {
            AppUtil.closeDialog(mGameExitDialog);
        }
        super.onPause();
    }

    /**
     * Saves the resolution state.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IN_RESOLUTION, mIsInResolution);
        outState.putBoolean(KEY_GAME_IN_PROGRESS, mIsGameInProgress);
    }

    /**
     * Handles Google Play Services resolution callbacks.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mIsIntentInProgress = false;

        switch (requestCode) {
            case REQUEST_CODE_RESOLUTION:
                retryConnecting();
                break;
            case REQUEST_CODE_SIGN_IN:
                mIsSignInProgress = true;
                if (!mGoogleApiClient.isConnecting()) {
                    mGoogleApiClient.connect();
                }
                break;
        }
    }

    private void retryConnecting() {
        mIsInResolution = false;
        if (!mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
        }
    }

    /**
     * Called when {@code mGoogleApiClient} is connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        mIsSignInProgress = false;
        mIsSignedIn = true;
        signIn();
    }

    /**
     * Called when {@code mGoogleApiClient} connection is suspended.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended");
        retryConnecting();
    }

    /**
     * Called when {@code mGoogleApiClient} is trying to connect but failed.
     * Handle {@code result.getResolution()} if there is a resolution
     * available.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            // Show a localized error dialog.
            GooglePlayServicesUtil.getErrorDialog(
                    result.getErrorCode(), this, 0, new OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            retryConnecting();
                        }
                    }).show();
            return;
        }

        // If there is an existing resolution error being displayed or a resolution
        // activity has started before, do nothing and wait for resolution
        // progress to be completed.
        if (mIsInResolution) {
            return;
        }

        if (!mIsIntentInProgress) {
            // save the connection result for later
            this.mConnectionResult = result;

            if (mIsSignInProgress) {
                // the user is being signed in
                resolveSignInError();
            } else {
                // the user is signed out
                mNavigationDrawerFragment.setSignedOutView();
            }
        } else {
            mIsInResolution = true;
            try {
                result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
            } catch (SendIntentException e) {
                Log.e(TAG, "Exception while starting resolution activity", e);
                retryConnecting();
            }
        }
    }

    @Override
    public void onClick(View view) {
        mIsGameInProgress = false;
        switch (view.getId()) {
            case R.id.save_score:
                Game game = mGameFragment.getGame();
                GameHistory history = game.getHistory();
                mGameApi.saveGameResult(game.getRound(), game.getScore(), game.getPowerUps(),
                        history.getOneTimeAttempts(), history.getTwoTimeAttempts());
                mGameFragment.resetGame();
                break;
            case R.id.exit_game:
                mGameFragment.resetGame();
                mNavigationDrawerFragment.selectItem(mMenuItemIndex);
                break;
            case R.id.reset_game:
                mGameFragment.resetGame();
                break;
        }
        AppUtil.closeDialog(mGameExitDialog);
    }

    private void signIn() {
        if (mIsSignedIn) {
            final Person person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            final String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

            if (person != null) {
                final String url = person.getImage().getUrl();
                final String imageUrl = url.substring(0, url.length() - 2) + PROFILE_IMAGE_SIZE;

                UserProfile userProfile = new UserProfile(person.getDisplayName(), email, url);


                // Set up the sign-in / sign-out menu
                mNavigationDrawerFragment.setSignedInView(userProfile);
            }

        } else if (!mGoogleApiClient.isConnecting()) {
            mIsSignInProgress = true;
            mGoogleApiClient.connect();
        }
    }

    private void signOut() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
            mIsSignedIn = false;
        }
    }

    private void resolveSignInError() {
        if (mConnectionResult != null && mConnectionResult.hasResolution()) {
            mIsIntentInProgress = true;
            try {
                mConnectionResult.startResolutionForResult(this, REQUEST_CODE_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIsIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    private void showGameExitDialog() {
        mGameExitDialog = new GameExitDialog();
        mGameExitDialog.setOnClickListener(this);
        mGameExitDialog.setCancelable(false);
        mGameExitDialog.show(getFragmentManager(), null);
    }

    @Override
    public void onSaveGameResult(GameResult gameResult) {
        Toast.makeText(this, "yupee", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveGameError(Exception ex) {
        Toast.makeText(this, "nope", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onHomeMenuItemSelected(int position) {
        int itemId = R.string.title_home;
        switch (position) {
            case 0:
                itemId = R.string.title_play_single;
                break;
            case 1:
                itemId = R.string.title_play_challenge;
                break;
            case 2:
                itemId = R.string.title_stats_score;
                break;
            case 3:
                itemId = R.string.title_stats_rating;
                break;
        }
        // Delegates the action to the sidebar menu
        position = mNavigationDrawerFragment.getNavigationItemPosition(itemId);
        mNavigationDrawerFragment.selectItem(position);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_navigation, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }
}
