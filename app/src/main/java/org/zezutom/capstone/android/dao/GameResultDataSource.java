package org.zezutom.capstone.android.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import org.zezutom.capstone.android.util.DateTimeUtil;
import zezutom.org.quizzer.model.GameResult;

public class GameResultDataSource extends BaseDataSource<GameResult, GameResultDataHelper> {

    public GameResultDataSource(Context context) {
        super(new GameResultDataHelper(context));
    }

    @Override
    protected ContentValues getValues(GameResult gameResult) {
        ContentValues values = new ContentValues();

        values.put(GameResultDataHelper.COLUMN_CREATED_AT, DateTimeUtil.toString(gameResult.getCreatedAt()));
        values.put(GameResultDataHelper.COLUMN_SCORE, gameResult.getScore());
        values.put(GameResultDataHelper.COLUMN_ROUND, gameResult.getRound());
        values.put(GameResultDataHelper.COLUMN_POWER_UPS, gameResult.getPowerUps());
        values.put(GameResultDataHelper.COLUMN_ROUND_ONE_RATIO, gameResult.getAttemptOneRatio());
        values.put(GameResultDataHelper.COLUMN_ROUND_TWO_RATIO, gameResult.getAttemptTwoRatio());
        values.put(GameResultDataHelper.COLUMN_ROUND_THREE_RATIO, gameResult.getAttemptThreeRatio());
        values.put(GameResultDataHelper.COLUMN_EMAIL, gameResult.getEmail());

        return values;
    }

    @Override
    protected GameResult getEntity(Cursor cursor) {
        GameResult gameResult = new GameResult();

        gameResult.setCreatedAt(DateTimeUtil.toDateTime(cursor.getString(1)));
        gameResult.setScore(cursor.getInt(2));
        gameResult.setRound(cursor.getInt(3));
        gameResult.setPowerUps(cursor.getInt(4));
        gameResult.setAttemptOneRatio(cursor.getInt(5));
        gameResult.setAttemptTwoRatio(cursor.getInt(6));
        gameResult.setAttemptThreeRatio(cursor.getInt(7));
        gameResult.setEmail(cursor.getString(8));

        return gameResult;
    }
}
