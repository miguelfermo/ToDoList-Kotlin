package br.edu.satc.todolistcompose

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

enum class ModoTema {
    CLARO, ESCURO, SISTEMA
}

class GerenciadorTema(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("pref_tema", Context.MODE_PRIVATE)
    private val CHAVE_TEMA = "modo_tema"

    fun getModoTema(): ModoTema {
        val temaSalvo = prefs.getString(CHAVE_TEMA, ModoTema.SISTEMA.name)
        return ModoTema.valueOf(temaSalvo ?: ModoTema.SISTEMA.name)
    }

    fun setModoTema(modo: ModoTema) {
        val editor = prefs.edit()
        editor.putString(CHAVE_TEMA, modo.name)
        editor.apply()

        // Aplicar o modo de tema
        when (modo) {
            ModoTema.CLARO -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            ModoTema.ESCURO -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            ModoTema.SISTEMA -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}