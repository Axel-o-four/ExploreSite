package com.hciproject.exploresite

data class PointOfInterest(
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double
)

val CulturalPOI = listOf(
    PointOfInterest("Colosseo", "Anfiteatro Flavio", 41.8902, 12.4922),
    PointOfInterest("Fontana di Trevi", "Famosa fontana barocca", 41.9009, 12.4833),
    PointOfInterest("Pantheon", "Antico tempio romano", 41.8986, 12.4769)
)
