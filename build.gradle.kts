import org.jetbrains.intellij.tasks.PatchPluginXmlTask

buildscript {
  val kotlinVersion = "1.9.24"

  repositories {
    mavenCentral()
  }
  dependencies {
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
  }
}

plugins {
  id("me.filippov.gradle.jvm.wrapper") version "0.14.0"
  id("org.jetbrains.intellij") version "1.17.3"
}

apply(plugin = "kotlin")

group = "embeddedProjectJdk"
val buildNumber: String by rootProject.extra
version = buildNumber

intellij {
  version.set("2022.2")
  pluginName.set("embeddedProjectJdk")
  updateSinceUntilBuild.set(true)
  plugins.set(listOf("java"))
}

val patchPluginXml: PatchPluginXmlTask by tasks
patchPluginXml.untilBuild.set("")

repositories {
  mavenCentral()
}
