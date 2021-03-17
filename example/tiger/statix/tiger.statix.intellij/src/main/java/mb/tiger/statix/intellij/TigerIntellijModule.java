package mb.tiger.statix.intellij;

import com.intellij.openapi.util.IconLoader;
import dagger.Module;
import dagger.Provides;
import mb.spoofax.intellij.IntellijLanguage;
import mb.spoofax.intellij.editor.SpoofaxLexerFactory;
import mb.spoofax.intellij.psi.SpoofaxTokenTypeManager;
import mb.spoofax.intellij.editor.ScopeManager;
import mb.spoofax.intellij.editor.SpoofaxSyntaxHighlighter;

import mb.tiger.statix.spoofax.TigerScope;

import javax.swing.*;

@Module
public class TigerIntellijModule {
    @Provides @TigerScope
    static IntellijLanguage provideSpoofaxLanguage(mb.tiger.statix.intellij.TigerLanguage language) {
        // Downcast because injections in spoofax.intellij require an IntellijLanguage, and dagger does not implicitly downcast.
        return language;
    }

    @Provides @TigerScope
    static SpoofaxLexerFactory provideLexerFactory(TigerLexerFactory lexerFactory) {
        return lexerFactory;
    }

    @Provides @TigerScope
    static Icon provideFileIcon() {
        return IconLoader.getIcon("/META-INF/fileIcon.svg");
    }


    @Provides @TigerScope
    static SpoofaxSyntaxHighlighter.Factory provideSyntaxHighlighterFactory(ScopeManager scopeManager) {
        return new SpoofaxSyntaxHighlighter.Factory(scopeManager); // TODO: generate language-specific class instead.
    }

    @Provides @TigerScope
    static SpoofaxTokenTypeManager provideTokenTypeManager(IntellijLanguage language) {
        return new SpoofaxTokenTypeManager(language); // TODO: generate language-specific class instead.
    }

    @Provides @TigerScope
    static ScopeManager provideScopeManager() {
        return new ScopeManager(); // TODO: generate language-specific class instead.
    }
}
