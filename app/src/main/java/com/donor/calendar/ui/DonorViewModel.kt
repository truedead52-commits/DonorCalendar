package com.donor.calendar.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.donor.calendar.data.*
import com.donor.calendar.domain.AvailabilityResult
import com.donor.calendar.domain.DonationRules
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

data class DonationStatusUiState(
    val type: DonationType,
    val result: AvailabilityResult
)

data class MainUiState(
    val gender: DonorGender = DonorGender.MALE,
    val statuses: List<DonationStatusUiState> = emptyList(),
    val donations: List<DonationEntity> = emptyList(),
    val isLoading: Boolean = true
)

class DonorViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = DonorDatabase.getInstance(application).donationDao()
    private val prefs = DonorPreferences(application)

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        // Реактивно пересчитываем при любом изменении донаций или пола
        viewModelScope.launch {
            combine(
                dao.getAllDonations(),
                prefs.gender
            ) { donations, gender ->
                donations to gender
            }.collect { (donations, gender) ->
                val statuses = calculateStatuses(donations, gender)
                _uiState.update { state ->
                    state.copy(
                        gender = gender,
                        statuses = statuses,
                        donations = donations,
                        isLoading = false
                    )
                }
            }
        }
    }

    private suspend fun calculateStatuses(
        donations: List<DonationEntity>,
        gender: DonorGender
    ): List<DonationStatusUiState> {
        val today = LocalDate.now()
        val latest = dao.getLatestDonation()

        // Окно 365 дней назад
        val yearAgoEpochDay = DonationRules.localDateToEpochDay(today.minusDays(365))
        val bloodInYear = dao.getBloodDonationsSince(yearAgoEpochDay)

        return DonationType.values().map { type ->
            val result = DonationRules.getAvailability(
                targetType = type,
                latestDonation = latest,
                bloodDonationsInYear = bloodInYear,
                gender = gender,
                today = today
            )
            DonationStatusUiState(type = type, result = result)
        }
    }

    fun addDonation(date: LocalDate, type: DonationType) {
        viewModelScope.launch {
            dao.insert(
                DonationEntity(
                    date = DonationRules.localDateToEpochDay(date),
                    type = type
                )
            )
        }
    }

    fun deleteDonation(donation: DonationEntity) {
        viewModelScope.launch {
            dao.delete(donation)
        }
    }

    fun setGender(gender: DonorGender) {
        viewModelScope.launch {
            prefs.setGender(gender)
        }
    }
}
