package dev.marius.util

import java.util.Date

/*

Project: David Server Plugin
Author: Marius
Created: 22.11.2020, 15:32

*/
interface Date {
    fun date(): String
    fun time(): String
    fun all(): String
    fun asJavaDate(): Date
}