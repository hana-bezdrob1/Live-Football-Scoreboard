package com.hanabezdrob.scoreboard;

/**
 * Encapsulates business‑rule checks for creating and updating matches.
 */
public interface MatchValidator {
    /**
     * Validate the parameters for starting a new match.
     *
     * @throws IllegalArgumentException or IllegalStateException on violation
     */
    void validateNewMatch(final String homeTeam, final String awayTeam, final Iterable<Match> existingMatches);

    /**
     * Validate the parameters for updating an existing match's score.
     *
     * @throws IllegalArgumentException on violation
     */
    void validateScoreUpdate(final Match oldMatch, final Score newScore);
}
