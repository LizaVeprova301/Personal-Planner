package com.personalplanner.app.model

data class Task(
    val id: Long,
    val text: String,
    val completed: Boolean = false,
    val completedAt: Long? = null,
    val deleted: Boolean = false,
    val deletedFromArchive: Boolean = false
)