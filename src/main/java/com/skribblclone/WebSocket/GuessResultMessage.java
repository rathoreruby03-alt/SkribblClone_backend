package com.skribblclone.WebSocket;

public class GuessResultMessage {

    private String playerId;
    private String playerName;
    private String message;
    private boolean correct;

    public GuessResultMessage() {
    }

    public GuessResultMessage(
            String playerId,
            String playerName,
            String message,
            boolean correct) {

        this.playerId = playerId;
        this.playerName = playerName;
        this.message = message;
        this.correct = correct;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
}
