package com.jetbrains.embeddedProjectJdk

import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl
import com.intellij.openapi.util.JDOMUtil
import com.intellij.openapi.util.SystemInfo
import org.jdom.filter.ElementFilter
import java.io.File

object JdkUtil {
  private fun isValidJdk(jdk: Sdk): Boolean {
    val jdkHomePath = jdk.homePath ?: return false
    return File(jdkHomePath).exists()
  }

  private fun getJdkTableFile(project: Project): File {
    val ideaFolder = File(project.basePath ?: "").resolve(".idea")
    val allOsFile = ideaFolder.resolve("jdk.table.xml")
    val windowsFile = ideaFolder.resolve("jdk.table.win.xml")
    val linuxFile = ideaFolder.resolve("jdk.table.lin.xml")
    val macFile = ideaFolder.resolve("jdk.table.mac.xml")
    return when {
      SystemInfo.isWindows && windowsFile.exists() -> windowsFile
      SystemInfo.isLinux && linuxFile.exists() -> linuxFile
      SystemInfo.isMac && macFile.exists() -> macFile
      else -> allOsFile
    }
  }

  fun hasProjectJdkSettings(project: Project): Boolean {
    val perProjectJdkTableFile = getJdkTableFile(project)
    return perProjectJdkTableFile.exists() && perProjectJdkTableFile.isFile
  }

  fun hasDifferentJdkSettings(project: Project): Boolean {
    if (hasProjectJdkSettings(project).not())
      return false
    val jdkList = readProjectJdkSettings(project)
    if (jdkList.isEmpty()) return false
    val projectJdkTable = ProjectJdkTable.getInstance()
    for (jdk in jdkList) {
      val originJdk = projectJdkTable.findJdk(jdk.name) ?: return true
      if (isValidJdk(originJdk) && originJdk.homePath != jdk.homePath) {
        return true
      }
    }
    return false
  }

  fun readProjectJdkSettings(project: Project): List<Sdk> {
    val perProjectJdkTableFile = getJdkTableFile(project)
    val projectBaseDir = project.basePath ?: return emptyList()


    val element = JDOMUtil.load(perProjectJdkTableFile.readText().replace("\$PROJECT_DIR\$", projectBaseDir))
    val jdkList = mutableListOf<Sdk>()
    for (jdkElement in element.getDescendants(ElementFilter("jdk"))) {
      val jdk = ProjectJdkImpl(null, null)
      jdk.readExternal(jdkElement)
      jdkList.add(jdk)
    }
    return jdkList
  }
}
