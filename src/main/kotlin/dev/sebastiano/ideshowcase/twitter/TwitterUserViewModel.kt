package dev.sebastiano.ideshowcase.twitter

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.twitter.clientlib.model.User
import dev.sebastiano.ideshowcase.LoadableContent
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext

internal class TwitterUserViewModel(coroutineScope: CoroutineScope) : CoroutineScope by coroutineScope {

    private val _state: MutableValue<LoadableContent> = MutableValue(LoadableContent.Idle)
    val state: Value<LoadableContent> = _state

    private val twitter = Twitter(this + CoroutineName("twitter"))

    fun loadUser(username: String) {
        launch { loadUserData(username) }
    }

    private suspend fun loadUserData(username: String) {
        val content = state.asContentOrNull<StateModel>()
        if (username == content?.data?.username) return

        coroutineScope {
            launch(Dispatchers.Main) { _state.value = LoadableContent.Loading }

            try {
                delay(250)
                val user = withContext(Dispatchers.IO) { twitter.fetchUserInfo(username) }
                launch(Dispatchers.Main) { _state.value = LoadableContent.Content(StateModel(username, user)) }
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    _state.value = LoadableContent.Error("Unable to load Twitter user data. ${e.message}") {
                        launch { loadUserData(username) }
                    }
                }
            }
        }
    }

    fun clearUser() {
        _state.value = LoadableContent.Idle
    }

    data class StateModel(
        val username: String,
        val user: User?
    )
}

internal inline fun <reified T : Any> Value<LoadableContent>.asContentOrNull(): LoadableContent.Content<T>? {
    if (value !is LoadableContent.Content<*>) return null
    val content = (value as LoadableContent.Content<*>).data
    if (content !is T) return null
    return LoadableContent.Content(content)
}

internal inline fun <reified T : Any> LoadableContent.asContentOrNull(): LoadableContent.Content<T>? {
    if (this !is LoadableContent.Content<*>) return null
    val content = this.data
    if (content !is T) return null
    return LoadableContent.Content(content)
}
