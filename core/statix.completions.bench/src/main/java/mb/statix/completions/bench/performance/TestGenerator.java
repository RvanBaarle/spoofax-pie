package mb.statix.completions.bench.performance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.jsglr.shared.ArrayDeque;
import org.spoofax.terms.io.SimpleTextTermWriter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Generates test cases.
 */
public abstract class TestGenerator {

    private final ITermFactory factory;
    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    public TestGenerator(ITermFactory factory) {
        this.factory = factory;
        this.mapper.findAndRegisterModules();
    }

    /**
     * Generates all tests.
     *
     * @param outputDirectory the output directory where the test files are written
     */
    public abstract void generateAll(Path outputDirectory) throws IOException;

    /**
     * Writes a test suite.
     *
     * @param outputDirectory the output directory where the test files are written
     * @param testName the filename of the test (e.g, {@code xmpl/my_test.tig})
     * @param text the full text of the input file
     * @param term the full term that the input file parses to
     */
    protected void writeTestSuite(Path outputDirectory, String testName, String text, IStrategoTerm term) throws IOException {
        // Write xmpl/my_test.tig (the complete code)
        writeString(outputDirectory.resolve(testName), text, StandardCharsets.UTF_8);

        // Write xmpl/my_test.tig.aterm (the complete expected ast)
        writeTerm(outputDirectory.resolve(testName + ".aterm"), term, StandardCharsets.UTF_8);

        // Write xmpl/my_test.tig.yml (the test suite metadata)
        final TestSuiteMetadata metadata = new TestSuiteMetadata();
        // - filename
        metadata.setFilename(testName);
        // - number of nodes in the AST
        metadata.setAstSize(getTermSize(term));
        // - number of characters in the text
        metadata.setTextSize(text.length());
        writeMetadata(outputDirectory.resolve(testName + ".yml"), metadata, this.mapper);

        // Split the test suite into separate test cases,
        // each with a single subterm replaced by a placeholder
        final List<IStrategoTerm> alternatives = findAllAlternatives(term);
        assert alternatives.size() == metadata.getAstSize();

        // Write each test case
        for (int i = 0; i < alternatives.size(); i++) {
            final IStrategoTerm alternative = alternatives.get(i);
            writeTestCase(outputDirectory, testName, i, alternative, "Unknown Sort");
        }
    }

    /**
     * Writes a test case.
     *
     * @param outputDirectory the output directory where the test files are written
     * @param testName the filename of the test (e.g, {@code my_test.tig})
     * @param testIndex the one-based index of the test (e.g., {@code 1})
     * @param incompleteTerm a term with a single placeholder (e.g, {@code Exp-Plhdr()})
     * @param sort the sort of the placeholder (e.g., {@code Exp})
     */
    private void writeTestCase(Path outputDirectory, String testName, int testIndex, IStrategoTerm incompleteTerm, String sort) throws IOException {
        final String testCaseName = String.format("%s.test_%04d", testName, testIndex);

        // Write xmpl/my_test.tig.test_0001.aterm (the incomplete file)
        writeTerm(outputDirectory.resolve(testCaseName + ".aterm"), incompleteTerm, StandardCharsets.UTF_8);

        // Write xmpl/my_test.tig.test_0001.yml (the test case metadata)
        final TestCaseMetadata metadata = new TestCaseMetadata();
        // - filename
        metadata.setFilename(testCaseName);
        // - number of nodes in the AST
        metadata.setAstSize(getTermSize(incompleteTerm));
        // - number of characters in the text
        metadata.setTextSize(-1);    // TODO: pretty-print
        // - sort
        metadata.setSort(sort);
        writeMetadata(outputDirectory.resolve(testCaseName + ".yml"), metadata, this.mapper);
    }

    /**
     * Returns a list of all possible terms where one single (deep) (sub)term
     * is replaced by a term variable (placeholder).
     *
     * @param term the term
     * @return a list of terms
     */
    private List<IStrategoTerm> findAllAlternatives(IStrategoTerm term) {
        final List<IStrategoTerm> newTerms = new ArrayList<>();

        // The first candidate is, obviously, a placeholder for the term itself (e.g., `X-Plhdr`).
        newTerms.add(replaceWithPlaceholder(term));

        // For each subterms, split
        for (int i = 0; i < term.getSubtermCount(); i++) {
            IStrategoTerm subterm = term.getSubterm(i);
            // Get all possible alternatives for the given subterm
            final List<IStrategoTerm> newSubterms = findAllAlternatives(subterm);
            // Replace the given subterms in this term and add the results to the list
            final List<IStrategoTerm> replacedTerms = replaceSubterms(term, i, newSubterms, this.factory);
            newTerms.addAll(replacedTerms);
        }

        return newTerms;
    }

    /**
     * Replaces the term with a placeholder.
     *
     * @param term the term to replace
     * @return the placeholder
     */
    private IStrategoTerm replaceWithPlaceholder(IStrategoTerm term) {
        // TODO: Replace by placeholder term,
        //  we later replace it with a placeholder appl using the Stratego strategies of Tiger.
        return factory.replaceTerm(factory.makeAppl("X-Plhdr"), term);
    }

