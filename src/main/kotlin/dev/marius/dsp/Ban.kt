package dev.marius.dsp

import dev.marius.dsp.Plugin.Companion.accountManager
import dev.marius.dsp.Plugin.Companion.configFolder
import dev.marius.dsp.Plugin.Companion.jDA
import dev.marius.util.Config
import dev.marius.util.DataObject
import dev.marius.util.Error
import dev.marius.util.IntegerUtil.instanceOfInt
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import java.io.File
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import java.util.function.Consumer

/*

Project: David Server Plugin
Author: Marius
Created: 27.11.2020, 15:51

*/
class Ban : Runnable {
    private val config: Config = Config(File(configFolder, "ban.yml"))
    fun isBanned(`object`: DataObject): Boolean {
        val list = config.config.getStringList("saved")
        val code = accountManager!!.getByDc(`object`.body).authCode.authCode

        for (entry in list) {
            if (entry.split(";;;")[0] == code) {
                return true
            }
        }
        return false
    }

    fun setBanned(`object`: DataObject): Error {
        return setBanned(`object`, "permanent")
    }

    fun setBanned(`object`: DataObject, seconds: String): Error {
        if (`object`.body.isEmpty()) return Error("Cannot read Discord ID from Data Object")
        val discordID = `object`.body
        if (!accountManager!!.existsDc(discordID)) return Error("There is no Account with the provided ID")
        if (isBanned(DataObject("", discordID))) return Error("Account is already banned")
        val list = config.config.getStringList("saved")
        when {
            seconds == "permanent" -> {
                list.add(accountManager!!.getByDc(discordID).authCode.authCode + ";;;" + seconds)
            }
            instanceOfInt(seconds) -> {
                val sec = seconds.toInt()
                val millis = System.currentTimeMillis() + sec * 1000
                list.add(accountManager!!.getByDc(discordID).authCode.authCode + ";;;" + millis)
            }
            else -> return Error("Please add seconds")
        }
        config["saved"] = list
        val g = jDA?.getGuildById(Settings.GUILD_ID)
                ?: return Error("Initial Error: `Ban.java Z57`. Please report this error to a administrator")
        val member: Member = if (g.getMemberById(discordID) != null) g.getMemberById(discordID)!! else Storage.MEMBER_STORAGE[discordID]!!
        val registered: Role = g.getRoleById(Settings.REGISTERED_ROLE_ID)!!
        val banned: Role = g.getRoleById(Settings.BANNED_ROLE_ID)!!
        g.removeRoleFromMember(member, registered).queue()
        g.addRoleToMember(member, banned).queue()
        return Error.NO_ERROR
    }

    fun setUnbanned(`object`: DataObject): Error {
        if (`object`.body.isEmpty()) return Error("Cannot read Discord ID from Data Object")
        val discordID = `object`.body
        if (!accountManager!!.existsDc(discordID)) return Error("There is no Account with the provided ID")
        if (!isBanned(DataObject("", discordID))) return Error("Account is not banned")
        val list = config.config.getStringList("saved")
        val entry = AtomicReference("")
        list.forEach(Consumer { e: String ->
            if (e.split(";;;".toRegex()).toTypedArray()[0].equals(accountManager!!.getByDc(discordID).authCode.authCode, ignoreCase = true)) {
                entry.set(e)
            }
        })
        list.remove(entry.get())
        config["saved"] = list
        val g = jDA?.getGuildById(Settings.GUILD_ID)
                ?: return Error("Initial Error: `Ban.java Z57`. Please report this error to a administrator")
        g.removeRoleFromMember(Objects.requireNonNull(g.getMemberById(discordID))!!, Objects.requireNonNull(g.getRoleById(Settings.BANNED_ROLE_ID))!!).queue()
        g.addRoleToMember(Objects.requireNonNull(g.getMemberById(discordID))!!, Objects.requireNonNull(g.getRoleById(Settings.REGISTERED_ROLE_ID))!!).queue()
        return Error.NO_ERROR
    }

    override fun run() {
        val list = config.config.getStringList("saved")
        for (entry in list) {
            val arg = entry.split(";;;".toRegex()).toTypedArray()[1]
            if (arg == "permanent")
                continue
            try {
                val asLong = arg.toLong()
                if (asLong <= System.currentTimeMillis()) {
                    list.remove(entry)
                }
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }
        }
        config["saved"] = list
    }

    init {
        if (!config.exists("saved")) config["saved"] = emptyList<Any>()
    }
}