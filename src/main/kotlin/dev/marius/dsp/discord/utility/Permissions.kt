package dev.marius.dsp.discord.utility

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role

/*

Project: David Server Plugin
Author: Marius
Created: 19.11.2020, 20:04

*/   object Permissions {
    @JvmStatic
    fun isAdmin(member: Member): Boolean {
        return member.roles.stream().anyMatch { role: Role -> role.id.equals("779059510211117118", ignoreCase = true) }
    }
}