import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.accountastock_android.R
import com.example.accountastock_android.databinding.FragmentUserBinding
import com.example.accountastock_android.ui.user.UserViewModel
import com.shawnlin.numberpicker.NumberPicker
import java.util.*

class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configuration du Spinner
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.period_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerPeriod.adapter = adapter
        }

        // Initialiser les NumberPicker pour le mois et l'année avec les valeurs actuelles
        val currentCalendar = Calendar.getInstance()
        val currentYear = currentCalendar.get(Calendar.YEAR)
        val currentMonth = currentCalendar.get(Calendar.MONTH)

        val months = arrayOf(
            "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
            "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"
        )

        // Créer une liste dynamique d'années de 1900 à l'année actuelle (2024)
        val years = (1900 until currentYear).map { it.toString() }.toTypedArray()

        binding.numberPickerMonth.apply {
            minValue = 1
            maxValue = 12
            displayedValues = months
            value = currentMonth + 1 // Ajouter 1 car les mois commencent à 1
        }

        // Limiter les années de 1901 à l'année actuelle (2024)
        binding.numberPickerYear.apply {
            minValue = 1901
            maxValue = currentYear
            value = currentYear
        }

        binding.spinnerPeriod.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                if (selectedItem == "Mois") {
                    binding.monthYearContainer.visibility = View.VISIBLE
                    binding.numberPickerMonth.visibility = View.VISIBLE
                    binding.numberPickerYear.visibility = View.VISIBLE
                } else if (selectedItem == "Année") {
                    binding.monthYearContainer.visibility = View.VISIBLE
                    binding.numberPickerYear.visibility = View.VISIBLE
                    binding.numberPickerMonth.visibility = View.GONE
                } else {
                    binding.monthYearContainer.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Faire quelque chose lorsque rien n'est sélectionné
            }
        }

        // Bouton pour télécharger les déclarations fiscales
        binding.buttonDownloadPdf.setOnClickListener {
            val selectedPeriod = binding.spinnerPeriod.selectedItem.toString()
            val selectedMonth = binding.numberPickerMonth.value // Pour le mois
            val selectedYear = binding.numberPickerYear.value // Pour l'année
            // Logique pour télécharger le PDF en fonction de la période sélectionnée et de la valeur sélectionnée
        }

        // Calculate the height of the bottom navigation bar
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        val navigationBarHeight = if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else {
            0
        }

        // Adjust the bottom margin of the logout button
        val logoutButtonParams = binding.logoutButton.layoutParams as ViewGroup.MarginLayoutParams
        logoutButtonParams.setMargins(0, 0, 50, navigationBarHeight + 50)
        binding.logoutButton.layoutParams = logoutButtonParams

        binding.logoutButton.setOnClickListener {
            userViewModel.logout()
            Toast.makeText(context, getSubscriptionLevel(), Toast.LENGTH_SHORT).show()
        }

        userViewModel.logoutEvent.observe(viewLifecycleOwner, Observer { isLoggedOut ->
            if (isLoggedOut) {
                findNavController().navigate(R.id.action_navigation_user_to_navigation_login)
            }
        })
    }

    private fun getSubscriptionLevel(): String? {
        val sharedPreferences = requireActivity().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("subscription_level", null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}