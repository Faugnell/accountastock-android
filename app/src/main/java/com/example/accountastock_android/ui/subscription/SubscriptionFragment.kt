package com.example.accountastock_android.ui.subscription

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.accountastock_android.R
import com.example.accountastock_android.databinding.FragmentSubscriptionBinding
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import com.google.android.gms.wallet.WalletConstants
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.button.ButtonConstants
import com.google.android.gms.wallet.button.ButtonOptions
import com.google.android.gms.wallet.button.PayButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONArray
import org.json.JSONObject

class SubscriptionFragment : Fragment() {

    companion object {
        fun newInstance() = SubscriptionFragment()
    }

    private lateinit var paymentsClient: PaymentsClient

    private var _binding: FragmentSubscriptionBinding? = null
    private val binding get() = _binding!!

    private val subscriptionViewModel: SubscriptionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubscriptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeGooglePay()

        binding.paymentButton.setOnClickListener {
            showPaymentDialog()
        }

        binding.backToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_subscriptionFragment_to_navigation_login)
        }
    }

    private fun initializeGooglePay() {
        val walletOptions = Wallet.WalletOptions.Builder()
            .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
            .build()

        paymentsClient = Wallet.getPaymentsClient(requireContext(), walletOptions)

        val readyToPayRequest = IsReadyToPayRequest.fromJson(baseConfigurationJson().toString())

        val task: Task<Boolean> = paymentsClient.isReadyToPay(readyToPayRequest)
        task.addOnCompleteListener { completeTask ->
            if (completeTask.isSuccessful) {
                showGooglePayButton(completeTask.result)
            } else {
                // En cas d'échec
            }
        }
    }

    private fun createPaymentRequestJson(): JSONObject {
        val paymentRequestJson = baseConfigurationJson()

        paymentRequestJson.put("transactionInfo", JSONObject().apply {
            put("totalPrice", "123.45")
            put("totalPriceStatus", "FINAL")
            put("currencyCode", "USD")
        })

        paymentRequestJson.put("merchantInfo", JSONObject().apply {
            put("merchantId", "01234567890123456789")
            put("merchantName", "Example Merchant")
        })

        return paymentRequestJson
    }

    private fun baseConfigurationJson(): JSONObject {
        return JSONObject()
            .put("apiVersion", 2)
            .put("apiVersionMinor", 0)
            .put("allowedPaymentMethods", JSONArray().put(getCardPaymentMethod()))
    }

    private fun getCardPaymentMethod(): JSONObject {
        val card = JSONObject().apply {
            put("type", "CARD")
            put("parameters", JSONObject().apply {
                put("allowedAuthMethods", JSONArray().apply {
                    put("PAN_ONLY")
                    put("CRYPTOGRAM_3DS")
                })
                put("allowedCardNetworks", JSONArray().apply {
                    put("AMEX")
                    put("DISCOVER")
                    put("JCB")
                    put("MASTERCARD")
                    put("VISA")
                })
                put("billingAddressRequired", true)
                put("billingAddressParameters", JSONObject().apply {
                    put("format", "FULL")
                    put("phoneNumberRequired", false)
                })
            })
        }
        return card
    }

    private fun showGooglePayButton(userIsReadyToPay: Boolean) {
        if (userIsReadyToPay) {
            // Affichez le bouton Google Pay
        } else {
            // Cachez le bouton Google Pay
        }
    }

    private fun showPaymentDialog() {
        val dialogView = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)

            val title = TextView(context).apply {
                text = "Paiement"
                textSize = 20f
                setPadding(0, 0, 0, 16)
            }

            val paymentMethods = JSONArray().put(getCardPaymentMethod())

            val googlePayPaymentButton = PayButton(context).apply {
                initialize(
                    ButtonOptions.newBuilder()
                        .setButtonTheme(ButtonConstants.ButtonTheme.DARK)
                        .setButtonType(ButtonConstants.ButtonType.BUY)
                        .setCornerRadius(100)
                        .setAllowedPaymentMethods(paymentMethods.toString())
                        .build()
                )
                setOnClickListener {
                    requestPayment()
                }
            }

            addView(title)
            addView(googlePayPaymentButton)
        }

        MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Annuler", null)
            .show()
    }

    private fun requestPayment() {
        val paymentDataRequest = PaymentDataRequest.fromJson(createPaymentRequestJson().toString())

        val task = paymentsClient.loadPaymentData(paymentDataRequest)
        task.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Gérer le succès du paiement
            } else {
                // Gérer l'échec du paiement
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
