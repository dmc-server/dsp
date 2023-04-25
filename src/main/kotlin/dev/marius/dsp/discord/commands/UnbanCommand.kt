package dev.marius.dsp.discord.commands

import dev.marius.dsp.Plugin.Companion.accountManager
import dev.marius.dsp.Plugin.Companion.ban
import dev.marius.dsp.discord.utility.Command
import dev.marius.util.DataObject
import dev.marius.util.Error
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.util.*

/*

Project: David Server Plugin
Author: Marius
Created: 28.11.2020, 12:24

*/
class UnbanCommand : Command {
    override val name: String
        get() = "unban"
    override val description: String
        get() = "Unban a player from the Minecraft server"

    override fun performCommand(event: GuildMessageReceivedEvent) {
        val args = event.message.contentRaw.split(" ".toRegex()).toTypedArray()
        if (args.size == 2) {
            val arg = args[1]
            if (arg.startsWith("mc=")) {
                val name = arg.substring("mc=".length)
                if (accountManager!!.existsMc(name)) {
                    val discordID = accountManager!!.getByMc(name).discordId
                    if (ban!!.isBanned(DataObject("", discordID))) {
                        val result = ban!!.setUnbanned(DataObject("", discordID))
                        if (!result.errorMessage.equals(Error.NO_ERROR.errorMessage, ignoreCase = true)) {
                            event.channel.sendMessage(Objects.requireNonNull(event.member)!!.asMention + String.format("""
    , an error occurred while unbanning account:
    
    `%s`
    """.trimIndent(), result.errorMessage))
                                    .queue()
                        } else {
                            event.channel.sendMessage(Objects.requireNonNull(event.member)!!.asMention + ", your unbanned the user successfully").queue()
                        }
                    } else {
                        event.channel.sendMessage(Objects.requireNonNull(event.member)!!.asMention + ", this user is not banned").queue()
                    }
                } else {
                    event.channel.sendMessage(Objects.requireNonNull(event.member)!!.asMention + ", there is no MC Account with this Name").queue()
                }
            } else if (arg.startsWith("dc=")) {
                val id = arg.substring("dc=".length)
                if (accountManager!!.existsDc(id)) {
                    if (ban!!.isBanned(DataObject("", id))) {
                        val result = ban!!.setUnbanned(DataObject("", id))
                        if (!result.errorMessage.equals(Error.NO_ERROR.errorMessage, ignoreCase = true)) {
                            event.channel.sendMessage(Objects.requireNonNull(event.member)!!.asMention + String.format("""
    , an error occurred while unbanning account:
    
    `%s`
    """.trimIndent(), result.errorMessage))
                                    .queue()
                        } else {
                            event.channel.sendMessage(Objects.requireNonNull(event.member)!!.asMention + ", your unbanned the user successfully").queue()
                        }
                    } else {
                        event.channel.sendMessage(Objects.requireNonNull(event.member)!!.asMention + ", this user is not banned").queue()
                    }
                } else {
                    event.channel.sendMessage(Objects.requireNonNull(event.member)!!.asMention + ", there is no MC Account with this ID").queue()
                }
            } else {
                sendHelp("< mc=<MC Name> || dc=<Discord ID> >", event.channel)
            }
        } else {
            sendHelp("< mc=<MC Name> || dc=<Discord ID> >", event.channel)
        }
    }

    override val isAdminCommand: Boolean
        get() = true
}