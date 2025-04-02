package br.edu.satc.todolistcompose

import TaskData
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {
    val allTasks: Flow<List<TaskData>> = taskDao.getAllTasks()

    suspend fun insertTask(task: TaskData) {
        taskDao.insertTask(task)
    }

    suspend fun updateTask(task: TaskData) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: TaskData) {
        taskDao.deleteTask(task)
    }

    suspend fun updateTaskCompletion(taskId: Int, isCompleted: Boolean) {
        taskDao.updateTaskCompletion(taskId, isCompleted)
    }
}