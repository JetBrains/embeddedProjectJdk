package com.jetbrains.embeddedProjectJdk

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.projectRoots.ProjectJdkTable

class LoadJdkSettingsFromProject : AnAction(), DumbAware {

  override fun getActionUpdateThread(): ActionUpdateThread {
    return ActionUpdateThread.BGT
  }

  private val myLogger = Logger.getInstance(EmbeddedProjectJdkSettingsChecker::class.java)

  override fun update(e: AnActionEvent) {
    e.presentation.isEnabled = e.project?.let { JdkUtil.hasProjectJdkSettings(it) } ?: false
  }

  override fun actionPerformed(e: AnActionEvent) {
    val project = e.project ?: error("e.project shouldn't be null")
    val projectJdkTable = ProjectJdkTable.getInstance()
    val jdkList = JdkUtil.readProjectJdkSettings(project)
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
