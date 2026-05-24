package com.example.adopciontfg.app.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.adopciontfg.R
import com.example.adopciontfg.ui.theme.AdoptionTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AnimatedSplashScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val logoScale = remember { Animatable(0.78f) }
    val logoAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val infiniteTransition = rememberInfiniteTransition(label = "splash-pulse")
    val cardPulse by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "logo-pulse",
    )

    LaunchedEffect(Unit) {
        launch {
            logoAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 420),
            )
        }
        launch {
            logoScale.animateTo(
                targetValue = 1.06f,
                animationSpec = tween(durationMillis = 520, easing = FastOutSlowInEasing),
            )
            logoScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing),
            )
        }
        delay(360)
        textAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 360),
        )
        delay(850)
        onFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SplashBackground),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(192.dp)
                    .scale(cardPulse),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.adopta_launcher_foreground),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(logoScale.value)
                        .alpha(logoAlpha.value),
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.alpha(textAlpha.value),
            )
        }
    }
}

private val SplashBackground = Color(0xFFEAFBF6)

@Preview(showBackground = true)
@Composable
private fun AnimatedSplashScreenPreview() {
    AdoptionTheme {
        AnimatedSplashScreen(onFinished = {})
    }
}
