package com.personalplanner.app.ui.habit

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import com.personalplanner.app.manager.HabitManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester

@Composable
fun AddHabitField(
    habitManager: HabitManager,
    value: String,
    onValueChange: (String) -> Unit,
    onSaved: () -> Unit,
    focusRequester: FocusRequester
) {

    fun saveHabit() {
        val name = value.trim()

        if (name.isEmpty()) {
            onSaved()
            return
        }

        habitManager.addHabit(name)
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
            Text("Новая привычка")
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                saveHabit()
            }
        )
    )
}