package org.zezutom.capstone.android.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest;
import com.google.api.client.http.HttpRequest;

import org.json.JSONObject;
import org.zezutom.capstone.android.dao.QuizDataSource;
import org.zezutom.capstone.android.dao.QuizRatingDataSource;
import org.zezutom.capstone.android.util.HttpClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import zezutom.org.gameService.GameService;
import zezutom.org.quizService.QuizService;
import zezutom.org.statsService.StatsService;

public abstract class BaseApi {

    public static final String TAG = BaseApi.class.getName();

    protected HttpClient httpClient;

    protected QuizDataSource quizDataSource;

    protected QuizRatingDataSource quizRatingDataSource;

    protected GameService.GameServiceImpl gameService;

    protected QuizService.QuizServiceImpl quizService;

    protected StatsService.StatsServiceImpl statsService;


    public BaseApi(Context context) {

        // System services: network access and a database
        httpClient = new HttpClient(context);
        quizDataSource = new QuizDataSource(context);
        quizRatingDataSource = new QuizRatingDataSource(context);

        // Game API
        gameService = new GameService.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), null).build().gameServiceImpl();

        // Quizzes API
        quizService = new QuizService.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), null).build().quizServiceImpl();

        // User Stats API
        statsService = new StatsService.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), null).build().statsServiceImpl();

    }

    protected void httpGet(String url, Response.Listener<JSONObject> responseListener,
                           Response.ErrorListener errorListener) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                responseListener, errorListener);

        // Add request to the request queue
        httpClient.addRequest(request, TAG);
    }

    protected<T extends AbstractGoogleJsonClientRequest> String getUrl(T request) {

        String url = null;

        try {
            HttpRequest httpRequest = request.buildHttpRequest();
            url = httpRequest.getUrl().build();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        return url;
    }

    protected void httpPost(String url, final Map<String,String> params, Response.Listener<JSONObject> responseListener,
                            Response.ErrorListener errorListener) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null,
                responseListener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };

        // Add request to the request queue
        httpClient.addRequest(request, TAG);
    }
}
