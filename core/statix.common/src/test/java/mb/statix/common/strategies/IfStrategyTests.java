package mb.statix.common.strategies;

import mb.statix.common.sequences.InterruptibleConsumer;
import mb.statix.common.sequences.Sequence;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Tests the {@code IfStrategy} class.
 */
@SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
public final class IfStrategyTests {

    @Test
    public void shouldApplyOnFailureStrategy_whenConditionIsEmpty() throws InterruptedException {
//        // Arrange
//        final Strategy<Object, String, Integer> condition = (o, input) -> Sequence.of(); //.flatMap(it -> Arrays.stream(it.split("\\w")).map(String::length));
//        final Strategy<Object, Integer, Boolean> onSuccess = (o, input) -> Sequence.of(input).map(it -> it % 2 == 0);
//        final Strategy<Object, String, Boolean> onFailure = (o, input) -> Sequence.of(input).map(String::length);
//        final IfStrategy<Object, String, Integer, Boolean> sut = new IfStrategy<>(
//            condition,
//            onSuccess,
//            onFailure
//        );
//
//        // Act
//        final Sequence<Boolean> seq = sut.apply(new Object(), "abc");
//
//        // Assert
//        assertEquals(Arrays.asList(), seq.toList());
    }
}
