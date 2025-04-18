package com.hanabezdrob.scoreboard;

public record Score(int home, int away) {
    public Score {
        if (home < 0 || away < 0) {
            throw new IllegalArgumentException("Scores may not be negative.");
        }
    }

    public int total() {
        return home + away;
    }
}
