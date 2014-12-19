package org.zezutom.capstone.android.api;

import android.app.Activity;
import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import org.zezutom.capstone.android.MainActivity;
import org.zezutom.capstone.android.util.AppUtil;
import org.zezutom.capstone.android.util.AuthCache;

import java.io.IOException;

import zezutom.org.quizzer.Quizzer;

/**
 * Provides a common API handling
 */
public abstract class BaseApiTask<T extends Object> extends AsyncTask<Void, Void, Boolean> {

    public static final String TAG = "BaseApiTask";

    protected Activity activity;

    protected ResponseListener<T> listener;

    private T data;

    protected BaseApiTask(Activity activity, ResponseListener<T> listener) {
        this.activity = activity;
        this.listener = listener;
    }

    protected abstract T execute(Quizzer.QuizzerApi api) throws IOException;

    @Override
    protected Boolean doInBackground(Void... voids) {
        boolean success = true;
        try {
            // Initialize the API
            // TODO authentication credentials - oauth2 not working atm (spent like a week on it)
            Quizzer.QuizzerApi api = new Quizzer.Builder(
                    AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(),
                    null).build().quizzerApi();
            data = execute(api);
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

    protected String getEmail() {
        return new AuthCache(activity).getSelectedAccountName();
    }
}
