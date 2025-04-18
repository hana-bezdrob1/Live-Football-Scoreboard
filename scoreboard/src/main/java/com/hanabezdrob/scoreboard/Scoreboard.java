package com.hanabezdrob.scoreboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Scoreboard {
    private final List<Match> matches = new ArrayList<>();

    public Match startMatch(final String homeTeam, final String awayTeam) {
        final Match match = new Match(homeTeam, awayTeam);
        matches.add(match);

        return match;
    }

    public Match updateMatchScore(final Match match, final Score score) {
        int idx = matches.indexOf(match);
        if (idx < 0) {
            throw new IllegalArgumentException("Match not found");
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
