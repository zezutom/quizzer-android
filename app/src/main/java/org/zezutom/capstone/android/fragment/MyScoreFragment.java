package org.zezutom.capstone.android.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.zezutom.capstone.android.MainActivity;
import org.zezutom.capstone.android.R;
import org.zezutom.capstone.android.api.GameResultStatsListener;
import org.zezutom.capstone.android.api.StatsApi;
import org.zezutom.capstone.android.util.AppUtil;
import org.zezutom.capstone.android.util.UIHelper;

import zezutom.org.statsService.model.GameResultStats;

public class MyScoreFragment extends Fragment implements GameResultStatsListener {

    private StatsApi statsApi;

    private UIHelper uiHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Enables menu
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_stats_score, container, false);
        uiHelper = new UIHelper(view)
                .addView(R.id.best_score)
                .addView(R.id.furthest_round)
                .addView(R.id.max_power_ups)
                .addView(R.id.first_attempt)
                .addView(R.id.second_attempt)
                .addView(R.id.third_attempt);

        // Load data
        statsApi = new StatsApi(getActivity(), this);
        statsApi.setUp();
        statsApi.getGameResultStats();

        return view;
    }

    @Override
    public void onDestroyView() {
        statsApi.tearDown();
        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.global, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return ((MainActivity) getActivity()).onMenuItemSelected(item);
    }

    @Override
    public void onSuccess(GameResultStats stats) {
        TextView bestScoreView = uiHelper.getView(R.id.best_score);
        bestScoreView.setText(AppUtil.numberToString(stats.getScore()));

        TextView furthestRoundView = uiHelper.getView(R.id.furthest_round);
        furthestRoundView.setText(AppUtil.numberToString(stats.getRound()));

        TextView maxPowerUpsView = uiHelper.getView(R.id.max_power_ups);
        maxPowerUpsView.setText(AppUtil.numberToString(stats.getAttemptOneRatio()));

        TextView firstAttemptView = uiHelper.getView(R.id.first_attempt);
        firstAttemptView.setText(AppUtil.numberToString(stats.getAttemptTwoRatio()));

        TextView secondAttemptView = uiHelper.getView(R.id.second_attempt);
        secondAttemptView.setText(AppUtil.numberToString(stats.getAttemptTwoRatio()));

        TextView thirdAttemptView = uiHelper.getView(R.id.third_attempt);
        thirdAttemptView.setText(AppUtil.numberToString(stats.getAttemptThreeRatio()));
    }

    @Override
    public void onError(Exception ex) {
        Toast.makeText(getActivity(),
                "An error occurred, please try again later.", Toast.LENGTH_SHORT).show();
    }
}
