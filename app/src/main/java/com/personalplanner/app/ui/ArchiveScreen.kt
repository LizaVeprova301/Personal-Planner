package com.personalplanner.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.personalplanner.app.manager.TaskManager
import com.personalplanner.app.ui.archive.ArchiveDay
import com.personalplanner.app.ui.archive.ArchivedTaskRow
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ArchiveScreen(
    taskManager: TaskManager,
    onBack: () -> Unit
) {
    val archivedTasks =
        taskManager.getArchivedTasks()

    val zoneId =
        ZoneId.systemDefault()

    val groupedTasks =
        archivedTasks
            .filter { it.completedAt != null }
            .groupBy {
                Instant
                    .ofEpochMilli(it.completedAt!!)
                    .atZone(zoneId)
                    .toLocalDate()
            }
            .toSortedMap(
                compareByDescending { it }
            )

    var expandedDate by remember {
        mutableStateOf<LocalDate?>(null)
    }

    val dateFormatter =
        DateTimeFormatter.ofPattern(
            "d MMMM",
            Locale("ru")
        )

    fun formatDate(
        date: LocalDate
    ): String {
        val today =
            LocalDate.now(zoneId)

        val yesterday =
            today.minusDays(1)

        return when (date) {
            today -> "Сегодня"
            yesterday -> "Вчера"
            else -> date.format(
                dateFormatter
            )
        }
    }

    fun tasksCountText(
        count: Int
    ): String {
        val lastTwo =
            count % 100

        val last =
            count % 10

        return when {
            lastTwo in 11..14 ->
                "$count задач выполнено"

            last == 1 ->
                "$count задача выполнена"

            last in 2..4 ->
                "$count задачи выполнено"

            else ->
                "$count задач выполнено"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Архив",
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = onBack
            ) {
                Text("Назад")
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {

            groupedTasks.forEach { (date, tasks) ->

                item(
                    key = "header_$date"
                ) {

                    val isExpanded =
                        expandedDate == date

                    ArchiveDay(
                        dateText = formatDate(date),
                        tasksCountText =
                        tasksCountText(
                            tasks.size
                        ),
                        isExpanded = isExpanded,
                        onToggleExpanded = {
                            expandedDate =
                                if (isExpanded) {
                                    null
                                } else {
                                    date
                                }
                        }
                    )
                }

                if (expandedDate == date) {

                    items(
                        items = tasks,
                        key = { task ->
                            "task_${task.id}"
                        }
                    ) { task ->

                        ArchivedTaskRow(
                            task = task,
                            taskManager = taskManager
                        )
                    }
                }
            }
        }
    }
}