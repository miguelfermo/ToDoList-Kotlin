package br.edu.satc.todolistcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import br.edu.satc.todolistcompose.ui.screens.HomeScreen
import br.edu.satc.todolistcompose.ui.theme.ToDoListComposeTheme

class MainActivity : ComponentActivity() {
    private lateinit var gerenciadorTema: GerenciadorTema

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa o Gerenciador de Tema
        gerenciadorTema = GerenciadorTema(this)

        val taskViewModel: TaskViewModel = ViewModelProvider(
            owner = this,
            factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[TaskViewModel::class.java]

        setContent {
            // Obtém o modo de tema salvo
            val modoTemaSalvo = remember { gerenciadorTema.getModoTema() }
            var modoTema by remember { mutableStateOf(modoTemaSalvo) }

            // Aplica o tema escuro/claro com base na preferência
            val temaEscuro = when (modoTema) {
                ModoTema.CLARO -> false
                ModoTema.ESCURO -> true
                ModoTema.SISTEMA -> isSystemInDarkTheme()
            }

            ToDoListComposeTheme(darkTheme = temaEscuro) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val tarefas by taskViewModel.tasks.collectAsState()

                    HomeScreen(
                        tarefas = tarefas,
                        onCriarTarefa = { titulo, descricao ->
                            taskViewModel.adicionarTarefa(titulo, descricao)
                        },
                        onCompletarTarefa = { tarefa, completa ->
                            taskViewModel.atualizarCompletarTarefa(tarefa.id, completa)
                        },
                        modoTemaAtual = modoTema,
                        onMudarModoTema = { novoModo ->
                            gerenciadorTema.setModoTema(novoModo)
                            modoTema = novoModo
                        }
                    )
                }
            }
        }
    }
}