package com.donor.calendar.domain

import com.donor.calendar.data.DonationEntity
import com.donor.calendar.data.DonorGender
import com.donor.calendar.data.DonationType
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Вся бизнес-логика интервалов между донациями и годовых ограничений.
 */
object DonationRules {

    // Интервалы в днях: [последняя донация] -> [следующая донация] -> минимум дней
    private val INTERVALS: Map<DonationType, Map<DonationType, Int>> = mapOf(
        DonationType.BLOOD to mapOf(
            DonationType.BLOOD     to 60,
            DonationType.PLASMA    to 30,
            DonationType.PLATELETS to 30
        ),
        DonationType.PLASMA to mapOf(
            DonationType.BLOOD     to 14,
            DonationType.PLASMA    to 14,
            DonationType.PLATELETS to 14
        ),
        DonationType.PLATELETS to mapOf(
            DonationType.BLOOD     to 14,
            DonationType.PLASMA    to 14,
            DonationType.PLATELETS to 14
        )
    )

    private const val BLOOD_LIMIT_MALE   = 5
    private const val BLOOD_LIMIT_FEMALE = 4

    /**
     * Вычисляет доступность [targetType] для донации.
     *
     * @param latestDonation        последняя запись из БД (null — история пуста)
     * @param bloodDonationsInYear  список донаций крови за последние 365 дней
     * @param gender                пол донора
     * @param today                 сегодняшняя дата (параметр для тестируемости)
     */
    fun getAvailability(
        targetType: DonationType,
        latestDonation: DonationEntity?,
        bloodDonationsInYear: List<DonationEntity>,
        gender: DonorGender,
        today: LocalDate = LocalDate.now()
    ): AvailabilityResult {

        // 1. Ограничение по интервалу после последней донации
        val intervalDate: LocalDate? = latestDonation?.let { last ->
            val lastDate = epochDayToLocalDate(last.date)
            val days = INTERVALS[last.type]?.get(targetType) ?: 0
            lastDate.plusDays(days.toLong())
        }

        // 2. Для цельной крови — проверяем годовой лимит
        val limitDate: LocalDate? = if (targetType == DonationType.BLOOD) {
            val limit = if (gender == DonorGender.MALE) BLOOD_LIMIT_MALE else BLOOD_LIMIT_FEMALE
            if (bloodDonationsInYear.size >= limit) {
                // Самая ранняя из донаций в 365-дневном окне — когда она «выйдет» из окна, лимит освободится
                val oldestDate = bloodDonationsInYear
                    .minByOrNull { it.date }
                    ?.let { epochDayToLocalDate(it.date) }
                // На следующий день после выхода за 365-дневное окно
                oldestDate?.plusDays(366)
            } else null
        } else null

        // Берём максимальную (самую поздно снимающуюся) дату ограничения
        val restrictedUntil = listOfNotNull(intervalDate, limitDate).maxOrNull()

        return if (restrictedUntil == null || !today.isBefore(restrictedUntil)) {
            AvailabilityResult.Available
        } else {
            val daysLeft = ChronoUnit.DAYS.between(today, restrictedUntil)
            AvailabilityResult.NotAvailable(
                availableFrom = restrictedUntil,
                daysLeft = daysLeft
            )
        }
    }

    // Хранение дат как epoch-day (Long) — нет проблем с часовыми поясами
    fun epochDayToLocalDate(epochDay: Long): LocalDate =
        LocalDate.ofEpochDay(epochDay)

    fun localDateToEpochDay(date: LocalDate): Long =
        date.toEpochDay()
}

sealed class AvailabilityResult {
    object Available : AvailabilityResult()
    data class NotAvailable(
        val availableFrom: LocalDate,
        val daysLeft: Long
    ) : AvailabilityResult()
}
