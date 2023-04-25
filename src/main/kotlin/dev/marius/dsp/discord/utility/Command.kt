package dev.marius.dsp.discord.utility

import dev.marius.util.DateUtil.currentDate
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.awt.Color

/*

Project: David Server Plugin
Author: Marius
Created: 19.11.2020, 19:46

*/   interface Command {
    fun canExecute(member: Member, channel: TextChannel, cmdName: String): Boolean {
        return true
    }

    val name: String
    val description: String
    fun performCommand(event: GuildMessageReceivedEvent)
    val isAdminCommand: Boolean
        get() = false

    fun sendHelp(args: String, channel: TextChannel) {
        val builder = EmbedBuilder()
        builder.setTitle("**Command Error**: *Wrong Arguments*")
        val string = StringBuilder("**Name**: *$name*\n")
        if (description.isNotEmpty()) {
            string.append("**Description**: *").append(description).append("*\n")
        }
        string.append("**Arguments**: `").append(args).append("`")
        builder.setDescription(string.toString())
        builder.setColor(Color.RED)
        builder.setFooter("Help sent at " + currentDate.time())
        channel.sendMessage(builder.build()).queue()
    }
}