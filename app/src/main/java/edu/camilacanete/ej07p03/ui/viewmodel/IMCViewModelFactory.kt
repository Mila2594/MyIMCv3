package edu.camilacanete.ej07p03.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/*
   Garantiza que se cree una unica instancia del ViewModel durante el ciclo de vida del componente,
   pasando el contexto de la aplicacion al ViewModel
 */
class IMCViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IMCViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return IMCViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}