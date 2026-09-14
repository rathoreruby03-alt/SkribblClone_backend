package com.skribblclone.WebSocket;

public class HintMessage {

    private String roomId;
    private String playerId;
    private String playerName;

    public HintMessage() {
    }

    public HintMessage(String roomId, String playerId, String playerName) {
        this.roomId = roomId;
        this.playerId = playerId;
        this.playerName = playerName;
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
}