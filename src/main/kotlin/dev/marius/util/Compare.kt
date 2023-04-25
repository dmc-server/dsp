package dev.marius.util

import org.bukkit.Location
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

/*

Project: David Server Plugin
Author: Marius
Created: 06.12.2020, 13:00

*/
class Compare {
    /**
     * *
     * This Method checks, if both given objects are the same.
     * <br></br>
     * For checking at Strings if they are really equal or if Locations have the same YAW and PITCH values, please use <span style="color: yellow">compare(Object first, Object second, Class type, boolean strict)</span>
     * <br></br>
     * <span style="color: red">**WARNING**: Both objects have to be the given class (see param <span style="color: green">"type"</span>)</span>
     * *
     *
     * @since 1.0.0
     * @param first The first object of the comparing
     * @param second The second object of the comparing
     * @param type The class of both objects
     * @return Returns if both objects are the same
     */
    fun compare(first: Any, second: Any, type: Class<*>): Boolean {
        return if (type == String::class.java) {
            if (isInstance(first, String::class.java) && isInstance(second, String::class.java)) {
                first.toString().equals(second.toString(), ignoreCase = true)
            } else {
                false
            }
        } else if (type == Char::class.java) {
            if (isInstance(first, Char::class.java) && isInstance(second, Char::class.java)) {
                first as Char == second as Char
            } else {
                false
            }
        } else if (type == Int::class.java) {
            if (isInstance(first, Int::class.java) && isInstance(second, Int::class.java)) {
                first.toString().toInt() == second.toString().toInt()
            } else {
                false
            }
        } else if (type == Double::class.java) {
            if (isInstance(first, Double::class.java) && isInstance(second, Double::class.java)) {
                first.toString().toDouble() == second.toString().toDouble()
            } else {
                false
            }
        } else if (type == Float::class.java) {
            if (isInstance(first, Float::class.java) && isInstance(second, Float::class.java)) {
                first.toString().toFloat() == second.toString().toFloat()
            } else {
                false
            }
        } else if (type == Boolean::class.java) {
            if (isInstance(first, Boolean::class.java) && isInstance(second, Boolean::class.java)) {
                java.lang.Boolean.valueOf(first.toString()) === java.lang.Boolean.valueOf(second.toString())
            } else {
                false
            }
        } else if (type == Location::class.java) {
            if (isInstance(first, Location::class.java) && isInstance(second, Location::class.java)) {
                val location1 = first as Location
                val location2 = second as Location
                val comparedX = compare(location1.x, location2.x, Double::class.java)
                val comparedY = compare(location1.y, location2.y, Double::class.java)
                val comparedZ = compare(location1.z, location2.z, Double::class.java)
                val comparedWorld = compare(
                    Objects.requireNonNull(location1.world)!!.name,
                    Objects.requireNonNull(location2.world)!!.name, String::class.java
                )
                comparedX && comparedY && comparedZ && comparedWorld
            } else {
                false
            }
        } else {
            if (isInstance(first, type) && isInstance(second, type)) {
                first === second
            } else {
                false
            }
        }
    }

    /**
     * *
     * This Method checks, if both given objects are the same.
     * <br></br>
     * Parameter <span style="color: green">"strict"</span> is only usable if object type is <span style="color: blue">java.lang.String</span> or <span style="color: blue">org.bukkit.Location</span>. It checks, at Strings if they are really equal and at Locations if they really have the same YAW and PITCH values
     * <br></br>
     * <span style="color: red">**WARNING**: Both objects have to be the given class (see param <span style="color: green">"type"</span>)</span>
     * *
     *
     * @since 1.0.0
     * @param first The first object of the comparing
     * @param second The second object of the comparing
     * @param type The class of both objects
     * @param strict Strict compare?
     * @return Returns if both objects are the same
     */
    fun compare(first: Any, second: Any, type: Class<*>, strict: Boolean?): Boolean {
        return if (!strict!!) {
            if (type == String::class.java) {
                if (isInstance(first, String::class.java) && isInstance(second, String::class.java)) {
                    first.toString() == second.toString()
                } else {
                    false
                }
            } else if (type == Location::class.java) {
                if (isInstance(first, Location::class.java) && isInstance(second, Location::class.java)) {
                    val location1 = first as Location
                    val location2 = second as Location
                    val comparedX = compare(location1.x, location2.x, Double::class.java)
                    val comparedY = compare(location1.y, location2.y, Double::class.java)
                    val comparedZ = compare(location1.z, location2.z, Double::class.java)
                    val comparedYaw = compare(location1.yaw, location2.yaw, Float::class.java)
                    val comparedPitch = compare(location1.pitch, location2.pitch, Float::class.java)
                    val comparedWorld = compare(
                        Objects.requireNonNull(location1.world)!!.name,
                        Objects.requireNonNull(location2.world)!!.name, String::class.java
                    )
                    comparedX && comparedY && comparedZ && comparedYaw && comparedPitch && comparedWorld
                } else {
                    false
                }
            } else {
                compare(first, second, type)
            }
        } else {
            compare(first, second, type)
        }
    }

