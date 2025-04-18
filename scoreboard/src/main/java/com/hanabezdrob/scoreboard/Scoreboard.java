package com.hanabezdrob.scoreboard;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Manages the lifecycle of live football matches: start, update, finish, and summary printing.
 *
 * <p>Enforces:
 * <ul>
 *   <li>Single live match per team</li>
 *   <li>Scores non‑decreasing, within MAX_DELTA per update</li>
 *   <li>Scores less or equal to MAX_SCORE</li>
 * </ul>
 * </p>
 */
public class Scoreboard {
    public static final int MAX_SCORE = 30;
    public static final int MAX_DELTA = 5;

    private final List<Match> matches = new ArrayList<>();

    /**
     * Starts a new 0–0 match. Throws if either team is already playing or has invalid names.
     *
     * @throws IllegalStateException if homeTeam or awayTeam already has a match in progress
     * @throws IllegalArgumentException or NullPointerException for invalid names
     */
    public Match startMatch(final String homeTeam, final String awayTeam) {
        if (matches.stream().anyMatch(m -> m.homeTeam().equals(homeTeam) || m.awayTeam().equals(homeTeam))) {
            throw new IllegalStateException(MessageFormat.format("Team {0} has a match in progress", homeTeam));
        }

        if (matches.stream().anyMatch(m -> m.homeTeam().equals(awayTeam) || m.awayTeam().equals(awayTeam))) {
            throw new IllegalStateException(MessageFormat.format("Team {0} has a match in progress", awayTeam));
        }
        final Match match = new Match(homeTeam, awayTeam);
        matches.add(match);

        return match;
    }

    /**
     * Updates the {@code match} score, returning a new Match.
     *
     * @throws IllegalArgumentException if match is not found, scores go down,
     * MAX_SCORE is exceeded, or scores jump by more than MAX_DELTA
     */
    public Match updateMatchScore(final Match match, final Score score) {
        int idx = matches.indexOf(match);
        if (idx < 0) {
            throw new IllegalArgumentException("Match not found");
        }

        if (score.equals(match.score())) {
            return match;
        }

        if (match.getHomeScore() > score.home() || match.getAwayScore() > score.away()) {
            throw new IllegalArgumentException("Scores may not go down during a game");
        }

        if (score.home() > MAX_SCORE || score.away() > MAX_SCORE) {
            throw new IllegalArgumentException(
                    MessageFormat.format("Score {0} exceeds maximum allowed score", score)
            );
        }

        if (score.home() - match.getHomeScore() > MAX_DELTA || score.away() - match.getAwayScore() > MAX_DELTA) {
            throw new IllegalArgumentException(
                    MessageFormat.format(
                            "Score increase from {0} to {1} exceeds maximum score increase",
                            match.score(),
                            score
                    )
            );
        }

        final Match updated = new Match(match.homeTeam(), match.awayTeam(), score, match.startTime());
        matches.set(idx, updated);

        return updated;
    }

    /** Removes the match - does nothing if match is not found. */
    public void finishMatch(final Match match) {
        matches.remove(match);
    }

    /** @return unmodifiable list of live matches ordered by total goals desc, then startTime desc. */
    public List<Match> getSummary() {
        return matches.stream()
                .sorted(Comparator
                        .comparingInt((final Match m) -> m.score().total()).reversed()
                        .thenComparing(Match::startTime, Comparator.reverseOrder())
                )
                .toList();
    }

    /** @return unmodifiable list of matches in progress. */
    public List<Match> getMatchesInProgress() {
        return Collections.unmodifiableList(matches);
    }
}
