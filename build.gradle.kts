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
  id("org.jetbrains.intellij") version "1.4.0"
}

apply(plugin = "kotlin")

group = "embeddedProjectJdk"
val buildNumber: String by rootProject.extra
version = buildNumber

intellij {
  version.set("2021.1")
  pluginName.set("embeddedProjectJdk")
  updateSinceUntilBuild.set(true)
  plugins.set(listOf("java"))
}

val patchPluginXml: PatchPluginXmlTask by tasks
patchPluginXml.untilBuild.set("")

repositories {
  mavenCentral()
}
