package dev.sebastiano.ideshowcase.twitter

import com.twitter.clientlib.model.User
import kotlinx.coroutines.CoroutineScope
import java.io.File
import java.util.Properties

internal class Twitter(
    coroutineScope: CoroutineScope
) : CoroutineScope by coroutineScope {

    private val properties = loadPropertiesFile()

    private val repository = TwitterRepository.getInstance()

    suspend fun fetchUserInfo(username: String): User {
        val credentials = obtainCredentials()
        return repository.fetchUserInfo(credentials, username)
    }

    private fun obtainCredentials(): Credentials {
        val apiKey = checkNotNull(properties.getProperty("twitter_api_key")) { "Missing the twitter_api_key in local.properties" }
        val apiKeySecret = checkNotNull(properties.getProperty("twitter_api_key_secret")) { "Missing the twitter_api_key_secret in local.properties" }
        val bearerToken = checkNotNull(properties.getProperty("twitter_bearer_token")) { "Missing the twitter_bearer_token in local.properties" }
        return Credentials(apiKey, apiKeySecret, bearerToken)
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
        val apiKey: String,
        val apiKeySecret: String,
        val bearerToken: String,
    )
}
