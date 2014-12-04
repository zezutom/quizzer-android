package org.zezutom.capstone.android.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.zezutom.capstone.android.R;
import org.zezutom.capstone.android.adapter.MovieItemAdapter;
import org.zezutom.capstone.android.api.QuizApi;
import org.zezutom.capstone.android.api.QuizListener;
import org.zezutom.capstone.android.model.Game;
import org.zezutom.capstone.android.util.AppUtil;
import org.zezutom.capstone.android.util.GameCache;

import java.util.Arrays;
import java.util.List;

import zezutom.org.quizService.model.Quiz;
import zezutom.org.quizService.model.QuizRating;

public class GameFragment extends Fragment implements QuizListener,
        AdapterView.OnItemClickListener, View.OnClickListener {

    public static final String GAME_KEY = "mGame";

    public static final String NUMBER_FORMAT = "%01d";

    private View mMainView;

    private TextView mScoreView;

    private TextView mRoundView;

    private TextView mPowerUpView;

    private ImageView mAttemptViewOne;

    private ImageView mAttemptViewTwo;

    private ImageView mAttemptViewThree;

    private Button mNextQuizButton;

    private GridView mMoviesView;

    private List<Quiz> mQuizzes;

    private Game mGame;

    private FragmentManager mFragmentManager;

    private QuizSolutionDialog mQuizSolutionDialog;

    private QuizApi mQuizApi;

    private GameCache mGameCache;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragmentManager = getFragmentManager();

        final Activity activity = getActivity();
        mQuizApi = new QuizApi(activity, this);
        mQuizApi.setUp();

        mGameCache = new GameCache(getSharedPreferences());

        mMainView = inflater.inflate(R.layout.fragment_game, container, false);

        mMoviesView = (GridView) mMainView.findViewById(R.id.list_movies);
        mMoviesView.setOnItemClickListener(this);

        mRoundView = (TextView) mMainView.findViewById(R.id.round);
        mScoreView = (TextView) mMainView.findViewById(R.id.score);
        mPowerUpView = (TextView) mMainView.findViewById(R.id.power_ups);
        mAttemptViewOne = (ImageView) mMainView.findViewById(R.id.attempt_one);
        mAttemptViewTwo = (ImageView) mMainView.findViewById(R.id.attempt_two);
        mAttemptViewThree = (ImageView) mMainView.findViewById(R.id.attempt_three);

        mNextQuizButton = (Button) mMainView.findViewById(R.id.next_quiz);
        mNextQuizButton.setOnClickListener(this);

        loadGame();

        return mMainView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mGame != null) {
            if (mGame.isGameOver()) {
                // Remove all cached entries if the mGame is over
                mGameCache.clearCache();
            } else {
                // Save state when the mGame is in progress
                mGameCache.saveGame(mGame);
            }
        }
        mQuizApi.tearDown();
    }

    private void updateUI() {
        mScoreView.setText(toString(mGame.getScore()));
        mRoundView.setText(toString(mGame.getRound()));


        final int powerUps = mGame.getPowerUps();
        if (powerUps > 0) {
            mPowerUpView.setText(toString(powerUps));
            mPowerUpView.setVisibility(View.VISIBLE);
        } else {
            mPowerUpView.setVisibility(View.INVISIBLE);
        }

        final int remainingAttempts = mGame.getRemainingAttempts();
        int attemptOneVisibility, attemptTwoVisibility, attemptThreeVisibility;
        attemptOneVisibility = attemptTwoVisibility = attemptThreeVisibility = View.INVISIBLE;

        switch (remainingAttempts) {
            case 3:
                attemptOneVisibility = attemptTwoVisibility = attemptThreeVisibility = View.VISIBLE;
                break;
            case 2:
                attemptOneVisibility = attemptTwoVisibility = View.VISIBLE;
                break;
            case 1:
                attemptOneVisibility = View.VISIBLE;
                break;
        }
        mAttemptViewOne.setVisibility(attemptOneVisibility);
        mAttemptViewTwo.setVisibility(attemptTwoVisibility);
        mAttemptViewThree.setVisibility(attemptThreeVisibility);

        if (powerUps > 0) {
            mNextQuizButton.setVisibility(View.VISIBLE);
        } else {
            mNextQuizButton.setVisibility(View.INVISIBLE);
        }
    }

    private void loadGame() {
        mGame = mGameCache.loadGame();
        int round = mGame.getRound();
        if (mQuizzes != null && mQuizzes.size() > round) {
            loadQuiz(mQuizzes.get(round));
        } else {
            mQuizApi.loadQuizzes();
        }
    }

    private SharedPreferences getSharedPreferences() {
        return getActivity().getSharedPreferences(GAME_KEY, 0);
    }

    private void loadQuiz(Quiz quiz) {
        mMoviesView.setAdapter(
                new MovieItemAdapter(getActivity(),
                        Arrays.asList(
                                quiz.getMovieOne(),
                                quiz.getMovieTwo(),
                                quiz.getMovieThree(),
                                quiz.getMovieFour()
                        )));
        updateUI();
    }

    private void nextQuiz() {
        if (mQuizzes != null && mGame.getRound() < mQuizzes.size() - 1) {
            loadQuiz(mQuizzes.get(mGame.nextRound()));
        } else {
            resetGame();
        }
    }

    private Quiz getCurrentQuiz() {
        return mQuizzes.get(mGame.getRound());
    }

    @Override
    public void onGetAllSuccess(List<Quiz> quizzes) {
        this.mQuizzes = quizzes;
        loadQuiz(quizzes.get(0));
    }

    @Override
    public void onGetAllError(Exception ex) {
        // TODO
    }

    @Override
    public void onRateSuccess(QuizRating quizRating) {
        // TODO persist the rating
    }

    @Override
    public void onRateError(Exception ex) {
        // TODO
    }

    private String toString(int value) {
        return String.format(NUMBER_FORMAT, value);
    }

    private void toast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        if (mGame.isGameOver()) {
            resetGame();
            return;
        }

        final Quiz quiz = mQuizzes.get(mGame.getRound());

        // Quiz answers are 1-based, whereas the index is 0-based
        if ((i + 1) == quiz.getAnswer()) {
            mGame.score();
            showQuizSolutionDialog();
        } else {
            mGame.subtractAttempt();

            if (mGame.isGameOver()) {
                resetGame();
            } else {
                updateUI();
                toast("Please try again");
            }
        }

    }

    @Override
    public void onClick(View view) {
        final int viewId = view.getId();
        switch (viewId) {
           case R.id.next_quiz:
               mGame.subtractPowerUps();
               break;
           case R.id.vote_up:
           case R.id.vote_down:
           case R.id.close_dialog:
                if (viewId != R.id.close_dialog) {
                    rateQuiz(viewId == R.id.vote_up);
                }
               AppUtil.closeDialog(mQuizSolutionDialog);
               break;
       }
       nextQuiz();
    }

    private void rateQuiz(boolean liked) {
        Quiz quiz = getCurrentQuiz();
        mQuizApi.rateQuiz(liked, quiz.getId());
        Toast.makeText(this.getActivity(), "Thanks for your vote", Toast.LENGTH_SHORT).show();
    }

    private void showQuizSolutionDialog() {
        mQuizSolutionDialog = new QuizSolutionDialog();
        mQuizSolutionDialog.setOnClickListener(this);

        Bundle args = new Bundle();
        args.putString("explanation", getCurrentQuiz().getExplanation());
        mQuizSolutionDialog.setArguments(args);
        mQuizSolutionDialog.show(mFragmentManager, null);
    }

    public Game getGame() {
        return mGame;
    }

    public void resetGame() {
        mGameCache.clearCache();
        mGame = new Game();
        loadQuiz(mQuizzes.get(0));
    }
}
