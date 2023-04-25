package dev.marius.util

/*

Project: David Server Plugin
Author: Marius
Created: 20.11.2020, 18:25

*/
class Error(val errorMessage: String) {

    companion object {
        val MC_NAME_IN_LIST = Error("MNIL")
        val DC_ID_IN_LIST = Error("DIIL")

        @JvmField
        val NO_ERROR = Error("SUCCESS")
    }
}