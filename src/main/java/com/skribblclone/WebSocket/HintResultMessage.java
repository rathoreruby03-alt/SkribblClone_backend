package com.skribblclone.WebSocket;

public class HintResultMessage {

    private String hint;
    private int hintsUsed;
    private int maxHints;

    public HintResultMessage() {
    }

    public HintResultMessage(String hint, int hintsUsed, int maxHints) {
        this.hint = hint;
        this.hintsUsed = hintsUsed;
        this.maxHints = maxHints;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
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
}