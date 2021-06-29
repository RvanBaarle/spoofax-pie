package mb.statix.strategies.runtime;

import mb.statix.sequences.InterruptibleIterator;
import mb.statix.sequences.Seq;
import mb.statix.strategies.TestListStrategy;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Tests the {@link RepeatStrategy} class.
 */
@SuppressWarnings({"PointlessArithmeticExpression", "ArraysAsListWithZeroOrOneArgument"}) public final class RepeatStrategyTests {

    /** Scores a string by adding the letters. A has value 0, B has value 1, etc. */
    private static int scoreString(String s) {
        int sum = 0;
        for(int i = 0; i < s.length(); i++) {
            sum += (s.charAt(i) - 'A') + 1;
        }
        return sum;
    }

    @Test
    public void shouldApplyStrategy_untilStrategyFails() throws InterruptedException {
        // Arrange
        final RepeatStrategy<Object, String> strategy = RepeatStrategy.getInstance();
        final TestListStrategy<String, String> s = new TestListStrategy<>(it -> scoreString(it) < 5 ? Arrays.asList(it + "A", it + "B", it + "C") : Arrays.asList());

        // Act
        final Seq<String> result = strategy.eval(new Object(), s, "A");

        // Assert
        assertEquals(Arrays.asList(
            "AAAAA",
            "AAAAB",
            "AAAAC",
            "AAAB",
            "AAAC",
            "AABA",
            "AABB",
            "AABC",
            "AAC",
            "ABAA",
            "ABAB",
            "ABAC",
            "ABB",
            "ABC",
            "ACA",
            "ACB",
            "ACC"
        ), result.toList().eval());
    }

    @Test
    public void shouldEvaluateSequenceLazy() throws InterruptedException {
        // Arrange
        final RepeatStrategy<Object, String> strategy = RepeatStrategy.getInstance();
        final TestListStrategy<String, String> s = new TestListStrategy<>(it -> scoreString(it) < 5 ? Arrays.asList(it + "A", it + "B") : Arrays.asList());

        // Act
        final Seq<String> result = strategy.eval(new Object(), s, "A");
        assertEquals(0, s.evalCalls.get());         // not called yet
        assertEquals(0, s.iteratorCalls.get());     // not called yet
        assertEquals(0, s.nextCalls.get());         // not called yet

        final InterruptibleIterator<String> iterator = result.iterator();
        assertEquals(0, s.evalCalls.get());         // not called yet
        assertEquals(0, s.iteratorCalls.get());     // not called yet
        assertEquals(0, s.nextCalls.get());         // not called yet

        assertEquals("AAAAA", iterator.next());
        //                            ["AA", "AB"]
        //                    ["AAA", "AAB"]["AB"]
        //           ["AAAA", "AAAB"]["AAB"]["AB"]
        // ["AAAAA", "AAAAB"]["AAAB"]["AAB"]["AB"]
        //          ["AAAAB"]["AAAB"]["AAB"]["AB"] -> "AAAAA"
        assertEquals(5, s.evalCalls.get());
        assertEquals(5, s.iteratorCalls.get());
        assertEquals(5, s.nextCalls.get());

        assertEquals("AAAAB", iterator.next());
        //          ["AAAAB"]["AAAB"]["AAB"]["AB"]
        //                 []["AAAB"]["AAB"]["AB"] -> "AAAAB"
        assertEquals(6, s.evalCalls.get());
        assertEquals(6, s.iteratorCalls.get());
        assertEquals(7, s.nextCalls.get());

        assertEquals("AAAB", iterator.next());
        //                 []["AAAB"]["AAB"]["AB"]
        //                   ["AAAB"]["AAB"]["AB"]
        //                         []["AAB"]["AB"] -> "AAAB"
        assertEquals(7, s.evalCalls.get());
        assertEquals(7, s.iteratorCalls.get());
        assertEquals(10, s.nextCalls.get());

        assertEquals("AABA", iterator.next());
        //                         []["AAB"]["AB"]
        //                           ["AAB"]["AB"]
        //                ["AABA", "AABB"][]["AB"]
        //                        ["AABB"][]["AB"] -> "AABA"
        assertEquals(9, s.evalCalls.get());
        assertEquals(9, s.iteratorCalls.get());
        assertEquals(14, s.nextCalls.get());

        assertEquals("AABB", iterator.next());
        //                        ["AABB"][]["AB"]
        //                              [][]["AB"] -> "AABB"
        assertEquals(10, s.evalCalls.get());
        assertEquals(10, s.iteratorCalls.get());
        assertEquals(16, s.nextCalls.get());

        assertEquals("ABAA", iterator.next());
        //                              [][]["AB"]
        //                                []["AB"]
        //                                  ["AB"]
        //                        ["ABA", "ABB"][]
        //               ["ABAA", "ABAB"]["ABB"][]
        //                       ["ABAB"]["ABB"][] -> "ABAA"
        assertEquals(13, s.evalCalls.get());
        assertEquals(13, s.iteratorCalls.get());
        assertEquals(22, s.nextCalls.get());

        assertEquals("ABAB", iterator.next());
        //                       ["ABAB"]["ABB"][]
        //                             []["ABB"][] -> "ABAB"
        assertEquals(14, s.evalCalls.get());
        assertEquals(14, s.iteratorCalls.get());
        assertEquals(24, s.nextCalls.get());

        assertEquals("ABB", iterator.next());
        //                             []["ABB"][]
        //                               ["ABB"][]
        //                                    [][] -> "ABB"
        assertEquals(15, s.evalCalls.get());
        assertEquals(15, s.iteratorCalls.get());
        assertEquals(27, s.nextCalls.get());

        assertFalse(iterator.hasNext());
        //                                    [][]
        //                                      [] -> false
        assertEquals(15, s.evalCalls.get());
        assertEquals(15, s.iteratorCalls.get());
        assertEquals(29, s.nextCalls.get());
    }

}
