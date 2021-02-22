package mb.strategies;

import mb.sequences.Seq;

import java.util.function.Function;

///**
// * Evaluates a strategy and prints both its entrance and its results.
// *
// * @param <CTX> the type of context
// * @param <I> the type of input
// * @param <O> the type of outputs
// */
//public final class DebugStrategy1<CTX, A1, I, O> implements Strategy3<CTX, Function<O, String>, Strategy1<CTX, A1, I, O>, A1, I, O>{
//
//    @SuppressWarnings("rawtypes")
//    private static final DebugStrategy1 instance = new DebugStrategy1();
//    @SuppressWarnings("unchecked")
//    public static <CTX, A1, I, O> DebugStrategy1<CTX, A1, I, O> getInstance() { return (DebugStrategy1<CTX, A1, I, O>)instance; }
//
//    private DebugStrategy1() {}
//
//    @Override
//    public String getName() { return "debug1"; }
//
//    @Override
//    public boolean isAnonymous() { return false; }
//
//    @Override
//    public Seq<O> eval(CTX ctx, Function<O, String> transform, Strategy1<CTX, A1, I, O> strategy, A1 arg1, I input) {
//        Seq<O> results = strategy.eval(ctx, arg1, input);
//        System.out.print(strategy + ": ");
//        return results.debug(transform);
//    }
//
//    @Override
//    public String toString() { return getName(); }
//
//}
