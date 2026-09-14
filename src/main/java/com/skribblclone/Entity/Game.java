package com.skribblclone.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class Game {

    private int currentRound;
    private int totalRounds;

    private String drawerId;
    private String currentWord;

    private List<String> wordOptions = new ArrayList<>();
    private String gameStatus;

    private int remainingTime;
    private boolean roundActive;

    private String winnerId;
    private String winnerName;

    private int hintsUsed;
    private int maxHints = 3;
    private String hint;

    private int wordCount;

    private Set<String> correctGuessers = new HashSet<>();

    public Game() {
        this.currentRound = 0;
        this.gameStatus = "WAITING";
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    public int getTotalRounds() {
        return totalRounds;
    }

    public void setTotalRounds(int totalRounds) {
        this.totalRounds = totalRounds;
    }

    public String getDrawerId() {
        return drawerId;
    }

    public void setDrawerId(String drawerId) {
        this.drawerId = drawerId;
    }

    public String getCurrentWord() {
        return currentWord;
    }

    public void setCurrentWord(String currentWord) {
        this.currentWord = currentWord;
    }

    public List<String> getWordOptions() {
        return wordOptions;
    }

    public void setWordOptions(List<String> wordOptions) {
        this.wordOptions = wordOptions;
    }

    public String getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(String gameStatus) {
        this.gameStatus = gameStatus;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public boolean isRoundActive() {
        return roundActive;
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

    public int getHintsUsed() {
        return hintsUsed;
    }

    public void setHintsUsed(int hintsUsed) {
        this.hintsUsed = hintsUsed;
    }

    public int getMaxHints() {
        return maxHints;
    }

    public void setMaxHints(int maxHints) {
        this.maxHints = maxHints;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public int getWordCount() {
        return wordCount;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }

    public Set<String> getCorrectGuessers() {
        return correctGuessers;
    }

    public void setCorrectGuessers(Set<String> correctGuessers) {
        this.correctGuessers = correctGuessers;
    }
}
