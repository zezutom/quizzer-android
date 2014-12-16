package org.zezutom.capstone.android.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.util.DateTime;

import org.json.JSONException;
import org.json.JSONObject;
import org.zezutom.capstone.android.dao.GameResultDataSource;
import org.zezutom.capstone.android.util.DateTimeUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import zezutom.org.gameService.GameService;
import zezutom.org.gameService.model.GameResult;

public class GameApi extends BaseApi {

    private GameResultListener gameResultListener;

    private GameResultDataSource gameResultDataSource;

    private GameService.GameServiceImpl gameService;

    public GameApi(Context context, GameResultListener gameResultListener) {
        super(context);
        this.gameResultListener = gameResultListener;
        gameResultDataSource = new GameResultDataSource(context);
        registerDataSources(gameResultDataSource);
        gameService = new GameService.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), null).build().gameServiceImpl();
    }

    public void saveGameResult(int round, int score, int powerUps, int oneTimeAttempts, int twoTimeAttempts) {
        try {
            final String url = getUrl(gameService.saveGameResult(round, score, powerUps, oneTimeAttempts, twoTimeAttempts));

            final Map<String, String> params = new HashMap<>();
            params.put("round", Integer.toString(round));
            params.put("score", Integer.toString(score));
            params.put("powerUps", Integer.toString(powerUps));
            params.put("oneTimeAttempts", Integer.toString(oneTimeAttempts));
            params.put("twoTimeAttempts", Integer.toString(twoTimeAttempts));

            final Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        final DateTime createdAt = DateTimeUtil.toDateTime(jsonObject.getString("createdAt"));
                        GameResult gameResult = new GameResult();
                        gameResult.setId(jsonObject.getString("id"));
                        gameResult.setCreatedAt(createdAt);
                        gameResult.setRound(jsonObject.getInt("round"));
                        gameResult.setScore(jsonObject.getInt("score"));
                        gameResult.setPowerUps(jsonObject.getInt("powerUps"));
                        gameResult.setAttemptOneRatio(jsonObject.getInt("attemptOneRatio"));
                        gameResult.setAttemptTwoRatio(jsonObject.getInt("attemptTwoRatio"));
                        gameResult.setAttemptThreeRatio(jsonObject.getInt("attemptThreeRatio"));
                        gameResultListener.onSuccess(gameResult);
                        gameResultDataSource.addOne(gameResult);
                    } catch (JSONException e) {
                        gameResultListener.onError(e);
                    }
                }
            };

            final Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError ex) {
                    gameResultListener.onError(ex);
                }
            };

            httpPost(url, params, responseListener, errorListener);

        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

}
