package com.jetbrains.embeddedProjectJdk

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ApplicationComponent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.project.ProjectManagerListener
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl
import com.intellij.openapi.util.JDOMUtil
import org.jdom.filter.ElementFilter
import java.io.File

class EmbeddedProjectJdkProvider : ApplicationComponent {
  private val myLogger = Logger.getInstance(EmbeddedProjectJdkProvider::class.java)

  init {
    val connection = ApplicationManager.getApplication().messageBus.connect()
    connection.subscribe(ProjectManager.TOPIC, object : ProjectManagerListener {
      private fun isValidJdk(jdk: Sdk?): Boolean {
        if (jdk == null) return false
        val jdkHomePath = jdk.homePath ?: return false
        return File(jdkHomePath).exists()
      }

      override fun projectOpened(project: Project?) {
        if (project != null) {
          val perProjectJdkTableFile =
            File(project.baseDir.canonicalPath).resolve(".idea").resolve("jdk.table.xml")
          if (!perProjectJdkTableFile.exists() || !perProjectJdkTableFile.isFile)
            return
          val projectBaseDir = project.baseDir.canonicalPath ?: return
          val projectJdkTable = ProjectJdkTable.getInstance()

          val element = JDOMUtil.load(perProjectJdkTableFile.readText().replace("\$PROJECT_DIR\$", projectBaseDir))
          val jdkList = mutableListOf<Sdk>()
          for (jdkElement in element.getDescendants(ElementFilter("jdk"))) {
            val jdk = ProjectJdkImpl(null, null)
            jdk.readExternal(jdkElement)
            val originJdk = projectJdkTable.findJdk(jdk.name)
            if (!isValidJdk(originJdk)) {
              jdkList.add(jdk)
            } else {
              myLogger.info("Don't touch JDK: ${jdk.name} because it name exists and valid")
            }
          }
          ApplicationManager.getApplication().runWriteAction {
            jdkList.forEach { jdk ->
              val originJdk = projectJdkTable.findJdk(jdk.name)
              if (originJdk != null) {
                projectJdkTable.removeJdk(originJdk)
              }
              projectJdkTable.addJdk(jdk)
              myLogger.info("Add JDK from per project settings: ${jdk.name}")
            }
          }
        }
      }
    })
  }
}
