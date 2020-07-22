package mb.spt.expectations.spoofax;

import mb.common.message.Message;
import mb.common.message.Severity;
import mb.common.region.Region;
import mb.common.result.Result;
import mb.pie.api.ExecException;
import mb.pie.api.MixedSession;
import mb.pie.api.Pie;
import mb.pie.api.Task;
import mb.spt.FragmentUtils;
import mb.spt.ITestCase;
import mb.spt.ITestSuite;
import mb.spt.expectations.ITestExpectationBuilder;
import mb.spt.expectations.ITestExpectationEvaluator;
import mb.spt.expectations.ITestExpectationResult;
import mb.spt.expectations.ITestExpectationResultBuilder;
import mb.spt.expectations.TestExpectation;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.util.TermUtils;

import javax.inject.Inject;

import static mb.spt.expectations.BuilderUtils.checkFieldNotNull;

/**
 * Expectation that tests whether the fragment parses unambiguously.
 */
public final class ParseSucceedsExpectation extends TestExpectation {

    /**
     * Initializes a new instance of the {@link TestExpectation} class.
     *
     * @param region the region of the test expectation
     */
    public ParseSucceedsExpectation(@Nullable Region region) {
        super(region);
    }



    /**
     * Builder for {@link ParseSucceedsExpectation}.
     */
    public static final class Builder implements ITestExpectationBuilder {

        private @Nullable Region region = null;

        @Override public Builder withRegion(@Nullable Region region) {
            this.region = region;
            return this;
        }

        @Override public ParseSucceedsExpectation build() {

            return new ParseSucceedsExpectation(region);
        }

        @Override public Builder reset() {
            this.region = null;

            return this;
        }
    }



    /**
     * Extractor for {@link ParseSucceedsExpectation}.
     */
    public static final class Extractor implements ISpoofaxTestExpectationExtractor {

        // ParseToAterm(ToAterm(ast))
        private static final String PARSESUCCEEDS_CONS = "ParseSucceeds";

        @Inject public Extractor() {}

        @Override public boolean canExtract(IStrategoTerm expectationTerm) {
            return TermUtils.isAppl(expectationTerm, PARSESUCCEEDS_CONS, 0);
        }

        @Override public Builder extract(IStrategoTerm expectationTerm) {
            return new Builder();
        }
    }


    /**
     * Evaluator for {@link ParseSucceedsExpectation}.
     */
    public static final class Evaluator implements ITestExpectationEvaluator<ParseSucceedsExpectation> {

        private final ITestExpectationResultBuilder testExpectationResultBuilder;
        private final Pie pie;

        @Inject public Evaluator(Pie pie, ITestExpectationResultBuilder testExpectationResultBuilder) {
            this.pie = pie;
            this.testExpectationResultBuilder = testExpectationResultBuilder;
        }

        @Override public ITestExpectationResult evaluate(ParseSucceedsExpectation testExpectation, ITestCase testCase, ITestSuite testSuite)
            throws ExecException, InterruptedException {
            final ITestExpectationResultBuilder builder = testExpectationResultBuilder.reset();
            builder.withTestExpectation(testExpectation);

            final String fragmentText = FragmentUtils.getFragmentText(testSuite, testCase, testCase.getFragment());
            final Task<Result<IStrategoTerm, ?>> task = null; // TODO: Get the parse task
            final Result<IStrategoTerm, ?> parseResult;
            try (MixedSession session = pie.newSession()) {
                parseResult = session.require(task);
            }

            parseResult.ifElse(actualTerm -> {
                // FIXME: Test for ambiguities
                builder.isSuccessful(true);
            }, err -> {
                builder.isSuccessful(false);
                builder.addMessage(new Message("Expected the input fragment to parse successfully: " + err.getMessage(), Severity.Error, testExpectation.getRegion()));
            });

            return builder.build();
        }
    }

}