package dev.marius.util

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.jetbrains.annotations.Nullable
import java.io.File
import java.io.IOException
import java.nio.file.Files

/*

Project: David Server Plugin
Author: Marius
Created: 20.11.2020, 18:04

*/
class Config(private val file: File) {
    var config: FileConfiguration
        private set

    fun save() {
        try {
            config.save(file)
            config = YamlConfiguration.loadConfiguration(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    operator fun set(path: String, value: Any) {
        config[path] = value
        save()
    }

    operator fun get(path: String): @Nullable Any? {
        return config[path]
    }

    fun getString(path: String): String {
        return config.getString(path)!!
    }

    fun getInt(path: String): Int {
        return config.getInt(path)
    }

    fun getDouble(path: String): Double {
        return config.getDouble(path)
    }

    fun getBoolean(path: String): Boolean {
        return config.getBoolean(path)
    }

    fun exists(path: String): Boolean {
        return get(path) != null
    }

    init {
        if (!file.exists()) {
            try {
                Files.createFile(file.toPath())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        config = YamlConfiguration.loadConfiguration(file)
    }
}