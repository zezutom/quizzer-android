package org.zezutom.capstone.android.dao;

import android.content.Context;

public class QuizDataHelper extends BaseDataHelper {

    public static final String TABLE_NAME = "quizzes";

    public static final String COLUMN_QUIZ_ID = "quiz_id";

    public static final String COLUMN_CATEGORY = "category";

    public static final String COLUMN_DIFFICULTY = "difficulty";

    public static final String COLUMN_QUESTION = "question";

    public static final String COLUMN_ANSWER = "answer";

    public static final String COLUMN_EXPLANATION = "explanation";

    public static final String COLUMN_OPTION_1 = "option_1";

    public static final String COLUMN_OPTION_2 = "option_2";

    public static final String COLUMN_OPTION_3 = "option_3";

    public static final String COLUMN_OPTION_4 = "option_4";


    private String[] columns = {
            COLUMN_ID,
            COLUMN_QUIZ_ID,
            COLUMN_CATEGORY,
            COLUMN_DIFFICULTY,
            COLUMN_QUESTION,
            COLUMN_ANSWER,
            COLUMN_OPTION_1,
            COLUMN_OPTION_2,
            COLUMN_OPTION_3,
            COLUMN_OPTION_4,
            COLUMN_EXPLANATION
    };

    public QuizDataHelper(Context context) {
        super(context, "quizzes.db");
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
                "," + COLUMN_QUIZ_ID + " text not null" +
                "," + COLUMN_CATEGORY + " text not null" +
                "," + COLUMN_DIFFICULTY + " text not null" +
                "," + COLUMN_QUESTION + " text not null" +
                "," + COLUMN_ANSWER + " integer not null" +
                "," + COLUMN_OPTION_1 + " text not null" +
                "," + COLUMN_OPTION_2 + " text not null" +
                "," + COLUMN_OPTION_3 + " text not null" +
                "," + COLUMN_OPTION_4 + " text not null" +
                "," + COLUMN_EXPLANATION + " text not null);";
    }
}
