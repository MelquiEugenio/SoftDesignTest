package br.com.wolk.softdesign_teste.model.network

import br.com.wolk.softdesign_teste.model.network.dto.EventDto
import br.com.wolk.softdesign_teste.model.network.dto.EventsRequestDto
import br.com.wolk.softdesign_teste.model.network.dto.EventsResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface EventsApi {

    @GET("/api/events")
    suspend fun getEvents(): MutableList<EventDto>

    @POST("/api/checkin")
    suspend fun checkEvent(@Body body: EventsRequestDto?): EventsResponseDto
}