package com.composeflow.camp.android

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
            EventType.Navigate -> toast("\u8df3\u8f6c\uff1a${event.payload["route"].orEmpty()}")
            EventType.Track -> toast("\u57cb\u70b9\uff1a${event.payload["event"].orEmpty()}")
        }
    }
}

@Composable
private fun DemoScreen(dispatcher: EventDispatcher) {
    val parser = remember { DynamicDslParser() }
    val json = remember { Json { ignoreUnknownKeys = true } }

    var selectedMode by remember { mutableStateOf("valid") }
    var selectedName by remember { mutableStateOf("retention_dialog") }
    var showSampleDialog by remember { mutableStateOf(false) }

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
        Text("\u9009\u9898\u4e00\uff1a\u526a\u6620\u8ba2\u9605\u633d\u7559\u5f39\u7a97")

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { showSampleDialog = true },
        ) {
            Text("\u5207\u6362\u6837\u4f8b\uff1a${sampleLabel(safeSelectedName)}")
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

    if (showSampleDialog) {
        SamplePickerDialog(
            onDismiss = { showSampleDialog = false },
            onSelect = { mode, name ->
                selectedMode = mode
                selectedName = name
                showSampleDialog = false
            },
        )
    }
}

@Composable
private fun SamplePickerDialog(
    onDismiss: () -> Unit,
    onSelect: (String, String) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("\u9009\u62e9\u6f14\u793a\u6837\u4f8b") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("\u6b63\u5e38\u6837\u4f8b", style = MaterialTheme.typography.titleSmall)
                DemoSamples.validSamples.keys.forEach { name ->
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onSelect("valid", name) },
                    ) {
                        Text(sampleLabel(name))
                    }
                }

                Spacer(Modifier.height(8.dp))
                Text("\u9519\u8bef\u6837\u4f8b", style = MaterialTheme.typography.titleSmall)
                DemoSamples.invalidSamples.keys.forEach { name ->
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onSelect("invalid", name) },
                    ) {
                        Text(sampleLabel(name))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("\u5173\u95ed")
            }
        },
    )
}

private fun sampleLabel(name: String): String = when (name) {
    "retention_dialog" -> "\u526a\u6620\u8ba2\u9605\u633d\u7559\u5f39\u7a97"
    "subscription_page" -> "\u8ba2\u9605\u9875"
    "benefits_empty" -> "\u6743\u76ca\u7a7a\u6001"
    "benefits_list" -> "\u6743\u76ca\u5217\u8868"
    "missing_text" -> "\u9519\u8bef\uff1a\u7f3a\u5c11 Text \u6587\u6848"
    "illegal_color" -> "\u9519\u8bef\uff1a\u975e\u6cd5\u989c\u8272"
    "unknown_component" -> "\u9519\u8bef\uff1a\u672a\u77e5\u7ec4\u4ef6"
    "foreach_missing_items" -> "\u9519\u8bef\uff1aForEach \u7f3a\u5c11 items"
    else -> name
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
