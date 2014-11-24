package org.zezutom.capstone.android.util;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.zezutom.capstone.android.dao.QuizDataSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import zezutom.org.quizService.QuizService;
import zezutom.org.quizService.model.Quiz;

public class QuizProvider {

    public static final String TAG = QuizProvider.class.getName();

    private HttpClient httpService;

    private QuizDataSource dataSource;

    private QuizLoadListener listener;

    private QuizService quizService;


    public QuizProvider(Context context, QuizLoadListener listener) {
        this.httpService = new HttpClient(context);
        this.dataSource = new QuizDataSource(context);
        this.listener = listener;
    }

    public void loadQuizzes() {
        List<Quiz> quizzes = dataSource.getAll();
        if (quizzes.isEmpty())
            getFromApi();
        else
            listener.onSuccess(quizzes);
    }

    private void getFromApi() {
        JsonObjectRequest gameSetRequest = new JsonObjectRequest(Request.Method.GET, getUrl(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
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
                        listener.onSuccess(quizzes);
                        dataSource.addAll(quizzes);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, volleyError.getMessage());
                listener.onError();
            }
        });

        // Add request to the request view
        httpService.addRequest(gameSetRequest, TAG);
    }

    private String getUrl() {

        String url = null;

        if (quizService == null) {
            QuizService.Builder builder = new QuizService.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null);
            quizService = builder.build();
        }

        final QuizService.QuizServiceImpl quizServiceImpl = quizService.quizServiceImpl();

        if (quizServiceImpl != null) {
            try {
                QuizService.QuizServiceImpl.GetAll getAll = quizServiceImpl.getAll();
                HttpRequest httpRequest = getAll.buildHttpRequest();
                url = httpRequest.getUrl().build();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }

        return url;
    }
}
