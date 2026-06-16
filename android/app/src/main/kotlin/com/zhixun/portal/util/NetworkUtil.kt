package com.zhixun.portal.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class NetworkUtil(private val context: Context) {

    fun isNetworkAvailable(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    suspend fun testServerConnection(serverUrl: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val urlStr = serverUrl.trimEnd('/')
                val url = URL("$urlStr/api/news?page=1&size=1")
                val conn = url.openConnection() as HttpURLConnection
                conn.connectTimeout = 8000
                conn.readTimeout = 8000
                conn.requestMethod = "GET"
                conn.setRequestProperty("Accept", "application/json")

                val responseCode = conn.responseCode
                if (responseCode in 200..299) {
                    Result.success("连接成功 (HTTP $responseCode)")
                } else {
                    Result.failure(Exception("HTTP $responseCode: ${conn.responseMessage}"))
                }
            } catch (e: java.net.SocketTimeoutException) {
                Result.failure(Exception("连接超时"))
            } catch (e: java.net.UnknownHostException) {
                Result.failure(Exception("无法解析域名"))
            } catch (e: java.net.ConnectException) {
                Result.failure(Exception("无法连接到服务器"))
            } catch (e: javax.net.ssl.SSLException) {
                Result.failure(Exception("SSL 证书错误: ${e.message}"))
            } catch (e: Exception) {
                Result.failure(Exception(e.message ?: "未知错误"))
            }
        }
    }
}
