package dev.kuwa.commandLimiter.listeners

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.command.CommandExecuteEvent
import com.velocitypowered.api.event.command.PlayerAvailableCommandsEvent
import dev.kuwa.commandLimiter.CommandLimiter

class CommandEventListener(
    private val cl: CommandLimiter
) {
    // 表示処理
    @Subscribe
    fun onPlayerAvailableCommands(event: PlayerAvailableCommandsEvent) {
        if(!event.player.hasPermission("${cl.commandPermissionNodeName}.admin")) {
            event.rootNode.children.toList().forEach{ node ->
                event.rootNode.removeChildByName(node.name)
            }
        }
    }

    @Subscribe
    fun onCommandExecute(event: CommandExecuteEvent) {
        if(!event.commandSource.hasPermission("${cl.commandPermissionNodeName}.admin")) {
            event.result = CommandExecuteEvent.CommandResult.denied()
        }
    }
}