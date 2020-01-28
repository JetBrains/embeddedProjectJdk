import org.jetbrains.intellij.tasks.PatchPluginXmlTask

buildscript {
  val kotlinVersion = "1.3.61"

  repositories {
    mavenCentral()
  }
  dependencies {
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
  }
}

plugins {
  id("me.filippov.gradle.jvm.wrapper") version "0.9.3"
  id("org.jetbrains.intellij") version "0.4.13"
}

apply(plugin = "kotlin")

group = "embeddedProjectJdk"
val buildNumber: String by rootProject.extra
version = buildNumber

intellij {
  version = "2017.3"
  pluginName = "embeddedProjectJdk"
  updateSinceUntilBuild = true
}

val patchPluginXml: PatchPluginXmlTask by tasks
patchPluginXml.setUntilBuild("")

repositories {
  mavenCentral()
}
