package org.zezutom.capstone.android.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.zezutom.capstone.android.MainActivity;
import org.zezutom.capstone.android.R;
import org.zezutom.capstone.android.adapter.OptionItemAdapter;
import org.zezutom.capstone.android.model.Game;
import org.zezutom.capstone.android.model.GameHistory;
import org.zezutom.capstone.android.util.AppUtil;
import org.zezutom.capstone.android.util.GameCache;
import org.zezutom.capstone.android.util.UIHelper;

import java.util.Arrays;
import java.util.List;

import zezutom.org.quizzer.model.Quiz;

public class GameFragment extends Fragment implements
        AdapterView.OnItemClickListener, View.OnClickListener {

    public static final String TAG = "GameFragment";

    private List<Quiz> quizzes;

    private Quiz currentQuiz;

    private Game game;

    private QuizSolutionDialog quizSolutionDialog;

    private GameCache gameCache;

    private UIHelper uiHelper;

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private GameCallbacks callbacks;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Enables menu
        setHasOptionsMenu(true);

        gameCache = new GameCache(getActivity());

        final View mainView = inflater.inflate(R.layout.fragment_game, container, false);
        uiHelper = new UIHelper(mainView);
        uiHelper.addView(R.id.quiz_list)
                .addView(R.id.question)
                .addView(R.id.round)
                .addView(R.id.score)
                .addView(R.id.power_ups_container)
                .addView(R.id.power_ups)
                .addView(R.id.attempt_one)
                .addView(R.id.attempt_two)
                .addView(R.id.attempt_three)
                .addView(R.id.next_quiz)
                .addView(R.id.category);

        GridView quizView = uiHelper.getView(R.id.quiz_list);
        quizView.setOnItemClickListener(this);

        Button nextQuizButton = uiHelper.getView(R.id.next_quiz);
        nextQuizButton.setOnClickListener(this);

        loadGame();

        return mainView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            callbacks = (GameCallbacks) activity;
            callbacks.setGameFragment(this);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement GameCallbacks.");
        }
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
                gameCache.deleteGame();
            } else {
                // Save state when the game is in progress
                gameCache.saveGame(game);
            }
        }
        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.game, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return ((MainActivity) getActivity()).onMenuItemSelected(item);
    }


    public void saveAndResetGame() {
        GameHistory history = game.getHistory();
        callbacks.saveGameResult(game.getRound(), game.getScore(), game.getPowerUps(),
                history.getOneTimeAttempts(), history.getTwoTimeAttempts());
        resetGame();
    }

    public void  setQuizzes(List<Quiz> quizzes) {
        this.quizzes = quizzes;
        loadGame();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        if (game.isGameOver()) {
            saveAndResetGame();
            return;
        }

        // Quiz answers are 1-based, whereas the index is 0-based
        if ((i + 1) == currentQuiz.getAnswer()) {
            game.score();
            showQuizSolutionDialog();
        } else {
            game.subtractAttempt();

            if (game.isGameOver()) {
                saveAndResetGame();
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

    public Game getGame() {
        return game;
    }

    public void resetGame() {
        gameCache.deleteGame();
        game = null;
        loadGame();
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

        scoreView.setText(AppUtil.numberToString(game.getScore()));
        roundView.setText(AppUtil.numberToString(game.getRound()));

        final int powerUps = game.getPowerUps();
        if (powerUps > 0) {
            powerUpsView.setText(AppUtil.numberToString(powerUps));
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
            callbacks.loadQuizzes();
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

    private void loadQuiz(Quiz quiz) {
        currentQuiz = quiz;

        loadQuizDialog(quiz.getExplanation());

        ImageView imageView = uiHelper.getView(R.id.category);
        TextView questionView = uiHelper.getView(R.id.question);
        GridView quizView = uiHelper.getView(R.id.quiz_list);

        imageView.setImageResource(getCategoryImage(quiz.getCategory()));
        questionView.setText(quiz.getQuestion());
        quizView.setAdapter(
                new OptionItemAdapter(getActivity(),
                        Arrays.asList(
                                quiz.getOptionOne(),
                                quiz.getOptionTwo(),
                                quiz.getOptionThree(),
                                quiz.getOptionFour()
                        )));
        updateGameUI();
    }

    private int getCategoryImage(String category) {

        int imageId = R.drawable.ic_launcher;

        if (category == null) return imageId;

        switch (category) {
            case "JAVA":
                imageId = R.drawable.ic_java;
                break;
            case "JAVASCRIPT":
                imageId = R.drawable.ic_javascript;
                break;
            case "HTML5":
                imageId = R.drawable.ic_html5;
                break;
        }
        return imageId;
    }

    private void nextQuiz() {
        if (game.isGameOver()) {
            saveAndResetGame();
        } else if (quizzes != null && game.getRound() < quizzes.size()) {
            loadQuiz(quizzes.get(game.nextRound() - 1));
        } else {
            resetGame();
        }
    }

    private void toast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    private void rateQuiz(boolean liked) {
        callbacks.rateQuiz(liked, currentQuiz.getId());
    }

    private void showQuizSolutionDialog() {
        if (quizSolutionDialog == null) loadQuizDialog(currentQuiz.getExplanation());
        quizSolutionDialog.show(getFragmentManager(), null);

    }

    private void loadQuizDialog(String explanation) {
        quizSolutionDialog = new QuizSolutionDialog();
        quizSolutionDialog.setOnClickListener(this);

        Bundle args = new Bundle();
        args.putString("explanation", currentQuiz.getExplanation());
        quizSolutionDialog.setArguments(args);
        quizSolutionDialog.preLoad(explanation, getActivity().getLayoutInflater(), getResources());
    }


    /**
     *  Callbacks interface allowing to handle game data asynchronously.
     */
    public static interface GameCallbacks {

        void loadQuizzes();

        void saveGameResult(int round, int score, int powerUps, int oneTimeAttempts, int twoTimeAttempts);

        void rateQuiz(boolean liked, String quizId);

        void setGameFragment(GameFragment gameFragment);
    }
}
