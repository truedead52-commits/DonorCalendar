package com.donor.calendar.data

enum class DonationType(val displayName: String, val icon: String) {
    BLOOD("Цельная кровь", "🩸"),
    PLASMA("Плазма", "💉"),
    PLATELETS("Тромбоциты", "🔬")
}
