package com.hciproject.exploresite.poi

import androidx.compose.ui.text.LinkAnnotation
import com.hciproject.exploresite.R

data class PointOfInterest(
    
    val name: String, //Nome del POI
    val location: LinkAnnotation, //Link di Google Maps quando si ha quel posto aperto
    val image: Int, //Drawable dell'immagine, è una cosa che mi gestico io con il drawable basta che mi fai avere l'immagine come nomepoi_primary
    val address: String, //Indirizzo del POI com via/viale/piazza, cap, città e provincia in caso la città non è una provincia, es una cosa a napoli scrivi Napoli una cosa a pozzuoli scrivi Pozzuoli (NA)
    val basePrice: Double, //Prezzo del biglietto base, quello standard che si vedeva affianco al nome del POI nel prototipo
    val detailedPrice: String, //Prezzi dettagliati di tutte le opzioni, in pratica quello che visualizzavi premendo sul prezzo nel prototipo
    val baseOpeningTime: String, //Orario base di apertura, quello che visualizzavi sotto il titolo nel prototipo
    val detailedOpeningTime: String, //Orario dettagliato, quello che visualizzavi quando premevi l'orario base
    val baseJourneyDuration: String, //Orario di percorrenza da mettere nell'ovale
    val detailedJourneyDuration: String, //Orario di percorrenza che leggi quando lo premevi
    val baseAccessibility: POIAccessibility, //Tipologia di accessibilità che vedevi nell'ovale, definiti come boolean, vedi il data class POIAccessibility
    val detailedAccessibility: String, //Quello che vedevi scritto qunado premevi sull'ovale dell'accessibilità
    val ticketShop: LinkAnnotation, //Link alla biglietteria ufficiale
    val suggestionNumber: Int, //Numero di "Suggerisce" che vedavamo anche nel prototipo, considera un numero inventato però abbastanza plausibile per il luogo che è
    val description: String, //Descrizione, testo della prima delle 3 card
    val descriptionImage: Int, //Immagine della prima delle 3 card, comportati come per l'immagine del POI che ti ho detto prima
    val curiosity1: String,//Titolo della seconda card, tipo "Un po di storia...", "Lo sapevi che..." cose così.
    val curiosity1Title: String,//Contenuto della seconda card
    val curiosity1Image: Int,//Immagine della seconda delle 3 card
    val curiosity2: String,//Contentuo della terza card
    val curiosity2Title: String,//Titolo della terza card
    val curiosity2Image: Int,//Immagine della terza delle 3 card
    val latitude: Double,//Latitudine GPS del punto su maps, la si copia premendo tasto destro sul punto
    val longitude: Double,//Longitudine GPS del punto su maps, la si copia premendo tasto destro sul punto
    val type: POICategory,//Categoria del punto di interesse
)

enum class POICategory{
    CULINARY,
    ARCHEOLOGICAL,
    MUSEUM,
    RELIGIOUS,
    ARCHITECTURE
}

data class POIAccessibility(
    val wheelchair: Boolean,
    val deaf: Boolean,
    val blind: Boolean,
)

val CulturalPOI = listOf(
    PointOfInterest("Reggia di Caserta",
        LinkAnnotation.Url("https://www.google.com/maps/place/Reggia+di+Caserta/@41.0731817,14.3245438,16z/data=!3m2!4b1!5s0x133a55b23cc51487:0xe6922395fd344b8b!4m6!3m5!1s0x133a55b22aed1333:0xc79c020a24847245!8m2!3d41.0731777!4d14.3271187!16zL20vMDNmaGpn?entry=ttu&g_ep=EgoyMDI2MDUyMC4wIKXMDSoASAFQAw%3D%3D"),
        R.drawable.reggia_di_caserta_primary,
        "Piazza Carlo di Borbone, 81100, Caserta",
        18.00,
        "Il prezzo indicato è il prezzo del biglietto base, senza agevolazioni. Le agevolazioni offerte dalla Reggia di Caserta sono:\n\n- Gratuito per minori di 18 anni\n- Ridotto a 2€ per un’età compresa tra 18 e 24 anni\n\nAl prezzo viene aggiunto 1€ in caso di acquisto online con prevendita",
        "8:30 - 19:15",
        "Reggia di Caserta ha i seguenti orari di servizio:\n\nOrario di apertura: 8:30\nOrario di chiusura: 19:15\n\nUltimo ingresso consentito alle 18:15",
        "4:00",
        "Il tempo medio di percorrenza di una visita completa della Reggia di Caserta è di circa 4 ore",
        POIAccessibility(true, true, true),
        "La Reggia di Caserta offre le seguenti opzioni di accessibilità:\n\nPer le persone con disabilità motorie:\nParcheggi per disabili\nAscensori dedicati\nServizio navetta dedicato\n\nPer le persone ipoacusiche:\nAudioguide con tecnologia ad induzione magnetica\n\nPer le persone ipovedenti:\nPlastici tattili\nGuide tattili",
        LinkAnnotation.Url("https://www.ticketone.it/artist/reggia-caserta/reggia-di-caserta-1818672/"),
        378491,
        "La Reggia di Caserta è la residenza reale più grande al mondo, fu commissionata da ReCarlo di Borbone nel 1752 e progettata da Luigi Vanvitelli.",
        R.drawable.reggia_di_caserta_description,
        "Un po' di storia...",
        "Re Carlo di Borbone fece costruire la residenza reale nell’entroterra casertano, anziché a napoli, per fare in modo che fosse al sicuro da qualsiasi bombardamento navale.",
        R.drawable.reggia_di_caserta_curiosity1,
        "Lo sapevi che...",
        "Nell’Episodio I della saga di Star Wars, la Reggia di Caserta è stata trasformata nell'iconico Palazzo Reale del pianeta Naboo.",
        R.drawable.reggia_di_caserta_curiosity2,
        41.07331516989688,
        14.327097240221528,
        POICategory.ARCHITECTURE),
)