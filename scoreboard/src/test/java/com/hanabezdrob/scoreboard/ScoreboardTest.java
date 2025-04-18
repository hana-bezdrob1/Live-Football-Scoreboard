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
        assertThat(matches.getFirst().getHomeTeam()).isEqualTo("Bosnia and Herzegovina");
        assertThat(matches.getFirst().getAwayTeam()).isEqualTo("Norway");
        assertThat(matches.getFirst().getHomeScore()).isZero();
        assertThat(matches.getFirst().getAwayScore()).isZero();
    }
}
