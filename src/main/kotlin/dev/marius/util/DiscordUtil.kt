package dev.marius.util

import dev.marius.dsp.Plugin
import dev.marius.dsp.Settings
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import java.util.*

/*

Project: David Server Plugin
Author: Marius
Created: 22.11.2020, 15:52

*/
object DiscordUtil {
    @JvmStatic
    fun getChannelMention(id: String, guild: Guild): String {
        return Objects.requireNonNull(guild.getTextChannelById(id))!!.asMention
    }
    @JvmStatic
    fun getById(id: String) : Member? {
        return Plugin.jDA!!.getGuildById(Settings.GUILD_ID)!!.members.stream().filter { m: Member -> m.id == id }.findFirst().orElse(null)
    }
}