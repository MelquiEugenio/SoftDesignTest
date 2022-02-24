package br.com.softdesign.teste.model.network.dto

data class EventDto (
    val people: List<Any>,
    val date: Long,
    val description: String,
    val image: String,
    val longitude: Double,
    val latitude: Double,
    val price: Float,
    val title: String,
    val id: String,
)