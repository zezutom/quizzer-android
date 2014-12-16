package org.zezutom.capstone.android.api;

import zezutom.org.gameService.model.GameResult;

public interface GameResultListener {

    void onSuccess(GameResult gameResult);

    void onError(Exception ex);
}
