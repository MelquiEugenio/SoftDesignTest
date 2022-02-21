package br.com.wolk.softdesign_teste.view

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.wolk.softdesign_teste.R
import br.com.wolk.softdesign_teste.viewmodel.EventsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewModel: EventsViewModel by viewModels()

        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        val errorImageView = findViewById<ImageView>(R.id.error_image_view)
        val recyclerview = findViewById<RecyclerView>(R.id.recycler_view)
        val adapter = EventsListAdapter(viewModel.events.value, viewModel, this)

        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = adapter

        viewModel.events.observe(this) {
            if (it != null) {
                progressBar.visibility = View.GONE
                adapter.updateData(it)
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
}