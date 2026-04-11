/*
 * SPDX-FileCopyrightText: DerpFest AOSP
 * SPDX-FileCopyrightText: crDroid Android Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.libremobileos.sidebar.ui.sidebar

import android.content.SharedPreferences
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.settingslib.spa.widget.scaffold.SettingsScaffold

@Composable
fun SidebarCustomizationSettingsPage(
    sharedPrefs: SharedPreferences,
    onBack: () -> Unit = {},
    onSettingChanged: () -> Unit = {},
) {
    BackHandler(onBack = onBack)

    var transparency by remember { mutableFloatStateOf(sharedPrefs.getFloat("slider_transparency", 0.8f)) }
    var sliderLength by remember { mutableIntStateOf(sharedPrefs.getInt("slider_length", 200)) }
    var position by remember { mutableIntStateOf(sharedPrefs.getInt("sideline_position_x", 1)) }
    var columnCount by remember { mutableIntStateOf(sharedPrefs.getInt("sidebar_columns", 1)) }
    var sliderWidth by remember { mutableIntStateOf(sharedPrefs.getInt("slider_width", 100)) }
    var iconSize by remember { mutableIntStateOf(sharedPrefs.getInt("sidebar_icon_size", 40)) }
    var iconPadding by remember { mutableIntStateOf(sharedPrefs.getInt("sidebar_icon_padding", 7)) }
    var columnSpacing by remember { mutableIntStateOf(sharedPrefs.getInt("sidebar_column_spacing", 4)) }
    var cornerRadius by remember { mutableFloatStateOf(sharedPrefs.getFloat("sidebar_corner_radius", 24f)) }
    var backgroundTransparency by remember {
        mutableFloatStateOf(sharedPrefs.getFloat("sidebar_background_transparency", 0.8f))
    }
    var showShadow by remember { mutableStateOf(sharedPrefs.getBoolean("sidebar_show_shadow", true)) }

    fun update(block: SharedPreferences.Editor.() -> Unit) {
        sharedPrefs.edit().apply(block).apply()
        onSettingChanged()
    }

    SettingsScaffold(title = "Sidebar customization") { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
        ) {
            item {
                CustomizationSection("Slider settings") {
                    SliderSetting("Transparency: ${"%.2f".format(transparency)}", transparency, 0.1f..1f) {
                        transparency = it
                        update { putFloat("slider_transparency", it) }
                    }
                    SliderSetting("Slider length: $sliderLength px", sliderLength.toFloat(), 100f..500f) {
                        sliderLength = it.toInt()
                        update { putInt("slider_length", sliderLength) }
                    }
                    Text("Position")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = position == -1,
                            onClick = { position = -1; update { putInt("sideline_position_x", position) } },
                            label = { Text("Left") },
                        )
                        FilterChip(
                            selected = position == 1,
                            onClick = { position = 1; update { putInt("sideline_position_x", position) } },
                            label = { Text("Right") },
                        )
                    }
                }
            }
            item {
                CustomizationSection("Layout settings") {
                    SliderSetting("Columns: $columnCount", columnCount.toFloat(), 1f..3f, 1) {
                        columnCount = it.toInt()
                        update { putInt("sidebar_columns", columnCount) }
                    }
                    SliderSetting("Slider width: $sliderWidth px", sliderWidth.toFloat(), 60f..200f) {
                        sliderWidth = it.toInt()
                        update { putInt("slider_width", sliderWidth) }
                    }
                    SliderSetting("Icon size: $iconSize dp", iconSize.toFloat(), 30f..80f) {
                        iconSize = it.toInt()
                        update { putInt("sidebar_icon_size", iconSize) }
                    }
                    SliderSetting("Icon padding: $iconPadding dp", iconPadding.toFloat(), 4f..20f) {
                        iconPadding = it.toInt()
                        update { putInt("sidebar_icon_padding", iconPadding) }
                    }
                    if (columnCount > 1) {
                        SliderSetting("Column spacing: $columnSpacing dp", columnSpacing.toFloat(), 1f..12f) {
                            columnSpacing = it.toInt()
                            update { putInt("sidebar_column_spacing", columnSpacing) }
                        }
                    }
                    SliderSetting("Corner radius: ${cornerRadius.toInt()} dp", cornerRadius, 0f..32f) {
                        cornerRadius = it
                        update { putFloat("sidebar_corner_radius", it) }
                    }
                    SliderSetting("Background transparency: ${"%.2f".format(backgroundTransparency)}", backgroundTransparency, 0.1f..1f) {
                        backgroundTransparency = it
                        update { putFloat("sidebar_background_transparency", it) }
                    }
                }
            }
            item {
                CustomizationSection("Visual effects") {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(
                            checked = showShadow,
                            onCheckedChange = {
                                showShadow = it
                                update { putBoolean("sidebar_show_shadow", it) }
                            },
                        )
                        Text("Drop shadow", modifier = Modifier.padding(start = 12.dp))
                    }
                }
            }
            item {
                OutlinedButton(
                    onClick = {
                        transparency = 0.8f
                        sliderLength = 200
                        position = 1
                        columnCount = 1
                        sliderWidth = 100
                        iconSize = 40
                        iconPadding = 7
                        columnSpacing = 4
                        cornerRadius = 24f
                        backgroundTransparency = 0.8f
                        showShadow = true
                        update {
                            putFloat("slider_transparency", transparency)
                            putInt("slider_length", sliderLength)
                            putInt("sideline_position_x", position)
                            putInt("sidebar_columns", columnCount)
                            putInt("slider_width", sliderWidth)
                            putInt("sidebar_icon_size", iconSize)
                            putInt("sidebar_icon_padding", iconPadding)
                            putInt("sidebar_column_spacing", columnSpacing)
                            putFloat("sidebar_corner_radius", cornerRadius)
                            putFloat("sidebar_background_transparency", backgroundTransparency)
                            putBoolean("sidebar_show_shadow", showShadow)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                ) {
                    Text("Reset to defaults")
                }
            }
        }
    }
}

@Composable
private fun CustomizationSection(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun SliderSetting(
    title: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    steps: Int = 0,
    onValueChange: (Float) -> Unit,
) {
    Text(title)
    Slider(
        value = value,
        onValueChange = onValueChange,
        valueRange = range,
        steps = steps,
        modifier = Modifier.fillMaxWidth(),
    )
}
