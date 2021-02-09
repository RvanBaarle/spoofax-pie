plugins {
  id("org.metaborg.gradle.config.java-library")
  id("org.metaborg.gradle.config.junit-testing")
}

dependencies {
  api(platform(project(":spoofax.depconstraints")))

  api("org.metaborg:common")

  compileOnly("org.checkerframework:checker-qual-android")

  testCompileOnly("org.checkerframework:checker-qual-android")
  testImplementation("org.metaborg:log.backend.slf4j")
  testImplementation("org.slf4j:slf4j-simple:1.7.10")
}
