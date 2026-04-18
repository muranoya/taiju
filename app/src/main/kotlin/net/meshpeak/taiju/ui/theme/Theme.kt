package net.meshpeak.taiju.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import net.meshpeak.taiju.domain.model.AppTheme

private val FallbackLightColors =
    lightColorScheme(
        primary = LightPrimary,
        onPrimary = LightOnPrimary,
        primaryContainer = LightPrimaryContainer,
        onPrimaryContainer = LightOnPrimaryContainer,
        background = LightBackground,
        onBackground = LightOnBackground,
        surface = LightSurface,
        onSurface = LightOnSurface,
    )

private val FallbackDarkColors =
    darkColorScheme(
        primary = DarkPrimary,
        onPrimary = DarkOnPrimary,
        primaryContainer = DarkPrimaryContainer,
        onPrimaryContainer = DarkOnPrimaryContainer,
        background = DarkBackground,
        onBackground = DarkOnBackground,
        surface = DarkSurface,
        onSurface = DarkOnSurface,
    )

@Composable
fun TaijuTheme(
    appTheme: AppTheme = AppTheme.SYSTEM,
    useDynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val systemDark = isSystemInDarkTheme()
    val useDarkTheme =
        when (appTheme) {
            AppTheme.SYSTEM -> systemDark
            AppTheme.LIGHT -> false
            AppTheme.DARK -> true
        }
    val context = LocalContext.current
    val colorScheme =
        when {
            useDynamicColor ->
                if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            useDarkTheme -> FallbackDarkColors
            else -> FallbackLightColors
        }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = TaijuTypography,
        content = content,
    )
}
