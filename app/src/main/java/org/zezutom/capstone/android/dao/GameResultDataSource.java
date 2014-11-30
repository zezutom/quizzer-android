package org.zezutom.capstone.android.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.zezutom.capstone.android.util.DateTimeUtil;

import zezutom.org.gameService.model.GameResult;

public class GameResultDataSource extends BaseDataSource<GameResult, GameResultDataHelper> {

    public GameResultDataSource(Context context) {
        super(new GameResultDataHelper(context));
    }

    @Override
    protected ContentValues getValues(GameResult gameResult) {
        ContentValues values = new ContentValues();

        values.put(GameResultDataHelper.COLUMN_ID, gameResult.getId());
        values.put(GameResultDataHelper.COLUMN_CREATED_AT, DateTimeUtil.toString(gameResult.getCreatedAt()));
        values.put(GameResultDataHelper.COLUMN_ROUND, gameResult.getRound());
        values.put(GameResultDataHelper.COLUMN_SCORE, gameResult.getScore());
        values.put(GameResultDataHelper.COLUMN_POWER_UPS, gameResult.getPowerUps());
        values.put(GameResultDataHelper.COLUMN_ROUND_ONE_RATIO, gameResult.getAttemptOneRatio());
        values.put(GameResultDataHelper.COLUMN_ROUND_TWO_RATIO, gameResult.getAttemptTwoRatio());
        values.put(GameResultDataHelper.COLUMN_ROUND_THREE_RATIO, gameResult.getAttemptThreeRatio());

        return values;
    }

    @Override
    protected GameResult getEntity(Cursor cursor) {
        GameResult gameResult = new GameResult();

        gameResult.setId(cursor.getString(1));
        gameResult.setCreatedAt(DateTimeUtil.toDateTime(cursor.getString(2)));
        gameResult.setRound(cursor.getInt(3));
        gameResult.setScore(cursor.getInt(4));
        gameResult.setPowerUps(cursor.getInt(5));
        gameResult.setAttemptOneRatio(cursor.getInt(6));
        gameResult.setAttemptTwoRatio(cursor.getInt(7));
        gameResult.setAttemptThreeRatio(cursor.getInt(8));

        return gameResult;
    }
}
