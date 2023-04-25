package dev.marius.dsp.discord.commands

import dev.marius.dsp.Plugin.Companion.accountManager
import dev.marius.dsp.Settings
import dev.marius.dsp.discord.Bot.jda
import dev.marius.dsp.discord.utility.Command
import dev.marius.dsp.discord.utility.Permissions.isAdmin
import dev.marius.util.DataObject
import dev.marius.util.Error
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.util.*

/*

Project: David Server Plugin
Author: Marius
Created: 22.11.2020, 16:26

*/

// TODO: fix
class EditCommand : Command {
    override val name: String
        get() = "edit"
    override val description: String
        get() = "Edit the Linking of your Discord Account to the Minecraft Server"

    override fun performCommand(event: GuildMessageReceivedEvent) {
        if (event.member == null) return
        val args = event.message.contentRaw.split(" ".toRegex()).toTypedArray()
        if (args.size == 2) {
            val mcName = args[1]
            if (accountManager!!.existsMc(name)) {
                event.channel.sendMessage(event.member!!.asMention + ", this Minecraft Username is already claimed!").queue()
            } else {
                val result = accountManager!!.editAccount(accountManager!!.getByDc(event.member!!.id), DataObject("minecraft", mcName))
                if (!result.left.errorMessage.equals(Error.NO_ERROR.errorMessage, ignoreCase = true)) {
                    event.channel.sendMessage(event.member!!.asMention + String.format("""
    , an error occurred while creating account:
    
    `%s`
    """.trimIndent(), result.left.errorMessage))
                            .queue()
                } else {
                    event.channel.sendMessage(event.member!!.asMention + ", you changed your Minecraft Name successfully!").queue()
                }
            }
        } else if (args.size == 3 && isAdmin(event.member!!)) {
            val mcName = args[1]
            val discordID = args[2]
            if (!accountManager!!.existsDc(discordID)) {
                event.channel.sendMessage(event.member!!.asMention + ", there is no Discord Account with this ID!").queue()
            } else if (accountManager!!.existsMc(name)) {
                event.channel.sendMessage(event.member!!.asMention + ", this Minecraft Username is already claimed!").queue()
            } else {
                if (isAdmin(event.member!!)) {
                    val target = Objects.requireNonNull(jda!!.getGuildById(Settings.GUILD_ID))!!.getMemberById(discordID)
                    if (target != null) {
                        val result = accountManager!!.editAccount(accountManager!!.getByDc(discordID), DataObject("minecraft", mcName))
                        if (!result.left.errorMessage.equals(Error.NO_ERROR.errorMessage, ignoreCase = true)) {
                            event.channel.sendMessage(event.member!!.asMention + String.format("""
    , an error occurred while creating account:
    
    `%s`
    """.trimIndent(), result.left.errorMessage))
                                    .queue()
                        } else {
                            event.channel.sendMessage(event.member!!.asMention + ", you changed the Minecraft Name of " + target.user.asTag + " successfully!").queue()
                        }
                    } else {
                        event.channel.sendMessage(event.member!!.asMention + ", the member you requested is null").queue()
                    }
                } else {
                    event.channel.sendMessage(event.member!!.asMention + ", you don't have the permission to execute that command").queue()
                }
            }
        } else {
            if (isAdmin(event.member!!)) {
                sendHelp("<MC Name> [Discord ID]", event.channel)
            } else {
                sendHelp("<MC Name>", event.channel)
            }
        }
    }
}