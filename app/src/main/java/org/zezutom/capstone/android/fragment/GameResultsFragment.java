package org.zezutom.capstone.android.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import org.zezutom.capstone.android.MainActivity;
import org.zezutom.capstone.android.R;
import org.zezutom.capstone.android.adapter.GameResultAdapter;

import java.util.List;

import zezutom.org.quizzer.model.GameResult;

public class GameResultsFragment extends Fragment {

    private GridView gameResultsView;

    private GameResultsCallbacks callbacks;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Enables menu
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_game_results, container, false);
        gameResultsView = (GridView) view.findViewById(R.id.result_list);
        callbacks.loadGameResults();

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            callbacks = (GameResultsCallbacks) activity;
            callbacks.setGameResultsFragment(this);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement GameResultsCallbacks.");
        }

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

    public void setResults(List<GameResult> results) {
        gameResultsView.setAdapter(new GameResultAdapter(getActivity(), results));
    }

    /**
     *  Callbacks interface allowing to handle game data asynchronously.
     */
    public static interface GameResultsCallbacks {

        void loadGameResults();

        void setGameResultsFragment(GameResultsFragment gameResultsFragment);
    }
}
