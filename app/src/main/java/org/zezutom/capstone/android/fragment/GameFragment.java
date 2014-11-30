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
import org.zezutom.capstone.android.api.GameApi;
import org.zezutom.capstone.android.api.GameResultListener;
import org.zezutom.capstone.android.api.QuizApi;
import org.zezutom.capstone.android.api.QuizListener;
import org.zezutom.capstone.android.model.Game;
import org.zezutom.capstone.android.model.GameHistory;
import org.zezutom.capstone.android.util.AppUtil;
import org.zezutom.capstone.android.util.GameCache;

import java.util.Arrays;
import java.util.List;

import zezutom.org.gameService.model.GameResult;
import zezutom.org.quizService.model.Quiz;
import zezutom.org.quizService.model.QuizRating;

public class GameFragment extends Fragment implements QuizListener, GameResultListener,
        AdapterView.OnItemClickListener, View.OnClickListener {

    public static final String GAME_KEY = "game";

    public static final String NUMBER_FORMAT = "%03d";

    private View mainView;

    private TextView scoreView;

    private TextView roundView;

    private TextView powerUpView;

    private TextView attemptView;

    private Button nextQuizButton;

    private ListView moviesView;

    private List<Quiz> quizzes;

    private Game game;

    private FragmentManager fragmentManager;

    private QuizSolutionDialog quizSolutionDialog;

    private QuizApi quizApi;

    private GameApi gameApi;

    private GameCache gameCache;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentManager = getFragmentManager();

        quizApi = new QuizApi(getActivity(), this);
        quizApi.setUp();

        gameApi = new GameApi(getActivity(), this);
        gameApi.setUp();

        gameCache = new GameCache(getSharedPreferences());

        mainView = inflater.inflate(R.layout.fragment_game, container, false);

        moviesView = (ListView) mainView.findViewById(R.id.list_movies);
        moviesView.setOnItemClickListener(this);

        roundView = (TextView) mainView.findViewById(R.id.round);
        scoreView = (TextView) mainView.findViewById(R.id.score);
        powerUpView = (TextView) mainView.findViewById(R.id.power_ups);
        attemptView = (TextView) mainView.findViewById(R.id.remaining_attempts);

        nextQuizButton = (Button) mainView.findViewById(R.id.next_quiz);
        nextQuizButton.setOnClickListener(this);

        loadGame();

        return mainView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
        gameApi.tearDown();
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
        game = gameCache.loadGame();
        int round = game.getRound();
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

    @Override
    public void onSaveGameResult(GameResult gameResult) {
        // TODO
    }

    @Override
    public void onSaveGameError(Exception ex) {
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
            showQuizSolutionDialog();
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
           case R.id.vote_up:
           case R.id.vote_down:
           case R.id.close_dialog:
                if (viewId != R.id.close_dialog) {
                    rateQuiz(viewId == R.id.vote_up);
                }
               AppUtil.closeDialog(quizSolutionDialog);
               break;
            case R.id.save_score:
                final GameHistory gameHistory = game.getHistory();
                gameApi.saveGameResult(game.getRound(), game.getScore(),
                        gameHistory.getPowerUps(),
                        gameHistory.getOneTimeAttempts(),
                        gameHistory.getTwoTimeAttempts());
                break;
       }
       nextQuiz();
    }

    private void rateQuiz(boolean liked) {
        Quiz quiz = getCurrentQuiz();
        quizApi.rateQuiz(liked, quiz.getId());
        Toast.makeText(this.getActivity(), "Thanks for your vote", Toast.LENGTH_SHORT).show();
    }

    private void showQuizSolutionDialog() {
        quizSolutionDialog = new QuizSolutionDialog();
        quizSolutionDialog.setOnClickListener(this);

        Bundle args = new Bundle();
        args.putString("explanation", getCurrentQuiz().getExplanation());
        quizSolutionDialog.setArguments(args);
        quizSolutionDialog.show(fragmentManager, null);
    }

    public void endGame() {
        toast("Game over");
        gameCache.clearCache();
        game = new Game();
        loadQuiz(quizzes.get(0));
    }
}
