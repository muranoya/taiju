package net.meshpeak.taiju.data.time

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import javax.inject.Inject
import javax.inject.Singleton

interface TimeProvider {
    fun today(): LocalDate

    fun now(): Instant
}

@Singleton
class SystemTimeProvider
    @Inject
    constructor() : TimeProvider {
        override fun today(): LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())

        override fun now(): Instant = Clock.System.now()
    }
