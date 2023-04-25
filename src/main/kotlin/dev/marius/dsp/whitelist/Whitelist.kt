package dev.marius.dsp.whitelist

import dev.marius.dsp.Plugin
import dev.marius.dsp.Plugin.Companion.ban
import dev.marius.dsp.Settings
import dev.marius.dsp.accounts.AccountManager
import dev.marius.util.DataObject
import org.bukkit.BanList
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent

/*

Project: David Server Plugin
Author: Marius
Created: 20.11.2020, 17:59

*/
class Whitelist(private val accountManager: AccountManager) : Listener {
    private fun canJoin(player: Player): Boolean {
        val ip = Plugin.essentials!!.getUser(player.uniqueId).lastLoginAddress
        return (
                accountManager.existsMc(player.name) &&                                            // check if this user is registered
                Bukkit.getBannedPlayers().stream().noneMatch { it.uniqueId == player.uniqueId } && // check if the user is banned on the server
                !Bukkit.getIPBans().contains(ip) &&                                                // check if the user is ip-banned on the server
                !Plugin.essentials!!.server.getBanList(BanList.Type.NAME).isBanned(player.name) && // check if the user is banned via Essentials
                !Plugin.essentials!!.server.getBanList(BanList.Type.IP).isBanned(ip)               // check if the user is ip-banned via Essentials
        )
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun login(event: PlayerLoginEvent) {
        if (canJoin(event.player)) {
            val account = accountManager.getByMc(event.player.name)
            if (ban!!.isBanned(DataObject("", account.discordId))) {
                event.disallow(
                    PlayerLoginEvent.Result.KICK_BANNED, """
                §cYou are banned from the Server. 
                §cGet help on our Discord Server §b${Settings.DC_URL}
            """.trimIndent()
                )
            } else
                event.allow()
        } else {
            event.disallow(
                PlayerLoginEvent.Result.KICK_WHITELIST, """
     §cYou are disallowed on the server because you are banned or not registered.
     
     §cFeel free to join our Discord-Server to register or to ask in the support why you are banned.
     
     §bhttps://discord.gg/6PbHqHd
     """.trimIndent()
            )
        }
    }
}