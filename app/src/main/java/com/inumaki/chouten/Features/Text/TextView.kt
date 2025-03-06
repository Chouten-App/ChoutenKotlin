package com.inumaki.chouten.Features.Text

import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inumaki.chouten.Components.NetworkImage
import com.inumaki.chouten.ui.theme.ChoutenTheme

@Composable
fun IndentedTextExample(text: String) {
    Text(
        text = buildAnnotatedString {
            withStyle(
                style = ParagraphStyle(textIndent = TextIndent(firstLine = 24.sp))
            ) {
                append(text)
            }
        },
        fontSize = 18.sp,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
@Preview
fun TextView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0C0C0C))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NetworkImage(
            "https://i.imgur.com/hfeUWgT.png",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        Text(
            "Chapter 1: Prologue - Three Ways to survive in a Ruined World.",
            modifier = Modifier.padding(bottom = 8.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFD4D4D4),
            textAlign = TextAlign.Center
        )
        Text(
            "「There are three ways to survive in a ruined world. I have forgotten some of them now. However, one thing is certain: you who are currently reading these words will survive.",
            color = Color(0xFFD4D4D4)
        )
        Text(
            "–Three Ways to Survive in a Ruined World [Complete]」",
            color = Color(0xFFD4D4D4)
        )
        Text(
            "A web novel platform filled the screen of my old smartphone. I scrolled down and then up again. How many times have I been doing this?",
            color = Color(0xFFD4D4D4)
        )
        Text(
            "\"Really? This is the end?”",
            color = Color(0xFFD4D4D4)
        )
        Text(
            "I looked again, and the ‘complete’ was unmistakable. The story was over.",
            color = Color(0xFFD4D4D4)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFD4D4D4))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "[Three ways to Survive in a Ruined World]",
                color = Color(0xFFD4D4D4)
            )
            Text(
                "Author: tls123",
                color = Color(0xFFD4D4D4)
            )
            Text(
                "3,149 chapters.",
                color = Color(0xFFD4D4D4)
            )
        }
    }
}