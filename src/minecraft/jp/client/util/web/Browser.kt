package jp.client.util.web

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import java.nio.charset.StandardCharsets
import javax.net.ssl.HttpsURLConnection

object Browser {
    fun postExternal(url: String, post: String, json: Boolean): String? {
        return try {
            val connection = URL(url).openConnection() as HttpsURLConnection
            connection.addRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36"
            )
            connection.requestMethod = "POST"
            connection.doOutput = true
            val out: ByteArray = post.toByteArray(StandardCharsets.UTF_8)
            val length = out.size
            connection.setFixedLengthStreamingMode(length)
            connection.addRequestProperty(
                "Content-Type",
                if (json) "application/json" else "application/x-www-form-urlencoded; charset=UTF-8"
            )
            connection.addRequestProperty("Accept", "application/json")
            connection.connect()
            connection.outputStream.use { os -> os.write(out) }
            val responseCode = connection.responseCode
            val stream: InputStream? =
                if (responseCode / 100 == 2 || responseCode / 100 == 3) connection.inputStream else connection.errorStream
            if (stream == null) {
                System.err.println("$responseCode: $url")
                return null
            }
            val reader = BufferedReader(InputStreamReader(stream))
            var lineBuffer: String?
            val response = StringBuilder()
            while (reader.readLine().also { lineBuffer = it } != null) {
                response.append(lineBuffer)
            }
            reader.close()
            response.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getBearerResponse(url: String?, bearer: String): String? {
        return try {
            val connection = URL(url).openConnection() as HttpsURLConnection
            connection.addRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36"
            )
            connection.addRequestProperty("Accept", "application/json")
            connection.addRequestProperty("Authorization", "Bearer $bearer")
            if (connection.responseCode == 200) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                var lineBuffer: String?
                val response = StringBuilder()
                while (reader.readLine().also { lineBuffer = it } != null) {
                    response.append(lineBuffer)
                }
                response.toString()
            } else {
                val reader = BufferedReader(InputStreamReader(connection.errorStream))
                var lineBuffer: String?
                val response = StringBuilder()
                while (reader.readLine().also { lineBuffer = it } != null) {
                    response.append(lineBuffer)
                }
                response.toString()
            }
        } catch (e: Exception) {
            null
        }
    }
}