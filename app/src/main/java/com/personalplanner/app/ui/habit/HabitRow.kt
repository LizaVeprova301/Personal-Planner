package com.personalplanner.app.ui.habit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.personalplanner.app.manager.HabitManager
import com.personalplanner.app.model.Habit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.foundation.layout.Box


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitRow(
    habit: Habit,
    habitManager: HabitManager,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit
) {
    val dismissState =
        rememberSwipeToDismissBoxState(
            confirmValueChange = { value ->
                if (value == SwipeToDismissBoxValue.EndToStart) {
                    habitManager.deleteHabit(habit.id)
                    true
                } else {
                    false
                }
            }
        )

    val isCompletedToday =
        habitManager.isCompletedToday(habit.id)

    val currentStreak =
        habitManager.getCurrentStreak(habit.id)

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        SwipeToDismissBox(
            state = dismissState,
            backgroundContent = {

                if (dismissState.dismissDirection ==
                    SwipeToDismissBoxValue.EndToStart
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = "🗑",
                            fontSize = 22.sp
                        )
                    }
                }
            }
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            )
            {

                Text(
                    text = if (isCompletedToday) {
                        "🔥"
                    } else {
                        "○"
                    },
                    fontSize = 22.sp,
                    modifier = Modifier
                        .clickable {
                            habitManager.toggleToday(habit.id)
                        }
                        .padding(end = 12.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = habit.name
                    )

                    Text(
                        text = "Текущая серия $currentStreak дней"
                    )
                }

                Text(
                    text = if (isExpanded) {
                        "↑"
                    } else {
                        ">"
                    },
                    modifier = Modifier
                        .clickable {
                            onToggleExpanded()
                        }
                        .padding(start = 12.dp)
                )
            }
        }
    }
}