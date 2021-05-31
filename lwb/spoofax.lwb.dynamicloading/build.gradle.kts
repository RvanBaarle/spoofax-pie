plugins {
  id("org.metaborg.gradle.config.java-library")
  id("org.metaborg.gradle.config.junit-testing")
}

fun compositeBuild(name: String) = "$group:$name:$version"

dependencies {
  api(platform(compositeBuild("spoofax.depconstraints")))
  annotationProcessor(platform(compositeBuild("spoofax.depconstraints")))

  api(compositeBuild("spoofax.core"))
  api(project(":spoofax.lwb.compiler"))
  api(project(":spoofax.lwb.compiler.dagger"))
  api("com.google.dagger:dagger")

  compileOnly("org.checkerframework:checker-qual-android")

  annotationProcessor("com.google.dagger:dagger-compiler")

  testImplementation("org.slf4j:slf4j-nop:1.7.30")
  testImplementation("org.metaborg:pie.runtime")
}

tasks.test {
  enableAssertions = false // HACK: disable assertions until we support JSGLR2 parsing for Stratego
  // Show stderr in tests.
  testLogging {
    events(org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_ERROR)
  }
}
