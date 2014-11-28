package org.zezutom.capstone.android.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.api.client.util.DateTime;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.zezutom.capstone.android.util.DateTimeUtil;
import org.zezutom.capstone.android.util.QuizListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zezutom.org.quizService.model.Quiz;
import zezutom.org.quizService.model.QuizRating;

public class QuizApi extends BaseApi {

    public static final String TAG = QuizApi.class.getName();

    private QuizListener quizListener;

    public QuizApi(Context context, QuizListener quizListener) {
        super(context);
        this.quizListener = quizListener;
    }

    private void onGetAll(JSONObject response) {
        try {
            JSONArray items = response.getJSONArray("items");
            List<Quiz> quizzes = new ArrayList<>();
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);

                if (item == null) continue;

                Quiz quiz = new Quiz();
                quiz.setId(item.getString("id"));
                quiz.setTitle(item.getString("title"));
                quiz.setExplanation(item.getString("explanation"));
                quiz.setAnswer(item.getInt("answer"));
                quiz.setMovieOne(item.getString("movieOne"));
                quiz.setMovieTwo(item.getString("movieTwo"));
                quiz.setMovieThree(item.getString("movieThree"));
                quiz.setMovieFour(item.getString("movieFour"));
                quiz.setDifficulty(item.getInt("difficulty"));
                quizzes.add(quiz);
            }

            if (!quizzes.isEmpty()) {
                quizListener.onGetAllSuccess(quizzes);
                quizDataSource.addAll(quizzes);
            }
        } catch (JSONException e) {
            quizListener.onGetAllError(e);
        }
    }

    public void rateQuiz(boolean liked, String quizId) {
        try {
            final String url = getUrl(quizService.rate(liked, quizId));

            final Map<String, String> params = new HashMap<>();
            params.put("liked", Boolean.toString(liked));
            params.put("quizId", quizId);

            final Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        DateTime createdAt = DateTimeUtil.toDateTime(jsonObject.getString("createdAt"));
                        QuizRating quizRating = new QuizRating();
                        quizRating.setId(jsonObject.getString("id"));
                        quizRating.setCreatedAt(createdAt);
                        quizRating.setLiked(jsonObject.getBoolean("liked"));
                        quizRating.setQuizId(jsonObject.getString("quizId"));
                        quizListener.onRateSuccess(quizRating);
                        quizRatingDataSource.addOne(quizRating);
                    } catch (JSONException e) {
                        quizListener.onRateError(e);
                    }
                }
            };

            final Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError ex) {
                    quizListener.onRateError(ex);
                }
            };

            httpPost(url, params, responseListener, errorListener);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

    }

    public void loadQuizzes() {
        List<Quiz> quizzes = quizDataSource.getAll();
        if (quizzes.isEmpty())
            try {

                final String url = getUrl(quizService.getAll());

                final Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        onGetAll(jsonObject);
                    }
                };

                final Response.ErrorListener errorListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError ex) {
                        Log.e(TAG, ex.getMessage());
                        quizListener.onGetAllError(ex);
                    }
                };

                httpGet(url, responseListener, errorListener);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        else
            quizListener.onGetAllSuccess(quizzes);
    }

}
