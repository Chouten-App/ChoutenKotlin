package com.inumaki.chouten.Features.Settings

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.LaptopMac
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inumaki.chouten.SettingsLink
import com.inumaki.chouten.getAppVersion

@Composable
fun SettingsView(
    context: Context,
    onNavigate: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(fraction = 0.92f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF171717))
                .border(
                    0.5.dp,
                    Color(0xFF3B3B3B),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 12.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Not logged in.",
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD4D4D4)
            )

            Text(
                "Logging in is currently unsupported. We hope to bring Discord login or something similar fairly soon.",
                fontSize = 12.sp,
                lineHeight = 14.sp,
                textAlign = TextAlign.Center,
                color = Color(0xB2D4D4D4)
            )

            Text(
                "Note: This login is unrelated to tracking.",
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp,
                color = Color(0x80D4D4D4)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF171717))
                .border(
                    0.5.dp,
                    Color(0xFF3B3B3B),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 12.dp, vertical = 16.dp)
        ) {
            SettingsLink(icon = Icons.Filled.WbSunny, title = "Appearance", onTap = {})

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                thickness = 0.5.dp,
                color = Color(0xFF3B3B3B)
            )

            SettingsLink(
                icon = Icons.Outlined.LaptopMac,
                title = "Developer",
                onTap = {
                    onNavigate()
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(-2.dp)
        ) {
            Text(
                buildAnnotatedString {
                    append("Made by ")

                    withStyle(style = SpanStyle(color = Color(0xFF6458ED))) {
                        append("Inumaki")
                    }
                },
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD4D4D4)
            )

            Text(
                "Version ${getAppVersion(context = context)}",
                fontSize = 12.sp,
                color = Color(0xFFD4D4D4)
            )
        }

    }
}