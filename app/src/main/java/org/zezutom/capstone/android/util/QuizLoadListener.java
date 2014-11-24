package org.zezutom.capstone.android.util;

import java.util.List;

import zezutom.org.quizService.model.Quiz;

public interface QuizLoadListener {

    void onSuccess(List<Quiz> quizzes);

    void onError();
}
