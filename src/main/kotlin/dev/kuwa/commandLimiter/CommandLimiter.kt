package dev.kuwa.commandLimiter;

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import dev.kuwa.commandLimiter.commands.ReloadCommand
import dev.kuwa.commandLimiter.listeners.CommandEventListener
import org.slf4j.Logger
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

@Plugin(
    id = "command-limiter",
    name = "CommandLimiter",
    version = BuildConstants.VERSION,
    description = "プロキシやプロキシ下にある全てのコマンドを操作して表示と実行を制御する強力なPlugin",
    authors = ["kuwa"]
)
class CommandLimiter @Inject constructor(
    @DataDirectory private val dataDirectory: Path,
    val logger: Logger
) {
    val commandPermissionNodeName = "commandlimiter"
    private lateinit var proxyServer: ProxyServer

    lateinit var configManager: ConfigManager
    lateinit var config: PluginConfig

    @Inject
    fun protocolChanger(proxyServer: ProxyServer) {
        this.proxyServer = proxyServer
    }

    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent) {
        try {
            loadConfig()
        } catch (e: IOException) {
            logger.error("Failed to load configuration", e)
        }

        // イベントリスナー登録
        proxyServer.eventManager.register(this, CommandEventListener(this))

        // コマンド登録
        val commandManager = proxyServer.commandManager

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
}
