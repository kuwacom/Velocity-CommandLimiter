package dev.kuwa.commandLimiter

import com.moandjiezana.toml.Toml
import com.moandjiezana.toml.TomlWriter
import org.slf4j.Logger
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Path

class ConfigManager(
    private val configPath: Path,
    private val logger: Logger
) {

    private val tomlWriter: TomlWriter = TomlWriter()

    fun loadConfig(): PluginConfig {
        val configFile = configPath.toFile()
        val defaultConfig = PluginConfig()

        // 設定ファイルが存在しない場合、デフォルト設定を保存
        if (!configFile.exists()) {
            logger.info("Config file not found, creating default config.")
            saveConfig(defaultConfig)
            return defaultConfig
        }

        return try {
            logger.info("Loading config file...")
            FileReader(configFile).use { reader ->
                // TOMLデータを直接PluginConfigにマッピング
                Toml().read(reader).to(PluginConfig::class.java)
            }
        } catch (e: IOException) {
            logger.error("Failed to load config file. Using default values.", e)
            defaultConfig // デフォルト設定を返す
        } catch (e: Exception) {
            logger.error("Invalid config file format. Using default values.", e)
            defaultConfig
        }
    }

    fun saveConfig(config: PluginConfig) {
        try {
            FileWriter(configPath.toFile()).use { writer ->
                tomlWriter.write(config, writer) // PluginConfigを直接書き込み
                logger.info("Config file saved successfully.")
            }
        } catch (e: IOException) {
            logger.error("Failed to save config file.", e)
        }
    }
}