package org.zezutom.capstone.android.dao;

import android.content.Context;

public class QuizDataHelper extends BaseDataHelper {

    public static final String TABLE_NAME = "quizzes";

    public static final String COLUMN_QUIZ_ID = "quiz_id";

    public static final String COLUMN_TITLE = "title";

    public static final String COLUMN_EXPLANATION = "explanation";

    public static final String COLUMN_ANSWER = "answer";

    public static final String COLUMN_MOVIE_1 = "movie_1";

    public static final String COLUMN_MOVIE_2 = "movie_2";

    public static final String COLUMN_MOVIE_3 = "movie_3";

    public static final String COLUMN_MOVIE_4 = "movie_4";

    public static final String COLUMN_DIFFICULTY = "difficulty";

    public QuizDataHelper(Context context) {
        super(context, "quizzes.db");
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
                "," + COLUMN_TITLE + " text not null" +
                "," + COLUMN_EXPLANATION + " text not null" +
                "," + COLUMN_ANSWER + " integer not null" +
                "," + COLUMN_MOVIE_1 + " text not null" +
                "," + COLUMN_MOVIE_2 + " text not null" +
                "," + COLUMN_MOVIE_3 + " text not null" +
                "," + COLUMN_MOVIE_4 + " text not null" +
                "," + COLUMN_DIFFICULTY + " integer not null);";
    }
}
