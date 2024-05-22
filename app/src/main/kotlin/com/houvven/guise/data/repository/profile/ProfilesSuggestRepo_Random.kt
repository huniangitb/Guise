package com.houvven.guise.data.repository.profile

import com.houvven.guise.data.domain.ProfileSuggest
import java.security.SecureRandom
import kotlin.random.Random

private typealias RandomRepo<T> = ProfilesSuggestRepo.Random<T>

@OptIn(ExperimentalStdlibApi::class)
val AndroidIdRandomRepo = RandomRepo {
    val hexString = ByteArray(8).apply { SecureRandom().nextBytes(this) }.toHexString()
    ProfileSuggest(hexString, hexString)
}

val MacAddressRandomRepo = RandomRepo {
    val bytes = ByteArray(6).apply { SecureRandom().nextBytes(this) }
    val macAddress = bytes.joinToString(":") { it.toUByte().toString(16).padStart(2, '0') }
    ProfileSuggest(macAddress, macAddress)
}

val WiFiNameRandomRepo = RandomRepo {
    val wifiBrand = listOf("TP-Link", "ASUS", "Xiaomi", "HUAWEI")
    val randomString = (1..(4..8).random()).joinToString("") { ('A'..'Z').random().toString() }
    val wifiName = listOf(
        wifiBrand.random(),
        randomString,
        if (Random.nextBoolean()) "5G" else ""
    ).joinToString("-")
    ProfileSuggest(wifiName, wifiName)
}