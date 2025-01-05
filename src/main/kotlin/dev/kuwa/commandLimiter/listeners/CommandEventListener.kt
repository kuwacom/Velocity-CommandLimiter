package dev.kuwa.commandLimiter.listeners

import com.mojang.brigadier.tree.CommandNode
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.command.CommandExecuteEvent
import com.velocitypowered.api.event.command.PlayerAvailableCommandsEvent
import dev.kuwa.commandLimiter.CommandLimiter
import java.sql.Array

class CommandEventListener(
    private val cl: CommandLimiter
) {
    // 表示処理
    @Subscribe
    fun onPlayerAvailableCommands(event: PlayerAvailableCommandsEvent) {
        // 権限持ちはスキップ
        if (event.player.hasPermission("${cl.commandPermissionNodeName}.${cl.config.allAllowed}")) return

        var hasPermission = false
        for (permission in cl.config.permissions) {
            if (event.player.hasPermission("${cl.commandPermissionNodeName}.${permission.nodeName}")) {
                hasPermission = true
                var removeCommands: List<CommandNode<out Any>>
                if (permission.whiteList) { // ホワイトリストの時は書かれているもののみ有効化させる
                    removeCommands = event.rootNode.children.toList().filter { node ->
                        permission.commands.none { pattern ->
                            // ワイルドカードを正規表現に変換して一致をチェック
                            val regexPattern = pattern.replace("*", ".*")
                            Regex(regexPattern).matches(node.name)
                        }
                    }
                } else {
                    removeCommands = event.rootNode.children.toList().filter { node ->
                        permission.commands.any { pattern ->
                            // ワイルドカードを正規表現に変換して一致をチェック
                            val regexPattern = pattern.replace("*", ".*")
                            Regex(regexPattern).matches(node.name)
                        }
                    }
                }
                removeCommands.forEach { removeCommand ->
                    event.rootNode.removeChildByName(removeCommand.name)
                }
            }
        }

        // デフォルトの権限のユーザーの処理
        if (!hasPermission && cl.config.limitNoPermissionsPlayer) {
            event.rootNode.children.toList().forEach{ node ->
                event.rootNode.removeChildByName(node.name)
            }
        }
    }

    @Subscribe
    fun onCommandExecute(event: CommandExecuteEvent) {
        // 権限持ちはスキップ
        if (event.commandSource.hasPermission("${cl.commandPermissionNodeName}.${cl.config.allAllowed}")) return

        var hasPermission = false
        for (permission in cl.config.permissions) {
            if (event.commandSource.hasPermission("${cl.commandPermissionNodeName}.${permission.nodeName}")) {
                hasPermission = true
                val hasCommandPermission = if (permission.whiteList) { // ホワイトリストの時は書かれているもののみ有効化させる
                    permission.commands.none { pattern ->
                        // ワイルドカードを正規表現に変換して一致をチェック
                        val regexPattern = pattern.replace("*", ".*")
                        Regex(regexPattern).matches(event.command)
                    }
                } else {
                    permission.commands.any { pattern ->
                        // ワイルドカードを正規表現に変換して一致をチェック
                        val regexPattern = pattern.replace("*", ".*")
                        Regex(regexPattern).matches(event.command)
                    }
                }
                if (hasCommandPermission)
                    event.result = CommandExecuteEvent.CommandResult.denied()
            }
        }

        // デフォルトの権限のユーザーの処理
        if (!hasPermission && cl.config.limitNoPermissionsPlayer) {
            event.result = CommandExecuteEvent.CommandResult.denied()
        }
    }
}