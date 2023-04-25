package dev.marius.dsp

import com.earth2me.essentials.IEssentials
import com.earth2me.essentials.spawn.IEssentialsSpawn
import dev.marius.dsp.accounts.AccountManager
import dev.marius.dsp.discord.Bot
import dev.marius.dsp.whitelist.Whitelist
import dev.marius.util.Compare
import net.dv8tion.jda.api.JDA
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException
import java.nio.file.Files

/*

Project: David Server Plugin
Author: Marius
Created: 19.11.2020, 19:34

*/
class Plugin : JavaPlugin() {
    private var task = 0
    private var storageTask = 0

    override fun onEnable() {
        try {
            /*if (!server.pluginManager.isPluginEnabled("Essentials") || !server.pluginManager.isPluginEnabled("EssentialsSpawn")) {
                println("§4[Server] This server needs EssentialsX and EssentialsXSpawn installed!")
                server.pluginManager.disablePlugin(this)
                return
            }*/

            essentials = server.pluginManager.getPlugin("Essentials") as IEssentials
            essentialsSpawn = server.pluginManager.getPlugin("EssentialsSpawn") as IEssentialsSpawn

            accountManager = AccountManager()
            accountManager!!.save()
            server.pluginManager.registerEvents(Whitelist(accountManager!!), this)
            Login(this)
            task = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, Ban().also { ban = it }, 0, 200)
            Bot.startBot(this)
            storageTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, Storage(), 0, 20 * 5)
            println("§6[Server] Plugin started")
            println(if (Settings.beta) "§cBETA MODE IS ACTIVE" else "§aBETA MOD IS NOT ACTIVE")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDisable() {
        Bot.stopBot()
        Bukkit.getScheduler().cancelTask(task)
        Bukkit.getScheduler().cancelTask(storageTask)
        println("§6[Server] Plugin stopped")
    }

    companion object {
        @JvmStatic
        var accountManager: AccountManager? = null
            private set
        get() {
            field!!.update()
            return field
        }
        @JvmStatic
        var ban: Ban? = null
            private set
        @JvmStatic
        val configFolder: File
            get() {
                val dir = File("ServerConfig//")
                if (!dir.exists()) {
                    try {
                        Files.createDirectory(dir.toPath())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                return dir
            }
        @JvmStatic
        val jDA: JDA?
            get() = Bot.jda
        @JvmStatic
        val compare = Compare()
        @JvmStatic
        var essentials: IEssentials? = null
            private set
        @JvmStatic
        var essentialsSpawn: IEssentialsSpawn? = null
            private set
    }
}