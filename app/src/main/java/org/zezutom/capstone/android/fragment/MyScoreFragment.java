package org.zezutom.capstone.android.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.zezutom.capstone.android.R;

import zezutom.org.statsService.StatsService;

public class MyScoreFragment extends Fragment {

    private StatsService.StatsServiceImpl statsService;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_stats_score, container, false);
        return view;
    }
}
