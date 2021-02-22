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
//public final class DebugStrategy2<CTX, A1, A2, I, O> implements Strategy4<CTX, Function<O, String>, Strategy2<CTX, A1, A2, I, O>, A1, A2, I, O>{
//
//    @SuppressWarnings("rawtypes")
//    private static final DebugStrategy2 instance = new DebugStrategy2();
//    @SuppressWarnings("unchecked")
//    public static <CTX, A1, A2, I, O> DebugStrategy2<CTX, A1, A2, I, O> getInstance() { return (DebugStrategy2<CTX, A1, A2, I, O>)instance; }
//
//    private DebugStrategy2() {}
//
//    @Override
//    public String getName() { return "debug2"; }
//
//    @Override
//    public boolean isAnonymous() { return false; }
//
//    @Override
//    public Seq<O> eval(CTX ctx, Function<O, String> transform, Strategy2<CTX, A1, A2, I, O> strategy, A1 arg1, A2 arg2, I input) {
//        Seq<O> results = strategy.eval(ctx, arg1, arg2, input);
//        System.out.print(strategy + ": ");
//        return results.debug(transform);
//    }
//
//    @Override
//    public String toString() { return getName(); }
//
//}
