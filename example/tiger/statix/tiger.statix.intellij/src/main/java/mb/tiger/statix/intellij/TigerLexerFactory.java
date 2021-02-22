package mb.tiger.statix.intellij;

import mb.spoofax.intellij.editor.SpoofaxLexerFactory;
import mb.tiger.statix.intellij.TigerPlugin;
import mb.tiger.statix.spoofax.TigerScope;

import javax.inject.Inject;

@TigerScope
public class TigerLexerFactory extends SpoofaxLexerFactory {
    @Inject public TigerLexerFactory() {
        super(TigerPlugin.getComponent(), TigerPlugin.getResourceServiceComponent(), TigerPlugin.getPieComponent());
    }
}
