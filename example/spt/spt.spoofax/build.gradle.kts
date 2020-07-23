import mb.spoofax.compiler.gradle.spoofaxcore.*
import mb.spoofax.compiler.spoofaxcore.*
import mb.spoofax.compiler.util.*
import mb.spoofax.compiler.cli.CliCommandRepr
import mb.spoofax.compiler.cli.CliParamRepr
import mb.spoofax.core.language.command.CommandExecutionType
import mb.common.util.ListView
import mb.spoofax.compiler.util.*
import mb.spoofax.compiler.gradle.spoofaxcore.*
import mb.spoofax.core.language.command.CommandContextType
import mb.spoofax.compiler.command.ParamRepr
import mb.spoofax.compiler.command.CommandDefRepr
import mb.spoofax.compiler.command.ArgProviderRepr

import mb.spoofax.core.language.command.EnclosingCommandContextType

plugins {
  id("org.metaborg.gradle.config.java-library")
  id("org.metaborg.gradle.config.junit-testing")
  id("org.metaborg.spoofax.compiler.gradle.spoofaxcore.adapter")
}

dependencies {
  // Required because @Nullable has runtime retention (which includes classfile retention), and the Java compiler requires access to it.
  compileOnly("com.google.code.findbugs:jsr305")
  implementation(project(":sdf3.spoofax"))
  implementation("org.metaborg:log.backend.slf4j")
  implementation("org.slf4j:slf4j-simple:1.7.30")
  implementation("org.metaborg:pie.runtime")
  implementation("org.metaborg:pie.dagger")
  compileOnly("org.checkerframework:checker-qual-android")

  testAnnotationProcessor(platform("$group:spoofax.depconstraints:$version"))
//  testImplementation("org.metaborg:log.backend.slf4j")
//  testImplementation("org.slf4j:slf4j-simple:1.7.30")
  testImplementation("org.metaborg:pie.runtime")
  testImplementation("org.metaborg:pie.dagger")
  testImplementation("com.google.jimfs:jimfs:1.1")
  testCompileOnly("org.checkerframework:checker-qual-android")
  testAnnotationProcessor("com.google.dagger:dagger-compiler")
}

spoofaxAdapterProject {
  languageProject.set(project(":spt"))
  settings.set(AdapterProjectSettings(
    parser = ParserCompiler.AdapterProjectInput.builder(),
    styler = StylerCompiler.AdapterProjectInput.builder(),
    completer = CompleterCompiler.AdapterProjectInput.builder(),
    strategoRuntime = StrategoRuntimeCompiler.AdapterProjectInput.builder(),

    builder = run {
      val packageId = "mb.spt.spoofax"
      val taskPackageId = "$packageId.task"
      val commandPackageId = "$packageId.command"

      val builder = AdapterProjectCompiler.Input.builder()

      // Utility task definitions
      val desugar = TypeInfo.of(taskPackageId, "SptDesugar")
      builder.addTaskDefs(desugar)

      builder.classKind(ClassKind.Extended)
      builder.genComponent(packageId, "GeneratedSptComponent")
      builder.manualComponent(packageId, "SptComponent")
      builder.genModule(packageId, "GeneratedSptModule")
      builder.manualModule(packageId, "SptModule")

      // Command line actions
      val runTestsTaskDef: TypeInfo = TypeInfo.of(taskPackageId, "RunTestsTaskDef")
      builder.addTaskDefs(
        runTestsTaskDef
      )
      val runTestsCommand = CommandDefRepr.builder()
        .type(commandPackageId, runTestsTaskDef.id() + "Command")
        .taskDefType(runTestsTaskDef)
        .argType(runTestsTaskDef.appendToId(".Args"))
        .displayName("Run tests")
        .description("Runs tests.")
        .addSupportedExecutionTypes(CommandExecutionType.ManualOnce, CommandExecutionType.ManualContinuous)
        .addAllParams(listOf(
          ParamRepr.of("input", TypeInfo.of("mb.resource", "ResourceKey"), true, ArgProviderRepr.context(CommandContextType.File)),
          ParamRepr.of("output", TypeInfo.of("mb.resource", "ResourceKey"), false, ArgProviderRepr.context(CommandContextType.File))
        ))
        .build()
      builder.addCommandDefs(
        runTestsCommand
      )
      builder.cliCommand(CliCommandRepr.builder()
        .name("spt")
        .description("SPT command-line interface")
        .addSubCommands(
          CliCommandRepr.builder()
            .name("test")
            .description("Runs tests.")
            .commandDefType(runTestsCommand.type())
            .addAllParams(listOf(
              CliParamRepr.positional("input", 0, "FILE", "SPT file"),
              CliParamRepr.option("output", ListView.of("-o", "--output"), false, "FILE", "Target file to write the tests results to")
            ))
            .build()
        ).build())

      builder
    }
  ))
}
