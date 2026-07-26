package com.hciproject.exploresite.itinerary

import com.hciproject.exploresite.poi.PointOfInterest
import com.hciproject.exploresite.poi.CulturalPOI

data class Itineraries(
    val title: String = "",
    val author: String = "",
    val suggestionNumber: Int = 0,
    var saved: Boolean = false,
    val poiNames: List<String>? = emptyList(),
    val isPrivate: Boolean = false
) {
    // Helper to get the actual POI objects, safely handling potentially null poiNames from older serialized data
    val pois: List<PointOfInterest> get() {
        return poiNames?.mapNotNull { name ->
            CulturalPOI.find { it.name == name }
        } ?: emptyList()
    }
}

val SampleItineraries = listOf(
    Itineraries(
        title = "Napoli e i suoi tesori",
        author = "Admin",
        suggestionNumber = 12500,
        saved = false,
        poiNames = CulturalPOI.filter { it.address.contains("Napoli") }.map { it.name },
        isPrivate = false
    ),
    Itineraries(
        title = "L'Irpinia tra storia e sapori",
        author = "Guida Locale",
        suggestionNumber = 8400,
        saved = false,
        poiNames = CulturalPOI.filter { it.address.contains("AV") || it.address.contains("Avellino") }.map { it.name },
        isPrivate = false
    ),
    Itineraries(
        title = "Un viaggio nel Sannio",
        author = "StoriaViva",
        suggestionNumber = 5600,
        saved = false,
        poiNames = CulturalPOI.filter { it.address.contains("Benevento") }.map { it.name },
        isPrivate = false
    ),
    Itineraries(
        title = "Tour delle Regge Campane",
        author = "Architetto Viaggiante",
        suggestionNumber = 9800,
        saved = false,
        poiNames = listOfNotNull(
            CulturalPOI.find { it.name == "Reggia di Caserta" }?.name,
            CulturalPOI.find { it.name == "Anfiteatro Campano" }?.name
        ),
        isPrivate = false
    ),
    Itineraries(
        title = "Antica Salerno e Borghi",
        author = "ViaggiatoreCampano",
        suggestionNumber = 7200,
        saved = false,
        poiNames = CulturalPOI.filter { it.address.contains("Salerno") || it.address.contains("Cetara") }.map { it.name },
        isPrivate = false
    )
)
