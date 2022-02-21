package br.com.wolk.softdesign_teste.view

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var customAlertDialogView : View
    private lateinit var nameTextField : TextInputLayout
    private lateinit var emailTextField : TextInputLayout

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

        materialAlertDialogBuilder = MaterialAlertDialogBuilder(this)
        customAlertDialogView = LayoutInflater.from(this)
            .inflate(R.layout.credentials_dialog, null, false)

        viewModel.events.observe(this) {
            if (it != null) {
                progressBar.visibility = View.GONE
                adapter.updateData(it)
                launchCustomAlertDialog(viewModel)
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

        // Building the Alert dialog using materialAlertDialogBuilder instance
        val dialog = materialAlertDialogBuilder.setView(customAlertDialogView)
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
}