package com.hanabezdrob.scoreboard;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @Test
    void finishMatch_shouldRemoveMatchFromScoreboard() {
        final Scoreboard scoreboard = new Scoreboard();
        final Match firstMatch = scoreboard.startMatch("Bosnia and Herzegovina", "Norway");
        final Match secondMatch = scoreboard.startMatch("Croatia", "Mexico");

        assertThat(scoreboard.getMatchesInProgress()).hasSize(2);

        scoreboard.finishMatch(firstMatch);

        final List<Match> remainingMatches = scoreboard.getMatchesInProgress();
        assertThat(remainingMatches).hasSize(1);
        assertThat(remainingMatches.getFirst()).isSameAs(secondMatch);

        // Ensure that trying to complete a non-existent match will not do anything.
        scoreboard.finishMatch(firstMatch);
        assertThat(scoreboard.getMatchesInProgress()).hasSize(1);
    }

    @Test
    void getSummary_shouldOrderByScoreDesc_thenByStartTimeDesc() throws InterruptedException {
        final Scoreboard scoreboard = new Scoreboard();
        Match firstMatch = scoreboard.startMatch("Bosnia and Herzegovina", "Norway");
        firstMatch = scoreboard.updateMatchScore(firstMatch, new Score(1, 0));

        Thread.sleep(50); // pause so match start times are different

        Match secondMatch = scoreboard.startMatch("Croatia", "Mexico");
        secondMatch = scoreboard.updateMatchScore(secondMatch, new Score(1, 0));

        Match thirdMatch = scoreboard.startMatch("Germany", "Spain");
        thirdMatch = scoreboard.updateMatchScore(thirdMatch, new Score(2, 3));

        final var summary = scoreboard.getSummary();
        assertThat(summary).hasSize(3);
        assertThat(summary).containsExactly(thirdMatch, secondMatch, firstMatch);
    }

    @Test
    void updateScore_nonExistentMatch_shouldThrowException() {
        final Scoreboard scoreboard = new Scoreboard();
        final Match ghost = new Match("USA", "UK");

        assertThatThrownBy(() -> scoreboard.updateMatchScore(ghost, new Score(1, 0)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Match not found");
    }
}
