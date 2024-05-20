import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.accountastock_android.R

class InventoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inventory, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val inventoryContainer = view.findViewById<LinearLayout>(R.id.inventoryContainer)

        for (i in 1..20) {
            val itemView = createInventoryItemView("Item $i", "Comment $i")
            inventoryContainer.addView(itemView)
        }

        // Adjust the height of the inventoryContainer to accommodate the bottom navigation bar
        val navigationBarHeight = getNavigationBarHeight()
        val params = inventoryContainer.layoutParams as ViewGroup.MarginLayoutParams
        params.bottomMargin = navigationBarHeight
        inventoryContainer.layoutParams = params
    }

    private fun getNavigationBarHeight(): Int {
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else {
            0
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun createInventoryItemView(itemName: String, itemComment: String): View {
        val context = requireContext()

        // Create visible layout
        val visibleLayout = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.HORIZONTAL
            setPadding(16, 16, 16, 16)
        }

        val textLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        val itemNameView = TextView(context).apply {
            text = itemName
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val itemCommentView = TextView(context).apply {
            text = itemComment
            textSize = 14f
        }

        textLayout.addView(itemNameView)
        textLayout.addView(itemCommentView)

        val quantityLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val quantityInput = EditText(context).apply {
            setText("0")
            inputType = InputType.TYPE_CLASS_NUMBER
            layoutParams = LinearLayout.LayoutParams(
                100,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            gravity = android.view.Gravity.CENTER
        }

        val minusButton = Button(context).apply {
            text = "-"
            setTypeface(null, android.graphics.Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                100,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setOnClickListener {
                // Handle decrease quantity
            }
        }

        val plusButton = Button(context).apply {
            text = "+"
            layoutParams = LinearLayout.LayoutParams(
                100,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setOnClickListener {
                // Handle increase quantity
            }
        }

        quantityLayout.addView(minusButton)
        quantityLayout.addView(quantityInput)
        quantityLayout.addView(plusButton)

        visibleLayout.addView(textLayout)
        visibleLayout.addView(quantityLayout)

        return visibleLayout
    }
}