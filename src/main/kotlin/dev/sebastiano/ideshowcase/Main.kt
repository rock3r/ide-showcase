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
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.LocalTextStyle
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.twitter.clientlib.model.CashtagEntity
import com.twitter.clientlib.model.FullTextEntities
import com.twitter.clientlib.model.HashtagEntity
import com.twitter.clientlib.model.MentionEntity
import com.twitter.clientlib.model.UrlEntity
import com.twitter.twittertext.Extractor
import com.twitter.twittertext.Extractor.Entity
import dev.sebastiano.ideshowcase.twitter.TwitterUserViewModel
import dev.sebastiano.ideshowcase.twitter.asContentOrNull
import io.chozzle.composemacostheme.MacTheme
import io.chozzle.composemacostheme.modifiedofficial.MacOutlinedTextField
import io.kamel.image.KamelImage
import io.kamel.image.lazyPainterResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.awt.Desktop
import java.net.URI
import java.net.URL
import java.time.format.TextStyle
import java.util.*

private val logger = LoggerFactory.getLogger("SampleLog")

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

@OptIn(ExperimentalComposeUiApi::class)
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
                    Modifier.fillMaxSize().padding(16.dp),
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

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp)) {
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
                            contentDescription = "$username's profile picture",
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
                                        Modifier.size(16.dp).rotate(currentRotation).padding(start = 16.dp)
                                            .alpha(ContentAlpha.disabled)
                                    )
                                }
                            },
                            onFailure = { error ->
                                logger.error(
                                    "Unable to load profile pic for $username at ${user.profileImageUrl.toOriginalSizePicture()}",
                                    error
                                )
                                Box(Modifier.size(72.dp).clip(CircleShape).background(Color(0xFFCECECE))) {
                                    Text(
                                        ":(",
                                        Modifier.align(Alignment.Center),
                                        fontSize = MaterialTheme.typography.h4.fontSize,
                                        color = Color.Red
                                    )
                                }
                            }
                        )
                    }

                    Spacer(Modifier.width(16.dp))

                    val displayedName = user?.name ?: username
                    Text(displayedName, style = MaterialTheme.typography.h3)
                }

                if (user != null) {
                    Row(Modifier.padding(start = 104.dp, end = 24.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("@$username", color = secondaryColor)

                        if (user.verified == true) {
                            Spacer(Modifier.width(4.dp))
                            Image(
                                painterResource("verified.svg"),
                                contentDescription = "Verified",
                                Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
                val scope = rememberCoroutineScope()

                if (user != null) {
                    if (!user.description.isNullOrBlank()) {
                        val description = user.description!!.trim().injectEntities(user.entities?.description)
                        logger.debug("Description:\n$description")

                        AnnotatedText(description, Modifier.padding(start = 104.dp, end = 24.dp)) { entity ->
                            val url = when (entity) {
                                is ParsedEntity.Hashtag -> "https://twitter.com/hashtag/${entity.text}"
                                is ParsedEntity.Mention -> "https://twitter.com/${entity.text}"
                                is ParsedEntity.Url -> entity.expandedUrl.toString()
                            }
                            scope.launch {
                                withContext(Dispatchers.IO) {
                                    Desktop.getDesktop().browse(URI.create(url))
                                }
                            }
                        }
                        Spacer(Modifier.height(24.dp))
                    }

                    if (user.publicMetrics != null) {
                        val publicMetrics = user.publicMetrics!!
                        Row(
                            Modifier.padding(start = 104.dp, end = 24.dp),
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
                        Modifier.padding(start = 104.dp, end = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (user.location != null) {
                            Image(
                                painterResource("location.svg"),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(user.location!!, color = secondaryColor)
                        }

                        val userUrl = user.url.takeIf { !it.isNullOrBlank() }?.let { userUrl ->
                            val parsedUrl = URL(userUrl)
                            user.entities?.url?.urls?.find { it.url == parsedUrl }
                        }

                        if (userUrl != null) {
                            Spacer(Modifier.width(16.dp))
                            Image(
                                painterResource("url.svg"),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = userUrl.displayUrl ?: userUrl.expandedUrl?.toString() ?: userUrl.url.toString(),
                                color = Color(0xFF1D9BF0),
                                modifier = Modifier
                                    .pointerHoverIcon(PointerIconDefaults.Hand)
                                    .clickable {
                                        Desktop.getDesktop().browse((userUrl.expandedUrl ?: userUrl.url).toURI())
                                    }
                            )
                        }

                        if (user.createdAt != null) {
                            Spacer(Modifier.width(16.dp))
                            Image(
                                painterResource("joined.svg"),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
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

private fun URL?.toOriginalSizePicture(): String? {
    if (this == null) return null
    return toString().replace("_normal.", ".")
}

private fun String.injectEntities(entities: FullTextEntities?): AnnotatedString {
    if (entities == null) return AnnotatedString(this)

    data class UrlEntityData(val displayUrl: String?, val expandedUrl: URL?)

    val urlEntitiesByTcoUrl = entities.urls
        ?.associate { it.url to UrlEntityData(it.displayUrl, it.expandedUrl) }
        .orEmpty()

    val allEntities = Extractor().extractEntitiesWithIndices(this)
        .map {
            if (it.type == Entity.Type.URL && it.value.isNotBlank()) {
                val url = URL(it.value)
                it.apply {
                    displayURL = urlEntitiesByTcoUrl[url]?.displayUrl
                    expandedURL = urlEntitiesByTcoUrl[url]?.expandedUrl.toString()
                }
            } else {
                it
            }
        }
        .mapNotNull { ParsedEntity.from(it) }
        .sortedBy { it.range.first }

    logger.debug("Entities:\n${allEntities.joinToString("\n") { "  - $it" }}")

    if (allEntities.isEmpty()) return AnnotatedString(this)

    val entityStyle = SpanStyle(color = Color(0xFF1D9BF0))

    return buildAnnotatedString {
        var lastPosition = 0
        allEntities.forEach { entity ->
            if (entity.range.first >= lastPosition) {
                val textBeforeEntityRange = lastPosition until entity.range.first
                append(this@injectEntities.substring(textBeforeEntityRange))
                lastPosition += textBeforeEntityRange.last - textBeforeEntityRange.first
            }

            entity.appendTo(this, entityStyle)

            lastPosition += entity.range.last - entity.range.first + 1
        }
    }
}

@Composable
private fun AnnotatedText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    style: androidx.compose.ui.text.TextStyle = LocalTextStyle.current,
    onEntityAnnotationClick: (ParsedEntity) -> Unit
) {
    var textLayoutResult: TextLayoutResult? by remember { mutableStateOf(null) }

    Text(
        text = text,
        modifier = modifier.pointerInput(onEntityAnnotationClick) {
            detectTapGestures { offset ->
                val clickedPosition = textLayoutResult?.getOffsetForPosition(offset) ?: return@detectTapGestures
                val matchingAnnotations = text.getStringAnnotations(clickedPosition, clickedPosition)
                    .filter { ParsedEntity.isParsedEntityAnnotation(it.tag) }

                val annotation = matchingAnnotations.firstOrNull() ?: return@detectTapGestures
                val range = annotation.start..annotation.end
                val entity = when (annotation.tag) {
                    ParsedEntity.Hashtag::class.java.simpleName -> ParsedEntity.Hashtag(range, annotation.item)
                    ParsedEntity.Mention::class.java.simpleName -> ParsedEntity.Mention(range, annotation.item)
                    ParsedEntity.Url::class.java.simpleName -> ParsedEntity.Url(
                        range,
                        annotation.item,
                        URL(annotation.item)
                    )

                    else -> error("Unhandled entity tag: ${annotation.tag} (item: ${annotation.item})")
                }
                onEntityAnnotationClick(entity)
            }
        },
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        onTextLayout = { result: TextLayoutResult -> textLayoutResult = result },
        style = style
    )
}

sealed class ParsedEntity {

    abstract val range: IntRange
    abstract val text: String

    fun appendTo(builder: AnnotatedString.Builder, style: SpanStyle) = builder.apply {
        val styleId = pushStyle(style)
        doAppend(builder)
        pop(styleId)
    }

    protected abstract fun doAppend(builder: AnnotatedString.Builder)

    data class Hashtag(override val range: IntRange, override val text: String) : ParsedEntity() {

        override fun doAppend(builder: AnnotatedString.Builder) {
            builder.apply {
                append(text)
                addStringAnnotation(Hashtag::class.java.simpleName, text, range.first, range.last)
            }
        }
    }

    data class Mention(override val range: IntRange, override val text: String) : ParsedEntity() {

        override fun doAppend(builder: AnnotatedString.Builder) {
            builder.apply {
                append(text)
                addStringAnnotation(Mention::class.java.simpleName, text, range.first, range.last)
            }
        }
    }

    data class Url(override val range: IntRange, override val text: String, val expandedUrl: URL) : ParsedEntity() {

        override fun doAppend(builder: AnnotatedString.Builder) {
            builder.apply {
                append(text)
                addStringAnnotation(Url::class.java.simpleName, expandedUrl.toString(), range.first, range.last)
            }
        }
    }

    companion object {

        fun parseOrNull(entity: Any): ParsedEntity? =
            when (entity) {
                is HashtagEntity -> Hashtag(entity.start..entity.end, "#${entity.tag}")
                is CashtagEntity -> Hashtag(entity.start..entity.end, "#${entity.tag}")
                is MentionEntity -> Mention(entity.start..entity.end, "@${entity.username}")
                is UrlEntity -> Url(
                    entity.start..entity.end,
                    entity.displayUrl ?: entity.url.toString(),
                    entity.expandedUrl ?: entity.url
                )

                else -> null
            }

        fun isParsedEntityAnnotation(tag: String) =
            tag == Hashtag::class.java.simpleName || tag == Mention::class.java.simpleName || tag == Url::class.java.simpleName

        fun from(entity: Entity?): ParsedEntity? =
            when (entity?.type) {
                Entity.Type.HASHTAG -> Hashtag(entity.start..entity.end, "#${entity.value}")
                Entity.Type.CASHTAG -> Hashtag(entity.start..entity.end, "#${entity.value}")
                Entity.Type.MENTION -> Mention(entity.start..entity.end, "@${entity.value}")
                Entity.Type.URL -> Url(
                    entity.start..entity.end,
                    entity.displayURL,
                    URL(entity.expandedURL)
                )

                else -> null
            }
    }
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
