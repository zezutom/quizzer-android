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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import org.zezutom.capstone.android.api.GetGameResultStatsTask;
import org.zezutom.capstone.android.api.GetQuizzesTask;
import org.zezutom.capstone.android.api.RateQuizTask;
import org.zezutom.capstone.android.api.ResponseListener;
import org.zezutom.capstone.android.api.SaveGameResultTask;
import org.zezutom.capstone.android.dao.BaseDataSource;
import org.zezutom.capstone.android.dao.GameResultDataSource;
import org.zezutom.capstone.android.dao.GameResultStatsDataSource;
import org.zezutom.capstone.android.dao.QuizDataSource;
import org.zezutom.capstone.android.dao.QuizRatingDataSource;
import org.zezutom.capstone.android.fragment.GameExitDialog;
import org.zezutom.capstone.android.fragment.GameFragment;
import org.zezutom.capstone.android.fragment.HomeFragment;
import org.zezutom.capstone.android.fragment.MyScoreFragment;
import org.zezutom.capstone.android.fragment.NavigationDrawerFragment;
import org.zezutom.capstone.android.fragment.QuizRatingFragment;
import org.zezutom.capstone.android.model.Game;
import org.zezutom.capstone.android.model.NavigationItem;
import org.zezutom.capstone.android.model.UserProfile;
import org.zezutom.capstone.android.util.AppUtil;
import org.zezutom.capstone.android.util.AuthCache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zezutom.org.quizzer.Quizzer;
import zezutom.org.quizzer.model.GameResult;
import zezutom.org.quizzer.model.GameResultStats;
import zezutom.org.quizzer.model.Quiz;
import zezutom.org.quizzer.model.QuizRating;

/**
 * Don't forget to add a link to:
 * http://www.icons4android.com
 * http://www.iconarchive.com
 *
 * http://rominirani.com/2014/02/25/google-cloud-endpoints-tutorial-part-7/
 */
