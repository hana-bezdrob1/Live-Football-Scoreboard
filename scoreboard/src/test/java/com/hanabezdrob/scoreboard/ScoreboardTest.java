package com.hanabezdrob.scoreboard;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

class ScoreboardTest {
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
    void updateMatchScore_shouldUpdateExistingMatchScore() {
        final Scoreboard scoreboard = new Scoreboard();
        final Match match = scoreboard.startMatch("Bosnia and Herzegovina", "Norway");

        scoreboard.updateMatchScore(match, new Score(1, 0));

        final Match updatedMatch = scoreboard.getMatchesInProgress().getFirst();
        assertThat(updatedMatch.getHomeScore()).isEqualTo(1);
        assertThat(updatedMatch.getAwayScore()).isEqualTo(0);
    }

    @Test
    void updateMatchScore_nonExistentMatch_shouldThrowException() {
        final Scoreboard scoreboard = new Scoreboard();
        final Match ghost = new Match("USA", "UK");

        assertThatThrownBy(() -> scoreboard.updateMatchScore(ghost, new Score(1, 0)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Match not found");
    }

    @Test
    void updateMatchScore_sameScore_noOp() {
        final Scoreboard scoreboard = new Scoreboard();
        final Match match = scoreboard.startMatch("Germany", "Spain");
        final Score originalScore = match.score();
        final Match result = scoreboard.updateMatchScore(match, originalScore);

        assertThat(result).isSameAs(match);
        assertThat(scoreboard.getMatchesInProgress()).containsExactly(match);
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
    void finishMatch_nullIgnored() {
        final Scoreboard scoreboard = new Scoreboard();
        final Match match = scoreboard.startMatch("Bosnia and Herzegovina", "Sweden");
        scoreboard.finishMatch(null);
        assertThat(scoreboard.getMatchesInProgress()).containsExactly(match);
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
    void getSummary_noMatches_returnsEmptyList() {
        final Scoreboard scoreboard = new Scoreboard();
        assertThat(scoreboard.getSummary()).isEmpty();
    }

    @Test
    void getMatchesInProgress_onModification_shouldThrowException() {
        final Scoreboard scoreboard = new Scoreboard();
        scoreboard.startMatch("Germany", "Spain");
        final List<Match> list = scoreboard.getMatchesInProgress();
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> list.add(new Match("X","Y")));
    }

    @Test
    void largeSequence_ofOperations_maintainsCorrectness() {
        final Scoreboard scoreboard = new Scoreboard();

        Match firstMatch = scoreboard.startMatch("Bosnia and Herzegovina","Croatia");
        firstMatch = scoreboard.updateMatchScore(firstMatch, new Score(1,1));

        Match secondMatch = scoreboard.startMatch("Serbia","Italy");
        secondMatch = scoreboard.updateMatchScore(secondMatch, new Score(2,0));

        scoreboard.finishMatch(firstMatch);
        assertThat(scoreboard.getMatchesInProgress()).containsExactly(secondMatch);

        Match thirdMatch = scoreboard.startMatch("Bosnia and Herzegovina","Spain");
        thirdMatch = scoreboard.updateMatchScore(thirdMatch, new Score(3,2));

        // thirdMatch (total = 5) then secondMatch (total = 2)
        final List<Match> summary = scoreboard.getSummary();
        assertThat(summary).containsExactly(thirdMatch, secondMatch);

        scoreboard.finishMatch(secondMatch);
        scoreboard.finishMatch(thirdMatch);
        assertThat(scoreboard.getMatchesInProgress()).isEmpty();
    }

    @Test
    void startMatch_usesInjectedValidator() {
        final AtomicBoolean called = new AtomicBoolean(false);
        final MatchValidator validator = new MatchValidator() {
            @Override
            public void validateNewMatch(final String home, final String away, final Iterable<Match> existing) {
                called.set(true);
            }

            @Override
            public void validateScoreUpdate(final Match oldMatch, final Score newScore) {
                // no‑op for this test
            }
        };
        final Scoreboard scoreboard = new Scoreboard(validator);
        scoreboard.startMatch("Bosnia and Herzegovina","Croatia");
        assertThat(called).isTrue();
    }
}
