plugins {
  id("org.metaborg.gradle.config.java-library")
  id("org.metaborg.gradle.config.junit-testing")
  application
}

dependencies {
  val jmhVersion = "1.28"

  // Platform
  api(platform(project(":spoofax.depconstraints")))
  implementation(platform(project(":spoofax.depconstraints")))
  compileOnly(platform(project(":spoofax.depconstraints")))
  annotationProcessor(platform(project(":spoofax.depconstraints")))
  testImplementation(platform(project(":spoofax.depconstraints")))
  testCompileOnly(platform(project(":spoofax.depconstraints")))
  testAnnotationProcessor(platform(project(":spoofax.depconstraints")))

  // Spoofax
  implementation(project(":jsglr.common"))
  implementation(project(":statix.completions"))

  // Benchmark
  api("org.openjdk.jmh:jmh-core:$jmhVersion")
  annotationProcessor("org.openjdk.jmh:jmh-generator-annprocess:$jmhVersion")

  // Immutables
  compileOnly("org.immutables:value")
  annotationProcessor("org.immutables:value")

  // Nullable
  compileOnly("org.checkerframework:checker-qual-android")
  compileOnly("javax.annotation:javax.annotation-api")

  // Logging
  implementation("org.metaborg:log.backend.slf4j")
  implementation("org.slf4j:slf4j-simple:1.7.10")

  // JUnit
  implementation  ("org.junit.jupiter:junit-jupiter-api")
  implementation  ("org.junit.platform:junit-platform-launcher")
  runtimeOnly     ("org.junit.jupiter:junit-jupiter-engine")

  // CSV
  implementation("org.apache.commons:commons-csv:1.8")
  implementation("com.opencsv:opencsv:4.1")

  // Yaml
  implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.12.3")

  // Command-Line
  implementation("commons-cli:commons-cli:1.4")

}

application {
  mainClass.set("mb.statix.completions.bench.Main")
}

