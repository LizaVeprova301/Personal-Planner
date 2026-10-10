package com.personalplanner.app.model

data class Habit(
    val id: Long,
    val name: String,
    val createdAt: Long,
    val deleted: Boolean = false,
    val completions: List<HabitCompletion> = emptyList()
)

data class HabitCompletion(
    val date: String
)