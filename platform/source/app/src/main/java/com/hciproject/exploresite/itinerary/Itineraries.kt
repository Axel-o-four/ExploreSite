package com.hciproject.exploresite.itinerary

import com.hciproject.exploresite.poi.PointOfInterest
import com.hciproject.exploresite.poi.CulturalPOI

data class Itineraries(
    val title: String,
    val author: String,
    val suggestionNumber: Int,
    var saved: Boolean = false,
    val pois: List<PointOfInterest>
)

val SampleItineraries = listOf(
    Itineraries(
        title = "Napoli e i suoi tesori",
        author = "Admin",
        suggestionNumber = 12500,
        saved = false,
        pois = CulturalPOI.filter { it.address.contains("Napoli") }
    ),
    Itineraries(
        title = "L'Irpinia tra storia e sapori",
        author = "Guida Locale",
        suggestionNumber = 8400,
        saved = false,
        pois = CulturalPOI.filter { it.address.contains("AV") || it.address.contains("Avellino") }
    ),
    Itineraries(
        title = "Un viaggio nel Sannio",
        author = "StoriaViva",
        suggestionNumber = 5600,
        saved = false,
        pois = CulturalPOI.filter { it.address.contains("Benevento") }
    ),
    Itineraries(
        title = "Tour delle Regge Campane",
        author = "Architetto Viaggiante",
        suggestionNumber = 9800,
        saved = false,
        pois = listOfNotNull(
            CulturalPOI.find { it.name == "Reggia di Caserta" },
            CulturalPOI.find { it.name == "Anfiteatro Campano" }
        )
    ),
    Itineraries(
        title = "Antica Salerno e Borghi",
        author = "ViaggiatoreCampano",
        suggestionNumber = 7200,
        saved = false,
        pois = CulturalPOI.filter { it.address.contains("Salerno") || it.address.contains("Cetara") }
    )
)
