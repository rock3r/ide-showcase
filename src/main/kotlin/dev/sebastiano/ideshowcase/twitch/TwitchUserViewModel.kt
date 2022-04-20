package dev.sebastiano.ideshowcase.twitch

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import dev.sebastiano.ideshowcase.LoadableContent
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import java.io.IOException

internal class TwitchUserViewModel(
    initialUsername: String = "codewiththeitalians",
    coroutineScope: CoroutineScope
) : CoroutineScope by coroutineScope {

    private val _state: MutableValue<LoadableContent> = MutableValue(LoadableContent.Idle)
    val state: Value<LoadableContent> = _state

    private val twitch = Twitch(this + Dispatchers.IO + CoroutineName("twitch"))

    var username: String = initialUsername
        set(value) {
            launch { loadUserData(value) }
        }

    private suspend fun loadUserData(username: String) {
        if (username == this.username) return

        coroutineScope {
            _state.value = LoadableContent.Loading

            try {
                _state.value = LoadableContent.Content(twitch.fetchUserInfo(username))
            } catch (e: IOException) {
                _state.value = LoadableContent.Error("Unable to load Twitch user data. ${e.message}") {
                    launch { loadUserData(username) }
                }
            }
        }
    }
}
