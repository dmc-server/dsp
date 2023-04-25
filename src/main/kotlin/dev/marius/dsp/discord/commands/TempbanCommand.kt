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

// TODO: Not in work
class TempbanCommand : Command {
    override val name: String
        get() = "tempban"
    override val description: String
        get() = "Tempban a player from the Minecraft server"

    override fun performCommand(event: GuildMessageReceivedEvent) {
        val args = event.message.contentRaw.split(" ".toRegex()).toTypedArray()
        if (args.size == 3) {
            val arg = args[1]
            val time: Int = try {
                args[2].toInt()
            } catch (e: NumberFormatException) {
                event.channel.sendMessage(Objects.requireNonNull(event.member)!!.asMention + ", please enter as time the right Number Format").queue()
                return
            }
            if (arg.startsWith("mc=")) {
                val name = arg.substring("mc=".length)
                if (accountManager!!.existsMc(name)) {
                    val discordID = accountManager!!.getByMc(name).discordId
                    if (!ban!!.isBanned(DataObject("", discordID))) {
                        val result = ban!!.setBanned(DataObject("", discordID), time.toString())
                        if (!result.errorMessage.equals(Error.NO_ERROR.errorMessage, ignoreCase = true)) {
                            event.channel.sendMessage(Objects.requireNonNull(event.member)!!.asMention + String.format("""
    , an error occurred while banning account:
    
    `%s`
    """.trimIndent(), result.errorMessage))
                                    .queue()
                        } else {
                            event.channel.sendMessage(Objects.requireNonNull(event.member)!!.asMention + ", your banned the user successfully. Time: " + time + "s").queue()
                        }
                    } else {
                        event.channel.sendMessage(Objects.requireNonNull(event.member)!!.asMention + ", this user is already banned").queue()
                    }
                } else {
                    event.channel.sendMessage(Objects.requireNonNull(event.member)!!.asMention + ", there is no MC Account with this Name").queue()
                }
            } else if (arg.startsWith("dc=")) {
                val id = arg.substring("dc=".length)
                if (accountManager!!.existsDc(id)) {
                    if (!ban!!.isBanned(DataObject("", id))) {
                        val result = ban!!.setBanned(DataObject("", id), time.toString())
                        if (!result.errorMessage.equals(Error.NO_ERROR.errorMessage, ignoreCase = true)) {
                            event.channel.sendMessage(Objects.requireNonNull(event.member)!!.asMention + String.format("""
    , an error occurred while banning account:
    
    `%s`
    """.trimIndent(), result.errorMessage))
                                    .queue()
                        } else {
                            event.channel.sendMessage(Objects.requireNonNull(event.member)!!.asMention + ", your banned the user successfully. Time: " + time + "s").queue()
                        }
                    } else {
                        event.channel.sendMessage(Objects.requireNonNull(event.member)!!.asMention + ", this user is already banned").queue()
                    }
                } else {
                    event.channel.sendMessage(Objects.requireNonNull(event.member)!!.asMention + ", there is no MC Account with this ID").queue()
                }
            } else {
                sendHelp("< mc=<MC Name> || dc=<Discord ID> > <Time in seconds>", event.channel)
            }
        } else {
            sendHelp("< mc=<MC Name> || dc=<Discord ID> > <Time in seconds>", event.channel)
        }
    }

    override val isAdminCommand: Boolean
        get() = true
}