    /**
     * *
     * This Method checks, if the given object is the required class
     * *
     *
     * @param object The Object you want to compare
     * @param type The Class your want to compare
     * @return The Output of the compare
     */
    private fun isInstance(`object`: Any, type: Class<*>): Boolean {
        return `object`.javaClass == type
    }

    /**
     * *
     * Check if X, Y, Z and WORLD are the same Variables at both locations
     * *
     *
     * @param loc1 First Location
     * @param loc2 Second Location
     * @return Returns if locations are the same
     */
    fun compareLocation(loc1: Location, loc2: Location): Boolean {
        return if (Files.exists(Path.of("strictLocationChecker"))) {
            strictCompareLocation(loc1, loc2)
        } else {
            compareLocation(loc1.x, loc1.y, loc1.z, loc1.world!!.name, loc2.x, loc2.y, loc2.z, loc2.world!!.name)
        }
    }

    /**
     * *
     * Check if X, Y, Z and WORLD are the same Variables at both locations
     * *
     *
     * @param x1 X Position of first location
     * @param y1 Y Position of first location
     * @param z1 Z Position of first location
     * @param world1 World of first location
     * @param x2 X Position of second location
     * @param y2 Y Position of second location
     * @param z2 Z Position of second location
     * @param world2 World of second location
     * @return Returns if locations are the same
     */
    fun compareLocation(
        x1: Double, y1: Double, z1: Double, world1: String,
        x2: Double, y2: Double, z2: Double, world2: String
    ): Boolean {
        return x1.toInt() == x2.toInt() &&
                y1.toInt() == y2.toInt() &&
                z1.toInt() == z2.toInt() &&
                world1 == world2
    }

    /**
     * *
     * Check if X, Y, Z, Yaw, Pitch and WORLD are the same Variables at both locations
     * *
     *
     * @param loc1 First Location
     * @param loc2 Second Location
     * @return Returns if locations are the same
     */
    fun strictCompareLocation(loc1: Location, loc2: Location): Boolean {
        return strictCompareLocation(
            loc1.x,
            loc1.y,
            loc1.z,
            loc1.yaw,
            loc1.pitch,
            loc1.world!!.name,
            loc2.x,
            loc2.y,
            loc2.z,
            loc2.yaw,
            loc2.pitch,
            loc2.world!!.name
        )
    }

    /**
     * *
     * Check if X, Y, Z, Yaw, Pitch and WORLD are the same Variables at both locations
     * *
     *
     * @param x1 X Position of first location
     * @param y1 Y Position of first location
     * @param z1 Z Position of first location
     * @param yaw1 Yaw Position of first location
     * @param pitch1 Pitch Position of first location
     * @param world1 World of first location
     * @param x2 X Position of second location
     * @param y2 Y Position of second location
     * @param z2 Z Position of second location
     * @param yaw2 Yaw Position of second location
     * @param pitch2 Pitch position of second location
     * @param world2 World of second location
     * @return Returns if locations are the same
     */
    fun strictCompareLocation(
        x1: Double, y1: Double, z1: Double, yaw1: Float, pitch1: Float, world1: String,
        x2: Double, y2: Double, z2: Double, yaw2: Float, pitch2: Float, world2: String
    ): Boolean {
        return x1.toInt() == x2.toInt() &&
                y1.toInt() == y2.toInt() &&
                z1.toInt() == z2.toInt() &&
                yaw1.toInt() == yaw2.toInt() &&
                pitch1.toInt() == pitch2.toInt() &&
                world1 == world2
    }
}