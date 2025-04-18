package com.hanabezdrob.scoreboard;

import java.time.Instant;

public record Match(String homeTeam, String awayTeam, Score score, Instant startTime) {
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
