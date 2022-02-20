package br.com.wolk.softdesign_teste.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.wolk.softdesign_teste.model.network.EventsApi
import br.com.wolk.softdesign_teste.model.network.dto.Event
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

    private val state = MutableLiveData<List<Event>?>(emptyList())
    private val isChecked = MutableLiveData<Boolean>(false)

    private fun getEvents() {
        viewModelScope.launch {
            try {
                val events: List<Event> = api.getEvents().events
                state.value = events

            } catch (e: Exception) {
                // I'll consider null as an error in the API query.
                state.value = null
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