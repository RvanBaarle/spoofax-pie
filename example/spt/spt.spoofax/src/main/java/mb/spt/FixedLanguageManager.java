package mb.spt;

import mb.common.result.Result;
import mb.jsglr1.common.JSGLR1ParseException;
import mb.jsglr1.common.JSGLR1ParseOutput;
import mb.jsglr1.pie.JSGLR1ParseTaskDef;
import mb.log.slf4j.SLF4JLoggerFactory;
import mb.pie.api.Supplier;
import mb.pie.api.Task;
import mb.pie.api.TaskDef;
import mb.pie.runtime.PieBuilderImpl;
import mb.sdf3.spoofax.DaggerSdf3Component;
import mb.sdf3.spoofax.Sdf3Component;
import mb.sdf3.spoofax.Sdf3Instance;
import mb.spoofax.core.platform.DaggerPlatformComponent;
import mb.spoofax.core.platform.LoggerFactoryModule;
import mb.spoofax.core.platform.PlatformComponent;
import mb.spoofax.core.platform.PlatformPieModule;
import org.spoofax.interpreter.terms.IStrategoTerm;
import mb.sdf3.spoofax.task.Sdf3Parse;

public final class FixedLanguageManager implements ILanguageManager {
    @Override
    public TaskDef<Supplier<String>, Result<JSGLR1ParseOutput, JSGLR1ParseException>> getParseTaskDef(String languageId) {
        switch (languageId) {
            case "sdf3":
                final PlatformComponent platformComponent = DaggerPlatformComponent.builder()
                    .loggerFactoryModule(new LoggerFactoryModule(new SLF4JLoggerFactory()))
                    .platformPieModule(new PlatformPieModule(PieBuilderImpl::new))
                    .build();
                final Sdf3Component sdf3Component = DaggerSdf3Component.builder()
                    .platformComponent(platformComponent)
                    .build();
                return sdf3Component.getParse();
                // TODO: Add statix, stratego
            case "weblab-language":
                // The Weblab test language
                // TODO
            default:
                throw new UnsupportedOperationException("Unsupported language: " + languageId);
        }
    }
}
