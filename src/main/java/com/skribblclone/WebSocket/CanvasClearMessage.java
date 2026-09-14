package com.skribblclone.WebSocket;

public class CanvasClearMessage {

    private String roomId;
    private String playerId;

    public CanvasClearMessage() {
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
}