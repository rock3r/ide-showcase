package dev.sebastiano.ideshowcase.twitch

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import dev.sebastiano.ideshowcase.LoadableContent
import kotlinx.coroutines.CoroutineScope

internal class TwitchViewModel(
    initialUsername: String = "codewiththeitalians",
    val coroutineScope: CoroutineScope
) {

    private val _state: MutableValue<LoadableContent> = MutableValue(LoadableContent.Idle)
    val state: Value<LoadableContent> = _state

    var username: String = initialUsername
        set(value) {
        }
}
