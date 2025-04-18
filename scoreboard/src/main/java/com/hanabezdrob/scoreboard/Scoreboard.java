package com.hanabezdrob.scoreboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Scoreboard {
    private final List<Match> matches = new ArrayList<>();

    public void startMatch(final String homeTeam, final String awayTeam) {
        final Match match = new Match(homeTeam, awayTeam);
        matches.add(match);
    }

    public List<Match> getMatchesInProgress() {
        return Collections.unmodifiableList(matches);
    }
}
