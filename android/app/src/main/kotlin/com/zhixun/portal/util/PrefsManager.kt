package com.zhixun.portal.util

import android.content.Context
import android.content.SharedPreferences

class PrefsManager(private val context: Context) {

    companion object {
        private const val PREFS_NAME = "zhixun_portal_prefs"
        private const val KEY_SERVER_URL = "api_server_url"
        private const val KEY_FRONTEND_URL = "frontend_url"
        private const val KEY_DASHBOARD_URL = "dashboard_url"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_ALLOW_HTTP = "allow_http"

        const val DEFAULT_FRONTEND_URL = "https://ff5241f.r7.cpolar.cn"
        const val DEFAULT_SERVER_URL = "https://ff5241f.r7.cpolar.cn"
        const val DEFAULT_DASHBOARD_URL = "https://ff5241f.r7.cpolar.cn/bigscreen/"
    }

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getServerUrl(): String =
        prefs.getString(KEY_SERVER_URL, DEFAULT_SERVER_URL) ?: DEFAULT_SERVER_URL

    fun setServerUrl(url: String) {
        prefs.edit().putString(KEY_SERVER_URL, url.trimEnd('/')).apply()
    }

    fun getFrontendUrl(): String =
        prefs.getString(KEY_FRONTEND_URL, DEFAULT_FRONTEND_URL) ?: DEFAULT_FRONTEND_URL

    fun setFrontendUrl(url: String) {
        prefs.edit().putString(KEY_FRONTEND_URL, url.trimEnd('/')).apply()
    }

    fun getDashboardUrl(): String =
        prefs.getString(KEY_DASHBOARD_URL, DEFAULT_DASHBOARD_URL) ?: DEFAULT_DASHBOARD_URL

    fun setDashboardUrl(url: String) {
        prefs.edit().putString(KEY_DASHBOARD_URL, url.trimEnd('/')).apply()
    }

    fun isFirstLaunch(): Boolean =
        prefs.getBoolean(KEY_FIRST_LAUNCH, true)

    fun setFirstLaunchDone() {
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
    }

    fun isAllowHttp(): Boolean =
        prefs.getBoolean(KEY_ALLOW_HTTP, true)

    fun setAllowHttp(allow: Boolean) {
        prefs.edit().putBoolean(KEY_ALLOW_HTTP, allow).apply()
    }
}
