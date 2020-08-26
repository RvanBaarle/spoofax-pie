package mb.spt.io;

import mb.spt.ITestSuiteResult;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Writes test results.
 */
public interface ITestResultWriter {

    default void write(ITestSuiteResult result, OutputStream stream) throws IOException {
        write(result, stream, StandardCharsets.UTF_8);
    }

    default void write(ITestSuiteResult result, OutputStream stream, Charset characterSet) throws IOException {
        // TODO: Don't close the stream when the writer is closed
        // TODO: Needs buffering?
        try (final OutputStreamWriter writer = new OutputStreamWriter(stream, characterSet)) {
            write(result, writer);
        }
    }

    void write(ITestSuiteResult result, Writer writer) throws IOException;

}
