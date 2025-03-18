package jp.client.util.account

import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import jp.client.util.web.Browser.getBearerResponse
import jp.client.util.web.Browser.postExternal
import org.apache.http.client.utils.URLEncodedUtils
import java.awt.Desktop
import java.io.IOException
import java.net.InetSocketAddress
import java.net.URI
import java.net.URISyntaxException
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors
import java.util.function.Consumer

object MicrosoftLogin {
    private val executor = Executors.newSingleThreadExecutor()

    class LoginData {
        var mcToken: String? = null
        var newRefreshToken: String? = null
        var uuid: String? = null
        var username: String? = null

        constructor()
        constructor(mcToken: String?, newRefreshToken: String?, uuid: String?, username: String?) {
            this.mcToken = mcToken
            this.newRefreshToken = newRefreshToken
            this.uuid = uuid
            this.username = username
        }

        val isGood: Boolean
            get() = mcToken != null
    }


    private const val CLIENT_ID = "54fd49e4-2103-4044-9603-2b028c814ec3"
    private const val PORT = 59125

    private var server: HttpServer? = null
    private var callback: Consumer<String?>? = null

    private fun browse(url: String) {
        try {
            Desktop.getDesktop().browse(URI(url))
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    fun getRefreshToken(callback: Consumer<String?>?) {
        MicrosoftLogin.callback = callback
        startServer()
        browse("https://login.live.com/oauth20_authorize.srf?client_id=$CLIENT_ID&response_type=code&redirect_uri=http://localhost:$PORT&scope=XboxLive.signin%20offline_access")
    }

    private val gson = Gson()

    fun login(refreshToken: String?): LoginData {
        // Refresh access token
        var refreshToken = refreshToken
        val res = gson.fromJson(
            postExternal(
                "https://login.live.com/oauth20_token.srf",
                "client_id=$CLIENT_ID&refresh_token=$refreshToken&grant_type=refresh_token&redirect_uri=http://localhost:$PORT",
                false
            ),
            AuthTokenResponse::class.java
        ) ?: return LoginData()
        val accessToken = res.access_token
        refreshToken = res.refresh_token

        // XBL
        val xblRes = gson.fromJson(
            postExternal(
                "https://user.auth.xboxlive.com/user/authenticate",
                "{\"Properties\":{\"AuthMethod\":\"RPS\",\"SiteName\":\"user.auth.xboxlive.com\",\"RpsTicket\":\"d=$accessToken\"},\"RelyingParty\":\"http://auth.xboxlive.com\",\"TokenType\":\"JWT\"}",
                true
            ),
            XblXstsResponse::class.java
        ) ?: return LoginData()

        // XSTS
        val xstsRes = gson.fromJson(
            postExternal(
                "https://xsts.auth.xboxlive.com/xsts/authorize",
                "{\"Properties\":{\"SandboxId\":\"RETAIL\",\"UserTokens\":[\"" + xblRes.Token + "\"]},\"RelyingParty\":\"rp://api.minecraftservices.com/\",\"TokenType\":\"JWT\"}",
                true
            ),
            XblXstsResponse::class.java
        ) ?: return LoginData()

        // Minecraft
        val mcRes = gson.fromJson(
            postExternal(
                "https://api.minecraftservices.com/authentication/login_with_xbox",
                "{\"identityToken\":\"XBL3.0 x=" + xblRes.DisplayClaim!!.xui?.get(0)!!.uhs + ";" + xstsRes.Token + "\"}", true
            ),
            McResponse::class.java
        ) ?: return LoginData()

        // Check game ownership
        /*
        val gameOwnershipRes = gson.fromJson(
            getBearerResponse("https://api.minecraftservices.com/entitlements/mcstore", mcRes.access_token!!),
            GameOwnershipResponse::class.java
        )
        if (gameOwnershipRes == null || !gameOwnershipRes.hasGameOwnership()) return LoginData()

         */

        // Profile
        val profileRes = gson.fromJson(
            getBearerResponse("https://api.minecraftservices.com/minecraft/profile", mcRes.access_token!!),
            ProfileResponse::class.java
        ) ?: return LoginData()
        return LoginData(mcRes.access_token, refreshToken, profileRes.id, profileRes.name)
    }

    private fun startServer() {
        if (server != null) return
        try {
            server = HttpServer.create(InetSocketAddress("localhost", PORT), 0)
            (server as HttpServer).createContext("/", Handler())
            (server as HttpServer).setExecutor(executor)
            (server as HttpServer).start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun stopServer() {
        if (server == null) return
        server!!.stop(0)
        server = null
        callback = null
    }


    private class Handler : HttpHandler {
        @Throws(IOException::class)
        override fun handle(req: HttpExchange) {
            if (req.requestMethod == "GET") {
                // Login
                val query = URLEncodedUtils.parse(req.requestURI, StandardCharsets.UTF_8.name())
                var ok = false
                for (pair in query) {
                    if (pair.name == "code") {
                        handleCode(pair.value)
                        ok = true
                        break
                    }
                }
                if (!ok) writeText(req, "Cannot authenticate.") else writeText(
                    req,
                    "<html>You may now close this page.<script>close()</script></html>"
                )
            }
            stopServer()
        }

        private fun handleCode(code: String) {
            //System.out.println(code);
            val response = postExternal(
                "https://login.live.com/oauth20_token.srf",
                "client_id=$CLIENT_ID&code=$code&grant_type=authorization_code&redirect_uri=http://localhost:$PORT",
                false
            )
            val res = gson.fromJson(
                response,
                AuthTokenResponse::class.java
            )
            if (res == null) callback!!.accept(null) else callback!!.accept(res.refresh_token)
        }

        @Throws(IOException::class)
        private fun writeText(req: HttpExchange, text: String) {
            val out = req.responseBody
            req.responseHeaders.add("Content-Type", "text/html; charset=utf-8")
            req.sendResponseHeaders(200, text.length.toLong())
            out.write(text.toByteArray(StandardCharsets.UTF_8))
            out.flush()
            out.close()
        }
    }


    private class AuthTokenResponse {
        @Expose
        @SerializedName("access_token")
        var access_token: String? = null

        @Expose
        @SerializedName("refresh_token")
        var refresh_token: String? = null
    }


    private class XblXstsResponse {
        @Expose
        @SerializedName("Token")
        var Token: String? = null

        @Expose
        @SerializedName("DisplayClaims")
        var DisplayClaim: DisplayClaims? = null

        class DisplayClaims {
            @Expose
            @SerializedName("xui")
            val xui: Array<Claim>? = null

            class Claim {
                @Expose
                @SerializedName("uhs")
                val uhs: String? = null
            }
        }
    }


    private class McResponse {
        @Expose
        @SerializedName("access_token")
        var access_token: String? = null
    }


    private class GameOwnershipResponse {
        @Expose
        @SerializedName("items")
        private val items: Array<Item>? = null

        private class Item {
            @Expose
            @SerializedName("name")
            val name: String? = null
        }

        fun hasGameOwnership(): Boolean {
            var hasProduct = false
            var hasGame = false
            if (items != null) {
                for (item in items) {
                    if (item.name == "product_minecraft") hasProduct =
                        true else if (item.name == "game_minecraft") hasGame = true
                }
            }
            return hasProduct && hasGame
        }
    }


    private class ProfileResponse {
        @Expose
        @SerializedName("id")
        var id: String? = null

        @Expose
        @SerializedName("name")
        var name: String? = null
    }

}