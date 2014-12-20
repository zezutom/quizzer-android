package org.zezutom.capstone.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.zezutom.capstone.android.R;
import org.zezutom.capstone.android.util.AppUtil;
import org.zezutom.capstone.android.util.DateTimeUtil;

import java.util.List;

import zezutom.org.quizzer.model.GameResult;

public class GameResultAdapter extends ArrayAdapter<GameResult> {

    private List<GameResult> results;

    public GameResultAdapter(Context context, List<GameResult> results) {
        super(context, (results == null) ? 0 : results.size());
        this.results = results;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            GameResult result = this.results.get(position);

            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_game_result, parent, false);

            TextView createdAtView = (TextView) convertView.findViewById(R.id.created_at);
            createdAtView.setText(DateTimeUtil.toString(result.getCreatedAt()));

            TextView scoreView = (TextView) convertView.findViewById(R.id.score);
            scoreView.setText(AppUtil.numberToString(result.getScore()));

            TextView roundView = (TextView) convertView.findViewById(R.id.round);
            roundView.setText(AppUtil.numberToString(result.getRound()));

            TextView powerUpsView = (TextView) convertView.findViewById(R.id.power_ups);
            powerUpsView.setText(AppUtil.numberToString(result.getPowerUps()));

            TextView guessRatioView = (TextView) convertView.findViewById(R.id.guess_ratio);
            guessRatioView.setText(formatSuccessRatio(result));

        }
        return convertView;
    }

    @Override
    public int getCount() {
        return results.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public GameResult getItem(int position) {
        return results.get(position);
    }


    private String formatSuccessRatio(GameResult result) {
        final String delimiter = " - ";
        StringBuilder sb = new StringBuilder().append(result.getAttemptOneRatio());

        final int attemptTwoRatio = result.getAttemptTwoRatio();
        final int attemptThreeRatio = result.getAttemptThreeRatio();

        if (attemptTwoRatio > 0 || attemptThreeRatio > 0) {

            // We surely want to show the 2nd attempt ratio:
            // either it's 0, but since 3rd attempt ratio exists we want to show it anyway
            // or it's a positive number, so we definitely want to display it
            sb.append(delimiter).append(attemptTwoRatio);

            if (attemptThreeRatio > 0) {
                sb.append(delimiter).append(attemptThreeRatio);
            }
        }

        return sb.toString();
    }
}
