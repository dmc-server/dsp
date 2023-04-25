package dev.marius.dsp.discord.listener

import dev.marius.dsp.Settings
import dev.marius.dsp.discord.commands.*
import dev.marius.dsp.discord.utility.Command
import dev.marius.dsp.discord.utility.Permissions.isAdmin
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.*
import javax.annotation.Nonnull

/*

Project: David Server Plugin
Author: Marius
Created: 19.11.2020, 19:47

*/   class CommandListener : ListenerAdapter() {
    private fun registerCommand(command: Command) {
        if (!COMMANDS.containsKey(command.name)) {
            COMMANDS[command.name] = command
        }
    }

    override fun onGuildMessageReceived(@Nonnull event: GuildMessageReceivedEvent) {
        if (event.author.isBot) return
        val msgSplit = event.message.contentRaw.split(" ".toRegex()).toTypedArray()
        if (msgSplit.isEmpty()) return
        val invoke = msgSplit[0]
        if (invoke.startsWith(java.lang.String.valueOf(Settings.BOT_PREFIX))) {
            val name = invoke.replaceFirst(java.lang.String.valueOf(Settings.BOT_PREFIX).toRegex(), "")
            val command = COMMANDS.getOrDefault(name, HelpCommand())
            val canExecute = if (command.isAdminCommand) {
                isAdmin(Objects.requireNonNull(event.member)!!)
            } else {
                command.canExecute(event.member!!, event.channel, name)
            }
            if (canExecute) {
                command.performCommand(event)
            } else {
                event.channel.sendMessage(Objects.requireNonNull(event.member)!!.asMention + ", you don't have the permission to execute that command").queue()
            }
            event.message.delete().queue()
        }
    }

    companion object {
        @JvmField
        val COMMANDS: MutableMap<String, Command> = HashMap()
    }

    init {
        registerCommand(HelpCommand())
        registerCommand(RegisterCommand())
        registerCommand(EditCommand())
        registerCommand(GetCommand())
        registerCommand(InfoCommand())
        registerCommand(BanCommand())
        registerCommand(TempbanCommand())
        registerCommand(UnbanCommand())
        registerCommand(DeleteCommand())
    }
}