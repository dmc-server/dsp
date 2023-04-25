package dev.marius.dsp.discord.commands

import dev.marius.dsp.Settings
import dev.marius.dsp.discord.listener.CommandListener
import dev.marius.dsp.discord.utility.Command
import dev.marius.dsp.discord.utility.Permissions.isAdmin
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.awt.Color
import java.util.*
import java.util.function.Predicate

/*

Project: David Server Plugin
Author: Marius
Created: 19.11.2020, 19:51

*/
class HelpCommand : Command {
    override val name: String
        get() = "help"
    override val description: String
        get() = "List all commands of this bot"

    override fun performCommand(event: GuildMessageReceivedEvent) {
        val user = EmbedBuilder()
        var admin: EmbedBuilder? = null
        val builder = StringBuilder()
        CommandListener.COMMANDS.values.stream().filter { cmd: Command -> !cmd.isAdminCommand }.forEach { cmd: Command ->
            builder
                    .append("**" + Settings.BOT_PREFIX).append(cmd.name).append("** - *")
                    .append(cmd.description).append("*").append("\n")
        }
        user.setDescription(builder.toString())
        user.setTitle("**User Commands**")
        user.setColor(Color.GREEN)
        if (isAdmin(Objects.requireNonNull(event.member)!!)) {
            val builder2 = StringBuilder()
            CommandListener.COMMANDS.values.stream().filter(Command::isAdminCommand)
                .filter { it.name.toLowerCase() != "tempban" } // TODO: Remove if tempban is fixed
                .forEach { cmd: Command ->
                builder2
                        .append("**" + Settings.BOT_PREFIX).append(cmd.name).append("** - *")
                        .append(cmd.description).append("*").append("\n")
            }
            admin = EmbedBuilder()
            admin.setDescription(builder2.toString())
            admin.setTitle("**Admin Commands**")
            admin.setColor(Color.RED)
        }
        event.channel.sendMessage(user.build()).queue()
        if (admin != null) event.channel.sendMessage(admin.build()).queue()
    }
}