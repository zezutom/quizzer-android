package org.zezutom.capstone.android.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import org.zezutom.capstone.android.R;
import org.zezutom.capstone.android.adapter.OptionItemAdapter;
import org.zezutom.capstone.android.api.QuizApi;
import org.zezutom.capstone.android.api.QuizListener;
import org.zezutom.capstone.android.model.Game;
import org.zezutom.capstone.android.util.AppUtil;
import org.zezutom.capstone.android.util.GameCache;
import org.zezutom.capstone.android.util.UIHelper;

import java.util.Arrays;
import java.util.List;

import zezutom.org.quizService.model.Quiz;
import zezutom.org.quizService.model.QuizRating;

public class GameFragment extends Fragment implements QuizListener,
        AdapterView.OnItemClickListener, View.OnClickListener {

    public static final String GAME_KEY = "game";

    public static final String NUMBER_FORMAT = "%01d";

    private List<Quiz> quizzes;

    private Quiz currentQuiz;

    private Game game;

    private QuizSolutionDialog quizSolutionDialog;

    private QuizApi quizApi;

    private GameCache gameCache;

    private UIHelper uiHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final Activity activity = getActivity();
        quizApi = new QuizApi(activity, this);
        quizApi.setUp();

        gameCache = new GameCache(getSharedPreferences());

        final View mainView = inflater.inflate(R.layout.fragment_game, container, false);
        uiHelper = new UIHelper(mainView);
        uiHelper.addView(R.id.list_movies)
                .addView(R.id.question)
                .addView(R.id.round)
                .addView(R.id.score)
                .addView(R.id.power_ups_container)
                .addView(R.id.power_ups)
                .addView(R.id.attempt_one)
                .addView(R.id.attempt_two)
                .addView(R.id.attempt_three)
                .addView(R.id.next_quiz);

        GridView moviesView = uiHelper.getView(R.id.list_movies);
        moviesView.setOnItemClickListener(this);

        Button nextQuizButton = uiHelper.getView(R.id.next_quiz);
        nextQuizButton.setOnClickListener(this);

        loadGame();

        return mainView;
    }

    @Override
    public void onPause() {
        if (quizSolutionDialog != null && quizSolutionDialog.isVisible()) {
            AppUtil.closeDialog(quizSolutionDialog);
            game.nextRound();
        }
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        if (game != null) {
            if (game.isGameOver()) {
                // Remove all cached entries if the game is over
                gameCache.clearCache();
            } else {
                // Save state when the game is in progress
                gameCache.saveGame(game);
            }
        }
        quizApi.tearDown();
        super.onDestroyView();
    }

    private void updateGameUI() {
        TextView scoreView = uiHelper.getView(R.id.score);
        TextView roundView = uiHelper.getView(R.id.round);
        TextView powerUpsView = uiHelper.getView(R.id.power_ups);

        View powerUpsContainerView = uiHelper.getView(R.id.power_ups_container);
        View attemptOneView = uiHelper.getView(R.id.attempt_one);
        View attemptTwoView = uiHelper.getView(R.id.attempt_two);
        View attemptThreeView = uiHelper.getView(R.id.attempt_three);

        Button nextQuizButton = uiHelper.getView(R.id.next_quiz);

        scoreView.setText(toString(game.getScore()));
        roundView.setText(toString(game.getRound()));

        final int powerUps = game.getPowerUps();
        if (powerUps > 0) {
            powerUpsView.setText(toString(powerUps));
            powerUpsContainerView.setVisibility(View.VISIBLE);
        } else {
            powerUpsContainerView.setVisibility(View.INVISIBLE);
        }

        final int remainingAttempts = game.getRemainingAttempts();
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
        attemptOneView.setVisibility(attemptOneVisibility);
        attemptTwoView.setVisibility(attemptTwoVisibility);
        attemptThreeView.setVisibility(attemptThreeVisibility);

        if (powerUps > 0) {
            nextQuizButton.setVisibility(View.VISIBLE);
        } else {
            nextQuizButton.setVisibility(View.INVISIBLE);
        }
    }

    private void loadGame() {

        if (quizzes == null || quizzes.isEmpty()) {
            quizApi.loadQuizzes();
            return;
        }

        game = gameCache.loadGame();

        if (game == null || game.isGameOver()) {
            loadQuiz(quizzes.get(0));
            return;
        }

        final int round = game.getRound();

        if (round >= quizzes.size()) {
            resetGame();
        } else {
            loadQuiz(quizzes.get(round == 0 ? round : round - 1));
        }

    }

    private SharedPreferences getSharedPreferences() {
        return getActivity().getSharedPreferences(GAME_KEY, 0);
    }

    private void loadQuiz(Quiz quiz) {
        currentQuiz = quiz;

        TextView questionView = uiHelper.getView(R.id.question);
        GridView moviesView = uiHelper.getView(R.id.list_movies);

        questionView.setText(quiz.getQuestion());
        moviesView.setAdapter(
                new OptionItemAdapter(getActivity(),
                        Arrays.asList(
                                quiz.getOptionOne(),
                                quiz.getOptionTwo(),
                                quiz.getOptionThree(),
                                quiz.getOptionFour()
                        )));
        updateGameUI();
    }

    private void nextQuiz() {
        if (quizzes != null && game.getRound() < quizzes.size()) {
            loadQuiz(quizzes.get(game.nextRound() - 1));
        } else {
            resetGame();
        }
    }

    @Override
    public void onGetAllSuccess(List<Quiz> quizzes) {
        this.quizzes = quizzes;
        loadGame();
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
            resetGame();
            return;
        }

        // Quiz answers are 1-based, whereas the index is 0-based
        if ((i + 1) == currentQuiz.getAnswer()) {
            game.score();
            showQuizSolutionDialog();
        } else {
            game.subtractAttempt();

            if (game.isGameOver()) {
                resetGame();
            } else {
                updateGameUI();
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
            case R.id.vote_up:
            case R.id.vote_down:
            case R.id.close_dialog:
                if (viewId != R.id.close_dialog) {
                    rateQuiz(viewId == R.id.vote_up);
                }
                AppUtil.closeDialog(quizSolutionDialog);
                break;
        }
        nextQuiz();
    }

    private void rateQuiz(boolean liked) {
        quizApi.rateQuiz(liked, currentQuiz.getId());
        Toast.makeText(this.getActivity(), "Thanks for your vote", Toast.LENGTH_SHORT).show();
    }

    private void showQuizSolutionDialog() {
        quizSolutionDialog = new QuizSolutionDialog();
        quizSolutionDialog.setOnClickListener(this);

        Bundle args = new Bundle();
        args.putString("explanation", currentQuiz.getExplanation());
        quizSolutionDialog.setArguments(args);
        quizSolutionDialog.show(getFragmentManager(), null);
    }

    public Game getGame() {
        return game;
    }

    public void resetGame() {
        gameCache.clearCache();
        game = null;
        loadGame();
    }
}
