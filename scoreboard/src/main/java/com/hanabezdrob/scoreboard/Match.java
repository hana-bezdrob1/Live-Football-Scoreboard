package com.hanabezdrob.scoreboard;

import java.time.Instant;
import java.util.Objects;

public record Match(String homeTeam, String awayTeam, Score score, Instant startTime) {
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
