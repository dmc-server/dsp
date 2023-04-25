package dev.marius.util

import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.*
import javax.crypto.spec.SecretKeySpec

/*

Project: David Server Plugin
Author: Marius
Created: 27.11.2020, 16:34

*/
object DataCipher {
    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES/ECB/PKCS5Padding"
    @JvmStatic
    fun encryptMessage(text: String, password: String): String {
        val cipher: Cipher
        try {
            cipher = Cipher.getInstance(TRANSFORMATION)
            val secretKey: SecretKey = SecretKeySpec(password.toByteArray(), ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val encryptedMessage = cipher.doFinal(text.toByteArray())
            return String(encryptedMessage)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        } catch (e: BadPaddingException) {
            e.printStackTrace()
        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace()
        }
        return ""
    }

    fun decryptMessage(text: String, password: String): String {
        try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val secretKey: SecretKey = SecretKeySpec(password.toByteArray(), ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, secretKey)
            val clearMessage = cipher.doFinal(text.toByteArray())
            return String(clearMessage)
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace()
        } catch (e: BadPaddingException) {
            e.printStackTrace()
        }
        return ""
    }
}