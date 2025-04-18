package com.hanabezdrob.scoreboard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class FootballMatchValidatorTest {
    private FootballMatchValidator validator;
    private Match existing;

    @BeforeEach
    void setUp() {
        validator = new FootballMatchValidator();
        existing = new Match("Bosnia and Herzegovina", "Croatia");
    }

    @Test
    void validateNewMatch_nullTeamName_shouldThrowException() {
        assertThatThrownBy(() -> validator.validateNewMatch(null, "Serbia", emptyList()))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("must not be null");
    }

    @ParameterizedTest
    @MethodSource("invalidTeamNamesProvider")
    void validateNewMatch_blankAway_shouldThrowException(final String home, final String away) {
        assertThatThrownBy(() -> validator.validateNewMatch(home, away, emptyList()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must not be blank");
    }

    @Test
    void validateNewMatch_teamPlayWithItself_shouldThrowException() {
        assertThatThrownBy(() -> validator.validateNewMatch("Bosnia and Herzegovina", "Bosnia and Herzegovina", emptyList()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be different");
    }

    @Test
    void validateNewMatch_homeAlreadyPlaying_shouldThrowException() {
        assertThatThrownBy(() -> validator.validateNewMatch("Croatia", "Mexico", List.of(existing)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Team Croatia has a match in progress");
    }

    @Test
    void validateNewMatch_awayAlreadyPlaying_shouldThrowException() {
        assertThatThrownBy(() -> validator.validateNewMatch("Morocco", "Bosnia and Herzegovina", List.of(existing)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Team Bosnia and Herzegovina has a match in progress");
    }

    @Test
    void validateNewMatch_validInputs_passes() {
        validator.validateNewMatch("France", "Belgium", List.of(existing));
    }

    @Test
    void validateScoreUpdate_sameScore_noException() {
        final Match match = new Match("Malta","Portugal", new Score(1,1), Instant.EPOCH);
        validator.validateScoreUpdate(match, new Score(1,1));
    }

    @Test
    void validateScoreUpdate_scoreLowering_shouldThrowException() {
        final Match match = new Match("Sweden","Iceland", new Score(2,1), Instant.EPOCH);
        assertThatThrownBy(() -> validator.validateScoreUpdate(match, new Score(1,1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Scores may not go down during a game");
    }

    @Test
    void validateScoreUpdate_negativeScore_shouldThrowException() {
        final Match match = new Match("Sweden","Iceland", new Score(0,0), Instant.EPOCH);
        assertThatThrownBy(() -> validator.validateScoreUpdate(match, new Score(-1,2)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Scores may not be negative");
    }

    @Test
    void validateScoreUpdate_aboveMaxScore_shouldThrowException() {
        final Match match = new Match("Poland","Denmark", new Score(29,0), Instant.EPOCH);
        assertThatThrownBy(() -> validator.validateScoreUpdate(match, new Score(31, 0)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("exceeds maximum allowed score");
    }

    @Test
    void validateScoreUpdate_deltaExceedsMax_shouldThrowException() {
        final Match match = new Match("The Netherlands","San Marino", new Score(5,0), Instant.EPOCH);
        assertThatThrownBy(() -> validator.validateScoreUpdate(match, new Score(20, 0)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("exceeds maximum score increase");
    }

    @Test
    void validateScoreUpdate_validIncrease_passes() {
        final Match match = new Match("Cyprus","China", new Score(1,1), Instant.EPOCH);
        final Score allowed = new Score(1 + Scoreboard.MAX_DELTA, 1);
        validator.validateScoreUpdate(match, allowed);
    }

    @Test
    void validateScoreUpdate_validIncreaseToMax_passes() {
        final Match match = new Match("Latvia","Italy", new Score(25,0), Instant.EPOCH);
        final Score allowed = new Score(Scoreboard.MAX_SCORE, 0);
        validator.validateScoreUpdate(match, allowed);
    }

    static Stream<Arguments> invalidTeamNamesProvider() {
        return Stream.of(
                // homeTeam invalid, awayTeam valid
                Arguments.of("", "TeamB"),
                Arguments.of("   ", "TeamB"),

                // homeTeam valid, awayTeam invalid
                Arguments.of("TeamA", ""),
                Arguments.of("TeamA", "   "),

                // both invalid at once
                Arguments.of("", ""),
                Arguments.of("   ", "   ")
        );
    }
}
