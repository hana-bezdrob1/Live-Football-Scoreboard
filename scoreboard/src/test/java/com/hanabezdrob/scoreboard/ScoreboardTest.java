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
        assertThat(matches.get(0).getHomeTeam()).isEqualTo("Bosnia and Herzegovina");
        assertThat(matches.get(0).getAwayTeam()).isEqualTo("Norway");
        assertThat(matches.get(0).getHomeScore()).isZero();
        assertThat(matches.get(0).getAwayScore()).isZero();
    }
}
