package org.zezutom.capstone.android.fragment;

import android.app.DialogFragment;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;

import org.zezutom.capstone.android.R;

public class QuizSolutionDialog extends DialogFragment {

    public static final String EXPLANATION_KEY = "explanation";

    /**
     * courtesy:
     * http://www.seal.io/2010/12/only-way-how-to-align-text-in-block-in.html
     * http://xjaphx.wordpress.com/2011/07/31/load-webview-with-progressdialog/
     */
    public static final String JUSTIFIED_TEXT =
            "<html><body><p align=\"justify\"><font color=\"%s\">%s</p></body></html>";

    private View dialogView;

    private WebView explanationView;

    private View.OnClickListener onClickListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setCancelable(false);

        if (dialogView == null) {
            String explanation = getArguments().getString(EXPLANATION_KEY);
            preLoad(explanation, inflater, getResources());
        }

        if (onClickListener != null) {
            onClick(R.id.vote_up, onClickListener);
            onClick(R.id.vote_down, onClickListener);
            onClick(R.id.close_dialog, onClickListener);
        }

        final Window window = getDialog().getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        window.setBackgroundDrawable(new ColorDrawable(0));

        dialogView.getBackground().setAlpha(200);
        return dialogView;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.onClickListener = listener;
    }

    private void onClick(int id, View.OnClickListener listener) {
        dialogView.findViewById(id).setOnClickListener(listener);
    }

    public void preLoad(String explanation, LayoutInflater inflater, Resources resources) {
        dialogView = inflater.inflate(R.layout.fragment_dialog_quiz_solution, null);

        explanationView = (WebView) dialogView.findViewById(R.id.explanation);
        explanationView.setBackgroundColor(Color.TRANSPARENT);

        String color = Integer.toHexString(resources.getColor(R.color.list_item_title));
        explanationView.loadData(String.format(JUSTIFIED_TEXT, color, explanation), "text/html", "utf-8");
    }

}
