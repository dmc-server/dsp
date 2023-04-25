package dev.marius.dsp.discord

import dev.marius.dsp.GameChat
import dev.marius.dsp.Plugin
import dev.marius.dsp.Settings
import dev.marius.dsp.discord.listener.CommandListener
import dev.marius.dsp.discord.listener.GuildLeaveListener
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.bukkit.Bukkit
import javax.security.auth.login.LoginException

/*

Project: David Server Plugin
Author: Marius
Created: 19.11.2020, 19:35

*/
object Bot {
    private val BOT_TOKEN = if (Settings.beta) Settings.BETA_BOT_TOKEN else Settings.REGULAR_BOT_TOKEN
    @JvmStatic
    var jda: JDA? = null
        private set

    @JvmStatic
    fun startBot(
        plugin: Plugin, vararg events: ListenerAdapter
    ) {
        val builder = JDABuilder.createDefault(BOT_TOKEN)
        builder.setAutoReconnect(true)
        builder.setStatus(OnlineStatus.ONLINE)
        builder.setActivity(Activity.playing(Settings.BOT_PREFIX + "help"))
        builder.addEventListeners(CommandListener())
        builder.addEventListeners(GuildLeaveListener())
        events.iterator().forEach { event: ListenerAdapter -> builder.addEventListeners(event) }
        val gameChat = GameChat()
        builder.addEventListeners(gameChat)
        Bukkit.getServer().pluginManager.registerEvents(gameChat, plugin)
        try {
            jda = builder.build()
            /*Thread {
               if (jda!!.status == JDA.Status.CONNECTED) {
                    gameChat.initChannel()
                }
            }.start()*/
            println("[!] Bot-Invite URL: " + jda!!.getInviteUrl(Permission.ADMINISTRATOR))
        } catch (e: LoginException) {
            e.printStackTrace()
        }
    }

    fun stopBot() {
        jda!!.shutdownNow()
    }
}
