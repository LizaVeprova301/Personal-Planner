package com.personalplanner.app.widget

import androidx.glance.appwidget.GlanceAppWidgetReceiver

class TaskWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget = TaskWidget()
}