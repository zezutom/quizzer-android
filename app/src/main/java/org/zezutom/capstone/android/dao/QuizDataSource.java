package org.zezutom.capstone.android.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import zezutom.org.quizService.model.Quiz;

public class QuizDataSource extends BaseDataSource<Quiz, QuizDataHelper> {

    public QuizDataSource(Context context) {
        super(new QuizDataHelper(context));
    }

    @Override
    protected ContentValues getValues(Quiz quiz) {
        ContentValues values = new ContentValues();

        values.put(QuizDataHelper.COLUMN_QUIZ_ID, quiz.getId());
        values.put(QuizDataHelper.COLUMN_TITLE, quiz.getTitle());
        values.put(QuizDataHelper.COLUMN_EXPLANATION, quiz.getExplanation());
        values.put(QuizDataHelper.COLUMN_ANSWER, quiz.getAnswer());
        values.put(QuizDataHelper.COLUMN_MOVIE_1, quiz.getMovieOne());
        values.put(QuizDataHelper.COLUMN_MOVIE_2, quiz.getMovieTwo());
        values.put(QuizDataHelper.COLUMN_MOVIE_3, quiz.getMovieThree());
        values.put(QuizDataHelper.COLUMN_MOVIE_4, quiz.getMovieFour());
        values.put(QuizDataHelper.COLUMN_DIFFICULTY, quiz.getDifficulty());

        return values;
    }

    @Override
    protected Quiz getEntity(Cursor cursor) {
        Quiz quiz = new Quiz();

        quiz.setId(cursor.getString(1));
        quiz.setTitle(cursor.getString(2));
        quiz.setExplanation(cursor.getString(3));
        quiz.setAnswer(cursor.getInt(4));
        quiz.setMovieOne(cursor.getString(5));
        quiz.setMovieTwo(cursor.getString(6));
        quiz.setMovieThree(cursor.getString(7));
        quiz.setMovieFour(cursor.getString(8));
        quiz.setDifficulty(cursor.getInt(9));

        return quiz;
    }
}
