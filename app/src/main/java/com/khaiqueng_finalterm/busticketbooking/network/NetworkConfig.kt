package com.khaiqueng_finalterm.busticketbooking.network

import android.os.Build

object NetworkConfig {
    private const val EMULATOR_BASE_URL = "http://10.0.2.2:8088"
    private const val PHYSICAL_DEVICE_BASE_URL = "http://192.168.1.2:8088"

    val BASE_URL: String
        get() = if (isEmulator()) EMULATOR_BASE_URL else PHYSICAL_DEVICE_BASE_URL

    private fun isEmulator(): Boolean {
        val fingerprint = Build.FINGERPRINT.lowercase()
        val model = Build.MODEL.lowercase()
        val manufacturer = Build.MANUFACTURER.lowercase()
        val brand = Build.BRAND.lowercase()
        val device = Build.DEVICE.lowercase()
        val product = Build.PRODUCT.lowercase()

        return fingerprint.startsWith("generic") ||
            fingerprint.startsWith("unknown") ||
            model.contains("google_sdk") ||
            model.contains("emulator") ||
            model.contains("android sdk built for") ||
            manufacturer.contains("genymotion") ||
            brand.startsWith("generic") && device.startsWith("generic") ||
            product == "google_sdk"
    }
}
