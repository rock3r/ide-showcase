package dev.sebastiano.ideshowcase.twitch

import com.github.twitch4j.helix.domain.User
import java.time.Instant

data class TwitchUser(
    val username: String,
    val displayName: String,
    val globalRole: GlobalRole?,
    val affiliationState: AffiliationState?,
    val channelDescription: String?,
    val views: Int,
    val createdAt: Instant,
    val images: Images
) {

    constructor(rawUser: User) : this(
        username = rawUser.login,
        displayName = rawUser.displayName,
        globalRole = GlobalRole.fromOrNull(rawUser.type),
        affiliationState = AffiliationState.fromOrNull(rawUser.broadcasterType),
        channelDescription = rawUser.description,
        views = rawUser.viewCount,
        createdAt = rawUser.createdAt,
        images = Images(rawUser.profileImageUrl, rawUser.offlineImageUrl)
    )

    enum class GlobalRole(val rawValue: String) {
        STAFF("staff"), ADMIN("admin"), GLOBAL_MOD("global_mod");

        companion object {

            fun fromOrNull(raw: String): GlobalRole? = values().find { it.rawValue == raw }
        }
    }

    enum class AffiliationState(val rawValue: String) {
        PARTNER("partner"), AFFILIATE("affiliate");

        companion object {

            fun fromOrNull(raw: String): AffiliationState? = values().find { it.rawValue == raw }
        }
    }

    data class Images(
        val profilePicUrl: String,
        val offlinePicUrl: String
    )
}
