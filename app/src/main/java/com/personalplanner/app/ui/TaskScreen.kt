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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.key
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Arrangement

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
    var selectedTopicId by remember {
        mutableStateOf<Long?>(null)
    }
    var inputBounds by remember { mutableStateOf<androidx.compose.ui.geometry.Rect?>(null) }
    var containerBounds by remember {
        mutableStateOf<androidx.compose.ui.geometry.Rect?>(null)
    }

    var isMenuExpanded by remember {
        mutableStateOf(false)
    }
    var isAddingTopic by remember {
        mutableStateOf(false)
    }

    var colorPickerTopicId by remember { mutableStateOf<Long?>(null) }


    var newTopicName by remember {
        mutableStateOf("")
    }

    val listState = rememberLazyListState()
    val topics = taskManager.getTopics()
    val focusRequester = remember {
        FocusRequester()
    }

    val focusManager = LocalFocusManager.current
    val keyboardController =
        LocalSoftwareKeyboardController.current


    LaunchedEffect(isAddingTask, selectedTopicId) {
        if (isAddingTask) {
            val topicIndex = topics.indexOfFirst {
                it.id == selectedTopicId
            }

            if (topicIndex >= 0) {
                listState.animateScrollToItem(topicIndex)

                snapshotFlow {
                    listState.layoutInfo.visibleItemsInfo.any {
                        it.index == topicIndex
                    }
                }
                    .filter { it }
                    .first()

                focusRequester.requestFocus()
                keyboardController?.show()
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(16.dp)
            .onGloballyPositioned {
                containerBounds = it.boundsInRoot()
            }
            .pointerInput(isAddingTask, inputBounds) {
                awaitEachGesture {
                    val down = awaitFirstDown(
                        requireUnconsumed = false,
                        pass = PointerEventPass.Initial
                    )
                    val up = waitForUpOrCancellation(
                        pass = PointerEventPass.Initial
                    )

                    if (up != null && isAddingTask) {
                        val position = down.position
                        val bounds = inputBounds

                        val container = containerBounds

                        if (bounds == null || container == null ||
                            !bounds.contains(
                                androidx.compose.ui.geometry.Offset(
                                    position.x + container.left,
                                    position.y + container.top
                                )
                            )
                        ) {
                            isAddingTask = false
                            newTaskText = ""
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }
                    }
                }
            }
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
                    DropdownMenuItem(
                        text = {
                            Text("Создать тему")
                        },
                        onClick = {
                            isMenuExpanded = false
                            newTopicName = ""
                            isAddingTopic = true
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
                items = topics,
                key = { "topic_${it.id}" }
            ) { topic ->

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                )
                {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    colorPickerTopicId = topic.id
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .background(
                                        color = Color(topic.color),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                            )
                        }


                        Text(
                            text = topic.name,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp),
                            fontSize = 24.sp
                        )

                        IconButton(
                            onClick = {
                                selectedTopicId = topic.id
                                isAddingTask = true
                                newTaskText = ""
                            }
                        ) {
                            Text("+", fontSize = 24.sp)
                        }
                    }

                    taskManager.getActiveTasksByTopic(topic.id).forEach { task ->
                        key(task.id) {
                            TaskRow(
                                task = task,
                                taskManager = taskManager
                            )
                        }
                    }

                    if (isAddingTask && selectedTopicId == topic.id) {
                        AddTaskField(
                            taskManager = taskManager,
                            topicId = selectedTopicId,
                            value = newTaskText,
                            onValueChange = {
                                newTaskText = it
                            },
                            onSaved = {
                                isAddingTask = false
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            },
                            onBoundsChanged = {
                                inputBounds = it
                            },
                            focusRequester = focusRequester
                        )
                    }

                }
            }
        }

        Button(
            onClick = {
                if (!isAddingTask) {
                    selectedTopicId = topics.find { it.name == "Общее" }?.id
                    isAddingTask = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("+")
        }
    }

    if (colorPickerTopicId != null) {
        val topicId = colorPickerTopicId!!

        val colors = listOf(
            0xFF9E9E9E,
            0xFFE57373,
            0xFFFFB74D,
            0xFFFFD54F,
            0xFF81C784,
            0xFF4DB6AC,
            0xFF4FC3F7,
            0xFF7986CB,
            0xFFBA68C8,
            0xFFF06292
        )

        AlertDialog(
            onDismissRequest = {
                colorPickerTopicId = null
            },
            title = {
                Text("Цвет темы")
            },
            text = {
                Column {
                    colors.chunked(5).forEach { rowColors ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            rowColors.forEach { color ->
                                Box(
                                    modifier = Modifier
                                        .padding(6.dp)
                                        .size(32.dp)
                                        .background(
                                            color = Color(color),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            taskManager.setTopicColor(
                                                topicId,
                                                color
                                            )
                                            colorPickerTopicId = null
                                        }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        colorPickerTopicId = null
                    }
                ) {
                    Text("Закрыть")
                }
            }
        )
    }


    if (isAddingTopic) {
        AlertDialog(
            onDismissRequest = {
                isAddingTopic = false
                newTopicName = ""
            },
            title = {
                Text("Новая тема")
            },
            text = {
                OutlinedTextField(
                    value = newTopicName,
                    onValueChange = { newTopicName = it },
                    label = { Text("Название темы") },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTopicName.isNotBlank()) {
                            taskManager.addTopic(
                                name = newTopicName,
                                color = 0xFF9E9E9E
                            )
                            isAddingTopic = false
                            newTopicName = ""
                        }
                    },
                    enabled = newTopicName.isNotBlank()
                ) {
                    Text("Создать")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        isAddingTopic = false
                        newTopicName = ""
                    }
                ) {
                    Text("Отмена")
                }
            }
        )
    }

}