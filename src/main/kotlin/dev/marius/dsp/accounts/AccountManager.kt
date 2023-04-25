package dev.marius.dsp.accounts

import dev.marius.dsp.Plugin
import dev.marius.util.AuthCode
import dev.marius.util.AuthCodeObject
import dev.marius.util.Config
import dev.marius.util.DataObject
import dev.marius.util.Error
import dev.marius.util.Pair
import org.jetbrains.annotations.Nullable
import java.io.File
import java.util.*
import java.util.function.Consumer

/*

Project: David Server Plugin
Author: Marius
Created: 20.11.2020, 18:03

*/
class AccountManager {
    private var registeredAccounts: MutableList<Account>
    private var config: Config

    internal fun save() {
        val accountList = config.config.getStringList("accounts")
        accountList.clear()
        registeredAccounts.forEach(Consumer { account: Account ->
            accountList.add(account.minecraftName + ";;;" +
                    account.discordId + ";;;" + account.authCode.authCode)
        })
        config["accounts"] = accountList
        config.save()
    }

    private fun refresh() {
        save()
        config = Config(File(Plugin.configFolder, "account_storage.yml"))
        registeredAccounts = ArrayList()
        if (!config.exists("accounts")) config["accounts"] = ArrayList<String>()
        config.config.getStringList("accounts").forEach(Consumer { string: String ->
            val split = string.split(";;;".toRegex()).toTypedArray()
            if (split.size == 3) {
                registeredAccounts.add(AccountObject(split[0], split[1], AuthCode.fromString(split[2])))
            } else {
                throw IllegalArgumentException("Cannot format following Account-String: $string")
            }
        })
    }

    internal fun update() {
        config = Config(File(Plugin.configFolder, "account_storage.yml"))
        registeredAccounts = ArrayList()
        val tempList = ArrayList<Account>()
        if (!config.exists("accounts")) config["accounts"] = ArrayList<String>()
        config.config.getStringList("accounts").forEach(Consumer { string: String ->
            val split = string.split(";;;".toRegex()).toTypedArray()
            if (split.size == 3) {
                tempList.add(AccountObject(split[0], split[1], AuthCode.fromString(split[2])))
            } else {
                throw IllegalArgumentException("Cannot format following Account-String: $string")
            }
        })
        registeredAccounts.forEach {
            if (!tempList.contains(it)) {
                println(("[Server] Found account that is not in the Config but registered. We will delete it.\n[Server] " +
                        "Data: {mcName:\"%s\", discordID:\"%s\", auth:\"%s\"}").format(it.minecraftName, it.discordId, it.authCode.authCode))
                registeredAccounts.remove(it)
            }
        }
        registeredAccounts.clear()
        registeredAccounts.addAll(tempList)
        tempList.clear()
    }

    fun createAccount(mc: String, id: String, authCode: String): Pair<Error, Account?> {
        if (existsMc(mc)) return Pair(Error.MC_NAME_IN_LIST, null)
        if (existsDc(id)) return Pair(Error.DC_ID_IN_LIST, null)
        registeredAccounts.add(object : Account {
            override val minecraftName: String
                get() = mc
            override val discordId: String
                get() = id
            override val authCode: AuthCode
                get() = AuthCodeObject(authCode)
        })
        save()
        return Pair(Error.NO_ERROR, getByAuth(authCode))
    }

    fun editAccount(account: Account, data: DataObject): Pair<Error, Account?> {
        if (!data.title.equals("minecraft", ignoreCase = true)) return Pair(Error("Wrong Data Title"), account)
        if (data.body.isEmpty()) return Pair(Error("Data Body cannot be null"), account)
        if (!config.exists("accounts")) config["accounts"] = ArrayList<String>()
        val accountFileData = config.config.getStringList("accounts").stream().filter { d: String ->
            val split = d.split(";;;".toRegex()).toTypedArray()
            split.size == 3 && split[0].equals(account.minecraftName, ignoreCase = true) && split[1].equals(account.discordId, ignoreCase = true) &&
                    split[2].equals(account.authCode.authCode, ignoreCase = true)
        }.findFirst().orElse("null")
        if (accountFileData.isEmpty() || accountFileData.equals("null", ignoreCase = true)) {
            val res = createAccount(account.minecraftName, account.discordId,
                    account.authCode.authCode)
            if (res.left.errorMessage != Error.NO_ERROR.errorMessage) return Pair(res.left, account)
            return Pair(Error.NO_ERROR, res.right)
        }
        if (data.body.split(" ".toRegex()).toTypedArray().size == 1) {
            val name = data.body.split(" ".toRegex()).toTypedArray()[0]
            val del = delAccount(account.discordId)
            if (del.errorMessage != Error.NO_ERROR.errorMessage)
                return Pair(del, account)
            return createAccount(name, account.discordId, account.authCode.authCode)
        } else {
            return Pair(Error("Wrong format"), account)
        }
    }

    fun delAccount(id: String): Error {
        if (!existsDc(id)) return Error("There is no Connection with this Discord ID")
        val account = registeredAccounts.stream().filter { acc: Account -> acc.discordId.equals(id, ignoreCase = true) }.findFirst().orElse(null)
                ?: return Error("Account is null")
        val accountFileData = config.config.getStringList("accounts").stream().filter { d: String ->
            val split = d.split(";;;".toRegex()).toTypedArray()
            split.size == 3 && split[0].equals(account.minecraftName, ignoreCase = true) && split[1].equals(account.discordId, ignoreCase = true) &&
                    split[2].equals(account.authCode.authCode, ignoreCase = true)
        }.findFirst().orElse("null")
        val list = config.config.getStringList("accounts")
        list.removeIf { s: String -> s == accountFileData }
        config["accounts"] = list
        registeredAccounts.removeIf { a: Account -> "${a.minecraftName};;;${a.discordId};;;${a.authCode.authCode}" == accountFileData }
        refresh()
        return Error.NO_ERROR
    }

    fun existsMc(name: String): Boolean {
        return registeredAccounts.stream().anyMatch { acc: Account -> acc.minecraftName.equals(name, ignoreCase = true) }
    }

    fun existsDc(id: String): Boolean {
        return registeredAccounts.stream().anyMatch { acc: Account -> acc.discordId.equals(id, ignoreCase = true) }
    }

    fun getByMc(name: String): Account {
        return registeredAccounts.stream().filter { acc: Account -> acc.minecraftName.equals(name, ignoreCase = true) }
                .findFirst().orElse(null)
    }

    fun getByDc(id: String): Account {
        return registeredAccounts.stream().filter { acc: Account -> acc.discordId.equals(id, ignoreCase = true) }
                .findFirst().orElse(null)
    }

    @Nullable
    fun getByAuth(authCode: String): Account {
        return registeredAccounts.stream().filter { acc: Account -> acc.authCode.authCode.equals(authCode, ignoreCase = true) }
                .findFirst().orElse(null)
    }

    init {
        config = Config(File(Plugin.configFolder, "account_storage.yml"))
        registeredAccounts = ArrayList()
        if (!config.exists("accounts")) config["accounts"] = ArrayList<String>()
        config.config.getStringList("accounts").forEach(Consumer { string: String ->
            val split = string.split(";;;".toRegex()).toTypedArray()
            if (split.size == 3) {
                registeredAccounts.add(AccountObject(split[0], split[1], AuthCode.fromString(split[2])))
            } else {
                throw IllegalArgumentException("Cannot format following Account-String: $string")
            }
        })
    }
}

class AccountObject(override val minecraftName: String, override val discordId: String, override val authCode: AuthCode) : Account