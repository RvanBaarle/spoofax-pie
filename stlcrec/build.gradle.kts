plugins {
  id("org.metaborg.gradle.config.java-library")
  id("org.metaborg.gradle.config.junit-testing")
}

dependencies {
  api(platform(project(":depconstraints")))

  api(project(":common"))
  api(project(":jsglr1.common"))
  api(project(":esv.common"))
  api(project(":stratego.common"))
  api(project(":constraint.common"))

  implementation(project(":statix.common"))
  implementation("org.metaborg:strategoxt-min-jar")

  compileOnly("org.checkerframework:checker-qual-android")

  testCompileOnly("org.checkerframework:checker-qual-android")
}

// Copy select files from the language into the resulting JAR file
run {
  // Create dependency to spoofax-language artifact.
  val dependency = run {
    val dep = dependencies.project(":lang.stlcrec", Dependency.DEFAULT_CONFIGURATION)
    dep.isTransitive = false // Don't care about transitive dependencies, just want the Tiger spoofax-language artifact.
    dep.artifact {
      name = dep.name
      type = "spoofax-language"
      extension = "spoofax-language"
    }
    dep
  }
  // Create 'resources' configuration that contains the dependency.
  val configuration = configurations.create("resources") {
    dependencies.add(dependency)
  }
  // First unpack Tiger language resources, because we cannot copy from a subdirectory in a ziptree.
  val unpackTask = tasks.register<Sync>("unpackResources") {
    dependsOn(configuration)
    from({ configuration.map { project.zipTree(it) } })  /* Closure inside `from` to defer evaluation until task execution time */
    into("$buildDir/unpacked")
    include("target/metaborg/editor.esv.af", "target/metaborg/sdf.tbl", "target/metaborg/stratego.ctree")
  }
  // Copy resources into `mainSourceSet.java.outputDir` and `testSourceSet.java.outputDir`, so the resources finally end up in the 'mb.tiger' package in the resulting JAR.
  val copySpec = copySpec {
    from("$buildDir/unpacked/target/metaborg")
    include("editor.esv.af", "sdf.tbl", "stratego.ctree")
  }
  val destPackage = "mb/stlcrec"
  val syncMainTask = tasks.register<Sync>("syncMainResources") {
    dependsOn(unpackTask)
    with(copySpec)
    into(sourceSets.main.get().java.outputDir.resolve(destPackage))
  }
  tasks.getByName(JavaPlugin.CLASSES_TASK_NAME).dependsOn(syncMainTask)
  val syncTestTask = tasks.register<Sync>("syncTestResources") {
    dependsOn(unpackTask)
    with(copySpec)
    into(sourceSets.test.get().java.outputDir.resolve(destPackage))
  }
  tasks.getByName(JavaPlugin.TEST_CLASSES_TASK_NAME).dependsOn(syncTestTask)
}
