package com.inumaki.chouten.Features.Settings.Developer

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DeveloperView() {
    Column(
        modifier = Modifier.fillMaxWidth()
            .fillMaxHeight(fraction = 0.92f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        SettingsGroup("Logs") {
            SettingsToggle("Enable Logs")

            HorizontalDivider()

            SettingsToggle("Enable Log Server", description = "Send logs to a server for easier debugging.")
        }
    }
}

@Composable
fun SettingsGroup(title: String, settings: @Composable () -> Unit) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            title,
            modifier = Modifier.padding(start = 16.dp),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFFD4D4D4)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF171717))
                .border(0.5.dp, Color(0xFF3B3B3B), shape = RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            settings()
        }
    }
}

@Composable
fun SettingsToggle(title: String, description: String? = null) {
    var toggleValue = remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(-8.dp)
        ) {
            Text(
                title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD4D4D4)
            )

            if (description != null) {
                Text(
                    description,
                    fontSize = 10.sp,
                    color = Color(0x92D4D4D4)
                )
            }
        }

        Switch(
            checked = toggleValue.value,
            modifier = Modifier.scale(0.8f),
            onCheckedChange = {
                // Update the state
                toggleValue.value = it

                /*
                val sharedPreferences = applicationContext.getSharedPreferences("Preferences", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putBoolean("showCarouselBanner", it)
                editor.apply()
                */
            }
        )
    }
}