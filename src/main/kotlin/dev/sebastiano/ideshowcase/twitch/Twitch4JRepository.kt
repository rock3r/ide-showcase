package dev.sebastiano.ideshowcase.twitch

import com.github.twitch4j.TwitchClientBuilder

internal interface TwitchRepository {

    suspend fun fetchUserInfo(credentials: Twitch.Credentials, username: String): TwitchUser

    companion object {

        private val INSTANCE by lazy { Twitch4JRepository() }

        fun getInstance(): TwitchRepository = INSTANCE
    }
}

private class Twitch4JRepository : TwitchRepository {

    // DO NOT DO THIS, it's horrible, and it's not ok even for a sample project like this!
    private val userInfoCache = mutableMapOf<String, TwitchUser>()

    private fun twitchClient(credentials: Twitch.Credentials) = TwitchClientBuilder.builder()
        .withClientId(credentials.clientId)
        .withEnableHelix(true)
        .build()

    override suspend fun fetchUserInfo(credentials: Twitch.Credentials, username: String): TwitchUser {
        val response = twitchClient(credentials).helix.getUsers(null, null, listOf(username))
            .execute() ?: error("Unable to fetch users")

        val user = response.users.firstOrNull() ?: error("No users found")
        return TwitchUser(user)
    }
}
