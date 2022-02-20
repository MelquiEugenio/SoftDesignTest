package br.com.wolk.softdesign_teste.model.network.dto

data class Event (
    val people: List<Any>,
    val description: String,
    val image: String,
    val longitude: Long,
    val latitude: Long,
    val price: Double,
    val title: String,
    val id: Long,
)