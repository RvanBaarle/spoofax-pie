package mb.strategies;

import java.util.function.Consumer;

public interface Context {

    StrategyEventHandler getEventHandler();

    Consumer<Long> getExpandPredicateReporter();
    Consumer<Long> getExpandInjectionReporter();
    Consumer<Long> getExpandQueryReporter();
    Consumer<Long> getExpandDeterministicReporter();
    Consumer<Long> getReporter(int index);

}
