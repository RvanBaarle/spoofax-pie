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

  implementation(project(":statix.completions"))

  api("org.openjdk.jmh:jmh-core:$jmhVersion")
  annotationProcessor("org.openjdk.jmh:jmh-generator-annprocess:$jmhVersion")
}

application {
  mainClass.set("mb.statix.bench.Main")
}

