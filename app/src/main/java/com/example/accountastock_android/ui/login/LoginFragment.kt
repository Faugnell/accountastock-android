package com.example.accountastock_android.ui.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.accountastock_android.R
import com.example.accountastock_android.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginButton.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            loginViewModel.login(email, password)
            loginViewModel.checkSubscription(email)
            var userId = getUserIdByEmail(email)
            saveUserId(userId)
        }

        binding.signUpLink.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_login_to_navigation_sign_up)
        }

        loginViewModel.loginResult.observe(viewLifecycleOwner, Observer { result ->
            if (result) {
                // Si le login est réussi, observer les résultats de l'abonnement
                loginViewModel.subscriptionResult.observe(viewLifecycleOwner, Observer { subscriptionResult ->
                    // Naviguer en fonction du résultat de l'abonnement
                    when (subscriptionResult) {
                        "level 1" -> {
                            findNavController().navigate(R.id.action_navigation_login_to_navigation_account)
                            saveSubscriptionLevel(subscriptionResult)
                            Toast.makeText(context, getSubscriptionLevel(), Toast.LENGTH_SHORT).show()
                        }
                        "level 2", "level 3" -> {
                            findNavController().navigate(R.id.action_navigation_login_to_navigation_account)
                            saveSubscriptionLevel(subscriptionResult)
                        }
                        else -> {
                            findNavController().navigate(R.id.action_navigation_login_to_subscriptionFragment)
                        }
                    }
                })
            } else {
                // Afficher un toast pour indiquer que le login a échoué
                Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getSubscriptionLevel(): String? {
        val sharedPreferences = requireActivity().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("subscription_level", null)
    }

    private fun saveSubscriptionLevel(subscriptionLevel: String) {
        val sharedPreferences = requireActivity().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("subscription_level", subscriptionLevel).apply()
    }

    private fun getUserIdByEmail(email: String): Int {
        var userId = loginViewModel.getUserId(email)
        return userId
    }

    private fun saveUserId(userId: Int) {
        val sharedPreferences = requireActivity().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt("user_id", userId).apply()
    }

    private fun navigateToDestination(subscriptionLevel: Int) {
        // Navigate based on subscription level
        if (subscriptionLevel == 1) {
            // Level 1 subscription, use SQLite
            findNavController().navigate(R.id.action_navigation_login_to_navigation_account)
        } else {
            // Other subscription levels, use API
            findNavController().navigate(R.id.action_navigation_login_to_navigation_account)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
