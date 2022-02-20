package br.com.wolk.softdesign_teste.model.network

import br.com.wolk.softdesign_teste.model.network.dto.EventsDto
import br.com.wolk.softdesign_teste.model.network.dto.EventsRequestDto
import br.com.wolk.softdesign_teste.model.network.dto.EventsResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface EventsApi {

    @GET("/events")
    suspend fun getEvents(): EventsDto

    @POST("/checkin")
    suspend fun checkEvent(@Body body: EventsRequestDto?): EventsResponseDto
}