package br.edu.satc.todolistcompose.ui.screens
import br.edu.satc.todolistcompose.TaskCard
import TaskData
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import br.edu.satc.todolistcompose.ModoTema
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    tarefas: List<TaskData> = emptyList(),
    onCriarTarefa: (String, String) -> Unit,
    onCompletarTarefa: (TaskData, Boolean) -> Unit,
    modoTemaAtual: ModoTema,
    onMudarModoTema: (ModoTema) -> Unit
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var showMenuTema by remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("ToDoList UniSATC") },
                actions = {
                    IconButton(onClick = { showMenuTema = true }) {
                        Icon(Icons.Rounded.Settings, contentDescription = "Configurações de Tema")
                    }
                    DropdownMenu(
                        expanded = showMenuTema,
                        onDismissRequest = { showMenuTema = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Tema Claro") },
                            onClick = {
                                onMudarModoTema(ModoTema.CLARO)
                                showMenuTema = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Tema Escuro") },
                            onClick = {
                                onMudarModoTema(ModoTema.ESCURO)
                                showMenuTema = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Sistema") },
                            onClick = {
                                onMudarModoTema(ModoTema.SISTEMA)
                                showMenuTema = false
                            },
                            leadingIcon = {
                                Icon(Icons.Rounded.Settings, contentDescription = null)
                            }
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Nova tarefa") },
                icon = { Icon(Icons.Filled.Add, contentDescription = "") },
                onClick = {
                    showBottomSheet = true
                }
            )
        }
    ) { innerPadding ->
        // Conteúdo principal - lista de tarefas
        HomeContent(innerPadding, tarefas, onCompletarTarefa)

        // Modal para adicionar nova tarefa
        NewTask(
            showBottomSheet = showBottomSheet,
            onSalvar = { titulo, descricao ->
                onCriarTarefa(titulo, descricao)
            },
            onComplete = { showBottomSheet = false }
        )
    }
}

@Composable
fun HomeContent(
    innerPadding: PaddingValues,
    tarefas: List<TaskData>,
    onCompletarTarefa: (TaskData, Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .padding(top = innerPadding.calculateTopPadding())
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top
    ) {
        if (tarefas.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Nenhuma tarefa encontrada. Adicione uma nova tarefa!")
            }
        } else {
            for (tarefa in tarefas) {
                TaskCard(
                    title = tarefa.title,
                    description = tarefa.description,
                    complete = tarefa.complete,
                    onCheckChange = { isCompleted ->
                        onCompletarTarefa(tarefa, isCompleted)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTask(
    showBottomSheet: Boolean,
    onSalvar: (String, String) -> Unit,
    onComplete: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var taskTitle by remember { mutableStateOf("") }
    var taskDescription by remember { mutableStateOf("") }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                onComplete()
            },
            sheetState = sheetState,
        ) {
            // Conteúdo do Sheet
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = taskTitle,
                    onValueChange = { taskTitle = it },
                    label = { Text(text = "Título da tarefa") }
                )

                OutlinedTextField(
                    value = taskDescription,
                    onValueChange = { taskDescription = it },
                    label = { Text(text = "Descrição da tarefa") }
                )

                Button(
                    modifier = Modifier.padding(top = 4.dp),
                    onClick = {
                        if (taskTitle.isNotBlank()) {
                            onSalvar(taskTitle, taskDescription)
                            taskTitle = ""
                            taskDescription = ""
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    onComplete()
                                }
                            }
                        }
                    }
                ) {
                    Text("Salvar")
                }
            }
        }
    }
}