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
public abstract class BaseApiTask<T extends Object> extends AsyncTask<Void, Void, Void> {

    public static final String TAG = "BaseApiTask";

    protected Activity activity;

    protected Quizzer.QuizzerApi api;

    protected ResponseListener<T> listener;

    protected BaseApiTask(Activity activity, Quizzer.QuizzerApi api, ResponseListener<T> listener) {
        this.activity = activity;
        this.api = api;
        this.listener = listener;
    }

    protected abstract T execute() throws IOException;

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            T data = execute();
            listener.onSuccess(data);
        } catch (UserRecoverableAuthIOException e) {
            activity.startActivityForResult(
                    e.getIntent(), MainActivity.REQUEST_CODE_RESOLUTION);
        } catch (IOException e) {
            listener.onError(e);
        } finally {
            return null;
        }
    }
}
