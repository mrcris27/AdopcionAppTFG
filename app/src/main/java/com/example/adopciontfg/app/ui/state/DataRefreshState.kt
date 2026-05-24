package com.example.adopciontfg.app.ui.state

import com.example.adopciontfg.R
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout

private const val DATA_REFRESH_TIMEOUT_MS = 10_000L

enum class DataRefreshError(val messageRes: Int) {
    CONNECTION(R.string.error_connection_refresh),
    TIMEOUT(R.string.error_timeout_refresh),
}

suspend fun awaitDataRefresh(
    refresh: (onSuccess: () -> Unit, onFailure: (Exception) -> Unit) -> Unit,
) {
    withTimeout(DATA_REFRESH_TIMEOUT_MS) {
        suspendCancellableCoroutine { continuation ->
            refresh(
                {
                    if (continuation.isActive) {
                        continuation.resume(Unit)
                    }
                },
                { exception ->
                    if (continuation.isActive) {
                        continuation.resumeWithException(exception)
                    }
                },
            )
        }
    }
}

fun Throwable.toDataRefreshError(): DataRefreshError =
    if (this is TimeoutCancellationException) {
        DataRefreshError.TIMEOUT
    } else {
        DataRefreshError.CONNECTION
    }
