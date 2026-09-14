package com.skribblclone.WebSocket;

public class ChatMessage {

    private String roomId;
    private String playerId;
    private String playerName;
    private String message;

    public ChatMessage() {
    }

    public ChatMessage(
            String roomId,
            String playerId,
            String playerName,
            String message) {

        this.roomId = roomId;
        this.playerId = playerId;
        this.playerName = playerName;
        this.message = message;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
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
}