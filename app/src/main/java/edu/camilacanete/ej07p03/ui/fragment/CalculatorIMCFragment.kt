package edu.camilacanete.ej07p03.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import edu.camilacanete.ej07p03.databinding.FragmentCalculatorImcBinding
import edu.camilacanete.ej07p03.data.model.ImcRecord
import edu.camilacanete.ej07p03.ui.viewmodel.IMCViewModel
import edu.camilacanete.ej07p03.ui.viewmodel.IMCViewModelFactory
import java.util.Calendar
import java.util.Locale

class CalculatorIMCFragment : Fragment() {

    private lateinit var binding: FragmentCalculatorImcBinding
    private val imcViewModel: IMCViewModel by activityViewModels{
        IMCViewModelFactory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCalculatorImcBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Botón Calcular
        binding.buttonCalcular.setOnClickListener {
            ocultarTeclado()
            obtenerResultadosIMC()
        }
    }

    override fun onPause() {
        super.onPause()
        limpiarInterfaz()
    }

    // Método que llama a validaciones y luego recoje el calculo del IMC
    private fun obtenerResultadosIMC() {
        // Validar los datos de entrada
        val (peso, altura) = validarDatos() ?: return

        // Calcular el IMC y manejar los resultados
        val resultIMC = realizarCalculoIMC(peso, altura)
        // Asignar resultado e estado a los TextViews
        mostrarResultadoIMC(resultIMC)
        mostrarEstadoIMC(resultIMC)

        // Preguntar al usuario si quiere guardar el registro con un Dialog
        mostrarDialogoGuardarIMC(resultIMC,peso,altura)
    }

    // Método para validar los datos y mostrar toasts en caso de error
    private fun validarDatos(): Pair<Double, Double>? {
        val pesoObt = binding.pesoEditText.text.toString().toDoubleOrNull()
        val alturaObt = binding.alturaEditText.text.toString().toDoubleOrNull()

        if (pesoObt == null || alturaObt == null) {
            Snackbar.make(binding.root,"Los campos Peso y Altura no deben estar vacíos para realizar el cálculo",Snackbar.LENGTH_SHORT).show()
            return null
        }
        return Pair(pesoObt, alturaObt)
    }

    // Método para calcular el IMC y guardar registro
    private fun realizarCalculoIMC(peso: Double, altura: Double) : Double {
        val heightM = altura / 100
        val resultIMC = peso / (heightM * heightM)
        return resultIMC
    }

    //Método para mostrar el dialogo preguntado si guarda el calculo IMC en el registro
    private fun mostrarDialogoGuardarIMC(imc: Double,peso: Double,altura: Double) {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Guardar Registro")
            .setMessage("¿Deseas guarda el registro en el Histórico")
            .setPositiveButton(android.R.string.ok){ _, _ ->
                guardarIMCRegistro(imc,peso,altura)
                Snackbar.make(binding.root,"Registro guardado en el Histórico",Snackbar.LENGTH_SHORT).show()
            }
            .setNegativeButton(android.R.string.cancel){_,_ ->
                Snackbar.make(binding.root,"Registro no ha sido guardado",Snackbar.LENGTH_SHORT).show()
            }
            .create()

        dialog.show()
    }

    //Método para guardar registro histórico
    private fun guardarIMCRegistro (imc: Double,peso: Double,altura: Double){
        val estado = asignarEstadoIMC(imc)
        val isMan = binding.radioButtonHombre.isChecked

        // Obtener la fecha y hora actual con Calendar
        val hoy = Calendar.getInstance()
        Log.d("FECHA",
            "${hoy.get(Calendar.DAY_OF_MONTH)}" +
                    "/${hoy.get(Calendar.MONTH) + 1 }" +
                    "/${hoy.get(Calendar.YEAR)}"
        )

        // Formatear la fecha como string
        val formattedDate = "${hoy.get(Calendar.DAY_OF_MONTH)}/${hoy.get(Calendar.MONTH) + 1}/${hoy.get(Calendar.YEAR)}"
        val record = ImcRecord(formattedDate,estado,isMan,peso,altura,imc)

        imcViewModel.addRecord(record)

        //Log para registrar actividad de guardado
        Log.d("IMC:APP","Registro guardado correctamente: $record")
    }

    // Método para mostrar el resultado del IMC en el TextView
    private fun mostrarResultadoIMC(imc: Double) {
        // Asignar resultado a TextView
        binding.resultTextView.text = String.format(Locale.getDefault(), "%.2f", imc)
        binding.resultTextView.visibility = TextView.VISIBLE // Asegúrate de que sea visible
    }

    // Método para mostrar el estado del IMC en el TextView
    private fun mostrarEstadoIMC(imc: Double) {
        // Asignar estado al TextView
        val estado = asignarEstadoIMC(imc)
        binding.resultStateIMCTextView.text = estado
        binding.resultStateIMCTextView.visibility = TextView.VISIBLE // Asegúrate de que sea visible
    }

    //método para asignar estado según IMC
    private fun asignarEstadoIMC (imc : Double) : String {
        //variables estado
        val pesoInferior = "Peso inferior al normal"
        val pesoNormal = "Normal"
        val pesoSobre  = "Sobrepeso"
        val pesoObesidad = "Obesidad"

        when {
            //Si es hombre se asignan estos rangos según el IMC calculado
            binding.radioButtonHombre.isChecked -> {
                return when {
                    imc < 18.5 -> pesoInferior
                    imc in 18.5..24.9 -> pesoNormal
                    imc in 25.0..29.9 -> pesoSobre
                    else -> pesoObesidad //si el imc es mayor a 30.0
                }
            }
            //si es mujer se asignan estos rangos según el IMC calculado
            binding.radioButtonMujer.isChecked -> {
                return when {
                    imc < 18.5 -> pesoInferior
                    imc in 18.5..23.9 -> pesoNormal
                    imc in 24.0..28.9 -> pesoSobre
                    else -> pesoObesidad //si el imc es mayor a 29.0
                }
            }
            else -> {
                return "Sexo no seleccinado"
            }
        }
    }

    //método para ocultar teclado al presionar botón Calcular
    private  fun ocultarTeclado () {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager //acceder a los servicios del sistema para manejar el teclado
        imm.hideSoftInputFromWindow(binding.pesoEditText.windowToken,0) //ocultar el teclado para editText de peso
        imm.hideSoftInputFromWindow(binding.alturaEditText.windowToken,0) //ocultar el telcado para editText de altura
    }

    //método para limpiar los campos de entradas y resultados al ir a la vista historico
    private fun limpiarInterfaz(){

        with(binding){
            pesoEditText.text.clear()
            alturaEditText.text.clear()
            resultTextView.text = ""
            resultStateIMCTextView.text = ""
        }
    }
}