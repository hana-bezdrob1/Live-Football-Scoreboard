package com.hanabezdrob.scoreboard;

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

    private final MatchValidator validator;
    private final List<Match> matches = new ArrayList<>();

    public Scoreboard() {
        this.validator = new FootballMatchValidator();
    }

    public Scoreboard(final MatchValidator validator) {
        this.validator = validator;
    }

    /**
     * Starts a new 0–0 match. Throws if either team is already playing or has invalid names.
     *
     * @throws IllegalStateException if homeTeam or awayTeam already has a match in progress
     * @throws IllegalArgumentException or NullPointerException for invalid names
     */
    public Match startMatch(final String homeTeam, final String awayTeam) {
        validator.validateNewMatch(homeTeam, awayTeam, matches);
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

        validator.validateScoreUpdate(match, score);

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
