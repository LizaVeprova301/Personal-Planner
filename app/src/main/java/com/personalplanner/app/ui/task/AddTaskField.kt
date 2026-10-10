package com.personalplanner.app.ui.task

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import com.personalplanner.app.manager.TaskManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.layout.onGloballyPositioned

@Composable
fun AddTaskField(
    taskManager: TaskManager,
    topicId: Long? = null,
    value: String,
    onValueChange: (String) -> Unit,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier,
    onBoundsChanged: (androidx.compose.ui.geometry.Rect) -> Unit,
    focusRequester: FocusRequester
) {
    fun saveTask() {
        val text = value.trim()

        if (text.isEmpty()) {
            onSaved()
            return
        }

        taskManager.addTask(text, topicId)

        onValueChange("")
        onSaved()
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = TextStyle(color = Color.Black),
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onGloballyPositioned {
                onBoundsChanged(it.boundsInRoot())
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
            }
        )
    )

}