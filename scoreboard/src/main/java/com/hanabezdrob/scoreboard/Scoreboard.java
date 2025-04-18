package com.hanabezdrob.scoreboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Scoreboard {
    private final List<Match> matches = new ArrayList<>();

    public Match startMatch(final String homeTeam, final String awayTeam) {
        final Match match = new Match(homeTeam, awayTeam);
        matches.add(match);

        return match;
    }

    public void updateMatchScore(final Match match, final Score score) {
        int idx = matches.indexOf(match);

        if (idx != -1) {
            final Match updated = new Match(match.homeTeam(), match.awayTeam(), score, match.startTime());
            matches.set(idx, updated);
        }
    }

    public void finishMatch(final Match match) {
        matches.remove(match);
    }

    public List<Match> getMatchesInProgress() {
        return Collections.unmodifiableList(matches);
    }
}
