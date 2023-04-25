/*









THIS FILE IS A SAMPLE FILE.
PLEASE EDIT IT TO YOUR NEEDS BECAUSE
THE ORIGINAL SETTINGS FILE IS NOT
PROVIDED IN THIS PROJECT.











 */

package dev.marius.dsp

/*

Project: David Server Plugin
Author: Marius
Created: 19.11.2020, 19:42

*/

import java.io.File

object SettingsSample {
    const val BOT_PREFIX = ';'
    const val MC_URL = ""
    const val DC_URL = ""
    const val DYNMAP_URL = "" // our server supports dynmap so this ip will be sent to the user
    @JvmField
    val OWNER_TAGS = arrayOf("") // list of owners, like `arrayOf("Marius#0686")`
    const val GUILD_ID = ""
    const val REGISTERED_ROLE_ID = "" // registered users will be granted this role to identify them
    const val BANNED_ROLE_ID = "" // banned users will be granted this role to identify them
    const val GAME_CHAT_ID = "// not needed //"
    @JvmStatic
    var beta = File("beta").exists() // for testing purposes, we introduced a beta bot that can be toggled like that
    const val REGULAR_BOT_TOKEN = ""
    const val BETA_BOT_TOKEN = ""
}
