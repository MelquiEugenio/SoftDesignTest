package br.com.wolk.softdesign_teste.viewmodel

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.wolk.softdesign_teste.model.DataSave
import br.com.wolk.softdesign_teste.model.network.EventsApi
import br.com.wolk.softdesign_teste.model.network.dto.EventDto
import br.com.wolk.softdesign_teste.model.network.dto.EventsRequestDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * Controller class. Business logic is here.
 */
@HiltViewModel
class EventsViewModel @Inject constructor(
    private val api: EventsApi,
    private val dataSave: DataSave,
) : ViewModel() {

    val events = MutableLiveData<MutableList<EventDto>?>(mutableListOf())

    private fun getEvents() {
        viewModelScope.launch {
            try {
                val events: MutableList<EventDto> = api.getEvents()
                this@EventsViewModel.events.value = events

            } catch (e: Exception) {
                // I'll consider null as an error in the API query.
                e.printStackTrace()
                events.value = null
            }
        }
    }

    suspend fun checkEvent(body: EventsRequestDto): Boolean {
        return api.checkEvent(body).code == "200"
    }

    fun saveCredentials(name: String, email: String) {
        dataSave.saveCredentials(name, email)
    }

    fun getName(): String {
        return dataSave.getDataName()
    }

    fun getEmail(): String {
        return dataSave.getDataEmail()
    }

    init {
        getEvents()
    }
}