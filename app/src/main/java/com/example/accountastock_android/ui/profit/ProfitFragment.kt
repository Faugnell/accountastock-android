import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.accountastock_android.MainActivity
import com.example.accountastock_android.R
import com.example.accountastock_android.data.database.MyDatabaseHelper
import com.example.accountastock_android.data.model.Transaction
import com.example.accountastock_android.databinding.FragmentProfitBinding
import com.google.gson.Gson
import java.util.Locale

class ProfitFragment : Fragment() {

    private var _binding: FragmentProfitBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: MyDatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity = activity as MainActivity
        dbHelper = mainActivity.getDatabaseHelper()

        if (getSubscriptionLevel() == "level 1") {
            val transactions = dbHelper.getTransactionsLast30Days()
            val dailyNetProfits = calculateDailyNetProfits(transactions)

            if (dailyNetProfits.isNotEmpty()) {
                binding.textProfitAmount.text = dailyNetProfits.joinToString(", ")

                val profitPercentage = calculateProfitPercentage(dailyNetProfits)
                binding.textProfitPercentage.text = String.format("%.2f%%", profitPercentage)

                updateUIBasedOnProfitPercentage(profitPercentage)

                val chartData = createChartData(dailyNetProfits)
                val jsonChartData = Gson().toJson(chartData)
                Log.d("ChartDataJSON", jsonChartData)
                Log.d("getHtmlData", getHtmlData(jsonChartData))

                binding.webView.settings.javaScriptEnabled = true
                binding.webView.loadDataWithBaseURL(
                    null,
                    getHtmlData(jsonChartData),
                    "text/html",
                    "UTF-8",
                    null
                )
            } else {
                binding.textProfitAmount.text = "Pas encore de transactions"
                val webViewText = """
                        <html>
                        <head>
                            <style>
                                body {
                                    display: flex;
                                    justify-content: center;
                                    align-items: center;
                                    height: 100vh;
                                    margin: 0;
                                    font-family: Arial, sans-serif;
                                }
                                p {
                                    text-align: center;
                                }
                            </style>
                        </head>
                        <body>
                            <p>Pas encore de données</p>
                        </body>
                        </html>
                    """
                binding.webView.loadDataWithBaseURL(
                    null,
                    webViewText,
                    "text/html",
                    "UTF-8",
                    null
                )

            }
        }

        binding.imageView.setOnClickListener {
            findNavController().navigate(R.id.action_profitFragment_to_addTransactionFragment)
        }
    }

    private fun getSubscriptionLevel(): String? {
        val sharedPreferences = requireActivity().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("subscription_level", null)
    }

    private fun calculateDailyNetProfits(transactions: List<Transaction>): List<Double> {
        val dailyNetProfits = mutableListOf<Double>()
        if (transactions.isEmpty()) return dailyNetProfits

        transactions.sortedBy { it.date }

        var currentDate = transactions.first().date
        var dailyProfit = 0.0

        transactions.forEach { transaction ->
            if (transaction.date != currentDate) {
                dailyNetProfits.add(dailyProfit)
                dailyProfit = 0.0
                currentDate = transaction.date
            }
            dailyProfit += if (transaction.fkIdProfit != null) {
                transaction.amount
            } else {
                -transaction.amount
            }
        }
        dailyNetProfits.add(dailyProfit) // add the last day's profit

        // Log the daily net profits to verify
        Log.d("DailyNetProfits", dailyNetProfits.toString())

        return dailyNetProfits
    }

    private fun calculateProfitPercentage(dailyNetProfits: List<Double>): Double {
        if (dailyNetProfits.isEmpty()) return 0.0
        val totalProfit = dailyNetProfits.sum()
        val averageDailyProfit = totalProfit / dailyNetProfits.size
        return (averageDailyProfit / totalProfit) * 100
    }

    private fun updateUIBasedOnProfitPercentage(profitPercentage: Double) {
        if (profitPercentage < 0) {
            binding.textProfitPercentage.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            binding.imageArrow.setImageResource(R.drawable.ic_arrow_down)
        } else if (profitPercentage > 0) {
            binding.textProfitPercentage.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
            binding.imageArrow.setImageResource(R.drawable.ic_arrow_up)
        } else {
            binding.textProfitPercentage.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.imageArrow.visibility = View.GONE
        }
    }

    private fun createChartData(dailyNetProfits: List<Double>): List<List<Any>> {
        val chartData = mutableListOf<List<Any>>()
        for (i in dailyNetProfits.indices) {
            chartData.add(listOf("J-${dailyNetProfits.size - 1 - i}", dailyNetProfits[i]))
        }

        // Log the chart data to verify
        Log.d("ChartData", chartData.toString())

        return chartData
    }

    private fun getHtmlData(jsonChartData: String): String {
        return """
    <html>
      <head>
        <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
        <script type="text/javascript">
          google.charts.load('current', {'packages':['corechart']});
          google.charts.setOnLoadCallback(drawChart);

          function drawChart() {
            var data = google.visualization.arrayToDataTable([
              ['Date', 'Montant'],
              ${jsonChartData.substring(1, jsonChartData.length - 1)}
            ]);

            var options = {
              legend: 'none',
              curveType: 'function',
              hAxis: {
                textPosition: 'none',
                title: '',
                slantedText: true,
                slantedTextAngle: 45
              },
              vAxis: {
              textPosition: 'none',
                title: '',
                minValue: 0,
                gridlines: { count: 0 }
              },
              chartArea: {
                width: '90%',
                height: '80%'
              },
              width: '100%',
              height: 400,
              areaOpacity: 0.2,
              lineWidth: 2
            };

            var chart = new google.visualization.AreaChart(document.getElementById('curve_chart'));

            chart.draw(data, options);
          }
        </script>
      </head>
      <body>
        <div id="curve_chart" style="width: 100vw; height: 100%;"></div>
      </body>
    </html>
    """
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
