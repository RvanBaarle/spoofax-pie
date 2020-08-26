package mb.spt.io;

import mb.spt.ITestSuiteResult;

import javax.inject.Inject;
import java.io.IOException;
import java.io.Writer;

public final class WeblabTestResultWriter implements ITestResultWriter {

    @Inject WeblabTestResultWriter() { }

    @Override
    public void write(ITestSuiteResult result, Writer writer) throws IOException {
        throw new UnsupportedOperationException("Not implemented");
    }
}
