package dev.marius.util

/*

Project: David Server Plugin
Author: Marius
Created: 20.11.2020, 18:02

*/
interface Comparable<O> {
    fun compare(`object`: O): Boolean
}