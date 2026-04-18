package net.meshpeak.taiju.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.meshpeak.taiju.data.datastore.SettingsKeys
import net.meshpeak.taiju.domain.model.AppTheme
import net.meshpeak.taiju.domain.model.UserSettings
import net.meshpeak.taiju.domain.repository.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
    ) : SettingsRepository {
        override val settings: Flow<UserSettings> =
            dataStore.data.map { prefs ->
                UserSettings(
                    theme =
                        prefs[SettingsKeys.THEME]
                            ?.let { runCatching { AppTheme.valueOf(it) }.getOrNull() }
                            ?: AppTheme.SYSTEM,
                    useDynamicColor = prefs[SettingsKeys.USE_DYNAMIC_COLOR] ?: true,
                    targetWeightKg = prefs[SettingsKeys.TARGET_WEIGHT_KG],
                )
            }

        override suspend fun setTheme(theme: AppTheme) {
            dataStore.edit { it[SettingsKeys.THEME] = theme.name }
        }

        override suspend fun setUseDynamicColor(enabled: Boolean) {
            dataStore.edit { it[SettingsKeys.USE_DYNAMIC_COLOR] = enabled }
        }

        override suspend fun setTargetWeight(kg: Double?) {
            dataStore.edit { prefs ->
                if (kg == null) {
                    prefs.remove(SettingsKeys.TARGET_WEIGHT_KG)
                } else {
                    prefs[SettingsKeys.TARGET_WEIGHT_KG] = kg
                }
            }
        }
    }
