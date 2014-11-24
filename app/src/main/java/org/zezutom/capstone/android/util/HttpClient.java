package org.zezutom.capstone.android.util;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class HttpClient {

    public static final String TAG = HttpClient.class.getSimpleName();

    private RequestQueue requestQueue;

    private Context context;

    public HttpClient(Context context) {
        this.context = context;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(context);

        return requestQueue;
    }

    public <T> void addRequest(Request<T> request, String tag) {
        request.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(request);
    }

    public void cancelRequest(String tag) {
        getRequestQueue().cancelAll(tag);
    }

}