    /**
     * Replaces a subterm of the given term and returns the result,
     * for each subterm in the given list.
     *
     * @param term the term whose subterm to replace
     * @param index the zero-based index of the subterm to replace
     * @param newSubterms the new subterms to insert
     * @param factory the term factory
     * @return the new terms, each with its subterm replaced
     */
    private static List<IStrategoTerm> replaceSubterms(IStrategoTerm term, int index, List<IStrategoTerm> newSubterms, ITermFactory factory) {
        final List<IStrategoTerm> newTerms = new ArrayList<>(newSubterms.size());
        for (IStrategoTerm newSubterm : newSubterms) {
            newTerms.add(replaceSubterm(term, index, newSubterm, factory));
        }
        return newTerms;
    }

    /**
     * Replaces a subterm of the given term and returns the result.
     *
     * @param term the term whose subterm to replace
     * @param index the zero-based index of the subterm to replace
     * @param newSubterm the new subterm to insert
     * @param factory the term factory
     * @return the new term, with its subterm replaced
     */
    private static IStrategoTerm replaceSubterm(IStrategoTerm term, int index, IStrategoTerm newSubterm, ITermFactory factory) {
        // Construct the new array of subterms
        final IStrategoTerm[] newSubterms = new IStrategoTerm[term.getSubtermCount()];
        for (int i = 0; i < term.getSubtermCount(); i++) {
            newSubterms[i] = term.getSubterm(i);
        }
        newSubterms[index] = newSubterm;

        if (term instanceof IStrategoAppl) {
            final IStrategoAppl applTerm = (IStrategoAppl)term;
            return factory.replaceAppl(applTerm.getConstructor(), newSubterms, applTerm);
        } else if (term instanceof IStrategoList) {
            final IStrategoList listTerm = (IStrategoList)term;
            return factory.replaceList(newSubterms, listTerm);
        } else if (term instanceof IStrategoTuple) {
            final IStrategoTuple tupleTerm = (IStrategoTuple)term;
            return factory.replaceTuple(newSubterms, tupleTerm);
        } else {
            assert newSubterms.length == 0 : "Expected no subterms for a term of type " + term.getClass().getSimpleName();
            throw new IllegalStateException("Unknown term type: " + term.getClass().getSimpleName());
        }
    }

    /**
     * Writes the metadata YAML file to the specified path.
     *
     * @param path the path to write to
     * @param metadata the metadata to write
     * @param mapper the YAML mapper
     */
    private static void writeMetadata(Path path, Object metadata, ObjectMapper mapper) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            mapper.writeValue(writer, metadata);
        }
    }

    /**
     * Writes the specified term to the specified path.
     *
     * @param path the path to write to
     * @param term the term to write
     * @param cs the characer set to use
     */
    private static void writeTerm(Path path, IStrategoTerm term, Charset cs) throws IOException {
        SimpleTextTermWriter writer = new SimpleTextTermWriter(Integer.MAX_VALUE, true, true, true, true);
        writer.writeToPath(term, path, cs);
    }

    /**
     * Writes the specified string to the specified path.
     *
     * @param path the path to write to
     * @param csq the text to write
     * @param cs the characer set to use
     * @return the path
     */
    private static Path writeString(Path path, String csq, Charset cs)
        throws IOException
    {
        Files.write(path,
            csq.getBytes(cs),
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING);

        return path;
    }

    /**
     * Metadata for a test suite.
     */
    private static final class TestSuiteMetadata {
        private String filename;
        public String getFilename() { return filename; }
        public void setFilename(String filename) { this.filename = filename; }

        private int textSize;
        public int getTextSize() { return textSize; }
        public void setTextSize(int textSize) { this.textSize = textSize; }

        private int astSize;
        public int getAstSize() { return astSize; }
        public void setAstSize(int astSize) { this.astSize = astSize; }
    }

    /**
     * Metadata for a test case.
     */
    private static final class TestCaseMetadata {
        private String filename;
        public String getFilename() { return filename; }
        public void setFilename(String filename) { this.filename = filename; }

        private int textSize;
        public int getTextSize() { return textSize; }
        public void setTextSize(int textSize) { this.textSize = textSize; }

        private int astSize;
        public int getAstSize() { return astSize; }
        public void setAstSize(int astSize) { this.astSize = astSize; }

        private String sort;
        public String getSort() { return sort; }
        public void setSort(String sort) { this.sort = sort; }
    }

    /**
     * Computes the size (number of nodes) in a term.
     *
     * @param term the term
     * @return the number of nodes
     */
    private static int getTermSize(IStrategoTerm term) {
        int size = 0;

        final ArrayDeque<IStrategoTerm> worklist = new ArrayDeque<>();
        worklist.push(term);
        while (!worklist.isEmpty()) {
            IStrategoTerm currentTerm = worklist.pop();
            size += 1;
            for (IStrategoTerm subterm : currentTerm.getSubterms()) {
                worklist.push(subterm);
            }
        }

        return size;
    }

}
