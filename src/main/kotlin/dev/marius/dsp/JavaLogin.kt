package dev.marius.dsp

import dev.marius.util.Config
import java.net.InetSocketAddress
import java.util.ArrayList

/*

Project: David Server Plugin
Author: Marius
Created: 12.12.2020, 11:42

*/
internal object JavaLogin {
    internal fun setToLoginList(code: String, config: Config, address: InetSocketAddress) {
        val list: ArrayList<String> = if (config.exists("saved.$code")) {
            config.config.getStringList("saved.$code") as ArrayList<String>
        } else {
            ArrayList()
        }
        list.add(address.hostName)
        config["saved.$code"] = list
        config.save()
    }
}