package org.zezutom.capstone.android.dao;

import android.content.Context;

public class QuizRatingDataHelper extends BaseDataHelper {

    public static final String TABLE_NAME = "quiz_ratings";

    public static final String COLUMN_QUIZ_ID = "quiz_id";

    public static final String COLUMN_QUIZ_LIKED = "liked";

    public QuizRatingDataHelper(Context context) {
        super(context, "quiz_ratings.db");
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected String dbCreate() {
        return "create table " + TABLE_NAME +
                "(" + COLUMN_ID + " integer primary key autoincrement" +
                "," + COLUMN_QUIZ_ID + " text not null" +
                "," + COLUMN_CREATED_AT + " text not null" +
                "," + COLUMN_QUIZ_LIKED + " integer not null);";
    }
}
