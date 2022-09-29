package dev.sebastiano.ideshowcase.twitter

import com.twitter.clientlib.ApiClient
import com.twitter.clientlib.TwitterCredentialsBearer
import com.twitter.clientlib.api.TwitterApi
import com.twitter.clientlib.model.User
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.slf4j.LoggerFactory

internal interface TwitterRepository {

    suspend fun fetchUserInfo(credentials: Twitter.Credentials, username: String): User

    companion object {

        private val INSTANCE by lazy { TwitterApiV2Repository() }

        fun getInstance(): TwitterRepository = INSTANCE
    }
}

private class TwitterApiV2Repository : TwitterRepository {

    private val logger = LoggerFactory.getLogger(TwitterApiV2Repository::class.java)

    // DO NOT DO THIS, it's horrible, and it's not ok even for a sample project like this!
    private val userInfoCache = mutableMapOf<String, User>()

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
        .build()

    private fun twitterClient(credentials: Twitter.Credentials) =
        TwitterApi(
            ApiClient(httpClient).apply {
                setTwitterCredentials(TwitterCredentialsBearer(credentials.bearerToken))
            }
        )

    override suspend fun fetchUserInfo(credentials: Twitter.Credentials, username: String): User {
        if (userInfoCache.containsKey(username)) {
            logger.trace("User $username's data is present in the cache")
            return userInfoCache[username]!!
        }

        logger.debug("Fetching user $username's data...")
        val fields = setOf(
            "created_at",
            "description",
            "location",
            "profile_image_url",
            "public_metrics",
            "url",
            "verified",
            "entities",
            "protected"
        )
        val response = twitterClient(credentials).users()
            .findUserByUsername(username)
            .userFields(fields)
            .execute()

        return response.data?.also {
            logger.trace("Storing user $username's data in the cache:\n\n${response.data}")
            userInfoCache[username] = it
        }
            ?: error(
                "Unable to get info about user $username. " +
                        "Problems:\n${response.errors?.joinToString("\n") { " * $it" }} ?: [N/A]"
            )
    }
}
