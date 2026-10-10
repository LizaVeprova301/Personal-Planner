package com.personalplanner.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.personalplanner.app.manager.TaskManager
import com.personalplanner.app.ui.task.AddTaskField
import com.personalplanner.app.ui.task.TaskRow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester

@Composable
fun TaskScreen(
    taskManager: TaskManager,
    onOpenArchive: () -> Unit,
    onOpenHabits: () -> Unit,
    openAddTask: Boolean
) {
    var isAddingTask by remember {
        mutableStateOf(openAddTask)
    }
    var newTaskText by remember {
        mutableStateOf("")
    }

    var isMenuExpanded by remember {
        mutableStateOf(false)
    }

    val listState = rememberLazyListState()
    val focusRequester = remember {
        FocusRequester()
    }

    val focusManager = LocalFocusManager.current
    val keyboardController =
        LocalSoftwareKeyboardController.current

    LaunchedEffect(isAddingTask) {

        if (isAddingTask) {

            val inputIndex =
                taskManager.getActiveTasks().size

            listState.animateScrollToItem(
                inputIndex
            )

            snapshotFlow {
                listState.layoutInfo.visibleItemsInfo.any {
                    it.index == inputIndex
                }
            }
                .filter { it }
                .first()

            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Задачи",
                modifier = Modifier.weight(1f)
            )

            Box {

                IconButton(
                    onClick = {
                        isMenuExpanded = true
                    }
                ) {
                    Text(
                        text = "☰",
                        fontSize = 24.sp
                    )
                }

                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = {
                        isMenuExpanded = false
                    }
                ) {

                    DropdownMenuItem(
                        text = {
                            Text("Архив")
                        },
                        onClick = {
                            isMenuExpanded = false
                            onOpenArchive()
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text("Привычки")
                        },
                        onClick = {
                            isMenuExpanded = false
                            onOpenHabits()
                        }
                    )
                }
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {

            items(
                items = taskManager.getActiveTasks(),
                key = { it.id }
            ) { task ->

                TaskRow(
                    task = task,
                    taskManager = taskManager
                )
            }

            if (isAddingTask) {

                item {

                    AddTaskField(
                        taskManager = taskManager,
                        value = newTaskText,
                        onValueChange = {
                            newTaskText = it
                        },
                        onSaved = {
                            isAddingTask = false
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        },
                        focusRequester = focusRequester
                    )
                }
            }
        }

        Button(
            onClick = {

                if (!isAddingTask) {
                    isAddingTask = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("+")
        }
    }
}