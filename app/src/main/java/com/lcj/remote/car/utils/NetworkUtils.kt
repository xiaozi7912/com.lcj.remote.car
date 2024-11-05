package com.lcj.remote.car.utils

import android.content.Context
import android.net.wifi.WifiManager
import android.text.format.Formatter

object NetworkUtils {
    fun getGatewayIp(context: Context): String? {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val dhcpInfo = wifiManager.dhcpInfo
        val gatewayIp = Formatter.formatIpAddress(dhcpInfo.gateway)
        return gatewayIp
    }
}