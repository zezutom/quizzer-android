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
        values.put(QuizDataHelper.COLUMN_CATEGORY, quiz.getCategory());
        values.put(QuizDataHelper.COLUMN_DIFFICULTY, quiz.getDifficulty());
        values.put(QuizDataHelper.COLUMN_QUESTION, quiz.getQuestion());
        values.put(QuizDataHelper.COLUMN_ANSWER, quiz.getAnswer());
        values.put(QuizDataHelper.COLUMN_OPTION_1, quiz.getOptionOne());
        values.put(QuizDataHelper.COLUMN_OPTION_2, quiz.getOptionTwo());
        values.put(QuizDataHelper.COLUMN_OPTION_3, quiz.getOptionThree());
        values.put(QuizDataHelper.COLUMN_OPTION_4, quiz.getOptionFour());
        values.put(QuizDataHelper.COLUMN_EXPLANATION, quiz.getExplanation());

        return values;
    }

    @Override
    protected Quiz getEntity(Cursor cursor) {
        Quiz quiz = new Quiz();

        quiz.setId(cursor.getString(1));
        quiz.setCategory(cursor.getString(2));
        quiz.setDifficulty(cursor.getString(3));
        quiz.setQuestion(cursor.getString(4));
        quiz.setAnswer(cursor.getInt(5));
        quiz.setOptionOne(cursor.getString(6));
        quiz.setOptionTwo(cursor.getString(7));
        quiz.setOptionThree(cursor.getString(8));
        quiz.setOptionFour(cursor.getString(9));
        quiz.setExplanation(cursor.getString(10));

        return quiz;
    }
}
