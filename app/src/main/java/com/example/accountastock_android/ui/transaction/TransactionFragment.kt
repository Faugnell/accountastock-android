package com.example.accountastock_android.ui.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.accountastock_android.R

class TransactionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val transactionList = view.findViewById<LinearLayout>(R.id.transaction_list)

        for (i in 1..40) {
            val transactionView = createTransactionView("Transaction $i", "Note $i", "$${i * 100}")
            transactionList.addView(transactionView)
        }
    }

    private fun createTransactionView(titleText: String, noteText: String, amountText: String): View {
        val context = requireContext()
        val cardView = CardView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 8, 0, 8) // No horizontal margin to make it full width
            }
            cardElevation = 0f // Remove shadow by setting elevation to 0
            radius = 0f // Remove corner radius if not needed
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
            text = amountText
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = android.view.Gravity.END
        }

        linearLayout.addView(titleNoteLayout)
        linearLayout.addView(amount)

        cardView.addView(linearLayout)

        return cardView
    }
}
