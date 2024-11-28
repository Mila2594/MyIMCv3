package edu.camilacanete.ej07p03.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import edu.camilacanete.ej07p03.helper.FileHelper
import edu.camilacanete.ej07p03.data.model.ImcRecord

/*
   Clase para el ViewModel de la aplicación, permite centralizar la gestión de los registros IMC
   Permite que los datos se mantengan separados de la UI, asegurando que sobrevivan a cambios de configuración como rotaciones.
 */
class IMCViewModel (application: Application) : AndroidViewModel(application) {
    private val fileHelper: FileHelper = FileHelper(application)
    // Lista mutable de registros IMC
    private val _imcRecordList = MutableLiveData<MutableList<ImcRecord>>(mutableListOf())
    val imcRecordList: LiveData<MutableList<ImcRecord>> get() = _imcRecordList

    //Método para cargar registros en el histórico
    fun loadRecords() {
        _imcRecordList.value = fileHelper.readRecords().toMutableList()
    }

    // Método para añadir un registro
    fun addRecord(record: ImcRecord) {
        val currentList = _imcRecordList.value ?: mutableListOf()
        currentList.add(record)
        _imcRecordList.value = currentList
        fileHelper.writeRecords(record)
    }

    // Método para eliminar un registro
    fun deleteRecord(record: ImcRecord) {
        val currentList = _imcRecordList.value ?: mutableListOf()
        currentList.remove(record)
        _imcRecordList.value = currentList
        fileHelper.deleteRecordFromFile(record)
    }
}