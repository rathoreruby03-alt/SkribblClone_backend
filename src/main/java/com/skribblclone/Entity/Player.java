package com.skribblclone.Entity;

public class Player {

    private String id;
    private String name;
    private int score;
    private boolean ready;

    public Player() {
    }

    public Player(String id, String name) {
        this.id = id;
        this.name = name;
        this.score = 0;
        this.ready = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}