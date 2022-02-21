package br.com.wolk.softdesign_teste.view

import android.app.Activity
import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import br.com.wolk.softdesign_teste.R
import br.com.wolk.softdesign_teste.model.network.dto.EventDto
import br.com.wolk.softdesign_teste.model.network.dto.EventsRequestDto
import br.com.wolk.softdesign_teste.viewmodel.EventsViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.net.URL
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


class EventsListAdapter(
    private val dataSet: MutableList<EventDto>?,
    private val viewModel: EventsViewModel,
    private val activity: Activity,
) :
    RecyclerView.Adapter<EventsListAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title_text_view)
        val description: TextView = view.findViewById(R.id.description_text_view)
        val date: TextView = view.findViewById(R.id.date_text_view)
        val imageView: ImageView = view.findViewById(R.id.event_image_view)
        val checkInButton: MaterialButton = view.findViewById(R.id.check_in_button)
        val card: MaterialCardView = view.findViewById(R.id.card)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.event_card, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    @OptIn(DelicateCoroutinesApi::class)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.DOWN
        val priceNumber = df.format(dataSet!![position].price).replace(".", ",")
        val price = "R$ $priceNumber"

        viewHolder.title.text = dataSet[position].title
        viewHolder.description.text = dataSet[position].description
        viewHolder.checkInButton.text = price

        val formatter = SimpleDateFormat("dd/MM/yyyy - hh:mm")
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dataSet[position].date
        viewHolder.date.text = formatter.format(calendar.time).replace("-", "às")

        GlobalScope.launch {
            try {
                val url = URL(dataSet[position].image)
                val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                activity.runOnUiThread {
                    if (bmp != null) {
                        viewHolder.imageView.visibility = View.VISIBLE
                        viewHolder.imageView.setImageBitmap(bmp)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Dialog todo

        viewHolder.checkInButton.setOnClickListener {

            GlobalScope.launch {
                try {
                    activity.runOnUiThread {
                        viewHolder.checkInButton.text = "Carregando..."
                    }

                    val request = EventsRequestDto(
                        eventId = dataSet[position].id,
                        name = "teste",
                        email = "teste@gmail.com"
                    )
                    val isChecked = viewModel.checkEvent(request)

                    activity.runOnUiThread {
                        if (isChecked) {
                            viewHolder.checkInButton.text = "Inscrito!"
                            viewHolder.checkInButton.setBackgroundColor(Color.parseColor("#006400"))
                            viewHolder.card.isChecked = true
                        } else {
                            viewHolder.checkInButton.text = price
                            Toast.makeText(
                                activity.applicationContext,
                                "Ops, ocorreu um erro ao se inscrever.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    activity.runOnUiThread {
                        viewHolder.checkInButton.text = price
                        Toast.makeText(
                            activity.applicationContext,
                            "Ops, ocorreu um erro ao se inscrever.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet!!.size

    fun updateData(newData: MutableList<EventDto>?) {
        dataSet!!.clear()
        dataSet.addAll(newData!!)
        notifyDataSetChanged()
    }
}