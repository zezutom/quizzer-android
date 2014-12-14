package org.zezutom.capstone.android.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest;
import com.google.api.client.http.HttpRequest;

import org.json.JSONObject;
import org.zezutom.capstone.android.dao.BaseDataSource;
import org.zezutom.capstone.android.util.HttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseApi<T extends BaseDataSource> {

    public static final String TAG = "BaseApi";

    protected HttpClient httpClient;

    private List<T> dataSources;

    public BaseApi(Context context) {

        // System services: network access and a database
        httpClient = new HttpClient(context);
        dataSources = new ArrayList<>();
    }

    protected void registerDataSources(T... dataSources) {
        for (T dataSource : dataSources) {
            this.dataSources.add(dataSource);
        }
    }

    public void setUp() {
        for (T dataSource : dataSources) {
            dataSource.open();
        }
    }

    public void tearDown() {
        for (T dataSource : dataSources) {
            dataSource.close();
        }
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
