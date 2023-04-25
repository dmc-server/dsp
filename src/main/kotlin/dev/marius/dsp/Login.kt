@file:Suppress("DEPRECATION")

package dev.marius.dsp

import dev.marius.dsp.Plugin.Companion.accountManager
import dev.marius.dsp.Plugin.Companion.configFolder
import dev.marius.util.AuthCode.Companion.fromString
import dev.marius.util.Config
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.*
import java.io.File
import java.util.*
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

/*

Project: David Server Plugin
Author: Marius
Created: 27.11.2020, 15:26

*/

class Login(plugin: Plugin) : Listener, CommandExecutor, TabCompleter {
    private val loginLocationMap = HashMap<UUID, Location>()

    private val config: Config = Config(File(configFolder, "login.yml"))
    override fun onCommand(commandSender: CommandSender, command: Command, s: String, strings: Array<String>): Boolean {
        if (commandSender is Player && notContains(commandSender)) {
            if (strings.size == 1) {
                val code = strings[0]
                if (accountManager!!.getByMc(commandSender.name).authCode.compare(fromString(code))) {
                    commandSender.address?.let { JavaLogin.setToLoginList(code, config, it) }
                    commandSender.sendMessage("§aYou logged in! Have fun!")
                    commandSender.teleport(loginLocationMap[commandSender.uniqueId]!!)
                    loginLocationMap.remove(commandSender.uniqueId)
                } else {
                    commandSender.sendMessage(
                        """
§c[!] Auth Code does not match to the Auth Code of your Discord Connection
§c[!] Get help on our Discord Server: §7${Settings.DC_URL}
""".trimIndent()
                    )
                }
            } else {
                commandSender.sendMessage("§c[!] Usage: /login <Auth Code>\n§c[!] Please login with your Auth Code, that you get by your Discord Connection")
            }
        }
        return false
    }

    override fun onTabComplete(
        commandSender: CommandSender,
        command: Command,
        s: String,
        strings: Array<String>
    ): List<String> {
        return emptyList()
    }

    private fun notContains(player: Player): Boolean {
        config.save()
        if (!accountManager!!.existsMc(player.name)) {
            player.kickPlayer("§4Bugusing. Please report it to a discord moderator.\n\n§7" + Settings.DC_URL)
            return false
        }
        val account = accountManager!!.getByMc(player.name)
        val list = config.config.getStringList("saved.${account.authCode.authCode}")
        return !list.contains(player.address!!.hostName)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun join(event: PlayerJoinEvent) {
        if (notContains(event.player)) {
            event.player.sendMessage(SHOULD_LOGIN_MESSAGE)

            val pos1 = event.player.location
            val pos2 = Plugin.essentialsSpawn!!.getSpawn(Plugin.essentials!!.getUser(event.player.uniqueId).group)
            val distance = sqrt(
                (pos2.x - pos1.x).pow(2.0) +
                (pos2.y - pos1.y).pow(2.0) +
                (pos2.z - pos1.z).pow(2.0)
            ).roundToInt()
            if (distance > 3) loginLocationMap[event.player.uniqueId] = event.player.location
            event.player.teleport(pos2)
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun chat(event: AsyncPlayerChatEvent) {
        if (event.message.startsWith("/")) {
            command(PlayerCommandPreprocessEvent(event.player, event.message))
            return
        }

        if (notContains(event.player)) {
            event.isCancelled = true
            event.player.sendMessage(SHOULD_LOGIN_MESSAGE)
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun command(event: PlayerCommandPreprocessEvent) {
        if (notContains(event.player)) {
            if (event.message.split(" ")[0].replaceFirst("/", "").toLowerCase() == "login") {
                return
            }
            event.isCancelled = true
            event.player.sendMessage(SHOULD_LOGIN_MESSAGE)
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun blockBreak(event: BlockBreakEvent) {
        if (notContains(event.player)) {
            event.isCancelled = true
            event.player.sendMessage(SHOULD_LOGIN_MESSAGE)
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun blockPlace(event: BlockPlaceEvent) {
        if (notContains(event.player)) {
            event.isCancelled = true
            event.player.sendMessage(SHOULD_LOGIN_MESSAGE)
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun itemPickup(event: PlayerPickupItemEvent) {
        if (notContains(event.player)) {
            event.isCancelled = true
            event.player.sendMessage(SHOULD_LOGIN_MESSAGE)
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun itemDrop(event: PlayerDropItemEvent) {
        if (notContains(event.player)) {
            event.isCancelled = true
            event.player.sendMessage(SHOULD_LOGIN_MESSAGE)
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun interact(event: PlayerInteractEvent) {
        if (notContains(event.player)) {
            event.isCancelled = true
            event.player.sendMessage(SHOULD_LOGIN_MESSAGE)
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun move(event: PlayerMoveEvent) {
        if (notContains(event.player)) {
            if (event.to == null) {
                return
            }
            val loc1 = event.to!!
            val loc2 = event.from

            if (!Plugin.compare.compareLocation(
                    loc1.x, loc1.y, loc1.z, loc1.world!!.name, loc2.x, loc2.y, loc2.z,
                    loc2.world!!.name
                )
            ) {
                event.isCancelled = true
                event.player.sendMessage(SHOULD_LOGIN_MESSAGE)
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun damage(event: EntityDamageByEntityEvent) {
        if (event.entity !is Player) return
        if (notContains(event.entity as Player)) {
            event.isCancelled = true
            event.entity.sendMessage(SHOULD_LOGIN_MESSAGE)
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun inventoryClick(event: InventoryClickEvent) {
        if (notContains(event.whoClicked as Player)) {
            event.isCancelled = true
            event.whoClicked.sendMessage(SHOULD_LOGIN_MESSAGE)
        }
    }

    init {
        config.save()
        plugin.server.pluginManager.registerEvents(this, plugin)
        Objects.requireNonNull(plugin.getCommand("login"))!!.setExecutor(this)
        Objects.requireNonNull(plugin.getCommand("login"))!!.tabCompleter = this
    }

    companion object {
        private const val SHOULD_LOGIN_MESSAGE =
            "§6Please login with your Connection's Auth Code with §7/login <Auth Code>"
    }
}