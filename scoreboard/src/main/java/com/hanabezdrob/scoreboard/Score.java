package com.hanabezdrob.scoreboard;

/**
 * Immutable snapshot of the current goals in a match.
 *
 * @param home number of goals by home team, int, non-negative
 * @param away number of goals by away team, int, non-negative
 *
 */
public record Score(int home, int away) {
    /**
     * Canonical constructor enforcing non-negative scores.
     *
     * @throws IllegalArgumentException if either {@code home} or {@code away} is negative
     */
    public Score {
        if (home < 0 || away < 0) {
            throw new IllegalArgumentException("Scores may not be negative.");
        }
    }

    /**
     * @return the total number of goals scored in the match
     */
    public int total() {
        return home + away;
    }
}
