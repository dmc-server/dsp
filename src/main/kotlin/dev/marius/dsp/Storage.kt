package dev.marius.dsp

import dev.marius.dsp.Plugin.Companion.accountManager
import dev.marius.dsp.Plugin.Companion.jDA
import net.dv8tion.jda.api.entities.Member
import java.util.*

/*

Project: David Server Plugin
Author: Marius
Created: 12.12.2020, 11:02

*/
class Storage : Runnable {
    override fun run() {
        if (jDA == null) return
        if (jDA!!.getGuildById(Settings.GUILD_ID) == null) return
        for (m in Objects.requireNonNull(Objects.requireNonNull(jDA)!!.getGuildById(Settings.GUILD_ID))!!.members) {
            if (Objects.requireNonNull(accountManager)!!.existsDc(m.id)) {
                MEMBER_STORAGE.remove(m.id)
                MEMBER_STORAGE[m.id] = m
            }
        }
    }

    companion object {
        val MEMBER_STORAGE: MutableMap<String, Member> = HashMap()
    }
}