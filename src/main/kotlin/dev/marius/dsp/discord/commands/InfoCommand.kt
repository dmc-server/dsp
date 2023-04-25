package dev.marius.dsp.discord.commands

import dev.marius.dsp.Plugin.Companion.accountManager
import dev.marius.dsp.Plugin.Companion.ban
import dev.marius.dsp.Settings
import dev.marius.dsp.discord.Bot
import dev.marius.dsp.discord.utility.Command
import dev.marius.dsp.discord.utility.Permissions.isAdmin
import dev.marius.util.DataObject
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

/*

Project: David Server Plugin
Author: Marius
Created: 22.11.2020, 16:26

*/
class InfoCommand : Command {
    override val name: String
        get() = "info"
    override val description: String
        get() = "Get all information's about your linking"

    override fun performCommand(event: GuildMessageReceivedEvent) {
        val args = event.message.contentRaw.substring(name.length+2).split(" ")
        if (isAdmin(event.member!!)) {
            if (args.isEmpty()) {
                send(event.member!!, event.channel)
            } else if (args.size == 2) {
                val method = args[0].toLowerCase()
                val option = args[1]
                if (method == "minecraft") {
                    if (accountManager!!.existsMc(option)) {
                        val account = accountManager!!.getByMc(option)
                        val discord = Bot.jda!!.getGuildById(Settings.GUILD_ID)!!.getMemberById(account.discordId)
                        send(discord!!, event.channel)
                    } else {
                        event.channel.sendMessage("There is no account with this minecraft name!").queue()
                    }
                } else if (method == "discord") {
                    if (accountManager!!.existsDc(option)) {
                        val discord = Bot.jda!!.getGuildById(Settings.GUILD_ID)!!.getMemberById(option)
                        send(discord!!, event.channel)
                    } else {
                        event.channel.sendMessage("There is no account with this minecraft name!").queue()
                    }
                } else {
                    sendHelp("[minecraft / discord] [MC Name / Discord ID]", event.channel)
                }
            } else {
                sendHelp("[minecraft / discord] [MC Name / Discord ID]", event.channel)
            }
        } else {
            send(event.member!!, event.channel)
        }
    }

    private fun send(member: Member, channel: TextChannel) {
        val builder = EmbedBuilder()
        builder.setTitle("**User Information**")
        builder.addField(
            "MC Name",
            accountManager!!.getByDc(member.id).minecraftName,
            true
        )
        builder.addField(
            "Status",
            if (ban!!.isBanned(DataObject("", member.id))) "BANNED" else "REGISTERED",
            true
        )
        channel.sendMessage(builder.build()).queue()
    }

    override fun canExecute(member: Member, channel: TextChannel, cmdName: String): Boolean {
        return accountManager!!.existsDc(member.id)
    }
}