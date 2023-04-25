package dev.marius.util

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.util.*

/*

Project: David Server Plugin
Author: Marius
Created: 22.11.2020, 15:47

*/
class UUIDTypeAdapter : TypeAdapter<UUID>() {
    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: UUID) {
        out.value(fromUUID(value))
    }

    @Throws(IOException::class)
    override fun read(`in`: JsonReader): UUID {
        return fromString(`in`.nextString())
    }

    companion object {
        fun fromUUID(value: UUID): String {
            return value.toString().replace("-", "")
        }

        fun fromString(input: String): UUID {
            /*val array = input.split("")
            var res = ""
            for (i in 0..array.size) {
                res += array[i]
                if (i == 7 || i == 11 || i == 15 || i == 19 || i == 31)
                    res += "-"
            }
            return UUID.fromString(res)*/
            return UUID.fromString(input.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"))
        }
    }
}