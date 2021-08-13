package mb.spoofax.compiler.adapter;

import mb.common.option.Option;
import mb.spoofax.compiler.language.ClassLoaderResourcesCompiler;
import mb.spoofax.compiler.language.LanguageProject;
import mb.spoofax.compiler.language.LanguageProjectCompiler;
import mb.spoofax.compiler.util.GradleDependency;
import mb.spoofax.compiler.util.Shared;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Facade for consistently and easily building a {@link AdapterProjectCompiler.Input} instance.
 */
public class AdapterProjectCompilerInputBuilder {
    public final ClassLoaderResourcesCompiler.Input.Builder classLoaderResources = ClassLoaderResourcesCompiler.Input.builder();
    public final GetSourceFilesAdapterCompiler.Input.Builder getSourceFiles = GetSourceFilesAdapterCompiler.Input.builder();

    private boolean parserEnabled = false;
    public final ParserAdapterCompiler.Input.Builder parser = ParserAdapterCompiler.Input.builder();

    private boolean stylerEnabled = false;
    public final StylerAdapterCompiler.Input.Builder styler = StylerAdapterCompiler.Input.builder();

    private boolean strategoRuntimeEnabled = false;
    public final StrategoRuntimeAdapterCompiler.Input.Builder strategoRuntime = StrategoRuntimeAdapterCompiler.Input.builder();

    private boolean constraintAnalyzerEnabled = false;
    public final ConstraintAnalyzerAdapterCompiler.Input.Builder constraintAnalyzer = ConstraintAnalyzerAdapterCompiler.Input.builder();

    private boolean multilangAnalyzerEnabled = false;
    public final MultilangAnalyzerAdapterCompiler.Input.Builder multilangAnalyzer = MultilangAnalyzerAdapterCompiler.Input.builder();

    private boolean completerEnabled = false;
    public final CompleterAdapterCompiler.Input.Builder completer = CompleterAdapterCompiler.Input.builder();

    private boolean referenceResolutionEnabled = false;
    public final ReferenceResolutionAdapterCompiler.Input.Builder referenceResolution = ReferenceResolutionAdapterCompiler.Input.builder();

    public AdapterProjectCompiler.Input.Builder project = AdapterProjectCompiler.Input.builder();


    public ClassLoaderResourcesCompiler.Input.Builder withClassloaderResources() {
        return classLoaderResources;
    }

    public ParserAdapterCompiler.Input.Builder withParser() {
        parserEnabled = true;
        return parser;
    }

    public StylerAdapterCompiler.Input.Builder withStyler() {
        stylerEnabled = true;
        return styler;
    }

    public StrategoRuntimeAdapterCompiler.Input.Builder withStrategoRuntime() {
        strategoRuntimeEnabled = true;
        return strategoRuntime;
    }

    public ConstraintAnalyzerAdapterCompiler.Input.Builder withConstraintAnalyzer() {
        constraintAnalyzerEnabled = true;
        return constraintAnalyzer;
    }

    public MultilangAnalyzerAdapterCompiler.Input.Builder withMultilangAnalyzer() {
        multilangAnalyzerEnabled = true;
        return multilangAnalyzer;
    }

    public CompleterAdapterCompiler.Input.Builder withCompleter() {
        completerEnabled = true;
        return completer;
    }

    public ReferenceResolutionAdapterCompiler.Input.Builder withReferenceResolution() {
        referenceResolutionEnabled = true;
        return referenceResolution;
    }


