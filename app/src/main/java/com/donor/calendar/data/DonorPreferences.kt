package com.donor.calendar.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "donor_prefs")

class DonorPreferences(private val context: Context) {

    companion object {
        private val GENDER_KEY = stringPreferencesKey("donor_gender")
    }

    val gender: Flow<DonorGender> = context.dataStore.data.map { prefs ->
        val raw = prefs[GENDER_KEY] ?: DonorGender.MALE.name
        DonorGender.valueOf(raw)
    }

    suspend fun setGender(gender: DonorGender) {
        context.dataStore.edit { prefs ->
            prefs[GENDER_KEY] = gender.name
        }
    }
}
