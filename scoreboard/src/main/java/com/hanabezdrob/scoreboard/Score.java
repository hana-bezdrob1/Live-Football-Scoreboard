package com.hanabezdrob.scoreboard;

public record Score(int home, int away) {
    public int total() {
        return home + away;
    }
}
