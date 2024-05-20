package com.example.accountastock_android.ui.account

import ProfitFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ScrollView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.accountastock_android.R
import com.example.accountastock_android.databinding.FragmentAccountBinding
import com.example.accountastock_android.ui.transaction.TransactionFragment
import com.google.android.material.snackbar.Snackbar

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Replace the FrameLayout with the ProfitFragment
        childFragmentManager.commit {
            replace(R.id.profit, ProfitFragment())
        }

        // Replace the FrameLayout with the TransactionFragment
        childFragmentManager.commit {
            replace(R.id.transaction, TransactionFragment())
        }

        // Calculate the height of the bottom navigation bar
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        val navigationBarHeight = if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else {
            0
        }

        // Get the ScrollView from the layout
        val scrollView = binding.scrollView

        // Adjust the bottom margin of the ScrollView
        val params = scrollView.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(0, 0, 0, navigationBarHeight)
        scrollView.layoutParams = params

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
