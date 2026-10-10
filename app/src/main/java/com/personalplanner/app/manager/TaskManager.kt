package com.personalplanner.app.manager

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import com.personalplanner.app.model.Task
import com.personalplanner.app.storage.TaskStorage
import com.personalplanner.app.widget.TaskWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.personalplanner.app.model.Topic
import com.personalplanner.app.storage.TopicStorage

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
    private val topicStorage = TopicStorage(context)
    private val scope = CoroutineScope(Dispatchers.IO)
    private val tasks = mutableStateListOf<Task>()
    private val topics = mutableStateListOf<Topic>()


    init {
        topics.addAll(topicStorage.loadTopics())

        // Находим тему «Общее» или создаём её
        val generalTopic = topics.find { it.name == "Общее" }
            ?: Topic(
                id = if (topics.isEmpty()) 1L else topics.maxOf { it.id } + 1,
                name = "Общее",
                color = 0xFF9E9E9E
            ).also {
                topics.add(it)
                topicStorage.saveTopics(topics)
            }


        val hasSavedTasks = storage.hasSavedTasks()
        val savedTasks = storage.loadTasks()

        val initialTasks = if (hasSavedTasks) {
            savedTasks
        } else {
            listOf(
                Task(1, "Сделать презентацию"),
                Task(2, "Позвонить клиенту"),
                Task(3, "Купить билеты")
            )
        }

// Assign General to tasks with no topic
        val migratedTasks = initialTasks.map { task ->
            if (task.topicId == null) {
                task.copy(topicId = generalTopic.id)
            } else {
                task
            }
        }

        tasks.addAll(migratedTasks)

// Save initial tasks or persist topic migration.
// Do not overwrite an existing empty or unreadable task record.
        if (!hasSavedTasks || savedTasks.isNotEmpty()) {
            storage.saveTasks(tasks)
        }

    }



    fun reloadTasks() {
        val savedTasks = storage.loadTasks()

        tasks.clear()
        tasks.addAll(savedTasks)
    }

    fun getActiveTasks(): List<Task> =
        tasks.filter { !it.deleted }

    fun getActiveTasksByTopic(topicId: Long): List<Task> =
        tasks.filter { !it.deleted && it.topicId == topicId }

    fun getTopics(): List<Topic> = topics.toList()
    fun addTopic(name: String, color: Long) {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) return

        val nextId = if (topics.isEmpty()) {
            1L
        } else {
            topics.maxOf { it.id } + 1
        }

        topics.add(
            Topic(
                id = nextId,
                name = trimmedName,
                color = color
            )
        )

        topicStorage.saveTopics(topics)
    }


    fun setTopicColor(topicId: Long, color: Long) {
        val index = topics.indexOfFirst { it.id == topicId }
        if (index == -1) return

        topics[index] = topics[index].copy(color = color)
        topicStorage.saveTopics(topics)
    }

    fun setTaskTopic(taskId: Long, topicId: Long?) {
        val index = tasks.indexOfFirst { it.id == taskId }
        if (index == -1) return

        if (topicId != null && topics.none { it.id == topicId }) return

        tasks[index] = tasks[index].copy(topicId = topicId)
        storage.saveTasks(tasks)
        updateWidget()
    }

    fun getArchivedTasks(): List<Task> = tasks.filter {
        it.completed && !it.deletedFromArchive
    }



    fun addTask(text: String, topicId: Long? = null) {
        if (text.isBlank()) return

        val generalTopic = topics.find { it.name == "Общее" }
            ?: return

        val targetTopicId = topicId
            ?: generalTopic.id

        if (topics.none { it.id == targetTopicId }) return

        val nextId = if (tasks.isEmpty()) {
            1L
        } else {
            tasks.maxOf { it.id } + 1
        }

        tasks.add(
            Task(
                id = nextId,
                text = text.trim(),
                topicId = targetTopicId
            )
        )

        storage.saveTasks(tasks)
        updateWidget()
    }



    fun setTaskCompleted(id: Long, completed: Boolean) {
        val index = tasks.indexOfFirst { it.id == id }

        if (index != -1) {
            tasks[index] = tasks[index].copy(
                completed = completed,
                completedAt = if (completed) {
                    System.currentTimeMillis()
                } else {
                    null
                }
            )

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

    fun restoreTaskFromArchive(id: Long) {
        val index = tasks.indexOfFirst { it.id == id }
        if (index != -1) {
            tasks[index] = tasks[index].copy(
                completed = false,
                completedAt = null,
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