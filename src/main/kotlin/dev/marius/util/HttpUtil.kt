package dev.marius.util

import java.security.NoSuchAlgorithmException
import java.security.KeyManagementException
import java.io.IOException
import dev.marius.util.HTTPException
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import javax.net.ssl.HttpsURLConnection
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.URL
import java.security.SecureRandom
import java.security.cert.X509Certificate

object HttpUtil {
    @JvmStatic
    @Throws(NoSuchAlgorithmException::class, KeyManagementException::class, IOException::class, HTTPException::class)
    fun makeGetRequest(url: String?): String {
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, arrayOf<TrustManager>(
            object : X509TrustManager {
                override fun checkClientTrusted(x509Certificates: Array<X509Certificate>, s: String) {}
                override fun checkServerTrusted(x509Certificates: Array<X509Certificate>, s: String) {}
                override fun getAcceptedIssuers(): Array<X509Certificate>? {
                    return null
                }
            }
        ), SecureRandom())
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.socketFactory)
        val client = URL(url).openConnection() as HttpsURLConnection
        client.setRequestProperty(
            "User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36"
        )
        val responseCode = client.responseCode
        if (responseCode != 200) throw HTTPException(url!!, responseCode)
        BufferedReader(InputStreamReader(client.inputStream)).use { bufferedReader ->
            val responseBuilder = StringBuilder()
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                responseBuilder.append(line).append("\n")
            }
            bufferedReader.close()
            return responseBuilder.toString()
        }
    }
}