package org.zezutom.capstone.android.dao;

import android.content.Context;

public class GameResultStatsDataHelper extends BaseDataHelper {

    public static final String TABLE_NAME = "game_result_stats";

    public static final String COLUMN_SCORE = "score";

    public static final String COLUMN_ROUND = "round";

    public static final String COLUMN_POWER_UPS = "power_ups";

    public static final String COLUMN_ROUND_ONE_RATIO = "round_one_ratio";

    public static final String COLUMN_ROUND_TWO_RATIO = "round_two_ratio";

    public static final String COLUMN_ROUND_THREE_RATIO = "round_three_ratio";

    private String[] columns = {
            COLUMN_ID,
            COLUMN_SCORE,
            COLUMN_ROUND,
            COLUMN_POWER_UPS,
            COLUMN_ROUND_ONE_RATIO,
            COLUMN_ROUND_TWO_RATIO,
            COLUMN_ROUND_THREE_RATIO
    };

    public GameResultStatsDataHelper(Context context) {
        super(context, "game_result_stats.db");
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String[] getColumns() {
        return columns;
    }

    @Override
    protected String dbCreate() {
        return "create table " + TABLE_NAME +
                "(" + COLUMN_ID + " integer primary key autoincrement" +
                "," + COLUMN_SCORE + " integer not null" +
                "," + COLUMN_ROUND + " integer not null" +
                "," + COLUMN_POWER_UPS + " integer not null" +
                "," + COLUMN_ROUND_ONE_RATIO + " integer not null" +
                "," + COLUMN_ROUND_TWO_RATIO + " integer not null" +
                "," + COLUMN_ROUND_THREE_RATIO + " integer not null);";
    }
}
