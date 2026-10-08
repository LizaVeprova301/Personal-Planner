package com.personalplanner.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.personalplanner.app.manager.TaskManager
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchiveScreen(
    taskManager: TaskManager,
    onBack: () -> Unit
) {
    val archivedTasks = taskManager.getArchivedTasks()

    val zoneId = ZoneId.systemDefault()

    val groupedTasks = archivedTasks
        .filter { it.completedAt != null }
        .groupBy {
            Instant
                .ofEpochMilli(it.completedAt!!)
                .atZone(zoneId)
                .toLocalDate()
        }
        .toSortedMap(compareByDescending { it })

    var expandedDate by remember {
        mutableStateOf<LocalDate?>(null)
    }

    val russianLocale = Locale("ru")

    val dateFormatter = DateTimeFormatter
        .ofPattern("d MMMM", russianLocale)

    fun formatDate(date: LocalDate): String {
        val today = LocalDate.now(zoneId)
        val yesterday = today.minusDays(1)

        return when (date) {
            today -> "Сегодня"
            yesterday -> "Вчера"
            else -> date.format(dateFormatter)
        }
    }

    fun tasksCountText(count: Int): String {
        val lastTwo = count % 100
        val last = count % 10

        return when {
            lastTwo in 11..14 -> "$count задач выполнено"
            last == 1 -> "$count задача выполнена"
            last in 2..4 -> "$count задачи выполнено"
            else -> "$count задач выполнено"
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
                    val isExpanded = expandedDate == date

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                expandedDate = if (isExpanded) {
                                    null
                                } else {
                                    date
                                }
                            }
                            .padding(
                                vertical = 12.dp
                            )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = formatDate(date)
                                )

                                Text(
                                    text = tasksCountText(tasks.size)
                                )
                            }

                            Text(
                                text = if (isExpanded) "↑" else "›"
                            )
                        }
                    }
                }

                if (expandedDate == date) {
                    items(
                        items = tasks,
                        key = { task ->
                            "task_${task.id}"
                        }
                    ) { task ->

                        val dismissState =
                            rememberSwipeToDismissBoxState(
                                confirmValueChange = { value ->

                                    if (
                                        value ==
                                        SwipeToDismissBoxValue.EndToStart
                                    ) {
                                        taskManager.deleteTaskFromArchive(
                                            task.id
                                        )

                                        true
                                    } else {
                                        false
                                    }
                                }
                            )

                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = {
                                // Пока пустой фон при свайпе
                            }
                        ) {

                            Text(
                                text = "✓ ${task.text}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        vertical = 6.dp,
                                        horizontal = 8.dp
                                    ),
                                textDecoration =
                                TextDecoration.LineThrough
                            )
                        }
                    }
                }
            }
        }
    }
}