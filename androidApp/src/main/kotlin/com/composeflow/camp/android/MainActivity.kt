package com.composeflow.camp.android

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.composeflow.camp.dynamic.demo.DemoRegistry
import com.composeflow.camp.dynamic.demo.DemoSamples
import com.composeflow.camp.dynamic.model.EventSpec
import com.composeflow.camp.dynamic.model.EventType
import com.composeflow.camp.dynamic.parser.DynamicDslParser
import com.composeflow.camp.dynamic.parser.ParseResult
import com.composeflow.camp.dynamic.platform.EventDispatcher
import com.composeflow.camp.dynamic.render.DynamicPageRenderer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    DemoScreen(
                        dispatcher = AndroidUiEventDispatcher { message ->
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        },
                    )
                }
            }
        }
    }
}

private class AndroidUiEventDispatcher(
    private val toast: (String) -> Unit,
) : EventDispatcher {
    override fun dispatch(event: EventSpec) {
        when (event.type) {
            EventType.Toast -> toast(event.payload["message"] ?: "Toast")
            EventType.Navigate -> toast("Navigate: ${event.payload["route"].orEmpty()}")
            EventType.Track -> toast("Track: ${event.payload["event"].orEmpty()}")
        }
    }
}

@Composable
private fun DemoScreen(dispatcher: EventDispatcher) {
    val parser = remember { DynamicDslParser() }
    val json = remember { Json { ignoreUnknownKeys = true } }

    var selectedMode by remember { mutableStateOf("valid") }
    var selectedName by remember { mutableStateOf("retention_dialog") }

    val samples = if (selectedMode == "valid") DemoSamples.validSamples else DemoSamples.invalidSamples
    val safeSelectedName = selectedName.takeIf { samples.containsKey(it) } ?: samples.keys.first()
    val rawDsl = samples[safeSelectedName].orEmpty()
    val parseResult = remember(rawDsl) { parser.parse(rawDsl) }
    val dataResult = remember(safeSelectedName) {
        runCatching {
            json.parseToJsonElement(DemoRegistry.sampleData(safeSelectedName)).jsonObject
        }
    }
    val data = dataResult.getOrElse { buildJsonObject { } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F3F6))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("ComposeFlow Camp Demo", style = MaterialTheme.typography.titleLarge)
        Text("Topic 1: CapCut subscription retention dialog.")

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                selectedMode = "valid"
                selectedName = "retention_dialog"
            }) {
                Text("Valid")
            }
            OutlinedButton(onClick = {
                selectedMode = "invalid"
                selectedName = DemoSamples.invalidSamples.keys.first()
            }) {
                Text("Invalid")
            }
        }

        samples.keys.chunked(2).forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowItems.forEach { name ->
                    OutlinedButton(onClick = { selectedName = name }) {
                        Text(name)
                    }
                }
            }
        }

        Spacer(Modifier.height(4.dp))

        dataResult.exceptionOrNull()?.let { throwable ->
            ErrorPanel(
                title = "Data parse error",
                messages = listOf(throwable.message ?: throwable::class.simpleName.orEmpty()),
            )
        }

        when (parseResult) {
            is ParseResult.Success -> DynamicPageRenderer(
                page = parseResult.value,
                data = data,
                eventDispatcher = dispatcher,
                modifier = Modifier.fillMaxWidth(),
            )
            is ParseResult.Failure -> ErrorPanel(
                title = "Parser errors",
                messages = parseResult.errors.map { "${it.path}: ${it.message}" },
            )
        }
    }
}

@Composable
private fun ErrorPanel(title: String, messages: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFECEC))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, color = Color(0xFF9B1C1C))
        messages.forEach { message ->
            Text(message, color = Color(0xFF5F1111))
        }
    }
}
