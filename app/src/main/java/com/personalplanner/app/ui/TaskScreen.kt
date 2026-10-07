package com.personalplanner.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.personalplanner.app.model.Task
import com.personalplanner.app.manager.TaskManager
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.runtime.key

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    taskManager: TaskManager,
    onOpenArchive: () -> Unit
) {


    var isAddingTask = remember {
        androidx.compose.runtime.mutableStateOf(false)
    }

    var newTaskText = remember {
        androidx.compose.runtime.mutableStateOf("")
    }

    val focusRequester = remember {
        FocusRequester()
    }

    val focusManager = LocalFocusManager.current

    fun saveTask() {
        val text = newTaskText.value.trim()

        if (text.isNotEmpty()) {
            taskManager.addTask(text)
        }

        newTaskText.value = ""
        isAddingTask.value = false
    }

    LaunchedEffect(isAddingTask.value) {
        if (isAddingTask.value) {
            focusRequester.requestFocus()
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
                text = "Задачи",
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = {
                    onOpenArchive()
                }
            ) {
                Text("Архив")
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {

            taskManager.getActiveTasks().forEach { task ->

                key(task.id) {

                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { value ->

                            if (value == SwipeToDismissBoxValue.EndToStart) {

                                taskManager.deleteTask(task.id)

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

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Checkbox(
                                checked = task.completed,
                                onCheckedChange = { checked ->

                                    taskManager.setTaskCompleted(
                                        task.id,
                                        checked
                                    )
                                }
                            )

                            Text(
                                text = task.text,
                                modifier = Modifier.padding(start = 8.dp),
                                textDecoration = if (task.completed) {
                                    TextDecoration.LineThrough
                                } else {
                                    TextDecoration.None
                                }
                            )
                        }
                    }
                }
            }

            if (isAddingTask.value) {

                OutlinedTextField(
                    value = newTaskText.value,
                    onValueChange = {
                        newTaskText.value = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .onFocusChanged { focusState ->

                            if (!focusState.isFocused &&
                                newTaskText.value.isNotBlank()
                            ) {
                                saveTask()
                            }
                        },
                    placeholder = {
                        Text("Новая задача")
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            saveTask()
                            focusManager.clearFocus()
                        }
                    )
                )
            }
        }

        Button(
            onClick = {

                if (!isAddingTask.value) {
                    isAddingTask.value = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("+")
        }
    }

}