package mb.spt;

import mb.common.result.Result;
import mb.pie.api.ExecContext;
import mb.resource.ResourceKey;
import mb.resource.hierarchical.ResourcePath;
import mb.spoofax.core.language.LanguageComponent;
import org.checkerframework.checker.nullness.qual.Nullable;

public class StaticLanguageUnderTestProvider implements LanguageUnderTestProvider {
    private final LanguageComponent languageComponent;

    public StaticLanguageUnderTestProvider(LanguageComponent languageComponent) {
        this.languageComponent = languageComponent;
    }

    @Override
    public Result<LanguageComponent, ?> provide(ExecContext context, ResourceKey file, @Nullable ResourcePath rootDirectoryHint, @Nullable String languageIdHint) {
        return Result.ofOk(languageComponent);
    }
}