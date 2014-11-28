package org.zezutom.capstone.android.fragment;

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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.zezutom.capstone.android.R;
import org.zezutom.capstone.android.adapter.MovieItemAdapter;
import org.zezutom.capstone.android.api.QuizApi;
import org.zezutom.capstone.android.model.SingleGame;
import org.zezutom.capstone.android.util.QuizListener;

import java.util.Arrays;
import java.util.List;

import zezutom.org.quizService.model.Quiz;
import zezutom.org.quizService.model.QuizRating;

public class GameFragment extends Fragment implements QuizListener, AdapterView.OnItemClickListener, View.OnClickListener {

    public static final String NUMBER_FORMAT = "%03d";

    protected static final String GAME_KEY = "single_game";

    protected static final String SCORE_KEY = GAME_KEY + ".score";

    protected static final String ROUND_KEY = GAME_KEY + ".round";

    protected static final String POWER_UPS_KEY = GAME_KEY + ".powerUps";

    protected static final String REMAINING_ATTEMPTS_KEY = GAME_KEY + ".remainingAttempts";

    protected static final String FIRST_TIME_CORRECT_ATTEMPTS = GAME_KEY + ".firstTimeCorrectAttempts";

    private View mainView;

    private TextView scoreView;

    private TextView roundView;

    private TextView powerUpView;

    private TextView attemptView;

    private Button nextQuizButton;

    private ListView moviesView;

    private List<Quiz> quizzes;

    private SingleGame game;

    private FragmentManager fragmentManager;

    private GameSolutionDialog dialog;

    private QuizApi quizApi;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        quizApi = new QuizApi(getActivity(), this);

        mainView = inflater.inflate(R.layout.fragment_single_game, container, false);

        moviesView = (ListView) mainView.findViewById(R.id.list_movies);
        moviesView.setOnItemClickListener(this);

        roundView = (TextView) mainView.findViewById(R.id.round);
        scoreView = (TextView) mainView.findViewById(R.id.score);
        powerUpView = (TextView) mainView.findViewById(R.id.power_ups);
        attemptView = (TextView) mainView.findViewById(R.id.remaining_attempts);

        nextQuizButton = (Button) mainView.findViewById(R.id.next_quiz);
        nextQuizButton.setOnClickListener(this);

        fragmentManager = getFragmentManager();

        loadGame();

        return mainView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (game != null) {
            SharedPreferences sb = getSharedPreferences();
            SharedPreferences.Editor edit = sb.edit();

            if (game.isGameOver()) {
                // Remove all cached entries if the game is over
                edit.remove(SCORE_KEY);
                edit.remove(ROUND_KEY);
                edit.remove(POWER_UPS_KEY);
                edit.remove(REMAINING_ATTEMPTS_KEY);
                edit.remove(FIRST_TIME_CORRECT_ATTEMPTS);
            } else {
                // Save state when the game is in progress
                edit.putInt(SCORE_KEY, game.getScore());
                edit.putInt(ROUND_KEY, game.getRound());
                edit.putInt(POWER_UPS_KEY, game.getPowerUps());
                edit.putInt(REMAINING_ATTEMPTS_KEY, game.getRemainingAttempts());
                edit.putInt(FIRST_TIME_CORRECT_ATTEMPTS, game.getFirstTimeCorrectAttempts());
            }
            edit.commit();
        }
    }

    private void updateUI() {
        scoreView.setText(toString(game.getScore()));
        roundView.setText(toString(game.getRound()));
        powerUpView.setText(toString(game.getPowerUps()));
        attemptView.setText(toString(game.getRemainingAttempts()));

        if (game.getPowerUps() > 0) {
            nextQuizButton.setVisibility(View.VISIBLE);
        } else {
            nextQuizButton.setVisibility(View.GONE);
        }
    }

    private void loadGame() {
        SharedPreferences sb = getSharedPreferences();
        int score = sb.getInt(SCORE_KEY, 0);
        int round = sb.getInt(ROUND_KEY, 1);
        int powerUps = sb.getInt(POWER_UPS_KEY, 0);
        int remainingAttempts = sb.getInt(REMAINING_ATTEMPTS_KEY, 3);
        int firstTimeCorrectAttempts = sb.getInt(FIRST_TIME_CORRECT_ATTEMPTS, 0);

        game = new SingleGame();
        game.setScore(score);
        game.setRound(round);
        game.setPowerUps(powerUps);
        game.setRemainingAttempts(remainingAttempts);
        game.setFirstTimeCorrectAttempts(firstTimeCorrectAttempts);

        if (quizzes != null && quizzes.size() > round) {
            loadQuiz(quizzes.get(round));
        } else {
            quizApi.loadQuizzes();
        }
    }

    private SharedPreferences getSharedPreferences() {
        return getActivity().getSharedPreferences(GAME_KEY, 0);
    }

    private void loadQuiz(Quiz quiz) {
        moviesView.setAdapter(
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
        if (quizzes != null && game.getRound() < quizzes.size() - 1) {
            loadQuiz(quizzes.get(game.nextRound()));
        } else {
            endGame();
        }
    }

    private Quiz getCurrentQuiz() {
        return quizzes.get(game.getRound());
    }

    @Override
    public void onGetAllSuccess(List<Quiz> quizzes) {
        this.quizzes = quizzes;
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

        if (game.isGameOver()) {
            endGame();
            return;
        }

        final Quiz quiz = quizzes.get(game.getRound());

        // Quiz answers are 1-based, whereas the index is 0-based
        if ((i + 1) == quiz.getAnswer()) {
            game.score();
            displayExplanatoryPopup();
            nextQuiz();
        } else {
            game.subtractAttempt();

            if (game.isGameOver()) {
                endGame();
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
               game.subtractPowerUps();
               break;
           case R.id.voteUp:
           case R.id.voteDown:
           case R.id.closeDialog:
                if (viewId != R.id.closeDialog) {
                    rateQuiz(viewId == R.id.voteUp);
                }
                if (dialog != null) {
                    dialog.getDialog().cancel();
                }
               break;
       }
       nextQuiz();
    }

    private void rateQuiz(boolean liked) {
        Quiz quiz = getCurrentQuiz();
        quizApi.rateQuiz(liked, quiz.getId());
        Toast.makeText(this.getActivity(), "Thanks for your vote", Toast.LENGTH_SHORT).show();
    }

    private void displayExplanatoryPopup() {
        dialog = new GameSolutionDialog();
        dialog.setOnClickListener(this);

        Bundle args = new Bundle();
        args.putString("explanation", getCurrentQuiz().getExplanation());
        dialog.setArguments(args);
        dialog.show(fragmentManager, null);
    }



    private void endGame() {
        toast("Game over");
        game = new SingleGame();
        loadQuiz(quizzes.get(0));
    }


}
