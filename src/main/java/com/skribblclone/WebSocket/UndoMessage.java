package com.skribblclone.WebSocket;

public class UndoMessage {

    private String roomId;
    private String playerId;

    public UndoMessage() {
    }

    public UndoMessage(String roomId, String playerId) {
        this.roomId = roomId;
        this.playerId = playerId;
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