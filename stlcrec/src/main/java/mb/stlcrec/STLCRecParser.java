package mb.stlcrec;

import mb.jsglr1.common.JSGLR1ParseResult;
import mb.jsglr1.common.JSGLR1Parser;
import mb.resource.ResourceKey;
import org.checkerframework.checker.nullness.qual.Nullable;

public class STLCRecParser {
    private final JSGLR1Parser parser;

    public STLCRecParser(STLCRecParseTable parseTable) {
        this.parser = new JSGLR1Parser(parseTable.parseTable);
    }

    public JSGLR1ParseResult parse(String text, String startSymbol) throws InterruptedException {
        return parser.parse(text, startSymbol, null);
    }

    public JSGLR1ParseResult parse(String text, String startSymbol, @Nullable ResourceKey resource) throws InterruptedException {
        return parser.parse(text, startSymbol, resource);
    }
}
