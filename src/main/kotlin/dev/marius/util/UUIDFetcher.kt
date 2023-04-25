package dev.marius.util

import com.google.gson.GsonBuilder
import dev.marius.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

/*

Project: David Server Plugin
Author: Marius
Created: 22.11.2020, 15:41

*/
class UUIDFetcher {
    companion object {
        private const val NAME_URL = "https://api.mojang.com/user/profiles/%s/names"

        /**
         * Fetches the name synchronously and returns it
         *
         * @param uuid The uuid
         * @return The name
         */
        @JvmStatic
        fun getName(uuid: UUID): String {
            try {
                val connection = URL(String.format(NAME_URL, uuid.let { UUIDTypeAdapter.fromUUID(it) })).openConnection() as HttpURLConnection
                connection.readTimeout = 5000
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val builder = StringBuilder()
                var current = 0
                while (current != -1) {
                    current = reader.read()
                    builder.append(current.toChar())
                }
                val obj = JSONObject(builder.toString())
                return obj.getString("name")
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null.toString()
        }
    }
}