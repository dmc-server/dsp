package dev.marius.dsp.discord.listener

import dev.marius.dsp.Plugin.Companion.accountManager
import dev.marius.util.CharHelper
import dev.marius.util.DataCipher.encryptMessage
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.*
import javax.annotation.Nonnull

/*

Project: David Server Plugin
Author: Marius
Created: 27.11.2020, 16:20

*/   class GuildLeaveListener : ListenerAdapter() {
    override fun onGuildMemberRemove(@Nonnull event: GuildMemberRemoveEvent) {
        if (accountManager!!.existsDc(Objects.requireNonNull(event.member)!!.id)) {
            accountManager!!.delAccount(event.member!!.id)
        }
    }
}