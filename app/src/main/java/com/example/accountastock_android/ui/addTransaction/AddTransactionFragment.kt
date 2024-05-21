package com.example.accountastock_android.ui.addTransaction

import android.content.Context
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.accountastock_android.MainActivity
import com.example.accountastock_android.R
import com.example.accountastock_android.data.database.MyDatabaseHelper
import com.example.accountastock_android.databinding.FragmentAddTransactionBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AddTransactionFragment : Fragment() {

    private lateinit var dbHelper: MyDatabaseHelper
    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!

    private lateinit var expenseLayout: View
    private lateinit var profitLayout: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity = activity as MainActivity
        dbHelper = mainActivity.getDatabaseHelper()

        // Récupérer les vues pour ajouter une dépense et un profit
        expenseLayout = binding.layoutAddExpense.root
        profitLayout = binding.layoutAddProfit.root

        // Récupérer le RadioGroup
        val transactionTypeRadioGroup = binding.transactionTypeRadioGroup

        // Cacher les mises en page de dépense et de profit par défaut
        expenseLayout.visibility = View.VISIBLE
        profitLayout.visibility = View.GONE

        // Gérer le changement de sélection dans le RadioGroup
        transactionTypeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.expenseRadioButton -> {
                    // Afficher la vue pour ajouter une dépense et cacher la vue pour ajouter un profit
                    expenseLayout.visibility = View.VISIBLE
                    profitLayout.visibility = View.GONE
                }
                R.id.profitRadioButton -> {
                    // Afficher la vue pour ajouter un profit et cacher la vue pour ajouter une dépense
                    expenseLayout.visibility = View.GONE
                    profitLayout.visibility = View.VISIBLE
                }
            }
        }

        // Gérer le clic sur le bouton "Ajouter la transaction"
        binding.addTransactionButton.setOnClickListener {
            // Ici, vous pouvez récupérer les données des champs pour la dépense ou le profit et les traiter en conséquence
            if (expenseLayout.isVisible) {
                // Ajouter une dépense
                addExpense()
            } else if (profitLayout.isVisible) {
                // Ajouter un profit
                addProfit()
            }
        }

        binding.homeLink.setOnClickListener {
            findNavController().navigate(R.id.action_addTransactionFragment_to_navigation_account)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addExpense() {
        // Logique pour ajouter une dépense
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val title = binding.layoutAddExpense.editTextTitle.text.toString()
        val comment = binding.layoutAddExpense.editTextComment.text.toString()
        val amountText = binding.layoutAddExpense.editTextAmount.text.toString()
        val taxText = binding.layoutAddExpense.editTextTax.text.toString()

        // Vérifiez que le titre et le montant ne sont pas vides
        if (title.isNotEmpty() && amountText.isNotEmpty()) {
            try {
                val amount = amountText.toDouble()
                val tax: Int
                if (taxText.isNotEmpty()){
                    val tax = taxText.toInt()
                    val userId = getUserId()
                    val date = currentDate.format(formatter)

                    if (getSubscriptionLevel() == "Level 1") {
                        val dbHelper = MyDatabaseHelper(requireContext())
                        dbHelper.addExpense(title, comment, amount, tax, userId, date)
                        Toast.makeText(requireContext(), "Dépense ajoutée avec succès", Toast.LENGTH_SHORT).show()
                    } else {
                        // Gérer le cas où l'utilisateur n'a pas un abonnement de niveau 1
                        Toast.makeText(requireContext(), "Niveau d'abonnement insuffisant", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val tax = 0
                    val userId = getUserId()
                    val date = currentDate.format(formatter)

                    if (getSubscriptionLevel() == "Level 1") {
                        val dbHelper = MyDatabaseHelper(requireContext())
                        dbHelper.addExpense(title, comment, amount, tax, userId, date)
                        Toast.makeText(requireContext(), "Dépense ajoutée avec succès", Toast.LENGTH_SHORT).show()
                    } else {
                        // Gérer le cas où l'utilisateur n'a pas un abonnement de niveau 1
                        Toast.makeText(requireContext(), "Niveau d'abonnement insuffisant", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: NumberFormatException) {
                // Gérer les erreurs de conversion
                Toast.makeText(requireContext(), "Veuillez entrer des valeurs numériques valides", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Gérer les cas où les champs sont vides
            Toast.makeText(requireContext(), "Veuillez remplir tous les champs obligatoires", Toast.LENGTH_SHORT).show()
            Log.e("AddExpense", "Champs obligatoires manquants : titre, montant ou taxe")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addProfit() {
        // Logique pour ajouter un profit
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val title = binding.layoutAddExpense.editTextTitle.text.toString()
        val comment = binding.layoutAddExpense.editTextComment.text.toString()
        val amount = binding.layoutAddExpense.editTextAmount.text.toString().toDouble()
        val userId = getUserId()
        val date = currentDate.format(formatter)

        if (getSubscriptionLevel() == "Level 1") {
            val dbHelper = MyDatabaseHelper(requireContext())
            dbHelper.addProfit(title, comment, amount, userId, date)
            // Ajouter un toast pour indiquer que la dépense a été ajoutée avec succès
            Toast.makeText(requireContext(), "Profit ajoutée avec succès", Toast.LENGTH_SHORT).show()
        } else {
            // Gérer le cas où l'utilisateur n'a pas un abonnement de niveau 1
        }
    }

    private fun getSubscriptionLevel(): String? {
        val sharedPreferences = requireActivity().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("subscription_level", null)
    }

    private fun getUserId(): Int {
        val sharedPreferences = requireActivity().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("user_id", -1)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
