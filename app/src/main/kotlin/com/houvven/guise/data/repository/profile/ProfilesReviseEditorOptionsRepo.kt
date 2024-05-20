package com.houvven.guise.data.repository.profile

import android.content.Context
import android.icu.text.Collator
import android.net.ConnectivityManager
import com.houvven.guise.R
import com.houvven.guise.data.domain.ProfileSuggest
import java.util.Locale


class ProfilesReviseEditorOptionsRepo(context: Context) {

    val characteristics = ProfileSuggest.create(
        "tablet" to "tablet",
        "nosdcard" to "nosdcard",
        "default" to "default"
    )

    val language = Locale.getAvailableLocales().map {
        ProfileSuggest(it.displayName, it.toString())
    }.sortedWith { o1, o2 ->
        Collator.getInstance().compare(o1.label, o2.label)
    }

    val boolean: List<ProfileSuggest<Boolean>> = ProfileSuggest.create(
        context,
        R.string.turn_on to true,
        R.string.turn_off to false
    )

    @Suppress("DEPRECATION")
    val networkType: List<ProfileSuggest<Int>> = ProfileSuggest.create(
        context,
        R.string.network_type_none to -1,
        R.string.network_type_mobile to ConnectivityManager.TYPE_MOBILE,
        R.string.network_type_mobile_mms to ConnectivityManager.TYPE_MOBILE_MMS,
        R.string.network_type_wifi to ConnectivityManager.TYPE_WIFI,
        R.string.network_type_bluetooth to ConnectivityManager.TYPE_BLUETOOTH,
        R.string.network_type_wimax to ConnectivityManager.TYPE_WIMAX,
        R.string.network_type_ethernet to ConnectivityManager.TYPE_ETHERNET,
        R.string.network_type_vpn to ConnectivityManager.TYPE_VPN,
        R.string.network_type_proxy to 16
    )
}