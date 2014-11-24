package org.zezutom.capstone.android.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class QuizDataHelper extends SQLiteOpenHelper {

    public static final String TAG = QuizDataHelper.class.getSimpleName();

    public static final String DB_NAME = "quizzes.db";

    public static final int DB_VERSION = 6;

    public static final String TABLE_QUIZZES = "quizzes";

    public static final String COLUMN_ID = "id";

    public static final String COLUMN_QUIZ_ID = "quiz_id";

    public static final String COLUMN_TITLE = "title";

    public static final String COLUMN_EXPLANATION = "explanation";

    public static final String COLUMN_ANSWER = "answer";

    public static final String COLUMN_MOVIE_1 = "movie_1";

    public static final String COLUMN_MOVIE_2 = "movie_2";

    public static final String COLUMN_MOVIE_3 = "movie_3";

    public static final String COLUMN_MOVIE_4 = "movie_4";

    public static final String COLUMN_DIFFICULTY = "difficulty";

    public static final String DB_CREATE = "create table " + TABLE_QUIZZES +
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

    public static final String DB_DROP = "drop table if exists " + TABLE_QUIZZES;

    public QuizDataHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading db from version " + oldVersion +
                " to " + newVersion + ". Saved data will be lost.");
        db.execSQL(DB_DROP);
        onCreate(db);
    }
}
