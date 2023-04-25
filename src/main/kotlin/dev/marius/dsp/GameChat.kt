package dev.marius.dsp

import dev.marius.dsp.discord.Bot
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import java.util.*
import javax.annotation.Nonnull

class GameChat : ListenerAdapter(), Listener {

    private fun sendToDiscord(message: String, actor: Actor) {
        //val player = Bukkit.getServer().getPlayer(actor.get())
        //player?.sendMessage("called function [side=#${if (actor.side) "member" else "mc"}")
        if (!actor.side) {
            Bot.jda?.getGuildById(Settings.GUILD_ID)?.getTextChannelById(Settings.GAME_CHAT_ID)?.sendMessage("<${actor.get()}> $message")?.queue()
        }
    }
    
    private fun sendToMinecraft(message: String, actor: Actor) {
        if (actor.side)
            Bukkit.getServer().onlinePlayers.forEach { player : Player -> player.sendMessage("<${actor.get()}> $message") }
    }

    override fun onGuildMessageReceived(@Nonnull event: GuildMessageReceivedEvent) {
        if (event.author.isBot) return
        if (event.channel.id != Settings.GAME_CHAT_ID) return
        event.message.delete().queue()
        sendToMinecraft(event.message.contentRaw, Actor(Objects.requireNonNull(event.member)!!))
    }

    @EventHandler
    fun onChat(event: AsyncPlayerChatEvent) {
        //event.player.sendMessage("called")
        sendToDiscord(event.message, Actor(event.player.displayName))
    }
    
}

class Actor {
    private val member: Member?
    private val mcUser: String?
    val side: Boolean

    constructor(member: Member) {
        this.member = member
        this.mcUser = null
        this.side = true
    }

    constructor(mcUser: String) {
        this.member = null
        this.mcUser = mcUser
        this.side = false
    }

    fun get() : String = if (side) member?.nickname!! else mcUser!!
}