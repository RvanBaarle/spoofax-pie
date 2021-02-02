package mb.tiger.statix;

import mb.resource.hierarchical.HierarchicalResource;
import mb.spoofax.compiler.interfaces.spoofaxcore.ParserFactory;

public class TigerParserFactory implements ParserFactory {
    private final mb.tiger.statix.TigerParseTable parseTable;

    public TigerParserFactory(HierarchicalResource definitionDir) {
        this.parseTable = mb.tiger.statix.TigerParseTable.fromDefinitionDir(definitionDir);
    }

    @Override public mb.tiger.statix.TigerParser create() {
        return new mb.tiger.statix.TigerParser(parseTable);
    }
}
