package com.bumble.appyx.sandbox.client.integrationpoint

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import java.util.UUID

class StartActivityExample : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Gray)
            ) {
                Button(onClick = {
                    val uuid = UUID.randomUUID().toString()
                    setResult(RESULT_OK, Intent().apply {
                        putExtra(StringExtraKey, uuid)
                    })
                    finish()
                }) {
                    Text("Return random result")
                }
            }

        }
    }

    companion object {
        const val StringExtraKey = "StringExtraKey"
    }
}
