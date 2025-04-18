package com.hanabezdrob.scoreboard;

import java.time.Instant;
import java.util.Objects;

/**
 * Immutable snapshot of a live football match.
 * @param homeTeam name of home team, {@link String}, non-null, non-blank, distinct
 * @param awayTeam name of away team, {@link String}, non-null, non-blank, distinct
 * @param score match score, {@link Score}, non-negative
 * @param startTime start time of match, {@link Instant}
 */
public record Match(String homeTeam, String awayTeam, Score score, Instant startTime) {
    /**
     * Canonical constructor enforcing name validity and team‑distinctness.
     *
     * @throws NullPointerException     if either name is null
     * @throws IllegalArgumentException if names are blank or identical
     */
    public Match {
        Objects.requireNonNull(homeTeam, "Home team must not be null");
        Objects.requireNonNull(awayTeam, "Away team must not be null");

        if (homeTeam.isBlank() || awayTeam.isBlank()) {
            throw new IllegalArgumentException("Team names must not be blank");
        }

        if (homeTeam.equals(awayTeam)) {
            throw new IllegalArgumentException("Home and away team must be different");
        }
    }

    /**
     * Convenience constructor with starting score 0-0 and timestamp now.
     */
    public Match(final String homeTeam, final String awayTeam) {
        this(homeTeam, awayTeam, new Score(0, 0), Instant.now());
    }

    public int getHomeScore() {
        return score.home();
    }

    public int getAwayScore() {
        return score.away();
    }
}
