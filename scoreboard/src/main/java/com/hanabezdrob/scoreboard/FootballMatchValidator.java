package com.hanabezdrob.scoreboard;

import java.util.Objects;

import static java.lang.String.format;

public class FootballMatchValidator implements MatchValidator {
    @Override
    public void validateNewMatch(final String home, final String away, final Iterable<Match> existingMatches) {
        Objects.requireNonNull(home, "Home team must not be null");
        Objects.requireNonNull(away, "Away team must not be null");

        if (home.isBlank() || away.isBlank()) {
            throw new IllegalArgumentException("Team names must not be blank");
        }
        if (home.equals(away)) {
            throw new IllegalArgumentException("Home and away team must be different");
        }

        for (final Match match : existingMatches) {
            if (match.homeTeam().equals(home) || match.awayTeam().equals(home)) {
                throw new IllegalStateException(format("Team %s has a match in progress", home));
            }
            if (match.homeTeam().equals(away) || match.awayTeam().equals(away)) {
                throw new IllegalStateException(format("Team %s has a match in progress", away));
            }
        }
    }

    @Override
    public void validateScoreUpdate(final Match oldMatch, final Score newScore) {
        if (newScore.home() < oldMatch.getHomeScore() || newScore.away() < oldMatch.getAwayScore()) {
            throw new IllegalArgumentException("Scores may not go down during a game");
        }

        if (newScore.home() > Scoreboard.MAX_SCORE || newScore.away() > Scoreboard.MAX_SCORE) {
            throw new IllegalArgumentException(format("Score %s exceeds maximum allowed score", newScore));
        }

        if (newScore.home() - oldMatch.getHomeScore() > Scoreboard.MAX_DELTA ||
                newScore.away() - oldMatch.getAwayScore() > Scoreboard.MAX_DELTA) {
            throw new IllegalArgumentException(
                    format("Score increase from %s to %s exceeds maximum score increase", oldMatch.score(), newScore)
            );
        }
    }
}
