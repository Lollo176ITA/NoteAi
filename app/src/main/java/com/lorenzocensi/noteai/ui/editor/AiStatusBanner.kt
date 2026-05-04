package com.lorenzocensi.noteai.ui.editor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.lorenzocensi.noteai.R
import com.lorenzocensi.noteai.domain.model.AiStatus
import kotlinx.coroutines.delay

@Composable
fun AiStatusBanner(
    status: AiStatus,
    onOpenSettings: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    var doneVisible by remember { mutableStateOf(false) }
    LaunchedEffect(status) {
        if (status is AiStatus.Done) {
            doneVisible = true
            delay(4000)
            doneVisible = false
        }
    }

    val visible = when (status) {
        AiStatus.Idle -> false
        is AiStatus.Pending -> false
        is AiStatus.Done -> doneVisible
        else -> true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        when (status) {
            AiStatus.Idle -> Unit
            is AiStatus.Pending -> Unit
            AiStatus.Running -> BannerSurface(
                tone = BannerTone.Info,
                content = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.ai_status_running),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            )
            is AiStatus.Done -> BannerSurface(
                tone = BannerTone.Success,
                content = {
                    Text(
                        text = if (status.linkCount == 0)
                            stringResource(R.string.ai_status_done_zero)
                        else
                            stringResource(R.string.ai_status_done_n, status.linkCount),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            )
            AiStatus.MissingApiKey -> BannerSurface(
                tone = BannerTone.Error,
                content = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.ai_status_missing_key),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = onOpenSettings) {
                            Text(stringResource(R.string.ai_status_missing_key_cta))
                        }
                    }
                }
            )
            is AiStatus.Error -> BannerSurface(
                tone = BannerTone.Error,
                content = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.ai_status_error, status.reason),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = onRetry) {
                            Text(stringResource(R.string.ai_status_retry))
                        }
                    }
                }
            )
        }
    }
}

private enum class BannerTone { Info, Success, Error }

@Composable
private fun BannerSurface(
    tone: BannerTone,
    content: @Composable () -> Unit
) {
    val (bg, fg) = when (tone) {
        BannerTone.Info -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        BannerTone.Success -> Color(0xFFD7F0CB) to Color(0xFF1B3D14)
        BannerTone.Error -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
    }
    androidx.compose.material3.Surface(
        color = bg,
        contentColor = fg,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) { content() }
    }
}
