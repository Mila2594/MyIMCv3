package edu.camilacanete.ej07p03.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import edu.camilacanete.ej07p03.databinding.FragmentRecordImcBinding
import edu.camilacanete.ej07p03.data.model.ImcRecord
import edu.camilacanete.ej07p03.ui.adapter.ImcRecordRecyclerAdapter
import edu.camilacanete.ej07p03.ui.viewmodel.IMCViewModel
import edu.camilacanete.ej07p03.ui.viewmodel.IMCViewModelFactory


class HistoricalFragment : Fragment() {

    /*
       Se usa un binding opcional para manejar correctamente el ciclo de vida del fragmento y evitar
       problemas de acceso a vistas después de que la vista sea destruida. Esto previene fugas de memoria
       y errores como NullPointerException al acceder a la vista cuando ya no está disponible.
     */
    private var _binging: FragmentRecordImcBinding? = null
    /*
       Se establece 'binding' con '!!' para forzar la desreferenciación de '_binding'
       y asegura de que el binding no sea null mientras la vista esté activa.
     */
    private val binding get() = _binging!!

    /*
        Se instancia el ViewModel usando activityViewModels para compartir datos entre fragmentos
        El ViewModel es creado utilizando una fábrica personalizada, necesaria cuando se pasa un Application.
     */
    private val imcViewModel: IMCViewModel by activityViewModels{
        IMCViewModelFactory(requireActivity().application)
    }
    private lateinit var adapter: ImcRecordRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binging = FragmentRecordImcBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Cargar registros desde el archivo
        imcViewModel.loadRecords()

        // Inicializa el RecyclerView y el adaptador
        adapter = ImcRecordRecyclerAdapter(requireContext(), emptyList()){ record ->
            showDeleteConfirmationDialog(record) // Se pasa una función para manejar la eliminación
        }

        // Configurar RecyclerView para mostrar los registros en una lista vertical
        binding.recyclerviewRecordList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HistoricalFragment.adapter
        }

        // Observar cambios en la lista de registros del ViewModel
        // Si la lista está vacía se muestra el textView "No hay registros"
        imcViewModel.imcRecordList.observe(viewLifecycleOwner) { recordList ->
            adapter.updateRecords(recordList) // Actualiza el adaptador con los nuevos datos
            binding.textviewNorecords.visibility = if (recordList.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    //Método muestra alert para confirmar o no eliminacion del registro
    private fun showDeleteConfirmationDialog (record: ImcRecord){
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Eliminar registro")
            .setMessage("¿Estás seguro de elimnar este registro")
            .setPositiveButton(android.R.string.ok){ _,_ ->
                val position = imcViewModel.imcRecordList.value?.indexOf(record) ?: -1
                if (position != -1) {
                    imcViewModel.deleteRecord(record)
                    adapter.notifyItemRemoved(position)
                    Snackbar.make(binding.root, "Registro eliminado", Snackbar.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(android.R.string.cancel,null)
            .create()

        dialog.show()
    }

    //Se limpia el binding al destruir la vista para evitar fugas de memoria.
    override fun onDestroyView() {
        super.onDestroyView()
        _binging = null // Limpia la referencia al binding cuando la vista es destruida
    }

}