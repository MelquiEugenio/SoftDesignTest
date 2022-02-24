package br.com.softdesign.teste.model

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject


@ActivityScoped
class DataSave @Inject constructor(context: Context) {

    private val sharedPref: SharedPreferences = context.getSharedPreferences(
        "SHARED_PREFERENCES", Context.MODE_PRIVATE)

    fun saveCredentials(name: String, email: String) {
        with (sharedPref.edit()) {
            putString("name", name)
            putString("email", email)
            apply()
        }
    }

    fun getDataName(): String {
        return sharedPref.getString("name", "test")!!
    }

    fun getDataEmail(): String {
        return sharedPref.getString("email", "test")!!
    }
}