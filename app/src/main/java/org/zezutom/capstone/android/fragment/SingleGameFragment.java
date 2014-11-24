package org.zezutom.capstone.android.fragment;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.zezutom.capstone.android.R;
import org.zezutom.capstone.android.adapter.MovieItemAdapter;
import org.zezutom.capstone.android.model.SingleGame;
import org.zezutom.capstone.android.util.QuizLoadListener;
import org.zezutom.capstone.android.util.QuizProvider;

import java.util.Arrays;
import java.util.List;

import zezutom.org.quizService.model.Quiz;

public class SingleGameFragment extends Fragment implements QuizLoadListener {

    public static final String NUMBER_FORMAT = "%03d";

    protected static final String GAME_KEY = "single_game";

    protected static final String SCORE_KEY = GAME_KEY + ".score";

    protected static final String ROUND_KEY = GAME_KEY + ".round";

    protected static final String POWER_UPS_KEY = GAME_KEY + ".powerUps";

    protected static final String QUIZ_ID_KEY = GAME_KEY + ".quizId";

    private View mainView;

    private TextView scoreView;

    private TextView roundView;

    private ListView moviesView;

    private List<Quiz> quizzes;

    private SingleGame game;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_single_game, container, false);
        moviesView = (ListView) mainView.findViewById(R.id.list_movies);

        game = loadGame();

        scoreView = (TextView) mainView.findViewById(R.id.score);
        scoreView.setText(toString(game.getScore()));

        roundView = (TextView) mainView.findViewById(R.id.round);
        roundView.setText(toString(game.getRound()));

        return mainView;
    }

    private SingleGame loadGame() {
        SharedPreferences sb = getActivity().getSharedPreferences(GAME_KEY, 0);
        int score = sb.getInt(SCORE_KEY, 0);
        int round = sb.getInt(ROUND_KEY, 0);
        int powerUps = sb.getInt(POWER_UPS_KEY, 0);

        SingleGame game = new SingleGame();
        game.setScore(score);
        game.setRound(round);
        game.setPowerUps(powerUps);

        if (quizzes != null && quizzes.size() > round) {
            loadQuiz(quizzes.get(round));
        } else {
            QuizProvider quizProvider = new QuizProvider(getActivity(), this);
            quizProvider.loadQuizzes();
        }
        return game;
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

    }

    private void nextQuiz() {
        if (quizzes != null && game.getRound() < quizzes.size() - 1) {
            loadQuiz(quizzes.get(game.nextRound()));
            roundView.setText(toString(game.getRound()));
        }
    }
    @Override
    public void onSuccess(List<Quiz> quizzes) {
        this.quizzes = quizzes;
        loadQuiz(quizzes.get(0));
    }

    @Override
    public void onError() {

    }

    private String toString(int value) {
        return String.format(NUMBER_FORMAT, value);
    }
}
