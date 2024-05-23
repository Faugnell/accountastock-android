package com.example.accountastock_android.ui.transaction

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.accountastock_android.R
import com.example.accountastock_android.data.database.MyDatabaseHelper
import com.example.accountastock_android.data.model.Transaction

class TransactionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_transaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val transactionList = view.findViewById<LinearLayout>(R.id.transaction_list)

        val dbHelper = MyDatabaseHelper(requireContext())

        if (getSubscriptionLevel() == "level 1") {
            val transactions = dbHelper.getTransactionsLast30Days().sortedByDescending { it.date }

            for (transaction in transactions) {
                val transactionView = createTransactionView(transaction.title, transaction.note, "${transaction.amount}")
                transactionList.addView(transactionView)
            }
        }
    }

    private fun createTransactionView(titleText: String, noteText: String, amountText: String): View {
        val context = requireContext()
        val cardView = CardView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 8, 0, 8)
            }
            cardElevation = 0f
            radius = 0f
        }

        val linearLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(16, 16, 16, 16)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val titleNoteLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        val title = TextView(context).apply {
            text = titleText
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val note = TextView(context).apply {
            text = noteText
            textSize = 14f
        }

        titleNoteLayout.addView(title)
        titleNoteLayout.addView(note)

        val amount = TextView(context).apply {
            text = amountText + "€"
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = android.view.Gravity.END
        }

        linearLayout.addView(titleNoteLayout)
        linearLayout.addView(amount)

        cardView.addView(linearLayout)

        return cardView
    }

    private fun getSubscriptionLevel(): String? {
        val sharedPreferences = requireActivity().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("subscription_level", null)
    }

    private fun getUserId(): Int {
        val sharedPreferences = requireActivity().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("user_id", -1)
    }
}
