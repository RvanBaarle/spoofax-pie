package mb.spoofax.compiler.spoofaxcore;

import mb.common.util.ListView;
import mb.pie.api.ExecContext;
import mb.pie.api.TaskDef;
import mb.resource.hierarchical.ResourcePath;
import mb.spoofax.compiler.util.ClassKind;
import mb.spoofax.compiler.util.GradleConfiguredDependency;
import mb.spoofax.compiler.util.TemplateCompiler;
import mb.spoofax.compiler.util.TemplateWriter;
import mb.spoofax.compiler.util.TypeInfo;
import org.immutables.value.Value;

import javax.inject.Inject;
import java.io.IOException;
import java.io.Serializable;
import java.util.Optional;

@Value.Enclosing
public class ParserAdapterCompiler implements TaskDef<ParserAdapterCompiler.Input, ParserAdapterCompiler.Output> {
    private final TemplateWriter parseTaskDefTemplate;
    private final TemplateWriter tokenizeTaskDefTemplate;

    @Inject public ParserAdapterCompiler(TemplateCompiler templateCompiler) {
        this.parseTaskDefTemplate = templateCompiler.getOrCompileToWriter("parser/ParseTaskDef.java.mustache");
        this.tokenizeTaskDefTemplate = templateCompiler.getOrCompileToWriter("parser/TokenizeTaskDef.java.mustache");
    }


    @Override public String getId() {
        return getClass().getName();
    }

    @Override public Output exec(ExecContext context, Input input) throws IOException {
        final Output.Builder outputBuilder = Output.builder();
        if(input.classKind().isManualOnly()) return outputBuilder.build(); // Nothing to generate: return.
        final ResourcePath classesGenDirectory = input.classesGenDirectory();
        parseTaskDefTemplate.write(context, input.genParseTaskDef().file(classesGenDirectory), input);
        tokenizeTaskDefTemplate.write(context, input.genTokenizeTaskDef().file(classesGenDirectory), input);
        return outputBuilder.build();
    }


    public ListView<GradleConfiguredDependency> getDependencies(Input input) {
        return ListView.of(
            GradleConfiguredDependency.api(input.shared().jsglr1PieDep())
        );
    }


    @Value.Immutable public interface Input extends Serializable {
        class Builder extends ParserAdapterCompilerData.Input.Builder {}

        static Builder builder() { return new Builder(); }


        /// Kinds of classes (generated/extended/manual)

        @Value.Default default ClassKind classKind() {
            return ClassKind.Generated;
        }


        /// Classes

        default ResourcePath classesGenDirectory() {
            return adapterProject().project().genSourceSpoofaxJavaDirectory();
        }

        // Parse task definition

        @Value.Default default TypeInfo genParseTaskDef() {
            return TypeInfo.of(adapterProject().taskPackageId(), shared().defaultClassPrefix() + "Parse");
        }

        Optional<TypeInfo> manualParseTaskDef();

        default TypeInfo parseTaskDef() {
            if(classKind().isManual() && manualParseTaskDef().isPresent()) {
                return manualParseTaskDef().get();
            }
            return genParseTaskDef();
        }

        // Tokenize task definition

        @Value.Default default TypeInfo genTokenizeTaskDef() {
            return TypeInfo.of(adapterProject().taskPackageId(), shared().defaultClassPrefix() + "Tokenize");
        }

        Optional<TypeInfo> manualTokenizeTaskDef();

        default TypeInfo tokenizeTaskDef() {
            if(classKind().isManual() && manualTokenizeTaskDef().isPresent()) {
                return manualTokenizeTaskDef().get();
            }
            return genTokenizeTaskDef();
        }


        // List of all generated files

        default ListView<ResourcePath> generatedFiles() {
            if(classKind().isManualOnly()) {
                return ListView.of();
            }
            return ListView.of(
                genParseTaskDef().file(classesGenDirectory()),
                genTokenizeTaskDef().file(classesGenDirectory())
            );
        }


        /// Automatically provided sub-inputs.

        Shared shared();

        AdapterProject adapterProject();

        ParserLanguageCompiler.Input languageProjectInput();


        @Value.Check default void check() {
            final ClassKind kind = classKind();
            final boolean manual = kind.isManualOnly();
            if(!manual) return;
            if(!manualParseTaskDef().isPresent()) {
                throw new IllegalArgumentException("Kind '" + kind + "' indicates that a manual class will be used, but 'manualParseTaskDef' has not been set");
            }
            if(!manualTokenizeTaskDef().isPresent()) {
                throw new IllegalArgumentException("Kind '" + kind + "' indicates that a manual class will be used, but 'manualTokenizeTaskDef' has not been set");
            }
        }
    }

    @Value.Immutable public interface Output extends Serializable {
        class Builder extends ParserAdapterCompilerData.Output.Builder {}

        static Builder builder() { return new Builder(); }
    }
}
