package com.hanabezdrob.scoreboard;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Scoreboard {
    public static final int MAX_SCORE = 30;
    public static final int MAX_DELTA = 5;

    private final List<Match> matches = new ArrayList<>();

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

        final Match updated = new Match(match.homeTeam(), match.awayTeam(), score, match.startTime());
        matches.set(idx, updated);

        return updated;
    }

    public void finishMatch(final Match match) {
        matches.remove(match);
    }

    public List<Match> getSummary() {
        return matches.stream()
                .sorted(Comparator
                        .comparingInt((final Match m) -> m.score().total()).reversed()
                        .thenComparing(Match::startTime, Comparator.reverseOrder())
                )
                .toList();
    }

    public List<Match> getMatchesInProgress() {
        return Collections.unmodifiableList(matches);
    }
}
