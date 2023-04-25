package dev.marius.util

/*

Project: David Server Plugin
Author: Marius
Created: 25.11.2020, 17:04

*/
data class DataObject(val title: String, val body: String) {
    override fun toString(): String = "DataObject={title='$title', body='$body'}"
}