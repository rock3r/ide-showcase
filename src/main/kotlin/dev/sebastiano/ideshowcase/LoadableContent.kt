package dev.sebastiano.ideshowcase

sealed class LoadableContent {

    object Idle : LoadableContent()

    object Loading : LoadableContent()

    data class Error(val message: String, val retryAction: (() -> Unit)?) : LoadableContent()

    data class Content<T : Any>(val data: T) : LoadableContent()
}
