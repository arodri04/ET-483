package com.example.samrcourselab.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map



val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {

    companion object {
        val OVERRIDE_PREREQS_KEY = booleanPreferencesKey("override_prereqs")
        val SEARCH_QUERY_KEY = stringPreferencesKey("search_query")
    }

    val overridePrereqsFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[OVERRIDE_PREREQS_KEY] ?: false
    }

    val searchQueryFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[SEARCH_QUERY_KEY] ?: ""
    }

    suspend fun updateOverridePrereqs(override: Boolean) {
        dataStore.edit { preferences ->
            preferences[OVERRIDE_PREREQS_KEY] = override
        }
    }

    suspend fun updateSearchQuery(query: String) {
        dataStore.edit { preferences ->
            preferences[SEARCH_QUERY_KEY] = query
        }
    }
}