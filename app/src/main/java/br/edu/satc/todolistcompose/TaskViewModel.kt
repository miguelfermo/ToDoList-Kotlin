package br.edu.satc.todolistcompose

import TaskData
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository
    private val _tasks = MutableStateFlow<List<TaskData>>(emptyList())
    val tasks: StateFlow<List<TaskData>> = _tasks.asStateFlow()

    init {
        val dao = TaskDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(dao)
        viewModelScope.launch {
            repository.allTasks.collect { taskList ->
                _tasks.value = taskList
            }
        }
    }

    fun adicionarTarefa(titulo: String, descricao: String) {
        if (titulo.isNotBlank()) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.insertTask(TaskData(title = titulo, description = descricao, complete = false))
            }
        }
    }

    fun atualizarCompletarTarefa(taskId: Int, completa: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTaskCompletion(taskId, completa)
        }
    }

    fun excluirTarefa(task: TaskData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTask(task)
        }
    }
}