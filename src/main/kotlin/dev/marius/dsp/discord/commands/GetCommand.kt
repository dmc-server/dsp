package dev.marius.dsp.discord.commands

import dev.marius.dsp.Plugin.Companion.accountManager
import dev.marius.dsp.Settings
import dev.marius.dsp.discord.utility.Command
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.awt.Color
import java.util.*

/*

Project: David Server Plugin
Author: Marius
Created: 22.11.2020, 16:26

*/
class GetCommand : Command {
    private val links: MutableMap<String, Get> = HashMap()
    override val name: String
        get() = "get"
    override val description: String
        get() = "Get parameters of the server"

    override fun performCommand(event: GuildMessageReceivedEvent) {
        val args = event.message.contentRaw.split(" ".toRegex()).toTypedArray()
        when (args.size) {
            2 -> {
                val option = args[1]
                if (option == "list") {
                    var builder = StringBuilder("""
    ${Objects.requireNonNull(event.member)!!.asMention}, These are the GET options:
    
    """.trimIndent())
                    for (s in links.keys) builder.append("- ").append(s).append("\n")
                    builder = StringBuilder(builder.substring(0, builder.length - "\n".length))
                    event.channel.sendMessage(builder.toString()).queue()
                    return
                }
                val get = links.getOrDefault(option.toLowerCase(), object : Get {
                    override fun perform(event: GuildMessageReceivedEvent) {
                        event.channel.sendMessage(
                                Objects.requireNonNull(event.member)!!.asMention + String.format(", `%s` is not a valid GET option", option)).queue()
                    }
                })
                get.perform(event)
            }
            else -> {
                sendHelp("<Option / list>", event.channel)
            }
        }
    }

    override fun canExecute(member: Member, channel: TextChannel, cmdName: String): Boolean {
        return member.roles.stream().anyMatch { role: Role ->
            role.id.equals(Settings.REGISTERED_ROLE_ID, ignoreCase = true) ||
                    role.id.equals(Settings.BANNED_ROLE_ID, ignoreCase = true)
        }
    }

    private interface Get {
        fun perform(event: GuildMessageReceivedEvent)
    }

    private class IpGet : Get {
        override fun perform(event: GuildMessageReceivedEvent) {
            Objects.requireNonNull(event.member)!!.user.openPrivateChannel().complete()
                    .sendMessage(EmbedBuilder().setDescription(Settings.MC_URL).setTitle("**Server IP**",
                            Settings.DC_URL).setColor(Color.GREEN).build()).queue()
        }
    }

    private class AuthGet : Get {
        override fun perform(event: GuildMessageReceivedEvent) {
            Objects.requireNonNull(event.member)!!.user.openPrivateChannel().complete()
                    .sendMessage(EmbedBuilder().setDescription("`" + accountManager?.getByDc(event.member!!.id)?.authCode?.authCode + "`").setTitle("**Auth Code**")
                            .setColor(Color.GREEN).build()).queue()
        }
    }

    init {
        links["ip"] = IpGet()
        links["auth"] = AuthGet()
    }
}