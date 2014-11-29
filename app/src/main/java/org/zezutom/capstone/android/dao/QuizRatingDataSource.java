package org.zezutom.capstone.android.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.zezutom.capstone.android.util.DateTimeUtil;

import zezutom.org.quizService.model.QuizRating;

public class QuizRatingDataSource extends BaseDataSource<QuizRating, QuizRatingDataHelper> {

    public QuizRatingDataSource(Context context) {
        super(new QuizRatingDataHelper(context));
    }

    @Override
    protected ContentValues getValues(QuizRating quizRating) {
        ContentValues values = new ContentValues();

        values.put(QuizRatingDataHelper.COLUMN_ID, quizRating.getId());
        values.put(QuizRatingDataHelper.COLUMN_QUIZ_ID, quizRating.getQuizId());
        values.put(QuizRatingDataHelper.COLUMN_CREATED_AT, DateTimeUtil.toString(quizRating.getCreatedAt()));
        values.put(QuizRatingDataHelper.COLUMN_QUIZ_LIKED, quizRating.getLiked());

        return values;
    }

    @Override
    protected QuizRating getEntity(Cursor cursor) {
        QuizRating quizRating = new QuizRating();

        quizRating.setId(cursor.getString(1));
        quizRating.setQuizId(cursor.getString(2));
        quizRating.setCreatedAt(DateTimeUtil.toDateTime(cursor.getString(3)));
        quizRating.setLiked(Boolean.parseBoolean(cursor.getString(4)));

        return quizRating;
    }
}
