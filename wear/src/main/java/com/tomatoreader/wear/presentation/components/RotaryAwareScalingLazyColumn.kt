package com.tomatoreader.wear.presentation.components

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.ScalingLazyColumn
import androidx.wear.compose.foundation.ScalingLazyColumnDefaults
import androidx.wear.compose.foundation.ScalingLazyColumnState
import androidx.wear.compose.foundation.ScalingParams
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeText

@Composable
fun RotaryAwareScalingLazyColumn(
    modifier: Modifier = Modifier,
    state: ScalingLazyColumnState = ScalingLazyColumnDefaults.initial(),
    scalingParams: ScalingParams = ScalingLazyColumnDefaults.scalingParams(),
    contentPadding: PaddingValues = PaddingValues(),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(4.dp),
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    rotaryScrollingEnabled: Boolean = true,
    onRotaryScroll: ((Float) -> Float)? = null,
    content: androidx.wear.compose.foundation.lazy.ScalingLazyListScope.() -> Unit
) {
    ScalingLazyColumn(
        modifier = modifier,
        columnState = state,
        scalingParams = scalingParams,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        content = content
    )
}