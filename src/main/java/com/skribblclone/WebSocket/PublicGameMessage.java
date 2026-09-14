package com.skribblclone.WebSocket;
import com.skribblclone.Entity.Game;

import java.util.List;

public class PublicGameMessage {

    private int currentRound;
    private int totalRounds;
    private String drawerId;
    private String gameStatus;
    private int remainingTime;
    private boolean roundActive;
    private String winnerId;
    private String winnerName;

    private List<String> wordOptions;

    public PublicGameMessage(Game game) {
        this.currentRound = game.getCurrentRound();
        this.totalRounds = game.getTotalRounds();
        this.drawerId = game.getDrawerId();
        this.gameStatus = game.getGameStatus();
        this.remainingTime = game.getRemainingTime();
        this.roundActive = game.isRoundActive();
        this.winnerId = game.getWinnerId();
        this.winnerName = game.getWinnerName();

        // Do NOT send the actual word to guessers
        this.wordOptions = null;
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

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    public void setTotalRounds(int totalRounds) {
        this.totalRounds = totalRounds;
    }

    public void setDrawerId(String drawerId) {
        this.drawerId = drawerId;
    }

    public void setGameStatus(String gameStatus) {
        this.gameStatus = gameStatus;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public void setRoundActive(boolean roundActive) {
        this.roundActive = roundActive;
    }

    public String getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(String winnerId) {
        this.winnerId = winnerId;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public void setWinnerName(String winnerName) {
        this.winnerName = winnerName;
    }

    public void setWordOptions(List<String> wordOptions) {
        this.wordOptions = wordOptions;
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
}
