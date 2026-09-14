package com.skribblclone.WebSocket;

import com.skribblclone.Entity.Player;

import java.util.List;

public class LeaderboardMessage {

    private List<Player> players;

    public LeaderboardMessage() {
    }

    public LeaderboardMessage(List<Player> players) {
        this.players = players;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }
}