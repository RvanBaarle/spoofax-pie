plugins {
  id("org.metaborg.gradle.config.java-library")
  id("org.metaborg.gradle.config.junit-testing")
  application
}

dependencies {
  val jmhVersion = "1.28"

  api(platform(project(":spoofax.depconstraints")))
  implementation(platform(project(":spoofax.depconstraints")))
  compileOnly(platform(project(":spoofax.depconstraints")))
  annotationProcessor(platform(project(":spoofax.depconstraints")))
  testImplementation(platform(project(":spoofax.depconstraints")))
  testCompileOnly(platform(project(":spoofax.depconstraints")))
  testAnnotationProcessor(platform(project(":spoofax.depconstraints")))

  implementation(project(":jsglr.common"))
  implementation(project(":statix.completions"))

  api("org.openjdk.jmh:jmh-core:$jmhVersion")
  annotationProcessor("org.openjdk.jmh:jmh-generator-annprocess:$jmhVersion")

  compileOnly("org.immutables:value")
  annotationProcessor("org.immutables:value")

  compileOnly("org.checkerframework:checker-qual-android")
  compileOnly("javax.annotation:javax.annotation-api")
  implementation("org.metaborg:log.backend.slf4j")
  implementation("org.slf4j:slf4j-simple:1.7.10")


  implementation  ("org.junit.jupiter:junit-jupiter-api")
  implementation  ("org.junit.platform:junit-platform-launcher")
  runtimeOnly     ("org.junit.jupiter:junit-jupiter-engine")

  implementation("com.opencsv:opencsv:4.1")
}

application {
  mainClass.set("mb.statix.completions.bench.Main")
}

