package dev.kuwa.commandLimiter

import dev.kuwa.commandLimiter.models.Permissions

data class PluginConfig(
    var allAllowed: String = "admin",
    var limitNoPermissionsPlayer: Boolean = true,
    var permissions: Array<Permissions> = arrayOf(Permissions(
            "default",
            true,
            // Minecraft OPlevel 0 default commands
            // https://minecraft.fandom.com/wiki/Commands
            arrayOf(
                "help", "list", "me", "msg", "teammsg", "tell", "tm", "trigger", "w", "random"
            )
        )
    )
)