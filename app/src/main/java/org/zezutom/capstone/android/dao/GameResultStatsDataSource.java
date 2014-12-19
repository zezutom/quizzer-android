package org.zezutom.capstone.android.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import java.util.List;
import zezutom.org.quizzer.model.GameResultStats;

public class GameResultStatsDataSource extends BaseDataSource<GameResultStats, GameResultStatsDataHelper> {

    public GameResultStatsDataSource(Context context) {
        super(new GameResultStatsDataHelper(context));
    }

    @Override
    protected ContentValues getValues(GameResultStats gameResult) {
        ContentValues values = new ContentValues();

        values.put(GameResultDataHelper.COLUMN_SCORE, gameResult.getScore());
        values.put(GameResultDataHelper.COLUMN_ROUND, gameResult.getRound());
        values.put(GameResultDataHelper.COLUMN_POWER_UPS, gameResult.getPowerUps());
        values.put(GameResultDataHelper.COLUMN_ROUND_ONE_RATIO, gameResult.getAttemptOneRatio());
        values.put(GameResultDataHelper.COLUMN_ROUND_TWO_RATIO, gameResult.getAttemptTwoRatio());
        values.put(GameResultDataHelper.COLUMN_ROUND_THREE_RATIO, gameResult.getAttemptThreeRatio());

        return values;
    }

    @Override
    protected GameResultStats getEntity(Cursor cursor) {
        GameResultStats stats = new GameResultStats();

        stats.setScore(cursor.getInt(1));
        stats.setRound(cursor.getInt(2));
        stats.setPowerUps(cursor.getInt(3));
        stats.setAttemptOneRatio(cursor.getInt(4));
        stats.setAttemptTwoRatio(cursor.getInt(5));
        stats.setAttemptThreeRatio(cursor.getInt(6));

        return stats;
    }

    public GameResultStats getOne() {
        final List<GameResultStats> statsList = getAll();

        return (statsList == null || statsList.isEmpty()) ? null : statsList.get(0);
    }
}
