package dev.sebastiano.ideshowcase.twitch

import kotlinx.coroutines.CoroutineScope
import java.io.File
import java.util.Properties

internal class Twitch(
    coroutineScope: CoroutineScope
) : CoroutineScope by coroutineScope {

    private val properties = loadPropertiesFile()

    private val repository = TwitchRepository.getInstance()

    suspend fun fetchUserInfo(username: String): TwitchUser {
        val credentials = obtainCredentials()
        return repository.fetchUserInfo(credentials, username)
    }

    private fun obtainCredentials(): Credentials {
        val clientId = checkNotNull(properties.getProperty("twitch_client_id")) { "Missing the twitch_client_id in local.properties" }
        return Credentials(clientId)
    }

    private fun loadPropertiesFile(): Properties {
        // TODO error handling
        val propsFile = File("local.properties")
        check(propsFile.exists()) { "The local.properties file is missing" }
        val properties = Properties()
        propsFile.bufferedReader().use {
            properties.load(it)
        }
        return properties
    }

    data class Credentials(
        val clientId: String
    )
}
