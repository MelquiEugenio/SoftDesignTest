package br.com.wolk.softdesign_teste.view

import android.os.Bundle
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

        val recyclerview = findViewById<RecyclerView>(R.id.recycler_view)
        val adapter = EventsListAdapter(viewModel.events.value, viewModel, this)

        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = adapter

        viewModel.events.observe(this) {
            adapter.updateData(it)
        }
    }
}