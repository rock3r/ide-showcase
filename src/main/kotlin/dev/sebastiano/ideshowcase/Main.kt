package dev.sebastiano.ideshowcase

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIconDefaults
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import dev.sebastiano.ideshowcase.twitter.TwitterUserViewModel
import dev.sebastiano.ideshowcase.twitter.asContentOrNull
import io.chozzle.composemacostheme.MacTheme
import io.chozzle.composemacostheme.modifiedofficial.MacOutlinedTextField
import io.kamel.image.KamelImage
import io.kamel.image.lazyPainterResource
import kotlinx.coroutines.Dispatchers
import java.net.URI
import java.time.format.TextStyle
import java.util.Locale

fun main() {
    application(exitProcessOnExit = true) {
        Window(
            title = "IDE showcase",
            icon = painterResource("intellij.svg"),
            onCloseRequest = { exitApplication() }
        ) {
            MaterialTheme {
                MacTheme {
                    val scope = rememberCoroutineScope { Dispatchers.Default }
                    val twitterUserViewModel = remember {
                        TwitterUserViewModel(coroutineScope = scope)
                    }
                    MainWindowContent(twitterUserViewModel, modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

@Preview
@Composable
internal fun MainWindowContent(viewModel: TwitterUserViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.state.subscribeAsState()
    var username by remember { mutableStateOf("codewiththeita") }

    LaunchedEffect(username) {
        if (username.isNotBlank()) {
            viewModel.loadUser(username.trim())
        } else {
            viewModel.clearUser()
        }
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth().height(51.dp).padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Text("Lookup Twitter user:")

            Spacer(Modifier.width(16.dp))

            MacOutlinedTextField(
                username,
                onValueChange = { username = it },
                modifier = Modifier.size(width = 200.dp, height = 26.dp),
                leadingIcon = { Text("􀊫") },
                trailingIcon = {
                    if (username.isNotBlank()) {
                        CloseIconButton(onClick = { username = "" })
                    }
                },
                placeholder = { Text("Search") },
                singleLine = true,
                cornerRadius = SearchTextFieldCornerRadius
            )
        }

        Box(Modifier.height(1.dp).fillMaxWidth().background(Color(0xFFCECACC)))

        Spacer(Modifier.height(16.dp))

        when (state) {
            is LoadableContent.Idle -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nothing to see here", color = LocalContentColor.current.copy(alpha = ContentAlpha.disabled))
                }
            }
            is LoadableContent.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    val transition = rememberInfiniteTransition()

                    val SnapEasing = Easing { 1f }
                    val currentRotation by transition.animateFloat(
                        initialValue = 0f,
                        targetValue = 360f,
                        animationSpec = infiniteRepeatable(
                            animation = keyframes {
                                durationMillis = 1200

                                0f at 0 with SnapEasing
                                30f at 100 with SnapEasing
                                60f at 200 with SnapEasing
                                90f at 300 with SnapEasing
                                120f at 400 with SnapEasing
                                150f at 500 with SnapEasing
                                180f at 600 with SnapEasing
                                210f at 700 with SnapEasing
                                240f at 800 with SnapEasing
                                270f at 900 with SnapEasing
                                300f at 1000 with SnapEasing
                                330f at 1100 with SnapEasing
                            }
                        )
                    )
                    Image(
                        painterResource("loading-spinner.png"),
                        contentDescription = null,
                        Modifier.size(48.dp).rotate(currentRotation).alpha(ContentAlpha.disabled)
                    )
                }
            }
            is LoadableContent.Error -> {
                Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Something went wrong :(", color = LocalContentColor.current.copy(alpha = ContentAlpha.disabled))

                    val error = state as LoadableContent.Error
                    if (error.retryAction != null) {
                        TextButton(error.retryAction) {
                            Text("Retry")
                        }
                    }

                    Text(
                        text = error.message,
                        color = LocalContentColor.current.copy(alpha = ContentAlpha.disabled),
                        fontStyle = FontStyle.Italic,
                        fontSize = MaterialTheme.typography.caption.fontSize,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center
                    )
                }
            }
            is LoadableContent.Content<*> -> {
                val (_, user) = state.asContentOrNull<TwitterUserViewModel.StateModel>()!!.data

                val secondaryColor = Color(0xFF536571)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (user == null) {
                        Box(Modifier.size(72.dp).clip(CircleShape).background(Color(0xFFCECECE))) {
                            val transition = rememberInfiniteTransition()

                            val SnapEasing = Easing { 1f }
                            val currentRotation by transition.animateFloat(
                                initialValue = 0f,
                                targetValue = 360f,
                                animationSpec = infiniteRepeatable(
                                    animation = keyframes {
                                        durationMillis = 1200

                                        0f at 0 with SnapEasing
                                        30f at 100 with SnapEasing
                                        60f at 200 with SnapEasing
                                        90f at 300 with SnapEasing
                                        120f at 400 with SnapEasing
                                        150f at 500 with SnapEasing
                                        180f at 600 with SnapEasing
                                        210f at 700 with SnapEasing
                                        240f at 800 with SnapEasing
                                        270f at 900 with SnapEasing
                                        300f at 1000 with SnapEasing
                                        330f at 1100 with SnapEasing
                                    }
                                )
                            )
                            Image(
                                painterResource("loading-spinner.png"),
                                contentDescription = null,
                                Modifier.size(16.dp).rotate(currentRotation).padding(start = 16.dp).alpha(ContentAlpha.disabled)
                            )
                        }
                    } else {
                        KamelImage(
                            lazyPainterResource(user.profileImageUrl.toOriginalSizePicture() ?: ""),
                            contentDescription = null,
                            modifier = Modifier.size(72.dp).clip(CircleShape).background(Color(0xFFCECECE)),
                            onLoading = {
                                Box(Modifier.size(72.dp).clip(CircleShape).background(Color(0xFFCECECE))) {
                                    val transition = rememberInfiniteTransition()

                                    val SnapEasing = Easing { 1f }
                                    val currentRotation by transition.animateFloat(
                                        initialValue = 0f,
                                        targetValue = 360f,
                                        animationSpec = infiniteRepeatable(
                                            animation = keyframes {
                                                durationMillis = 1200

                                                0f at 0 with SnapEasing
                                                30f at 100 with SnapEasing
                                                60f at 200 with SnapEasing
                                                90f at 300 with SnapEasing
                                                120f at 400 with SnapEasing
                                                150f at 500 with SnapEasing
                                                180f at 600 with SnapEasing
                                                210f at 700 with SnapEasing
                                                240f at 800 with SnapEasing
                                                270f at 900 with SnapEasing
                                                300f at 1000 with SnapEasing
                                                330f at 1100 with SnapEasing
                                            }
                                        )
                                    )
                                    Image(
                                        painterResource("loading-spinner.png"),
                                        contentDescription = null,
                                        Modifier.size(16.dp).rotate(currentRotation).padding(start = 16.dp).alpha(ContentAlpha.disabled)
                                    )
                                }
                            },
                            onFailure = {
                                Box(Modifier.size(72.dp).clip(CircleShape).background(Color(0xFFCECECE))) {
                                    Text(":(", Modifier.align(Alignment.Center), fontSize = MaterialTheme.typography.h4.fontSize, color = Color.Red)
                                }
                            }
                        )
                    }

                    Spacer(Modifier.width(16.dp))

                    val displayedName = user?.name ?: username
                    Text(displayedName, style = MaterialTheme.typography.h3)
                }

                if (user != null) {
                    Row(Modifier.padding(start = 88.dp, end = 24.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("@$username", color = secondaryColor)

                        if (user.verified == true) {
                            Spacer(Modifier.width(4.dp))
                            Image(painterResource("verified.svg"), contentDescription = "Verified", Modifier.size(16.dp))
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                if (user != null) {
                    if (!user.description.isNullOrBlank()) {
                        // TODO apply entities from the entities field
                        Text(user.description!!.trim(), Modifier.padding(start = 88.dp, end = 24.dp))
                        Spacer(Modifier.height(24.dp))
                    }

                    if (user.publicMetrics != null) {
                        val publicMetrics = user.publicMetrics!!
                        Row(
                            Modifier.padding(start = 88.dp, end = 24.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                buildAnnotatedString {
                                    pushStyle(SpanStyle(fontWeight = FontWeight.SemiBold))
                                    append(publicMetrics.followingCount.toString())
                                    pop()
                                    pushStyle(SpanStyle(color = secondaryColor))
                                    append(" following")
                                }
                            )

                            Text(
                                buildAnnotatedString {
                                    pushStyle(SpanStyle(fontWeight = FontWeight.SemiBold))
                                    append(publicMetrics.followersCount.toString())
                                    pop()
                                    pushStyle(SpanStyle(color = secondaryColor))
                                    append(" followers")
                                }
                            )
                        }

                        Spacer(Modifier.height(8.dp))
                    }

                    Row(
                        Modifier.padding(start = 88.dp, end = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (user.location != null) {
                            Image(painterResource("location.svg"), contentDescription = null, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(user.location!!, color = secondaryColor)
                            Spacer(Modifier.width(16.dp))
                        }

                        if (user.url != null) {
                            Image(painterResource("url.svg"), contentDescription = null, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(user.entities?.url?.urls?.firstOrNull()?.displayUrl ?: user.url!!, color = secondaryColor)
                            Spacer(Modifier.width(16.dp))
                        }

                        if (user.createdAt != null) {
                            Image(painterResource("joined.svg"), contentDescription = null, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(4.dp))
                            val createdAt = user.createdAt!!
                            Text(
                                "Joined ${createdAt.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${createdAt.year}",
                                color = secondaryColor
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun URI?.toOriginalSizePicture(): String? {
    if (this == null) return null
    return toString().replace("_normal.", ".")
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
