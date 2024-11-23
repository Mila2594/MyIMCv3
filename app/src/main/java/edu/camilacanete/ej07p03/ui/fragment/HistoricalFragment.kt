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

    private var _binging: FragmentRecordImcBinding? = null
    private val binding get() = _binging!!

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
            showDeleteConfirmationDialog(record)
        }

        binding.recyclerviewRecordList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HistoricalFragment.adapter
        }

        // Observar cambios en la lista de registros
        imcViewModel.imcRecordList.observe(viewLifecycleOwner) { recordList ->
            adapter.updateRecords(recordList)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binging = null
    }

}