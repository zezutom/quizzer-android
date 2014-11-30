package org.zezutom.capstone.android.api;

import zezutom.org.gameService.model.GameResult;

public interface GameResultListener {

    void onSaveGameResult(GameResult gameResult);

    void onSaveGameError(Exception ex);
}
