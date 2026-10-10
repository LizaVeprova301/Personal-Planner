package com.personalplanner.app.manager

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import com.personalplanner.app.model.Habit
import com.personalplanner.app.model.HabitCompletion
import com.personalplanner.app.storage.HabitStorage
import java.time.LocalDate
import com.personalplanner.app.widget.HabitWidget

class HabitManager private constructor(
    private val context: Context
) {

    companion object {

        @Volatile
        private var INSTANCE: HabitManager? = null

        fun getInstance(context: Context): HabitManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: HabitManager(
                    context.applicationContext
                ).also {
                    INSTANCE = it
                }
            }
        }
    }

    private val storage = HabitStorage(context)

    private val habits = mutableStateListOf<Habit>()

    init {
        habits.addAll(storage.loadHabits())
    }

    fun getHabits(): List<Habit> =
        habits.filter { !it.deleted }

    fun addHabit(name: String) {

        if (name.isBlank()) return

        val nextId =
            if (habits.isEmpty()) {
                1
            } else {
                habits.maxOf { it.id } + 1
            }

        habits.add(
            Habit(
                id = nextId,
                name = name.trim(),
                createdAt = System.currentTimeMillis()
            )
        )

        save()
    }

    fun deleteHabit(id: Long) {

        val index = habits.indexOfFirst {
            it.id == id
        }

        if (index != -1) {

            habits[index] = habits[index].copy(
                deleted = true
            )

            save()
        }
    }

    fun isCompletedToday(id: Long): Boolean {

        val today = LocalDate.now().toString()

        val habit = findActiveHabit(id) ?: return false

        return habit.completions.any {
            it.date == today
        }
    }


    fun toggleToday(id: Long) {
        val index = habits.indexOfFirst { it.id == id }
        if (index == -1) return

        val habit = habits[index]
        val today = LocalDate.now().toString()

        val alreadyCompleted = habit.completions.any { it.date == today }

        val newCompletions = if (alreadyCompleted) {
            habit.completions.filter { it.date != today }
        } else {
            habit.completions + HabitCompletion(date = today)
        }

        habits[index] = habit.copy(completions = newCompletions)
        save()
    }

    fun getCurrentStreak(id: Long): Int {

        val habit = findActiveHabit(id) ?: return 0

        if (habit.completions.isEmpty()) {
            return 0
        }

        val completedDates = getCompletedDates(habit)

        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        val startDate = when {
            today in completedDates -> today
            yesterday in completedDates -> yesterday
            else -> return 0
        }

        var streak = 0
        var date = startDate

        while (date in completedDates) {
            streak++
            date = date.minusDays(1)
        }

        return streak
    }
    fun getStreaks(id: Long): List<Pair<LocalDate, LocalDate>> {

        val habit = findActiveHabit(id) ?: return emptyList()

        val dates = getCompletedDates(habit).sorted()

        if (dates.isEmpty()) {
            return emptyList()
        }

        val streaks = mutableListOf<Pair<LocalDate, LocalDate>>()

        var start = dates.first()
        var end = dates.first()

        for (i in 1 until dates.size) {

            val current = dates[i]

            if (current == end.plusDays(1)) {
                end = current
            } else {
                streaks.add(start to end)

                start = current
                end = current
            }
        }

        streaks.add(start to end)

        return streaks
            .sortedByDescending { it.second }
    }
    fun getTotalCompletedDays(id: Long): Int {

        val habit = findActiveHabit(id) ?: return 0

        return getCompletedDates(habit).size
    }

    private fun getCompletedDates(habit: Habit): Set<LocalDate> {
        return habit.completions.mapNotNull { completion ->
            runCatching {
                LocalDate.parse(completion.date)
            }.getOrNull()
        }.toSet()
    }

    private fun findActiveHabit(id: Long): Habit? {
        return habits.firstOrNull {
            it.id == id && !it.deleted
        }
    }


    private fun save() {
        storage.saveHabits(habits)
    }
}