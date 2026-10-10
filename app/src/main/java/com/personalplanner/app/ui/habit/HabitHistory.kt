package com.personalplanner.app.ui.habit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.personalplanner.app.manager.HabitManager
import com.personalplanner.app.model.Habit
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HabitHistory(
    habit: Habit,
    habitManager: HabitManager
) {
    val totalDays =
        habitManager.getTotalCompletedDays(habit.id)

    val streaks =
        habitManager.getStreaks(habit.id)

    val dateFormatter =
        DateTimeFormatter.ofPattern(
            "d MMMM",
            Locale("ru")
        )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 8.dp,
                bottom = 12.dp
            )
    ) {

        Text(
            text = "Всего дней $totalDays",
            modifier = Modifier.padding(
                bottom = 8.dp
            )
        )

        streaks.forEach { (start, end) ->

            val startText =
                start.format(dateFormatter)

            val endText =
                end.format(dateFormatter)

            Text(
                text = "$startText — $endText",
                modifier = Modifier.padding(
                    vertical = 3.dp
                )
            )
        }
    }
}