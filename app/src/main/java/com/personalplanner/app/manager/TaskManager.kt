package com.personalplanner.app.manager

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import com.personalplanner.app.model.Task
import com.personalplanner.app.storage.TaskStorage
import com.personalplanner.app.widget.TaskWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskManager private constructor(
    private val context: Context
) {

    companion object {
        @Volatile
        private var INSTANCE: TaskManager? = null

        fun getInstance(context: Context): TaskManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TaskManager(context.applicationContext).also {
                    INSTANCE = it
                }
            }
        }
    }

    private val storage = TaskStorage(context)
    private val scope = CoroutineScope(Dispatchers.IO)

    private val tasks = mutableStateListOf<Task>()

    init {
        val savedTasks = storage.loadTasks()

        if (savedTasks.isEmpty()) {
            tasks.addAll(
                listOf(
                    Task(1, "Сделать презентацию"),
                    Task(2, "Позвонить клиенту"),
                    Task(3, "Купить билеты")
                )
            )
        } else {
            tasks.addAll(savedTasks)
        }
    }

    fun getTasks(): List<Task> = tasks

    fun getActiveTasks(): List<Task> = tasks.filter { !it.deleted }

    fun getArchivedTasks(): List<Task> = tasks.filter {
        it.completed && !it.deletedFromArchive
    }

    fun addTask(text: String) {
        if (text.isBlank()) {
            return
        }

        val nextId = if (tasks.isEmpty()) 1 else tasks.maxOf { it.id } + 1

        tasks.add(Task(id = nextId, text = text.trim()))
        storage.saveTasks(tasks)
        updateWidget()
    }

    fun setTaskCompleted(id: Long, completed: Boolean) {
        val index = tasks.indexOfFirst { it.id == id }

        if (index != -1) {
            tasks[index] = tasks[index].copy(completed = completed)
            storage.saveTasks(tasks)
            updateWidget()
        }
    }

    fun deleteTask(id: Long) {
        val index = tasks.indexOfFirst { it.id == id }

        if (index != -1) {
            tasks[index] = tasks[index].copy(deleted = true)
            storage.saveTasks(tasks)
            updateWidget()
        }
    }

    fun deleteTaskFromArchive(id: Long) {
        val index = tasks.indexOfFirst { it.id == id }

        if (index != -1) {
            tasks[index] = tasks[index].copy(
                completed = false,
                deletedFromArchive = false
            )
            storage.saveTasks(tasks)
            updateWidget()
        }
    }

    private fun updateWidget() {
        scope.launch {
            try {
                TaskWidget.updateAll(context)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
