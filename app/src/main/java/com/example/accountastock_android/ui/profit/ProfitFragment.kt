import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.accountastock_android.R
import com.example.accountastock_android.databinding.FragmentProfitBinding

class ProfitFragment : Fragment() {

    private var _binding: FragmentProfitBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Exemple de données fictives pour le montant du profit net et le pourcentage d'augmentation
        val profitAmount = "$2000"
        val profitPercentage = "+10% sur 30 jours"

        // Affichage des données dans les TextViews
        binding.textProfitAmount.text = profitAmount
        binding.textProfitPercentage.text = profitPercentage
        // Activer l'exécution du JavaScript dans le WebView
        binding.webView.settings.javaScriptEnabled = true
        // Charger le graphique dans le WebView (exemple de code)
        val htmlData = """
            <html>
  <head>
    <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
    <script type="text/javascript">
      google.charts.load('current', {'packages':['corechart']});
      google.charts.setOnLoadCallback(drawChart);

      function drawChart() {
    var data = google.visualization.arrayToDataTable([
      ['Day', 'Profit'],
      ['J-30', 2000],
      ['J-29', 2100],
      ['J-28', 2200],
      ['J-27', 2300],
      ['J-26', 2400],
      ['J-25', 2500],
      ['J-24', 2600],
      ['J-23', 2700],
      ['J-22', 2800],
      ['J-21', 2900],
      ['J-20', 3000],
      ['J-19', 3100],
      ['J-18', 3200],
      ['J-17', 3300],
      ['J-16', 3400],
      ['J-15', 3500],
      ['J-14', 3600],
      ['J-13', 3700],
      ['J-12', 3800],
      ['J-11', 3900],
      ['J-10', 4000],
      ['J-9', 4100],
      ['J-8', 4200],
      ['J-7', 4300],
      ['J-6', 4400],
      ['J-5', 4500],
      ['J-4', 4600],
      ['J-3', 4700],
      ['J-2', 4800],
      ['J-1', 4900],
      ['J-0', 5000],
    ]);

    var options = {
    legend: 'none', // Masque la légende
    hAxis: {
        title: '', // Supprime le titre de l'axe des abscisses (X)
        slantedText: true,
        slantedTextAngle: 45
    },
    vAxis: {
        textPosition: 'none', // Supprime les valeurs de l'axe des ordonnées (Y)
        title: '', // Supprime le titre de l'axe des ordonnées (Y)
        minValue: 0
    },
    chartArea: {
        width: '90%', // Réduit la largeur de la zone du graphique pour éviter le défilement
        height: '90%'
    },
    width: '100%', // Ajuste la largeur du graphique à la largeur de l'écran
    height: 400 // Ajuste la hauteur du graphique
};


    var chart = new google.visualization.LineChart(document.getElementById('curve_chart'));

    chart.draw(data, options);
}
    </script>
  </head>
  <body>
        <div id="curve_chart" style="width: 100vw; height: 100%;"></div>
  </body>
</html>
            """
        binding.webView.loadData(htmlData, "text/html", "UTF-8")

        // Gérer le clic sur le bouton "Ajouter transaction"
        binding.imageView.setOnClickListener {
            // Naviguer vers l'écran d'ajout de transaction
            //findNavController().navigate(R.id.action_profitFragment_to_addTransactionFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}