    public AdapterProjectCompiler.Input build(LanguageProjectCompiler.Input languageProjectInput, Option<GradleDependency> languageProjectDependency, AdapterProject adapterProject) {
        final Shared shared = languageProjectInput.shared();

        final ClassLoaderResourcesCompiler.Input classLoaderResources = buildClassLoaderResources(shared, languageProjectInput.languageProject());
        project.classLoaderResources(classLoaderResources);

        final GetSourceFilesAdapterCompiler.Input getSourceFiles = buildGetSourceFiles(shared, adapterProject, classLoaderResources);
        project.sourceFiles(getSourceFiles);

        final ParserAdapterCompiler.@Nullable Input parser = buildParser(shared, adapterProject, languageProjectInput, classLoaderResources);
        if(parser != null) project.parser(parser);

        final StylerAdapterCompiler.@Nullable Input styler = buildStyler(shared, adapterProject, languageProjectInput, classLoaderResources);
        if(styler != null) project.styler(styler);

        final StrategoRuntimeAdapterCompiler.@Nullable Input strategoRuntime = buildStrategoRuntime(shared, adapterProject, languageProjectInput, classLoaderResources);
        if(strategoRuntime != null) project.strategoRuntime(strategoRuntime);

        final ConstraintAnalyzerAdapterCompiler.@Nullable Input constraintAnalyzer = buildConstraintAnalyzer(shared, adapterProject, languageProjectInput, classLoaderResources, strategoRuntime);
        if(constraintAnalyzer != null) project.constraintAnalyzer(constraintAnalyzer);

        final MultilangAnalyzerAdapterCompiler.@Nullable Input multilangAnalyzer = buildMultilangAnalyzer(shared, adapterProject, languageProjectInput, strategoRuntime);
        if(multilangAnalyzer != null) project.multilangAnalyzer(multilangAnalyzer);

        final ReferenceResolutionAdapterCompiler.@Nullable Input referenceResolution = buildReferenceResolution(shared, adapterProject, strategoRuntime, parser, constraintAnalyzer, classLoaderResources, getSourceFiles);
        if(referenceResolution != null) project.referenceResolution(referenceResolution);

        final CompleterAdapterCompiler.@Nullable Input completer = buildCompleter(shared, adapterProject, languageProjectInput);

        if(completer != null) project.completer(completer);
        project.languageProjectDependency(languageProjectDependency);

        return project
            .adapterProject(adapterProject)
            .shared(shared)
            .build();
    }


    private ClassLoaderResourcesCompiler.Input buildClassLoaderResources(Shared shared, LanguageProject languageProject) {
        return classLoaderResources
            .shared(shared)
            .languageProject(languageProject)
            .build();
    }

    private GetSourceFilesAdapterCompiler.Input buildGetSourceFiles(
        Shared shared,
        AdapterProject adapterProject,
        ClassLoaderResourcesCompiler.Input classloaderResources
    ) {
        return getSourceFiles
            .shared(shared)
            .adapterProject(adapterProject)
            .classLoaderResourcesInput(classloaderResources)
            .build();
    }

    private ParserAdapterCompiler.@Nullable Input buildParser(
        Shared shared,
        AdapterProject adapterProject,
        LanguageProjectCompiler.Input languageProjectInput,
        ClassLoaderResourcesCompiler.Input classloaderResources
    ) {
        if(!parserEnabled) return null;
        return parser
            .shared(shared)
            .adapterProject(adapterProject)
            .languageProjectInput(languageProjectInput.parser().orElseThrow(() -> new RuntimeException("Mismatch between presence of parser input between language project and adapter project")))
            .classLoaderResourcesInput(classloaderResources)
            .build();
    }

    private StylerAdapterCompiler.@Nullable Input buildStyler(
        Shared shared,
        AdapterProject adapterProject,
        LanguageProjectCompiler.Input languageProjectInput,
        ClassLoaderResourcesCompiler.Input classloaderResources
    ) {
        if(!stylerEnabled) return null;
        return styler
            .shared(shared)
            .adapterProject(adapterProject)
            .languageProjectInput(languageProjectInput.styler().orElseThrow(() -> new RuntimeException("Mismatch between presence of styler input between language project and adapter project")))
            .classLoaderResourcesInput(classloaderResources)
            .build();
    }

