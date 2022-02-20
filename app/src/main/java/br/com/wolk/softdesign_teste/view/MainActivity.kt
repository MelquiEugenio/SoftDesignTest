package br.com.wolk.softdesign_teste.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import br.com.wolk.softdesign_teste.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }
}