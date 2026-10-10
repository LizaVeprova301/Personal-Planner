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

@Composable
fun AddTaskField(
    taskManager: TaskManager,
    value: String,
    onValueChange: (String) -> Unit,
    onSaved: () -> Unit,
    focusRequester: FocusRequester
) {
    fun saveTask() {
        val text = value.trim()

        if (text.isEmpty()) {
            onSaved()
            return
        }

        taskManager.addTask(text)

        onValueChange("")
        onSaved()
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
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