package mb.statix.common;

import io.usethesource.capsule.Set;
import mb.nabl2.terms.ITerm;
import mb.nabl2.terms.unification.UnifierFormatter;
import mb.nabl2.terms.unification.ud.IUniDisunifier;
import mb.statix.solver.IConstraint;
import org.metaborg.util.functions.Function2;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * A search state with a selected constraint.
 *
 * @param <C> the type of selected constraint
 */
public final class SelectedConstraintSolverState<C extends IConstraint> {

    private final SolverState solverState;

    private final C selected;
    private final Set.Immutable<IConstraint> unselected;

    public SelectedConstraintSolverState(SolverState solverState, C selected) {
        this.solverState = solverState;
        Set.Immutable<IConstraint> constraints = solverState.getConstraints();
        if(!constraints.contains(selected)) {
            throw new IllegalArgumentException("The focus constraint is not one of the constraints in the state.");
        }
        this.selected = selected;
        this.unselected = constraints.__remove(selected);
    }

    public SolverState getInnerState() {
        return solverState;
    }

    public C getSelected() {
        return selected;
    }

    public Set<IConstraint> getUnselected() {
        return unselected;
    }

    @Override public String toString() {
        StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);
        try {
            writer.println("SelectedConstraintSolverState:");
            write(writer, (t, u) -> new UnifierFormatter(u, 2).format(t));
        } catch (IOException e) {
            // This can never happen.
            throw new RuntimeException(e);
        }
        return out.toString();
    }

    public void write(PrintWriter writer, Function2<ITerm, IUniDisunifier, String> prettyprinter) throws IOException {
        final IUniDisunifier unifier = getInnerState().getState().unifier();
        writer.println("| selected:");
        writer.println("|   " + selected.toString(t -> prettyprinter.apply(t, unifier)));
        this.getInnerState().write(writer, prettyprinter);
    }

}
