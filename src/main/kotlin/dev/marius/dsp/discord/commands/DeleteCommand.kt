package dev.marius.dsp.discord.commands

import dev.marius.dsp.Plugin.Companion.accountManager
import dev.marius.dsp.discord.utility.Command
import dev.marius.util.CharHelper
import dev.marius.util.DataCipher.encryptMessage
import dev.marius.util.Error
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.util.*

/*

Project: David Server Plugin
Author: Marius
Created: 28.11.2020, 12:24

*/

// TODO: fix
class DeleteCommand : Command {
    override val name: String
        get() = "delete"
    override val description: String
        get() = "Delete a Account"

    override fun performCommand(event: GuildMessageReceivedEvent) {
        val args = event.message.contentRaw.split(" ".toRegex()).toTypedArray()
        if (args.size == 2) {
            val discordID = args[1]
            if (accountManager!!.existsDc(discordID)) {
                val result = accountManager!!.delAccount(discordID)
                if (!result.errorMessage.equals(Error.NO_ERROR.errorMessage, ignoreCase = true)) {
                    event.channel.sendMessage(Objects.requireNonNull(event.member)!!.asMention + String.format("""
    , an error occurred while deleting account:
    
    `%s`
    """.trimIndent(), result.errorMessage))
                            .queue()
                } else {
                    event.channel.sendMessage(Objects.requireNonNull(event.member)!!.asMention + ", your deleted the account successfully").queue()
                }
            } else {
                event.channel.sendMessage(Objects.requireNonNull(event.member)!!.asMention + ", there is no MC Account with this ID").queue()
            }
        } else {
            sendHelp("<Discord ID>", event.channel)
        }
    }

    override val isAdminCommand: Boolean
        get() = true
}