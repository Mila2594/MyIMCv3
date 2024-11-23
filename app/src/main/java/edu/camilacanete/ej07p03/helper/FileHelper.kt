package edu.camilacanete.ej07p03.helper

import android.content.Context
import android.util.Log
import edu.camilacanete.ej07p03.data.model.ImcRecord
import java.io.File
import java.io.FileNotFoundException
import java.text.ParseException
import java.util.Locale

class FileHelper (private val context: Context){

    private val nameFile = "imc_records.csv"

    //método para escribir nuevo registro en el fichero imc_records
    fun writeRecords (record: ImcRecord){

        //clearFile() // Limpiar el archivo antes de escribir el nuevo registro

        val gender = if (record.isMan) "Hombre" else "Mujer"

        val file = File(context.filesDir, nameFile) // 'context.filesDir' apunta al directorio de archivos internos de la app en el dispositivo,
        file.appendText("${record.date};$gender;${record.imcResult};${record.stateResult};${"%.1f".format(record.weight)};${record.height}\n")
        mostrarContenidoEnConsola()
    }

    //método para leer registros del fichero imc_records
    fun readRecords () : List<ImcRecord>{
        val recordList = mutableListOf<ImcRecord>()
        val file = File(context.filesDir,nameFile)

        try {
            // Abrir el archivo y leer línea por línea
            file.bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    val data = line.split(";")

                    // Validar que la línea tenga exactamente 4 partes
                    if(data.size == 6){
                        val date = data[0]
                        val isMan = when (data[1].trim().lowercase(Locale.ROOT)) {
                            "hombre" -> true
                            "mujer" -> false
                            else -> false // Valor por defecto si el género no es válido
                        }
                        val imcResult = data[2].toDouble()
                        val stateResult = data[3]
                        val weight = "%.1f".format(data[4].toDouble()).toDouble()
                        val height = data[5].toDouble()

                        recordList.add(ImcRecord(date,stateResult,isMan,weight,height,imcResult))
                    }
                }
            }
        } catch (e: ParseException){
            println("Error formato fecha:  ${e.message}")
        } catch (e: FileNotFoundException){
            println("Archivo no encontrado. No hay registros para leer.")
        } catch (e: NumberFormatException){
            println("Error al formatear IMC result:  ${e.message}")
        } catch (e: Exception){
            e.printStackTrace()
        }
        return recordList
    }

    // Método para actualizar los registros en el archivo después de eliminar uno
    fun deleteRecordFromFile (recordToDelete : ImcRecord) {
        val file = File(context.filesDir,nameFile)

        try {
            // Leer todo el contenido del archivo
            val fileContent = file.readLines()

            // Filtrar las líneas para eliminar la que contiene el registro a eliminar
            val filteredContent = fileContent.filterNot { line ->
                // Compara si la línea contiene la fecha, estado y valores del registro a eliminar
                val parts = line.split(";")
                parts[0] == recordToDelete.date &&
                        parts[1] == (if (recordToDelete.isMan) "Hombre" else "Mujer") &&
                        parts[2] == recordToDelete.imcResult.toString() &&
                        parts[3] == recordToDelete.stateResult &&
                        parts[4] == recordToDelete.weight.toString() &&
                        parts[5] == recordToDelete.height.toString()
            }
            // Sobrescribir el archivo solo si hubo cambios
            if (filteredContent.size != fileContent.size) {
                file.writeText(filteredContent.joinToString("\n"))
                // Escribir nuevamente el archivo con el contenido filtrado (sin el registro eliminado)
                Log.d("FileHelper", "Registro eliminado correctamente.")
            } else {
                Log.d("FileHelper", "El registro no fue encontrado.")
            }
        }catch (e: Exception) {
            Log.e("FileHelper", "Error al eliminar el registro: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun mostrarContenidoEnConsola() {
        val file = File(context.filesDir, "imc_records.csv")
        if (file.exists()) {
            val contenido = file.readText()
            Log.d("ContenidoCSV", contenido)
        } else {
            Log.d("ContenidoCSV", "No hay registros aún.")
        }
    }

/*
    private fun clearFile() {
        val file = File(context.filesDir, nameFile)
        file.writeText("") // Esto borra todo el contenido del archivo
        Log.d("ContenidoCSV", "El archivo ha sido limpiado.")
    }

 */

}