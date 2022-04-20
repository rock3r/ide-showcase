package dev.sebastiano.ideshowcase

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIconDefaults
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.chozzle.composemacostheme.MacTheme
import io.chozzle.composemacostheme.modifiedofficial.MacOutlinedTextField

fun main() {
    application(exitProcessOnExit = true) {
        Window(
            title = "IDE showcase",
            icon = painterResource("intellij.svg"),
            onCloseRequest = { exitApplication() }
        ) {
            MaterialTheme {
                MacTheme {
                    MainWindowContent(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

@Preview
@Composable
internal fun MainWindowContent(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        var username by remember { mutableStateOf("") }

        Row(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Text("Lookup Twitch user:")

            Spacer(Modifier.width(16.dp))

            MacOutlinedTextField(
                username,
                onValueChange = { username = it },
                modifier = Modifier.size(width = 200.dp, height = 26.dp),
                leadingIcon = { Text("􀊫") },
                trailingIcon = {
                    if (username.isNotEmpty()) {
                        CloseIconButton(onClick = { username = "" })
                    }
                },
                placeholder = { Text("Search") },
                singleLine = true,
                cornerRadius = SearchTextFieldCornerRadius
            )
        }
    }

    Spacer(Modifier.height(16.dp))
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun CloseIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Box(
        modifier = modifier
            .pointerHoverIcon(PointerIconDefaults.Default)
            .clickable(
                onClick = onClick,
                enabled = enabled,
                role = Role.Button,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            )
            .padding(contentPadding),
    ) {
        Text("\uDBC0\uDD84", fontSize = 10.sp, fontWeight = FontWeight.W700)
    }
}

private val SearchTextFieldCornerRadius = 8.dp
