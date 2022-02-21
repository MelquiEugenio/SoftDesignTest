package br.com.wolk.softdesign_teste.view

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.wolk.softdesign_teste.R
import br.com.wolk.softdesign_teste.model.network.dto.EventDto
import br.com.wolk.softdesign_teste.model.network.dto.EventsRequestDto
import br.com.wolk.softdesign_teste.viewmodel.EventsViewModel
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.net.URL
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var alertDialogBuilder: AlertDialog.Builder
    private lateinit var customAlertDialogView : View
    private lateinit var nameTextField : TextInputLayout
    private lateinit var emailTextField : TextInputLayout

    private val linearLayoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(this)
    }
    private val gridLayoutManager: GridLayoutManager by lazy {
        GridLayoutManager(this, 2)
    }
    private lateinit var recyclerView: RecyclerView
    private lateinit var mapAdapter: RecyclerView.Adapter<EventsListAdapter.ViewHolder>

    /**
     * RecycleListener that completely clears the [com.google.android.gms.maps.GoogleMap]
     * attached to a row in the RecyclerView.
     * Sets the map type to [com.google.android.gms.maps.GoogleMap.MAP_TYPE_NONE] and clears
     * the map.
     */
    private val recycleListener = RecyclerView.RecyclerListener { holder ->
        val mapHolder = holder as EventsListAdapter.ViewHolder
        mapHolder.clearView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewModel: EventsViewModel by viewModels()
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        val errorImageView = findViewById<ImageView>(R.id.error_image_view)

        mapAdapter = EventsListAdapter(viewModel.events.value, viewModel, this)

        // Initialise the RecyclerView.
        recyclerView = findViewById<RecyclerView>(R.id.recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = linearLayoutManager
            adapter = mapAdapter
            setRecyclerListener(recycleListener)
        }

        alertDialogBuilder = AlertDialog.Builder(this)
        customAlertDialogView = LayoutInflater.from(this)
            .inflate(R.layout.credentials_dialog, null, false)

        viewModel.events.observe(this) { events ->
            if (events != null) {
                progressBar.visibility = View.GONE
                (mapAdapter as EventsListAdapter).updateData(events)
                if (viewModel.getName() == "test") launchCustomAlertDialog(viewModel)
            } else {
                progressBar.visibility = View.GONE
                errorImageView.visibility = View.VISIBLE
                Toast.makeText(
                    this,
                    "Algo impediu a lista de carregar. Tente novamente.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun launchCustomAlertDialog(viewModel: EventsViewModel) {
        nameTextField = customAlertDialogView.findViewById(R.id.name_text_field)
        emailTextField = customAlertDialogView.findViewById(R.id.email_text_field)

        val dialog = alertDialogBuilder.setView(customAlertDialogView)
            .setCancelable(false)
            .setPositiveButton("Confirmar", null)
            .create()

        dialog.show()
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val name = nameTextField.editText?.text.toString()
            val email = emailTextField.editText?.text.toString()

            when {
                name.isBlank() -> nameTextField.error = "Entre um nome"
                email.isBlank() || !email.contains("@") -> {
                    emailTextField.error = "Entre um email válido"
                }
                else -> {
                    viewModel.saveCredentials(name, email)
                    dialog.dismiss()
                }
            }
        }
    }

    /** Create options menu to switch between the linear and grid layout managers. */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.lite_list_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        recyclerView.layoutManager = when (item.itemId) {
            R.id.layout_linear -> linearLayoutManager
            R.id.layout_grid -> gridLayoutManager
            else -> return false
        }
        return true
    }

    inner class EventsListAdapter(
        private val dataSet: MutableList<EventDto>?,
        private val viewModel: EventsViewModel,
        private val activity: Activity,
    ) : RecyclerView.Adapter<EventsListAdapter.ViewHolder>() {

        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder).
         */
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), OnMapReadyCallback {
            val title: TextView = view.findViewById(R.id.title_text_view)
            val description: TextView = view.findViewById(R.id.description_text_view)
            val date: TextView = view.findViewById(R.id.date_text_view)
            val imageView: ImageView = view.findViewById(R.id.event_image_view)
            val checkInButton: MaterialButton = view.findViewById(R.id.check_in_button)
            val shareButton: ImageView = view.findViewById(R.id.share_button)
            val card: MaterialCardView = view.findViewById(R.id.card)

            val mapView: MapView = view.findViewById(R.id.map_view)

            private lateinit var map: GoogleMap
            private lateinit var latLng: LatLng

            /** Initialises the MapView by calling its lifecycle methods */
            init {
                with(mapView) {
                    // Initialise the MapView
                    onCreate(null)
                    // Set the map ready callback to receive the GoogleMap object
                    getMapAsync(this@ViewHolder)
                }
            }

            private fun setMapLocation() {
                if (!::map.isInitialized) return
                with(map) {
                    moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f))
                    addMarker(MarkerOptions().position(latLng))
                    mapType = GoogleMap.MAP_TYPE_NORMAL
                }
            }

            override fun onMapReady(googleMap: GoogleMap) {
                MapsInitializer.initialize(applicationContext)
                // If map is not initialised properly
                map = googleMap ?: return
                setMapLocation()
            }

            /** This function is called when the RecyclerView wants to bind the ViewHolder. */
            fun bindView(position: Int) {
                dataSet!![position].let {
                    latLng = LatLng(it.latitude, it.longitude)
                    mapView.tag = this
                    title.text = it.title
                    // We need to call setMapLocation from here because RecyclerView might use the
                    // previously loaded maps
                    setMapLocation()
                }
            }

            /** This function is called by the recycleListener, when we need to clear the map. */
            fun clearView() {
                with(map) {
                    // Clear the map and free up resources by changing the map type to none
                    clear()
                    mapType = GoogleMap.MAP_TYPE_NONE
                }
            }
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.event_card, viewGroup, false)

            return ViewHolder(view)
        }

        // Replace the contents of a view (invoked by the layout manager)
        @OptIn(DelicateCoroutinesApi::class)
        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

            viewHolder.bindView(position)

            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.DOWN
            val priceNumber = df.format(dataSet!![position].price).replace(".", ",")
            val price = "R$ $priceNumber"

            viewHolder.title.text = dataSet[position].title
            viewHolder.description.text = dataSet[position].description
            viewHolder.checkInButton.text = price

            viewHolder.shareButton.setOnClickListener {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, dataSet[position].title + ". Baixe o app e se inscreva.")
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                ContextCompat.startActivity(activity.applicationContext, shareIntent, null)
            }

            val formatter = SimpleDateFormat("dd/MM/yyyy - hh:mm")
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = dataSet[position].date
            viewHolder.date.text = formatter.format(calendar.time).replace("-", "às")

            // Get event image
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

            viewHolder.checkInButton.setOnClickListener {
                GlobalScope.launch {
                    try {
                        activity.runOnUiThread {
                            viewHolder.checkInButton.text = "Carregando..."
                        }

                        val request = EventsRequestDto(
                            eventId = dataSet[position].id,
                            name = viewModel.getName(),
                            email = viewModel.getEmail()
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
}