public class MainActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        NavigationDrawerFragment.NavigationDrawerCallbacks,
        HomeFragment.HomeMenuCallbacks,
        GameFragment.GameCallbacks,
        MyScoreFragment.MyScoreCallbacks,
        View.OnClickListener {

    public static final String TAG = "MainActivity";

    /**
     * A key to save the progress of signing in to the Google API.
     *
     * {@link #mIsInResolution}
     */
    public static final String KEY_IN_RESOLUTION = "is_in_resolution";

    /**
     * Request code for auto Google Play Services error resolution.
     */
    public static final int REQUEST_CODE_RESOLUTION = 1;

    /**
     * Request code for signing a user in.
     */
    public static final int REQUEST_CODE_SIGN_IN = 2;

    /**
     * Activity result indicating a return from the authorization approval intent.
     */
    private static final int ACTIVITY_RESULT_FROM_REQUEST_AUTH = 3;

    /**
     * Profile picture image size in pixels.
     */
    public static final int PROFILE_IMAGE_SIZE = 200;

    /**
     * Google API client.
     */
    private GoogleApiClient googleApiClient;

    /**
     * Google API latest connection result.
     * It's need for making an explicit login attempt.
     */
    private ConnectionResult connectionResult;

    /**
     * Determines if the client is in a resolution state, and
     * waiting for resolution intent to return.
     */
    private boolean mIsInResolution;

    /**
     * Determines if the client is waiting for sign-in intent to return.
     */
    private boolean isSignInProgress;

    /**
     * A flag indicating that a PendingIntent is in progress and prevents us
     * from starting further intents.
     */
    private boolean isIntentInProgress;

    /**
     * Determines if the user has been successfully signed in.
     */
    private boolean isSignedIn;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment navigationDrawerFragment;

    /**
     * Fragment managing a single game, incl. end-user interactions and presentation.
     */
    private GameFragment gameFragment;


    private MyScoreFragment myScoreFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence title;

    /**
     * Shown when the user is about to leave a game in progress.
     */
    private GameExitDialog gameExitDialog;

    /**
     * An index of the currently selected menu item.
     */
    private int menuItemIndex;

    /**
     * Google App Engine API providing server-side services
     */
    private Quizzer.QuizzerApi quizzerApi;

    private Map<Class<? extends BaseDataSource>, BaseDataSource> dataSourceMap;

    private AuthCache authCache;

    private GoogleAccountCredential credential;

    /**
     * Called when the activity is starting. Restores the activity state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Authentication cache - stores the selected account
        authCache = new AuthCache(this);

        // Authorization
        credential = GoogleAccountCredential.usingAudience(this, "server:client_id:" + AppUtil.WEB_CLIENT_ID);
        credential.setSelectedAccountName(authCache.getSelectedAccountName());

        /*if (credential.getSelectedAccountName() == null) {
            chooseAccount();
        }*/

        // Initialize the API, incl. authentication credentials
        quizzerApi = new Quizzer.Builder(
                AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(),
                credential).build().quizzerApi();

        // Set up the local storage
        initDb();


        if (savedInstanceState != null) {
            mIsInResolution = savedInstanceState.getBoolean(KEY_IN_RESOLUTION, false);
        }
        setContentView(R.layout.activity_main_navigation);

        gameFragment = new GameFragment();

        myScoreFragment = new MyScoreFragment();

        navigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);

        title = getTitle();

        // Set up the drawer.
        navigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }

    private void chooseAccount() {
        startActivityForResult(credential.newChooseAccountIntent(), REQUEST_CODE_SIGN_IN);
    }

    @Override
    public void onBackPressed() {
        if (isGameInProgress()) {
            showGameExitDialog();
        } else {
            navigationDrawerFragment.selectHomeItem();
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if (isGameInProgress(position)) {
            navigationDrawerFragment.getNavigationItem(position);
            menuItemIndex = position;
            showGameExitDialog();
            return;
        }

        // update the main content by replacing fragments
        getFragmentManager().beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position))
                .commit();

        if (navigationDrawerFragment == null) {
            return;
        }

        // take an action
        final NavigationItem item = navigationDrawerFragment.getNavigationItem(position);
        switch (item.getId()) {
            case R.string.label_sign_in:
                signIn();
                break;
            case R.string.label_sign_out:
                signOut();
                break;
        }
    }

    private boolean isGameInProgress(int position) {
        if (!isGameInProgress() || navigationDrawerFragment == null) {
            return false;
        }

        final NavigationItem item = navigationDrawerFragment.getNavigationItem(position);

        return item.getId() != R.string.label_play_single;
    }

    public boolean onMenuItemSelected(MenuItem item) {
        int position = -1;
        switch (item.getItemId()) {
            case R.id.menu_home:
                if (isGameInProgress()) showGameExitDialog();
                else position = 0;
                break;
            case R.id.menu_game:
                position = 1;
                break;
        }

        if (position >= 0) onSectionAttached(position);

        return true;
    }

    private void onSectionAttached(int position) {
        final NavigationItem item = navigationDrawerFragment.getNavigationItem(position);

        Fragment fragment = null;
        switch (item.getId()) {
            case R.string.label_home:
                title = getString(R.string.label_home);
                fragment = new HomeFragment();
                break;
            case R.string.label_play_single:
                title = getString(R.string.label_play_single);
                fragment = gameFragment;
                break;
            case R.string.label_stats_score:
                title = getString(R.string.label_stats_score);
                fragment = myScoreFragment;
                break;
            case R.string.label_stats_rating:
                title = getString(R.string.label_stats_rating);
                fragment = new QuizRatingFragment();
                break;
            case R.string.label_sign_in:
                title = getString(R.string.label_sign_in);
                break;
            case R.string.label_sign_out:
                title = getString(R.string.label_sign_out);
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
        actionBar.setTitle(title);
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
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Plus.API)
                    .addScope(Plus.SCOPE_PLUS_LOGIN)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        if (!googleApiClient.isConnected()) googleApiClient.connect();
    }

    /**
     * Called when activity gets invisible. Connection to Play Services needs to
     * be disconnected as soon as an activity is invisible.
     */
    @Override
    protected void onStop() {
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void onPause() {
        if (gameExitDialog != null) {
            AppUtil.closeDialog(gameExitDialog);
        }
        tearDownDb();
        super.onPause();
    }

    /**
     * Saves the resolution state.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IN_RESOLUTION, mIsInResolution);
    }

    /**
     * Handles Google Play Services resolution callbacks.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        isIntentInProgress = false;

        switch (requestCode) {
            case REQUEST_CODE_RESOLUTION:
                retryConnecting();
                break;
            case REQUEST_CODE_SIGN_IN:
                isSignInProgress = true;
                if (!googleApiClient.isConnecting()) {
                    googleApiClient.connect();
                }
                break;
        }
    }

    private void retryConnecting() {
        mIsInResolution = false;
        if (!googleApiClient.isConnecting()) {
            googleApiClient.connect();
        }
    }

    /**
     * Called when {@code googleApiClient} is connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        isSignInProgress = false;
        isSignedIn = true;
        signIn();
    }

    /**
     * Called when {@code googleApiClient} connection is suspended.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended");
        retryConnecting();
    }

    /**
     * Called when {@code googleApiClient} is trying to connect but failed.
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

        if (!isIntentInProgress) {
            // save the connection result for later
            this.connectionResult = result;

            if (isSignInProgress) {
                // the user is being signed in
                resolveSignInError();
            } else {
                // the user is signed out
                navigationDrawerFragment.setSignedOutView();
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
        switch (view.getId()) {
            case R.id.save_score:
                gameFragment.saveAndResetGame();
                break;
            case R.id.exit_game:
                gameFragment.resetGame();
                navigationDrawerFragment.selectItem(menuItemIndex);
                break;
            case R.id.reset_game:
                gameFragment.resetGame();
                break;
        }
        AppUtil.closeDialog(gameExitDialog);
    }

    private boolean isGameInProgress() {
        if (gameFragment == null) {
            return false;
        }

        Game game = gameFragment.getGame();

        return (game != null) ? game.isGameInProgress() : false;
    }

    private void signIn() {
        if (isSignedIn) {
            final Person person = Plus.PeopleApi.getCurrentPerson(googleApiClient);
            final String email = Plus.AccountApi.getAccountName(googleApiClient);

            // Save the id of the logged-in user, so that it can be accessed from other parts of the app
            authCache.setSelectedAccountName(email);
            credential.setSelectedAccountName(email);

            if (person != null) {
                final String url = person.getImage().getUrl();
                final String imageUrl = url.substring(0, url.length() - 2) + PROFILE_IMAGE_SIZE;

                UserProfile userProfile = new UserProfile(person.getDisplayName(), email, url);


                // Set up the sign-in / sign-out menu
                navigationDrawerFragment.setSignedInView(userProfile);
            }

        } else if (!googleApiClient.isConnecting()) {
            isSignInProgress = true;
            googleApiClient.connect();
        }
    }

    private void signOut() {
        if (googleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(googleApiClient);
            googleApiClient.disconnect();
            googleApiClient.connect();
            isSignedIn = false;
        }
    }

    private void resolveSignInError() {
        if (connectionResult != null && connectionResult.hasResolution()) {
            isIntentInProgress = true;
            try {
                connectionResult.startResolutionForResult(this, REQUEST_CODE_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                isIntentInProgress = false;
                if(!googleApiClient.isConnected()) googleApiClient.connect();
            }
        }
    }

    private void showGameExitDialog() {
        gameExitDialog = new GameExitDialog();
        gameExitDialog.setOnClickListener(this);
        gameExitDialog.setCancelable(false);
        gameExitDialog.show(getFragmentManager(), null);
    }

    @Override
    public void onHomeMenuItemSelected(int position) {
        int itemId = R.string.label_home;
        switch (position) {
            case 0:
                itemId = R.string.label_play_single;
                break;
            case 1:
                itemId = R.string.label_stats_score;
                break;
            case 2:
                itemId = R.string.label_stats_rating;
                break;
        }
        // Delegates the action to the sidebar menu
        position = navigationDrawerFragment.getNavigationItemPosition(itemId);
        navigationDrawerFragment.selectItem(position);
    }

    @Override
    public void loadQuizzes() {

        final QuizDataSource db = getDb(QuizDataSource.class);

        List<Quiz> quizzes = db.getAll();

        if (quizzes != null && !quizzes.isEmpty()) {
            gameFragment.setQuizzes(quizzes);
            return;
        }

        new GetQuizzesTask(this, quizzerApi, new ResponseListener<List<Quiz>>() {
            @Override
            public void onSuccess(List<Quiz> data) {
                gameFragment.setQuizzes(data);

                // Cache results
                db.addAll(data);
            }

            @Override
            public void onError(Exception e) {
                handleError(e, "Quizzes couldn't be loaded. Please try again.");
            }
        }).execute();
    }

    @Override
    public void saveGameResult(int round, int score, int powerUps, int oneTimeAttempts, int twoTimeAttempts) {
        SaveGameResultTask task = new SaveGameResultTask(this, quizzerApi, new ResponseListener<GameResult>() {
            @Override
            public void onSuccess(GameResult data) {
                showMessage("Your score has been successfully saved");

                // Cache results
                getDb(GameResultDataSource.class).addOne(data);
            }

            @Override
            public void onError(Exception e) {
                handleError(e, "Your score couldn't be saved");
            }
        });
        task.setScore(score);
        task.setRound(round);
        task.setOneTimeAttempts(oneTimeAttempts);
        task.setTwoTimeAttempts(twoTimeAttempts);
        task.execute();
    }

    @Override
    public void rateQuiz(boolean liked, String quizId) {
        RateQuizTask task = new RateQuizTask(this, quizzerApi, new ResponseListener<QuizRating>() {
            @Override
            public void onSuccess(QuizRating data) {
                showMessage("Thanks for your vote");
            }

            @Override
            public void onError(Exception e) {
                handleError(e, "Your rating couldn't be saved");
            }
        });
        task.setLiked(liked);
        task.setQuizId(quizId);
        task.execute();
    }

    private void initDb() {
        dataSourceMap = new HashMap<>();
        dataSourceMap.put(GameResultDataSource.class, new GameResultStatsDataSource(this));
        dataSourceMap.put(GameResultStatsDataSource.class, new GameResultStatsDataSource(this));
        dataSourceMap.put(QuizDataSource.class, new QuizDataSource(this));
        dataSourceMap.put(QuizRatingDataSource.class, new QuizRatingDataSource(this));
    }

    private void tearDownDb() {
        if (dataSourceMap == null) return;

        for (BaseDataSource dataSource : dataSourceMap.values()) {
            if (dataSource != null) dataSource.close();
        }
        dataSourceMap.clear();
        dataSourceMap = null;
    }

    private<T extends BaseDataSource> T getDb(Class<T> clazz) {
        if (dataSourceMap == null) initDb();
        return (T) dataSourceMap.get(clazz);
    }

    private void handleError(Exception e, String message) {
        Log.e(TAG, message, e);
        showMessage(message);
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loadGameResultsStats() {

        final GameResultStatsDataSource db = getDb(GameResultStatsDataSource.class);

        GameResultStats stats = db.getOne();

        if (stats != null) {
            myScoreFragment.showStats(stats);
            return;
        }

        new GetGameResultStatsTask(this, quizzerApi, new ResponseListener<GameResultStats>() {
            @Override
            public void onSuccess(GameResultStats data) {
                myScoreFragment.showStats(data);

                // Cache results - TODO this should be an update by username
                db.addOne(data);
            }

            @Override
            public void onError(Exception e) {
                handleError(e, "Your score couldn't be loaded. Please try again later.");
            }
        }).execute();
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
