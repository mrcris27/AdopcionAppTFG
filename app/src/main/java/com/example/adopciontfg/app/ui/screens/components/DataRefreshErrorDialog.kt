package com.example.adopciontfg.app.ui.screens.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.adopciontfg.R
import com.example.adopciontfg.app.ui.state.DataRefreshError

@Composable
fun DataRefreshErrorDialog(
    error: DataRefreshError?,
    onDismiss: () -> Unit,
    onRetry: (() -> Unit)? = null,
) {
    if (error == null) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.error_refresh_title)) },
        text = { Text(stringResource(error.messageRes)) },
        confirmButton = {
            if (onRetry != null) {
                TextButton(
                    onClick = {
                        onDismiss()
                        onRetry()
                    },
                ) {
                    Text(stringResource(R.string.actualizar))
                }
            } else {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.cerrar))
                }
            }
        },
        dismissButton = {
            if (onRetry != null) {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.cerrar))
                }
            }
        },
    )
}
