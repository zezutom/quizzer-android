package org.zezutom.capstone.android.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.zezutom.capstone.android.util.DateTimeUtil;

import java.util.ArrayList;
import java.util.List;

import zezutom.org.quizService.model.QuizRating;

public class QuizRatingDataSource {

    private SQLiteDatabase db;

    private QuizRatingDataHelper helper;

    private String[] columns = {
            QuizRatingDataHelper.COLUMN_ID,
            QuizRatingDataHelper.COLUMN_QUIZ_ID
    };

    public QuizRatingDataSource(Context context) {
        helper = new QuizRatingDataHelper(context);
    }

    public void open() {
        db = helper.getWritableDatabase();
    }

    public void close() {
        helper.close();
    }

    public void addOne(QuizRating quizRating) {
        ContentValues values = new ContentValues();

        values.put(QuizRatingDataHelper.COLUMN_ID, quizRating.getId());
        values.put(QuizRatingDataHelper.COLUMN_QUIZ_ID, quizRating.getQuizId());
        values.put(QuizRatingDataHelper.COLUMN_CREATED_AT, DateTimeUtil.toString(quizRating.getCreatedAt()));
        values.put(QuizRatingDataHelper.COLUMN_QUIZ_LIKED, quizRating.getLiked());

        if (db == null) open();
        db.insert(QuizRatingDataHelper.TABLE_NAME, null, values);
    }

    public List<QuizRating> getAll() {
        List<QuizRating> quizzes = new ArrayList<>();
        if (db == null) open();
        Cursor cursor = db.query(QuizRatingDataHelper.TABLE_NAME, columns,
                null, null, null, null, null);

        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            quizzes.add(createQuizRating(cursor));
            cursor.moveToNext();
        }

        return quizzes;
    }

    private QuizRating createQuizRating(Cursor cursor) {
        QuizRating quizRating = new QuizRating();

        quizRating.setId(cursor.getString(1));
        quizRating.setQuizId(cursor.getString(2));
        quizRating.setCreatedAt(DateTimeUtil.toDateTime(cursor.getString(3)));
        quizRating.setLiked(Boolean.parseBoolean(cursor.getString(4)));

        return quizRating;
    }

}
