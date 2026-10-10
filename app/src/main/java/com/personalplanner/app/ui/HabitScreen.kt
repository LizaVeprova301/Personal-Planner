package com.personalplanner.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.personalplanner.app.manager.HabitManager
import com.personalplanner.app.ui.habit.AddHabitField
import com.personalplanner.app.ui.habit.HabitHistory
import com.personalplanner.app.ui.habit.HabitRow
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

@Composable
fun HabitScreen(
    habitManager: HabitManager,
    onBack: () -> Unit
) {
    val habits = habitManager.getHabits()

    var expandedHabitId by remember {
        mutableStateOf<Long?>(null)
    }

    var isAddingHabit by remember {
        mutableStateOf(false)
    }

    var newHabitName by remember {
        mutableStateOf("")
    }

    val listState = rememberLazyListState()
    val focusRequester = remember {
        FocusRequester()
    }
    val keyboardController =
        LocalSoftwareKeyboardController.current

    LaunchedEffect(isAddingHabit) {

        if (isAddingHabit) {

            val inputIndex =
                habitManager.getHabits().size

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
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Привычки",
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = onBack
            ) {
                Text("Назад")
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {

            items(
                items = habits,
                key = { it.id }
            ) { habit ->

                val isExpanded =
                    expandedHabitId == habit.id

                HabitRow(
                    habit = habit,
                    habitManager = habitManager,
                    isExpanded = isExpanded,
                    onToggleExpanded = {
                        expandedHabitId =
                            if (isExpanded) {
                                null
                            } else {
                                habit.id
                            }
                    }
                )

                if (isExpanded) {
                    HabitHistory(
                        habit = habit,
                        habitManager = habitManager
                    )
                }
            }

            if (isAddingHabit) {
                item {
                    AddHabitField(
                        habitManager = habitManager,
                        value = newHabitName,
                        onValueChange = {
                            newHabitName = it
                        },
                        onSaved = {
                            isAddingHabit = false
                        },
                        focusRequester = focusRequester
                    )
                }
            }
        }

        Button(
            onClick = {
                if (!isAddingHabit) {
                    isAddingHabit = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("+")
        }
    }
}