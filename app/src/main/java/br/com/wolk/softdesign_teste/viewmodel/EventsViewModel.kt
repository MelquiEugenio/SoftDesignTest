package br.com.wolk.softdesign_teste.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
) : ViewModel() {

    val events = MutableLiveData<MutableList<EventDto>?>(mutableListOf())
    val isChecked = MutableLiveData<Boolean>(false)

    fun getEvents() {
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

    fun checkEvent(body: EventsRequestDto) {
        viewModelScope.launch {
            isChecked.value = api.checkEvent(body).code == "200"
        }
    }

    init {
        getEvents()
    }
}