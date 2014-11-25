package org.zezutom.capstone.android.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import org.zezutom.capstone.android.R;

public class SingleGameSolutionDialog extends DialogFragment {

    public static final String EXPLANATION_KEY = "explanation";

    private View dialogView;

    private View.OnClickListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dialogView = inflater.inflate(R.layout.fragment_dialog_single_game, container, false);
        TextView explanationView = (TextView) dialogView.findViewById(R.id.explanation);

        String explanation = getArguments().getString(EXPLANATION_KEY);
        explanationView.setText(explanation);

        if (listener != null) {
            onClick(R.id.voteUp, listener);
            onClick(R.id.voteDown, listener);
            onClick(R.id.closeDialog, listener);
        }

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return dialogView;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    private void onClick(int id, View.OnClickListener listener) {
        dialogView.findViewById(id).setOnClickListener(listener);
    }

}
