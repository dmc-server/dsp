package dev.marius.util

import java.util.*
import kotlin.Boolean
import kotlin.String

/*

Project: David Server Plugin
Author: Marius
Created: 20.11.2020, 18:00

*/
interface AuthCode : Comparable<AuthCode> {
    val authCode: String
    override fun compare(`object`: AuthCode): Boolean {
        return `object`.authCode == authCode
    }

    companion object {
        @JvmStatic
        fun createRandom(): AuthCode = AuthCodeObject(UUID.randomUUID().toString())

        @JvmStatic
        fun fromString(string: String): AuthCode = AuthCodeObject(string)
    }
}

class AuthCodeObject(override val authCode: String) : AuthCode