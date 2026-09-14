package com.skribblclone.WebSocket;

public class ReadyMessage {

    private String roomId;
    private String playerId;
    private boolean ready;

    public ReadyMessage() {
    }

    public ReadyMessage(String roomId, String playerId, boolean ready) {
        this.roomId = roomId;
        this.playerId = playerId;
        this.ready = ready;
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

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}