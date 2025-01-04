package dev.kuwa.commandLimiter;

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.command.CommandExecuteEvent
import com.velocitypowered.api.event.command.CommandExecuteEvent.CommandResult
import com.velocitypowered.api.event.command.PlayerAvailableCommandsEvent
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import org.slf4j.Logger
import java.nio.file.Path

@Plugin(
    id = "command-limiter",
    name = "CommandLimiter",
    version = BuildConstants.VERSION,
    description = "他のpluginで制限できないコマンドを通常プレイヤーに表示と実行をさせなくさせるplugin",
    authors = ["kuwa"]
)
class CommandLimiter @Inject constructor(
    @DataDirectory private val dataDirectory: Path,
    val logger: Logger
) {
    private lateinit var proxy: ProxyServer

    @Inject
    fun protocolChanger(proxy: ProxyServer) {
        this.proxy = proxy
    }

    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent) {
    }

    // 表示処理
    @Subscribe
    fun onPlayerAvailableCommands(event: PlayerAvailableCommandsEvent) {
        if(!event.player.hasPermission("commandlimiter.admin")) {
            event.rootNode.children.toList().forEach{ node ->
                event.rootNode.removeChildByName(node.name)
            }
        }
    }

    @Subscribe
    fun onCommandExecute(event: CommandExecuteEvent) {
        if(!event.commandSource.hasPermission("commandlimiter.admin")) {
            event.result = CommandResult.denied()
        }
    }
}
