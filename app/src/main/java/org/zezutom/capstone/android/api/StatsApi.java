package org.zezutom.capstone.android.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import org.json.JSONException;
import org.json.JSONObject;
import org.zezutom.capstone.android.dao.GameResultStatsDataSource;

import java.io.IOException;

import zezutom.org.statsService.StatsService;
import zezutom.org.statsService.model.GameResultStats;

public class StatsApi extends BaseApi {

    public static final String TAG = "StatsApi";

    private StatsService.StatsServiceImpl statsService;

    private GameResultStatsListener statsListener;

    private GameResultStatsDataSource statsDataSource;


    public StatsApi(Context context, GameResultStatsListener statsListener) {
        super(context);
        this.statsListener = statsListener;
        statsDataSource = new GameResultStatsDataSource(context);
        registerDataSources(statsDataSource);
        statsService = new StatsService.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), null).build().statsServiceImpl();
    }

    public void getGameResultStats() {
        GameResultStats stats = statsDataSource.getOne();

        if (stats != null) {
            statsListener.onSuccess(stats);
            return;
        }

        try {
            final String url = getUrl(statsService.getGameResultStats());

            final Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        GameResultStats stats = new GameResultStats();
                        stats.setScore(jsonObject.getInt("score"));
                        stats.setRound(jsonObject.getInt("round"));
                        stats.setPowerUps(jsonObject.getInt("powerUps"));
                        stats.setAttemptOneRatio(jsonObject.getInt("attemptOneRatio"));
                        stats.setAttemptTwoRatio(jsonObject.getInt("attemptTwoRatio"));
                        stats.setAttemptThreeRatio(jsonObject.getInt("attemptThreeRatio"));

                        statsDataSource.addOne(stats);
                        statsListener.onSuccess(stats);

                    } catch (JSONException ex) {
                        statsListener.onError(ex);
                    }
                }
            };

            final Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError ex) {
                    Log.e(TAG, ex.getMessage());
                    statsListener.onError(ex);
                }
            };

            httpGet(url, responseListener, errorListener);

        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

    }
}
