package dev.marius.dsp.discord.commands

import dev.marius.dsp.Plugin
import dev.marius.dsp.Settings
import dev.marius.dsp.discord.utility.Command
import dev.marius.util.AuthCode
import dev.marius.util.DiscordUtil
import dev.marius.util.Error
import dev.marius.util.StringUtil
import dev.marius.util.*
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.util.*

class RegisterCommand : Command {

    override val name: String
        get() = "register"

    override val description: String
        get() = "Register your Minecraft Account for the project"

    override fun performCommand(event: GuildMessageReceivedEvent) {
        val elements = event.message.contentRaw.split(" ")
        if (elements.size == 2) {
            val minecraftName = elements[1]
            val exists = Objects.requireNonNull(Plugin.accountManager)?.existsMc(minecraftName)
            if (exists!!) {
                event.channel.sendMessage(event.author.asMention + ", your Minecraft Username " +
                        "is already claimed. Go to the channel " + DiscordUtil.getChannelMention("779057853109370902",
                        event.guild) + " and create a ticket or contact one of our Admins for changing the ownership").queue()
            } else if (Objects.requireNonNull(event.member)?.id?.let { Plugin.accountManager?.existsDc(it) }!!) {
                event.channel.sendMessage(event.author.asMention + ", please edit your Minecraft " +
                        "Username if you want to with `" + Settings.BOT_PREFIX + "edit`").queue()
            } else {
                val result = event.member?.id?.let {
                    Plugin.accountManager?.createAccount(minecraftName, it, AuthCode.createRandom().authCode)
                }
                if (result != null) {
                    if (result.left != Error.NO_ERROR) {
                        when (result.left.errorMessage) {
                            "MNIL" -> event.channel.sendMessage(event.author.asMention + ", your Minecraft Username " +
                                    "is already claimed. Go to the channel " + DiscordUtil.getChannelMention("779057853109370902",
                                    event.guild) + " and create a ticket or contact one of our Admins for changing the ownership").queue()
                            "DIIL" -> event.channel.sendMessage(event.author.asMention + ", please edit your Minecraft " +
                                    "Username if you want to with `" + Settings.BOT_PREFIX + "edit`").queue()
                            "AIL" -> event.channel.sendMessage(event.author.asMention + ", AuthCode is already " +
                                    "taken").queue()
                            else -> event.channel.sendMessage(event.member!!.asMention + String.format(", an " +
                                    "error occurred while creating account:\n\n`%s`", result.left.errorMessage))
                                    .queue()
                        }
                    } else {
                        event.author.openPrivateChannel().complete().sendMessage("Hey and welcome to our Minecraft " +
                                "Project. \nYou registered your Minecraft Account successfully!\n\n**AuthCode**: `" +
                                result.right!!.authCode.authCode + "`\n**Server IP**: `" + Settings.MC_URL + "`" +
                                "\n**Our Server Map**: *${Settings.DYNMAP_URL}*" +
                                "\n\nPlease save your AuthCode for logging in to the Minecraft Server!\n\nHave Fun :partying_face:\n~ " +
                                "DMc Server Team\n\n\n:warning: If there are any questions please contact " +
                                StringUtil.join(Settings.OWNER_TAGS, "`%s%`", " or")).queue()

                        val role = event.guild.getRoleById(Settings.REGISTERED_ROLE_ID)
                                ?: throw IllegalStateException ("Cannot find role: 'registered'")
                        if (!event.member?.roles?.contains(role)!!) {
                            event.member?.let { event.guild.addRoleToMember(it, role).queue() }
                        }
                    }
                }
            }
        } else {
            sendHelp("<Minecraft Username>", event.channel)
        }
    }

}