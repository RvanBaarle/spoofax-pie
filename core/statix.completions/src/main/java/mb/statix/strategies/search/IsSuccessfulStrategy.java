package mb.statix.strategies.search;

import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.ITermVar;
import mb.nabl2.terms.unification.UnifierFormatter;
import mb.statix.common.SolverContext;
import mb.statix.common.SolverState;
import mb.statix.common.strategies.search.SearchStrategy;
import mb.statix.constraints.messages.IMessage;
import mb.statix.constraints.messages.MessageKind;
import mb.statix.solver.IConstraint;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * Search strategy that only succeeds if the search state has no errors.
 */
public final class IsSuccessfulStrategy implements SearchStrategy {

    @Override
    public List<SolverState> apply(SolverContext ctx, SolverState state) {
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
            return Collections.emptyList();
        } else {
            //System.out.println("isSuccessful(): Accepted");
            return Collections.singletonList(state);
        }
    }

    @Override
    public String toString() {
        return "isSuccessful";
    }

}
