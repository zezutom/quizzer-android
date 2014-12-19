package org.zezutom.capstone.android.api;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import org.zezutom.capstone.android.MainActivity;

import java.io.IOException;

import zezutom.org.quizzer.Quizzer;

/**
 * Provides a common API handling
 */
public abstract class BaseApiTask<T extends Object> extends AsyncTask<Void, Void, Boolean> {

    public static final String TAG = "BaseApiTask";

    protected Activity activity;

    protected Quizzer.QuizzerApi api;

    protected ResponseListener<T> listener;

    private T data;

    protected BaseApiTask(Activity activity, Quizzer.QuizzerApi api, ResponseListener<T> listener) {
        this.activity = activity;
        this.api = api;
        this.listener = listener;
    }

    protected abstract T execute() throws IOException;

    @Override
    protected Boolean doInBackground(Void... voids) {
        boolean success = true;
        try {
            data = execute();
        } catch (UserRecoverableAuthIOException e) {
            success = false;
            activity.startActivityForResult(
                    e.getIntent(), MainActivity.REQUEST_CODE_RESOLUTION);
        } catch (IOException e) {
            success = false;
            listener.onError(e);
        } finally {
            return success;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (success) listener.onSuccess(data);
    }
}
