package dev.kuwa.commandLimiter.commands

import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.command.SimpleCommand
import com.velocitypowered.api.command.SimpleCommand.Invocation
import dev.kuwa.commandLimiter.CommandLimiter
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import java.io.IOException

class ReloadCommand(
    private val cl: CommandLimiter
) : SimpleCommand {

    override fun execute(invocation: Invocation) {
        val commandSource: CommandSource = invocation.source()

        try {
            cl.loadConfig()
        } catch (e: IOException) {
            cl.logger.error("Failed to load configuration", e)
            commandSource.sendMessage(Component.text("Failed to load configuration!", NamedTextColor.RED))
        }

        commandSource.sendMessage(Component.text("Reload Config!", NamedTextColor.AQUA))
    }

    // コマンドを実行する権限があるかどうかを制御するメソッド
    override fun hasPermission(invocation: Invocation): Boolean {
        return invocation.source().hasPermission("${cl.commandPermissionNodeName}.reload")
    }
}