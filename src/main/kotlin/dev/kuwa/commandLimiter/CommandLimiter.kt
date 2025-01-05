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
import dev.kuwa.commandLimiter.commands.ReloadCommand
import org.slf4j.Logger
import java.io.IOException
import java.nio.file.Files
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
    val commandPermissionNodeName = "commandlimiter"
    private lateinit var proxy: ProxyServer

    private lateinit var configManager: ConfigManager
    private lateinit var config: PluginConfig

    @Inject
    fun protocolChanger(proxy: ProxyServer) {
        this.proxy = proxy
    }

    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent) {
        try {
            loadConfig()
        } catch (e: IOException) {
            logger.error("Failed to load configuration", e)
        }

        val commandManager = proxy.commandManager

        val reloadCommand = commandManager
            .metaBuilder("commandlimiter-reload")
            .aliases("cl-reload")
            .plugin(this)
            .build()

        commandManager.register(reloadCommand, ReloadCommand(this))
    }

    fun loadConfig() {
        if (!Files.exists(dataDirectory)) {
            Files.createDirectories(dataDirectory)
        }

        try {
            val configPath = dataDirectory.resolve("config.toml")
            configManager = ConfigManager(configPath, logger)
            config = configManager.loadConfig()
        } catch (e: IOException) {
            logger.error("Failed to initialize config manager.", e)
        }
    }

    // 表示処理
    @Subscribe
    fun onPlayerAvailableCommands(event: PlayerAvailableCommandsEvent) {
        if(!event.player.hasPermission("${commandPermissionNodeName}.admin")) {
            event.rootNode.children.toList().forEach{ node ->
                event.rootNode.removeChildByName(node.name)
            }
        }
    }

    @Subscribe
    fun onCommandExecute(event: CommandExecuteEvent) {
        if(!event.commandSource.hasPermission("${commandPermissionNodeName}.admin")) {
            event.result = CommandResult.denied()
        }
    }
}
