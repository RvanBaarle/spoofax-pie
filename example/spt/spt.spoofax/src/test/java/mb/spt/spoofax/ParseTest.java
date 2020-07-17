package mb.spt.spoofax;

import mb.common.result.Result;
import mb.jsglr1.common.JSGLR1ParseException;
import mb.jsglr1.common.JSGLR1ParseOutput;
import mb.pie.api.MixedSession;
import mb.resource.fs.FSResource;
import org.junit.jupiter.api.Test;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.spoofax.terms.util.TermUtils.isAppl;
import static org.spoofax.terms.util.TermUtils.isStringAt;

import mb.common.result.Result;
import mb.jsglr1.common.JSGLR1ParseException;
import mb.jsglr1.common.JSGLR1ParseOutput;
import mb.pie.api.MixedSession;
import mb.resource.fs.FSResource;
import org.junit.jupiter.api.Test;
import org.spoofax.interpreter.terms.IStrategoTerm;

import static org.junit.jupiter.api.Assertions.*;
import static org.spoofax.terms.util.TermUtils.*;

class ParseTest extends TestBase {
    @Test void testParseTask() throws Exception {
        final FSResource resource = createTextFile("module a\n", "a.spt");
        try(final MixedSession session = newSession()) {
            final Result<JSGLR1ParseOutput, JSGLR1ParseException> result = session.require(parse.createTask(resourceStringSupplier(resource)));
            assertTrue(result.isOk());
            final JSGLR1ParseOutput output = result.unwrap();
            log.info("{}", output);
            final IStrategoTerm ast = output.ast;
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
