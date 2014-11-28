package org.zezutom.capstone.android.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import zezutom.org.quizService.model.Quiz;

public class QuizDataSource {

    private SQLiteDatabase db;

    private QuizDataHelper helper;

    private String[] columns = {
            QuizDataHelper.COLUMN_ID,
            QuizDataHelper.COLUMN_QUIZ_ID,
            QuizDataHelper.COLUMN_TITLE,
            QuizDataHelper.COLUMN_EXPLANATION,
            QuizDataHelper.COLUMN_ANSWER,
            QuizDataHelper.COLUMN_MOVIE_1,
            QuizDataHelper.COLUMN_MOVIE_2,
            QuizDataHelper.COLUMN_MOVIE_3,
            QuizDataHelper.COLUMN_MOVIE_4,
            QuizDataHelper.COLUMN_DIFFICULTY
    };

    public QuizDataSource(Context context) {
        helper = new QuizDataHelper(context);
    }

    public void open() {
        db = helper.getWritableDatabase();
    }

    public void close() {
        helper.close();
    }

    public void addAll(List<Quiz> quizzes) {
        for (Quiz quiz : quizzes) addOne(quiz);
    }

    public void addOne(Quiz quiz) {
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

        db.insert(QuizDataHelper.TABLE_NAME, null, values);
    }

    public List<Quiz> getAll() {
        List<Quiz> quizzes = new ArrayList<>();
        if (db == null) open();
        Cursor cursor = db.query(QuizDataHelper.TABLE_NAME, columns,
                null, null, null, null, null);

        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            quizzes.add(createQuiz(cursor));
            cursor.moveToNext();
        }

        return quizzes;
    }

    private Quiz createQuiz(Cursor cursor) {
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
