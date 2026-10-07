package com.personalplanner.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.personalplanner.app.manager.TaskManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchiveScreen(
    taskManager: TaskManager,
    onBack: () -> Unit
) {

    val archivedTasks = taskManager.getArchivedTasks()

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

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {

            archivedTasks.forEachIndexed { index, task ->

                key(task.id) {

                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { value ->

                            if (value == SwipeToDismissBoxValue.EndToStart) {

                                taskManager.deleteTaskFromArchive(task.id)

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
                            text = "${index + 1}. ${task.text}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                }
            }
        }
    }
}