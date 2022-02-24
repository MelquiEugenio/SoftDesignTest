package br.com.softdesign.teste.model.network.dto

data class EventsRequestDto(
    val eventId: String,
    val name: String,
    val email: String,
)