package mb.spt.spoofax;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import mb.spt.ITestCaseBuilder;
import mb.spt.ITestExpectationEvaluatorService;
import mb.spt.ITestFixtureBuilder;
import mb.spt.ITestFragmentBuilder;
import mb.spt.ITestSuiteBuilder;
import mb.spt.TestCaseBuilder;
import mb.spt.TestExpectationEvaluatorService;
import mb.spt.TestFixtureBuilder;
import mb.spt.TestFragmentBuilder;
import mb.spt.TestSuiteBuilder;
import mb.spt.expectations.ITestExpectation;
import mb.spt.expectations.ITestExpectationBuilder;
import mb.spt.expectations.ITestExpectationExtractor;
import mb.spt.expectations.spoofax.ISpoofaxTestExpectationExtractor;
import mb.spt.expectations.spoofax.ParseSucceedsExpectation;
import mb.spt.expectations.spoofax.ParseToAtermTestExpectation;
import mb.spt.extract.ITestSuiteExtractor;
import mb.spt.io.ITestResultWriter;
import mb.spt.io.WeblabTestResultWriter;
import mb.spt.runner.ITestCaseRunner;
import mb.spt.runner.TestCaseRunner;
import org.spoofax.interpreter.terms.IStrategoTerm;

@Module
public abstract class SptModule extends GeneratedSptModule {

    @Binds abstract ITestSuiteExtractor<IStrategoTerm> bindTestSuiteExtractor(SpoofaxTestSuiteExtractor impl);

    @Binds abstract ITestCaseRunner bindTestCaseRunner(TestCaseRunner impl);

    @Binds abstract ITermTracer bindSpoofaxTermTracer(SpoofaxTermTracer impl);

    @Binds abstract ITestSuiteBuilder bindTestSuiteBuilder(ISpoofaxTestSuiteBuilder impl);

    @Binds abstract ITestExpectationEvaluatorService bindTestExpectationEvaluatorService(TestExpectationEvaluatorService impl);

    @Binds abstract ITestResultWriter bindTestResultWriter(WeblabTestResultWriter impl);

    @Provides static ISpoofaxTestSuiteBuilder provideSpoofaxTestSuiteBuilder() {
        return new SpoofaxTestSuiteBuilder();
    }

    @Provides static ITestCaseBuilder provideTestCaseBuilder() {
        return new TestCaseBuilder();
    }

    @Provides static ITestFixtureBuilder provideTestFixtureBuilder() {
        return new TestFixtureBuilder();
    }

    @Provides static ITestFragmentBuilder provideTestFragmentBuilder() {
        return new TestFragmentBuilder();
    }

    @Provides @IntoSet static ITestExpectationExtractor<IStrategoTerm> provideParseToAtermTestExpectation(ParseToAtermTestExpectation.Extractor e) {
        return e;
    }

    @Provides @IntoSet static ITestExpectationExtractor<IStrategoTerm> provideParseSucceedsExpectation(ParseSucceedsExpectation.Extractor e) {
        return e;
    }


}
