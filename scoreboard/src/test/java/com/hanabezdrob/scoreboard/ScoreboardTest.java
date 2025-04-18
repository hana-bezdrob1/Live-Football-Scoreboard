package com.hanabezdrob.scoreboard;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ScoreboardTest {
    @Test
    void startMatch_shouldAddNewMatchToScoreboard() {
        final Scoreboard scoreboard = new Scoreboard();

        scoreboard.startMatch("Bosnia and Herzegovina", "Norway");

        final var matches = scoreboard.getMatchesInProgress();
        assertThat(matches).hasSize(1);

        final Match stored  = matches.getFirst();
        assertThat(stored.homeTeam()).isEqualTo("Bosnia and Herzegovina");
        assertThat(stored.awayTeam()).isEqualTo("Norway");
        assertThat(stored.getHomeScore()).isZero();
        assertThat(stored.getAwayScore()).isZero();
    }

    @Test
    void updateMatch_shouldUpdateExistingMatchScore() {
        final Scoreboard scoreboard = new Scoreboard();
        final Match match = scoreboard.startMatch("Bosnia and Herzegovina", "Norway");

        scoreboard.updateMatchScore(match, new Score(1, 0));

        final Match updatedMatch = scoreboard.getMatchesInProgress().getFirst();
        assertThat(updatedMatch.getHomeScore()).isEqualTo(1);
        assertThat(updatedMatch.getAwayScore()).isEqualTo(0);
    }
}
