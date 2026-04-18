package net.meshpeak.taiju.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

private const val DATASTORE_NAME = "taiju_settings"

val Context.taijuDataStore: DataStore<Preferences> by preferencesDataStore(name = DATASTORE_NAME)

object SettingsKeys {
    val THEME = stringPreferencesKey("theme")
    val USE_DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
    val TARGET_WEIGHT_KG = doublePreferencesKey("target_weight_kg")
}
