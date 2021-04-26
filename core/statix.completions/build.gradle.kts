plugins {
  id("org.metaborg.gradle.config.java-library")
  id("org.metaborg.gradle.config.junit-testing")
}

dependencies {
  api(platform(project(":spoofax.depconstraints")))
  implementation(platform(project(":spoofax.depconstraints")))
  compileOnly(platform(project(":spoofax.depconstraints")))
  annotationProcessor(platform(project(":spoofax.depconstraints")))
  testImplementation(platform(project(":spoofax.depconstraints")))
  testCompileOnly(platform(project(":spoofax.depconstraints")))
  testAnnotationProcessor(platform(project(":spoofax.depconstraints")))
//  configurations.forEach { add(it.name, platform(project(":spoofax.depconstraints"))) }
////  api(platform(project(":spoofax.depconstraints")))
////  testCompileOnly(platform(project(":spoofax.depconstraints")))
////  testImplementation(platform(project(":spoofax.depconstraints")))
//  testAnnotationProcessor(platform(project(":spoofax.depconstraints")))
//
//  api(project(":common"))
//  api(project(":completions.common"))
//  api(project(":statix.common"))
//
//  api("org.metaborg:statix.solver")
//  api("org.metaborg:statix.generator")
//  //implementation("one.util:streamex")
//  api("org.metaborg:pie.api")
//
//  compileOnly("org.checkerframework:checker-qual-android")
//  compileOnly("org.immutables:value-annotations")
//
//  annotationProcessor("com.google.dagger:dagger-compiler")
//  annotationProcessor("org.immutables:value")
//  testAnnotationProcessor("org.immutables:value")
//
//  testCompileOnly("org.checkerframework:checker-qual-android")
//  testImplementation("nl.jqno.equalsverifier:equalsverifier")
//  testImplementation("org.metaborg:log.backend.slf4j")
//  testImplementation("org.slf4j:slf4j-simple:1.7.10")
//  testCompileOnly("org.immutables:value")
//  testCompileOnly("javax.annotation:javax.annotation-api")
  // ---

  api(platform(project(":spoofax.depconstraints")))

  api("org.metaborg:common")
  api("org.metaborg:completions.common")
  api("org.metaborg:statix.common")
  api("org.metaborg:statix.strategies")

  api("org.metaborg.devenv:statix.solver")
  api("org.metaborg.devenv:statix.generator")

  implementation(project(":stratego.common"))
  implementation(project(":jsglr.common"))

  compileOnly("org.checkerframework:checker-qual-android")

  annotationProcessor("org.immutables:value")
  testAnnotationProcessor("org.immutables:value")

  testCompileOnly("org.checkerframework:checker-qual-android")
  testImplementation("nl.jqno.equalsverifier:equalsverifier")
  testImplementation("org.metaborg:log.backend.slf4j")
  testImplementation("org.slf4j:slf4j-simple:1.7.10")
  testCompileOnly("org.immutables:value")
  testCompileOnly("javax.annotation:javax.annotation-api")

  testImplementation("com.opencsv:opencsv:4.1")
}

tasks { withType<Test> {
  debug = true
  maxHeapSize = "3g"
} }
