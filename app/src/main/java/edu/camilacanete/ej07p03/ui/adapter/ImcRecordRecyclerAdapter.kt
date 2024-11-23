package edu.camilacanete.ej07p03.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.camilacanete.ej07p03.R
import edu.camilacanete.ej07p03.data.model.ImcRecord
import edu.camilacanete.ej07p03.databinding.ItemViewHistoricalBinding
import java.util.Calendar
import java.util.Locale

class ImcRecordRecyclerAdapter(
    private val context: Context,
    private var recordList: List<ImcRecord>,
    private val onDeleteClick: (ImcRecord) -> Unit ):
    RecyclerView.Adapter<ImcRecordRecyclerAdapter.ImcRecordViewHolder> () {

        //clase anidada ViewHolder que contiene las vistas individuales
        class ImcRecordViewHolder(val binding: ItemViewHistoricalBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImcRecordViewHolder {
        //inflar el layout del item
        val binding = ItemViewHistoricalBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ImcRecordViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return recordList.size
    }

    override fun onBindViewHolder(holder: ImcRecordViewHolder, position: Int) {
        //obtener objeto de la lista
        val indexRecordList = recordList[position]
        // Formatear la fecha desde un mapa con las partes de la fecha (día, mes, año)
        val formattedDate = formatDate(indexRecordList.date)
        // Formatear el valor IMC con 2 decimales
        val formattedIMC = String.format(Locale.getDefault(), "%.2f", indexRecordList.imcResult)

        with(holder.binding){
            //asignar datos a los elementos del layout
            tvResultIMC.text = formattedIMC
            tvSex.text = if (indexRecordList.isMan) "Hombre" else "Mujer"
            tvStateResult.text = indexRecordList.stateResult
            tvWeight.text = context.getString(R.string.weight_placeholder,indexRecordList.weight)
            tvHeight.text = context.getString(R.string.height_placeholder,indexRecordList.height.toInt())
            tvDateMonth.text = formattedDate["month"] ?: "No month"
            tvDateNumberday.text = formattedDate["day"] ?: "No day"
            tvDateNumberYear.text = formattedDate["year"] ?: "No year"
        }

        // Listener para el botón de eliminar
        holder.binding.layoutItemHistorical.setOnClickListener {
            onDeleteClick(indexRecordList)
        }
    }

    // Actualiza los registros y notifica al adaptador
    fun updateRecords(newRecordList: List<ImcRecord>) {
        val oldRecordList = recordList
        recordList = newRecordList

        when {
            oldRecordList.size < newRecordList.size -> notifyItemRangeInserted(oldRecordList.size, newRecordList.size - oldRecordList.size)
            oldRecordList.size > newRecordList.size -> notifyItemRangeRemoved(newRecordList.size, oldRecordList.size - newRecordList.size)
            else -> notifyItemRangeChanged(0, newRecordList.size)
        }
    }

    //Método para descomponer la fecha en: nameMonth, numberDay, numberYear
    private fun formatDate (date : String) : Map<String,String>{
        // Separar el string "dd/MM/yyyy" en partes
        val parts = date.split("/")
        return if ( parts.size == 3){
            val numberDay = parts[0]
            val numberMonth = parts[1].toInt() - 1
            val numberYear = parts[2]

            // Convertir el número de mes en nombre
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.MONTH, numberMonth)
            val nameMonth = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) ?: "Unknown"

            // Devolver el mapa con las partes de la fecha
            mapOf(
                "day" to numberDay,
                "month" to nameMonth,
                "year" to  numberYear
            )
        } else {
            // Si el formato es incorrecto, devolver valores predeterminados
            mapOf("day" to "No day", "month" to "No month", "year" to "No year")
        }
    }
}