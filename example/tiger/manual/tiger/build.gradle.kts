plugins {
  id("org.metaborg.gradle.config.java-library")
  id("org.metaborg.gradle.config.junit-testing")
  id("de.set.ecj") // Use ECJ to speed up compilation of Stratego's generated Java files.
}

sourceSets {
  main {
    java {
      srcDir("$buildDir/generated/sources/spoofax/java")
    }
  }
}

fun compositeBuild(name: String) = "$group:$name"

dependencies {
  api(platform(compositeBuild("spoofax.depconstraints")))

  api(compositeBuild("spoofax.compiler.interfaces"))
  api(compositeBuild("common"))
  api(compositeBuild("jsglr1.common"))
  api(compositeBuild("esv.common"))
  api(compositeBuild("stratego.common"))
  api(compositeBuild("constraint.common"))

  implementation(compositeBuild("nabl2.common"))
  implementation("org.metaborg:strategoxt-min-jar")

  compileOnly("org.checkerframework:checker-qual-android")

  testImplementation("org.metaborg:log.backend.noop")

  testCompileOnly("org.checkerframework:checker-qual-android")
}

fun createProjectDependency(projectPath: String): ModuleDependency {
  return configureModuleDependency(dependencies.project(projectPath))
}

fun createModuleDependency(notation: String): ModuleDependency {
  return configureModuleDependency(dependencies.module(notation) as ModuleDependency)
}

fun createFilesDependency(vararg paths: Any?): Dependency {
  return dependencies.create(files(paths))
}

fun configureModuleDependency(dependency: ModuleDependency): ModuleDependency {
  dependency.targetConfiguration = Dependency.DEFAULT_CONFIGURATION
  dependency.isTransitive = false // Don't care about transitive dependencies, just want the '.spoofax-language' artifact.
  dependency.artifact {
    name = dependency.name
    type = "spoofax-language"
    extension = "spoofax-language"
  }
  return dependency
}

fun copySpoofaxLanguageResources(
  dependency: Dependency,
  destinationPackage: String,
  includeStrategoClasses: Boolean,
  includeStrategoJavastratClasses: Boolean,
  vararg resources: String
) {
  val allResources = resources.toMutableList()
  if(includeStrategoClasses) {
    allResources.add("target/metaborg/stratego.jar")
  }
  if(includeStrategoJavastratClasses) {
    allResources.add("target/metaborg/stratego-javastrat.jar")
  }

  // Create 'spoofaxLanguage' configuration that contains the dependency.
  val configuration = configurations.create("spoofaxLanguage") {
    dependencies.add(dependency)
  }
  // Unpack the '.spoofax-language' archive.
  val unpackSpoofaxLanguageDir = "$buildDir/unpackedSpoofaxLanguage/"
  val unpackSpoofaxLanguageTask = tasks.register<Sync>("unpackSpoofaxLanguage") {
    dependsOn(configuration)
    from({ configuration.map { project.zipTree(it) } })  /* Closure inside `from` to defer evaluation until task execution time */
    into(unpackSpoofaxLanguageDir)
    include(allResources)
  }
  // Copy resources into `mainSourceSet.java.outputDir` and `testSourceSet.java.outputDir`, so they end up in the target package.
  val resourcesCopySpec = copySpec {
    from(unpackSpoofaxLanguageDir)
    include(*resources)
  }
  val strategoCopySpec = copySpec {
    from(project.zipTree("$unpackSpoofaxLanguageDir/target/metaborg/stratego.jar"))
    exclude("META-INF")
  }
  val strategoJavastratCopySpec = copySpec {
    from(project.zipTree("$unpackSpoofaxLanguageDir/target/metaborg/stratego-javastrat.jar"))
    exclude("META-INF")
  }
  val copyMainTask = tasks.register<Copy>("copyMainResources") {
    dependsOn(unpackSpoofaxLanguageTask)
    into(sourceSets.main.get().java.outputDir)
    into(destinationPackage) { with(resourcesCopySpec) }
    if(includeStrategoClasses) {
      into(".") { with(strategoCopySpec) }
    }
    if(includeStrategoJavastratClasses) {
      into(".") { with(strategoJavastratCopySpec) }
    }
  }
  tasks.getByName(JavaPlugin.CLASSES_TASK_NAME).dependsOn(copyMainTask)
  val copyTestTask = tasks.register<Copy>("copyTestResources") {
    dependsOn(unpackSpoofaxLanguageTask)
    into(sourceSets.test.get().java.outputDir)
    into(destinationPackage) { with(resourcesCopySpec) }
    if(includeStrategoClasses) {
      into(".") { with(strategoCopySpec) }
    }
    if(includeStrategoJavastratClasses) {
      into(".") { with(strategoJavastratCopySpec) }
    }
  }
  tasks.getByName(JavaPlugin.TEST_CLASSES_TASK_NAME).dependsOn(copyTestTask)
}
copySpoofaxLanguageResources(
  createModuleDependency(compositeBuild("org.metaborg.lang.tiger")),
  "mb/tiger",
  true,
  true,
  "target/metaborg/editor.esv.af", "target/metaborg/sdf.tbl"
)

ecj {
  toolVersion = "3.20.0"
}