    private StrategoRuntimeAdapterCompiler.@Nullable Input buildStrategoRuntime(
        Shared shared,
        AdapterProject adapterProject,
        LanguageProjectCompiler.Input languageProjectInput,
        ClassLoaderResourcesCompiler.Input classloaderResources
    ) {
        if(!strategoRuntimeEnabled) return null;
        return strategoRuntime
            .shared(shared)
            .adapterProject(adapterProject)
            .languageProjectInput(languageProjectInput.strategoRuntime().orElseThrow(() -> new RuntimeException("Mismatch between presence of stratego runtime input between language project and adapter project")))
            .classLoaderResourcesInput(classloaderResources)
            .build();
    }

    private ConstraintAnalyzerAdapterCompiler.@Nullable Input buildConstraintAnalyzer(
        Shared shared,
        AdapterProject adapterProject,
        LanguageProjectCompiler.Input languageProjectInput,
        ClassLoaderResourcesCompiler.Input classloaderResources,
        StrategoRuntimeAdapterCompiler.@Nullable Input strategoRuntimeInput
    ) {
        if(!constraintAnalyzerEnabled) return null;
        if(strategoRuntimeInput == null) {
            throw new RuntimeException("Constraint analyzer input requires a Stratego runtime, but the Stratego runtime has not been set");
        }
        return constraintAnalyzer
            .shared(shared)
            .adapterProject(adapterProject)
            .languageProjectInput(languageProjectInput.constraintAnalyzer().orElseThrow(() -> new RuntimeException("Mismatch between presence of constraint analyzer input between language project and adapter project")))
            .classLoaderResourcesInput(classloaderResources)
            .strategoRuntimeInput(strategoRuntimeInput)
            .build();
    }

    private MultilangAnalyzerAdapterCompiler.@Nullable Input buildMultilangAnalyzer(
        Shared shared,
        AdapterProject adapterProject,
        LanguageProjectCompiler.Input languageProjectInput,
        StrategoRuntimeAdapterCompiler.@Nullable Input strategoRuntimeInput
    ) {
        if(!multilangAnalyzerEnabled) return null;
        if(strategoRuntimeInput == null) {
            throw new RuntimeException("Multi-language analyzer input requires a Stratego runtime, but the Stratego runtime has not been set");
        }
        return multilangAnalyzer
            .shared(shared)
            .adapterProject(adapterProject)
            .languageProjectInput(languageProjectInput.multilangAnalyzer().orElseThrow(() -> new RuntimeException("Mismatch between presence of multi-language analyzer input between language project and adapter project")))
            .strategoRuntimeInput(strategoRuntimeInput)
            .build();
    }

    private CompleterAdapterCompiler.@Nullable Input buildCompleter(
        Shared shared,
        AdapterProject adapterProject,
        LanguageProjectCompiler.Input languageProjectInput
    ) {
        if(!completerEnabled) return null;
        return completer
            .shared(shared)
            .adapterProject(adapterProject)
            .languageProjectInput(languageProjectInput.completer().orElseThrow(() -> new RuntimeException("Mismatch between presence of completer input between language project and adapter project")))
            .build();
    }

    private ReferenceResolutionAdapterCompiler.@Nullable Input buildReferenceResolution(
        Shared shared,
        AdapterProject adapterProject,
        StrategoRuntimeAdapterCompiler.@Nullable Input strategoRuntimeInput,
        ParserAdapterCompiler.@Nullable Input parseInput,
        ConstraintAnalyzerAdapterCompiler.@Nullable Input constraintAnalyzerInput,
        ClassLoaderResourcesCompiler.Input classloaderResources,
        GetSourceFilesAdapterCompiler.Input getSourceFiles
    ) {
        if(!referenceResolutionEnabled) return null;
        return referenceResolution
            .shared(shared)
            .adapterProject(adapterProject)
            .strategoRuntimeInput(strategoRuntimeInput)
            .parseInput(parseInput)
            .constraintAnalyzerInput(constraintAnalyzerInput)
            .classLoaderResourcesInput(classloaderResources)
            .sourceFilesInput(getSourceFiles)
            .build();
    }
}
