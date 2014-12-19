package org.zezutom.capstone.android.util;

import android.content.Context;
import android.content.SharedPreferences;
import org.zezutom.capstone.android.model.Game;
import org.zezutom.capstone.android.model.GameHistory;

public class GameCache {

    public static final String GAME_KEY = "game";

    protected static final String GAME_HISTORY_KEY = GAME_KEY + ".history";

    protected static final String SCORE_KEY = GAME_KEY + ".score";

    protected static final String ROUND_KEY = GAME_KEY + ".round";

    protected static final String POWER_UPS_KEY = GAME_KEY + ".powerUps";

    protected static final String REMAINING_ATTEMPTS_KEY = GAME_KEY + ".remainingAttempts";

    protected static final String ONE_TIME_CORRECT_ATTEMPTS = GAME_KEY + ".oneTimeCorrectAttempts";

    protected static final String POWER_UPS_TOTAL = GAME_HISTORY_KEY + ".powerUps";

    protected static final String ONE_TIME_CORRECT_ATTEMPTS_TOTAL = GAME_HISTORY_KEY + ".oneTimeCorrectAttempts";

    protected static final String TWO_TIME_CORRECT_ATTEMPTS_TOTAL = GAME_HISTORY_KEY + ".twoTimeCorrectAttempts";


    private SharedPreferences sharedPreferences;

    public GameCache(Context context) {
        this.sharedPreferences = context.getSharedPreferences(GAME_KEY, 0);
    }

    public void saveGame(Game game) {
        SharedPreferences.Editor edit = sharedPreferences.edit();

        edit.putInt(SCORE_KEY, game.getScore());
        edit.putInt(ROUND_KEY, game.getRound());
        edit.putInt(POWER_UPS_KEY, game.getPowerUps());
        edit.putInt(REMAINING_ATTEMPTS_KEY, game.getRemainingAttempts());
        edit.putInt(ONE_TIME_CORRECT_ATTEMPTS, game.getOneTimeCorrectAttempts());

        GameHistory history = game.getHistory();
        edit.putInt(POWER_UPS_TOTAL, history.getPowerUps());
        edit.putInt(ONE_TIME_CORRECT_ATTEMPTS_TOTAL, history.getOneTimeAttempts());
        edit.putInt(TWO_TIME_CORRECT_ATTEMPTS_TOTAL, history.getTwoTimeAttempts());

        edit.commit();
    }

    public Game loadGame() {
        int score = sharedPreferences.getInt(SCORE_KEY, 0);
        int round = sharedPreferences.getInt(ROUND_KEY, Game.FIRST_ROUND);
        int powerUps = sharedPreferences.getInt(POWER_UPS_KEY, 0);
        int remainingAttempts = sharedPreferences.getInt(REMAINING_ATTEMPTS_KEY, Game.MAX_ATTEMPTS);
        int oneTimeCorrectAttempts = sharedPreferences.getInt(ONE_TIME_CORRECT_ATTEMPTS, 0);

        Game game = new Game();
        game.setScore(score);
        game.setRound(round);
        game.setPowerUps(powerUps);
        game.setRemainingAttempts(remainingAttempts);
        game.setOneTimeCorrectAttempts(oneTimeCorrectAttempts);

        int powerUpsTotal = sharedPreferences.getInt(POWER_UPS_TOTAL, 0);
        int oneTimeAttemptsTotal = sharedPreferences.getInt(ONE_TIME_CORRECT_ATTEMPTS_TOTAL, 0);
        int twoTimeAttemptsTotal = sharedPreferences.getInt(TWO_TIME_CORRECT_ATTEMPTS_TOTAL, 0);

        GameHistory history = game.getHistory();
        history.setPowerUps(powerUpsTotal);
        history.setOneTimeAttempts(oneTimeAttemptsTotal);
        history.setTwoTimeAttempts(twoTimeAttemptsTotal);

        return game;
    }

    public void deleteGame() {
        SharedPreferences.Editor edit = sharedPreferences.edit();

        edit.remove(SCORE_KEY);
        edit.remove(ROUND_KEY);
        edit.remove(POWER_UPS_KEY);
        edit.remove(REMAINING_ATTEMPTS_KEY);
        edit.remove(ONE_TIME_CORRECT_ATTEMPTS);
        edit.remove(POWER_UPS_TOTAL);
        edit.remove(ONE_TIME_CORRECT_ATTEMPTS_TOTAL);
        edit.remove(TWO_TIME_CORRECT_ATTEMPTS_TOTAL);

        edit.commit();
    }
}
