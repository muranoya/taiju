package net.meshpeak.taiju.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import net.meshpeak.taiju.domain.model.UserSettings
import net.meshpeak.taiju.domain.usecase.GetUserSettingsUseCase
import javax.inject.Inject

@HiltViewModel
class RootViewModel
    @Inject
    constructor(
        getUserSettings: GetUserSettingsUseCase,
    ) : ViewModel() {
        val settings: StateFlow<UserSettings> =
            getUserSettings().stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = UserSettings(),
            )
    }
