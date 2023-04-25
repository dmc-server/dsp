package dev.marius.dsp.accounts

import dev.marius.util.AuthCode

/*

Project: David Server Plugin
Author: Marius
Created: 20.11.2020, 18:00

*/
interface Account {
    val minecraftName: String
    val discordId: String
    val authCode: AuthCode
}