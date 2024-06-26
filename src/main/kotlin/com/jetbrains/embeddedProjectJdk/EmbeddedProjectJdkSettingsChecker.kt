package com.jetbrains.embeddedProjectJdk

import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class EmbeddedProjectJdkSettingsChecker : ProjectActivity  {
  companion object {
    const val LOAD_SETTINGS_ACTION_ID = "LoadJdkSettingsFromProject"
  }

  private val myLogger = Logger.getInstance(EmbeddedProjectJdkSettingsChecker::class.java)

  override suspend fun execute(project: Project) {
    myLogger.debug("Handle before project loaded event")
    if (JdkUtil.hasDifferentJdkSettings(project)) {
      Notification("Per Project JDK Settings", Messages.message("notification.text"), "", NotificationType.WARNING)
        .addAction(object : NotificationAction(Messages.message("notification.action.text")) {
          override fun actionPerformed(e: AnActionEvent, notification: Notification) {
            ActionManager.getInstance().getAction(LOAD_SETTINGS_ACTION_ID).actionPerformed(e)
            notification.expire()
          }
        }).notify(project)
    }
  }
}
