package mb.statix.strategies;

import mb.statix.common.SolverContext;
import mb.statix.common.SolverState;
import mb.statix.constraints.CAstId;
import mb.statix.solver.IConstraint;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


///**
// * Removes AST index constraints that could not be solved.
// */
//public final class RemoveAstIdConstraintsStrategy implements Strategy<SolverContext, SolverState, SolverState> {
//
//    @Override
//    public List<SolverState> eval(SolverContext ctx, SolverState input) {
//        List<IConstraint> toRemove = input.getConstraints().stream().filter(c -> c instanceof CAstId).collect(Collectors.toList());
//        SolverState newState = input.updateConstraints(Collections.emptyList(), toRemove);
//        return Collections.singletonList(newState);
//    }
//
//    @Override
//    public String toString() {
//        return "removeAstIdConstraints";
//    }
//
//}