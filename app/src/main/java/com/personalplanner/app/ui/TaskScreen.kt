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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos

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
    val listState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current

    fun saveTask() {
        val text = newTaskText.value.trim()

        if (text.isEmpty()) {
            isAddingTask.value = false
            focusManager.clearFocus()
            keyboardController?.hide()
            return
        }

        // Сначала закрываем клавиатуру и убираем фокус
        focusManager.clearFocus(force = true)
        keyboardController?.hide()

        // Затем убираем поле ввода
        newTaskText.value = ""
        isAddingTask.value = false

        // И только после этого добавляем задачу
        taskManager.addTask(text)
    }

    LaunchedEffect(isAddingTask.value) {
        if (isAddingTask.value) {
            listState.animateScrollToItem(
                taskManager.getActiveTasks().size
            )
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

            if (isAddingTask.value) {
                item {

                    OutlinedTextField(
                        value = newTaskText.value,
                        onValueChange = {
                            newTaskText.value = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
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
                            }
                        )
                    )
                }
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