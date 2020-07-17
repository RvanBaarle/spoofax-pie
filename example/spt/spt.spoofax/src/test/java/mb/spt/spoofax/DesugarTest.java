package mb.spt.spoofax;

import mb.common.result.Result;
import mb.pie.api.MixedSession;
import mb.resource.text.TextResource;
import org.junit.jupiter.api.Test;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;

import static mb.spt.spoofax.TestUtils.assertResultOk;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.spoofax.terms.util.TermUtils.*;
import static org.spoofax.terms.util.TermUtils.toApplAt;

public class DesugarTest extends TestBase {
    @Test void testDesugarTask() throws Exception {
        final TextResource resource = createTextResource("module a\n", "a.spt");
//        final Sdf3ToPrettyPrinter taskDef = languageComponent.getToPrettyPrinter();
        try(final MixedSession session = newSession()) {
            final Result<IStrategoTerm, ?> result = session.require(desugar.createTask(desugarSupplier(resource)));
            assertResultOk(result);
            final IStrategoTerm ast = result.unwrap();
            log.info("{}", ast);
            assertNotNull(ast);
            assertTrue(isAppl(ast, "TestSuite"));
            assertTrue(isListAt(ast, 0));
            final IStrategoList header = toListAt(ast, 0);
            assertTrue(isApplAt(header, 0, "Name"));
            final IStrategoAppl name = toApplAt(header, 0);
            assertTrue(isStringAt(name, 0, "a"));
        }
    }
}
