package com.skribblclone.WebSocket;

import com.skribblclone.Entity.Game;

import java.util.List;

public class DrawerGameMessage {

    private int currentRound;
    private int totalRounds;
    private String drawerId;
    private String gameStatus;
    private int remainingTime;
    private boolean roundActive;

    private List<String> wordOptions;
    private String currentWord;

    public DrawerGameMessage(Game game) {
        this.currentRound = game.getCurrentRound();
        this.totalRounds = game.getTotalRounds();
        this.drawerId = game.getDrawerId();
        this.gameStatus = game.getGameStatus();
        this.remainingTime = game.getRemainingTime();
        this.roundActive = game.isRoundActive();

        // Drawer is allowed to see these
        this.wordOptions = game.getWordOptions();
        this.currentWord = game.getCurrentWord();
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public int getTotalRounds() {
        return totalRounds;
    }

    public String getDrawerId() {
        return drawerId;
    }

    public String getGameStatus() {
        return gameStatus;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public boolean isRoundActive() {
        return roundActive;
    }

    public List<String> getWordOptions() {
        return wordOptions;
    }

    public String getCurrentWord() {
        return currentWord;
    }
}