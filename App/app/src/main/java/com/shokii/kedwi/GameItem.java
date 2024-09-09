package com.shokii.kedwi;

public class GameItem {
    final String imgSrc, gameTitle, gameStatus;

    public GameItem(String  gameTitle, String imgSrc, String gameStatus) {
        this.gameTitle = gameTitle; this.imgSrc = imgSrc; this.gameStatus = gameStatus;
    }
}
