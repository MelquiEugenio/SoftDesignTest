package br.com.wolk.softdesign_teste.view

import android.app.Activity
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.wolk.softdesign_teste.R
import br.com.wolk.softdesign_teste.model.network.dto.EventDto
import br.com.wolk.softdesign_teste.model.network.dto.EventsRequestDto
import br.com.wolk.softdesign_teste.viewmodel.EventsViewModel
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URL
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
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.title.text = dataSet!![position].title
        viewHolder.description.text = dataSet[position].description

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
                    } else {
                        viewHolder.imageView.visibility = View.GONE
                    }
                }
            } catch (e: Exception) {
                viewHolder.imageView.visibility = View.GONE
                e.printStackTrace()
            }
        }

        // Diálogo de motor desligado

        viewHolder.checkInButton.setOnClickListener {
            val request = EventsRequestDto(eventId = dataSet[position].id, name = "teste", email = "teste@gmail.com")
            viewModel.checkEvent(request)
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