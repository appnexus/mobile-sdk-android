package appnexus.com.trackertestapp.util

import android.content.Context
import android.net.wifi.WifiManager
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.*

class Util {

    companion object {

        fun getWifiIp(context: Context): String? {
            return context.getSystemService<WifiManager>().let {
                when {
//                    it == null -> "No wifi available"
                    !it.isWifiEnabled -> "Wifi is disabled"
                    it.connectionInfo == null -> "Wifi not connected"
                    else -> {
                        it.isWifiEnabled = false
                        val ip = it.connectionInfo.ipAddress
                        ((ip and 0xFF).toString() + "." + (ip shr 8 and 0xFF) + "." + (ip shr 16 and 0xFF) + "." + (ip shr 24 and 0xFF))
                    }
                }
            }
        }


        fun getMobileIPAddress(): InetAddress? {
            try {
                val interfaces: List<NetworkInterface> =
                    Collections.list(NetworkInterface.getNetworkInterfaces())
                for (intf in interfaces) {
                    val addrs: List<InetAddress> = Collections.list(intf.getInetAddresses())
                    for (addr in addrs) {
                        if (!addr.isLoopbackAddress() && addr is Inet4Address) {
                            return addr
                        }
                    }
                }
            } catch (ex: Exception) {
            } // for now eat exceptions
            return null
        }
    }
}

private fun <T> Context.getSystemService(): WifiManager {
    return getSystemService(Context.WIFI_SERVICE) as WifiManager
}
