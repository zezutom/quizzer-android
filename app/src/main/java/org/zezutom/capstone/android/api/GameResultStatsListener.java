package org.zezutom.capstone.android.api;

import zezutom.org.statsService.model.GameResultStats;

public interface GameResultStatsListener {

    void onSuccess(GameResultStats stats);

    void onError(Exception ex);

}
