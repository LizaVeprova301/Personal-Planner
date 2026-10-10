package com.personalplanner.app.ui.archive

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.personalplanner.app.manager.TaskManager
import com.personalplanner.app.model.Task

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchivedTaskRow(
    task: Task,
    taskManager: TaskManager
) {
    val dismissState =
        rememberSwipeToDismissBoxState(
            confirmValueChange = { value ->
                if (value == SwipeToDismissBoxValue.EndToStart) {
                    taskManager.restoreTaskFromArchive(task.id)
                    true
                } else {
                    false
                }
            }
        )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {}
    ) {
        Text(
            text = "✓ ${task.text}",
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 6.dp,
                    horizontal = 8.dp
                ),
            textDecoration = TextDecoration.LineThrough
        )
    }
}