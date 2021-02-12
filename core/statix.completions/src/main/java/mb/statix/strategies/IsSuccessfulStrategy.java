package mb.statix.strategies;

import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.ITermVar;
import mb.nabl2.terms.unification.UnifierFormatter;
import mb.sequences.Seq;
import mb.statix.common.SolverContext;
import mb.statix.common.SolverState;
import mb.statix.constraints.messages.MessageKind;
import mb.strategies.Strategy;
import org.checkerframework.checker.nullness.qual.Nullable;


/**
 * Search strategy that only succeeds if the search state has no errors.
 */
public final class IsSuccessfulStrategy implements Strategy<SolverContext, SolverState, SolverState> {

    @SuppressWarnings("rawtypes")
    private static final IsSuccessfulStrategy instance = new IsSuccessfulStrategy();
    @SuppressWarnings("unchecked")
    public static IsSuccessfulStrategy getInstance() { return instance; }

    private IsSuccessfulStrategy() {}

    @Override
    public String getName() {
        return "isSuccessful";
    }

    @Override
    public Seq<SolverState> apply(SolverContext ctx, SolverState state) {
        if (state.hasErrors()) {
            @Nullable final ITermVar focusVar = ctx.getFocusVar();
            System.out.println("isSuccessful(): rejected: " + (focusVar != null ? state.project(focusVar) : ""));
            state.getMessages().entrySet().stream().filter(entry -> entry.getValue().kind().equals(MessageKind.ERROR)).forEach(e -> {
                final String message = e.getValue().toString(ITerm::toString);
                final String constraintStr = e.getKey().toString(t -> new UnifierFormatter(state.getState().unifier(), 8).format(t));
                if (message.isEmpty()) {
                    System.out.println("- " + constraintStr);
                } else {
                    System.out.println("- " + message);
                    System.out.println("  " + constraintStr);
                }
            });
            return Seq.empty();
        } else {
            //System.out.println("isSuccessful(): Accepted");
            return Seq.of(state);
        }
    }

}
