package com.houvven.guise.hook.hooker

import android.os.Build
import com.highcapable.yukihookapi.hook.factory.classOf
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.type.android.BuildClass
import com.houvven.guise.hook.hooker.base.GuiseBaseHooker
import com.houvven.guise.hook.profile.item.PropertiesProfile
import com.houvven.guise.hook.type.SystemPropertiesClass


internal class PropertiesHooker(profile: PropertiesProfile) :
    GuiseBaseHooker<PropertiesProfile>(profile) {

    private val options = mutableListOf<PropertiesHookOption>()

    init {
        options.addAll(buildHookOption())
    }


    override fun doHook() = options.forEach { option ->
        val (value, fieldName, propertiesKey, type) = option

        // Hook the value of the field in the Build class
        if (!fieldName.isNullOrBlank()) {
            when (type) {
                BuildPropAscription.BUILD -> BuildClass
                BuildPropAscription.VERSION -> classOf<Build.VERSION>()
                BuildPropAscription.VERSION_CODES -> classOf<Build.VERSION_CODES>()
                null -> null
            }?.run {
                field { name = fieldName }.ignored().get(null).set(value)
            }
        }

        // Hook the value of the system properties
        if (propertiesKey.isNotBlank()) {
            SystemPropertiesClass.run {
                method {
                    name = "native_get"
                }.hook().before {
                    if (args[0] == propertiesKey) {
                        result = value
                    }
                }
            }
        }
    }


    private fun buildHookOption() = profile.run {
        mutableListOf(
            PropertiesHookOption(
                value = brand,
                fieldName = "BRAND",
                propertiesKey = "ro.product.brand"
            ),
            PropertiesHookOption(
                value = manufacturer,
                fieldName = "MANUFACTURER",
                propertiesKey = "ro.product.manufacturer"
            ),
            PropertiesHookOption(
                value = model,
                fieldName = "MODEL",
                propertiesKey = "ro.product.model"
            ),
            PropertiesHookOption(
                value = product,
                fieldName = "PRODUCT",
                propertiesKey = "ro.product.name"
            ),
            PropertiesHookOption(
                value = device,
                fieldName = "DEVICE",
                propertiesKey = "ro.product.device"
            ),
            PropertiesHookOption(
                value = characteristics,
                propertiesKey = "ro.build.characteristics"
            )
        ).filter { it.value != null }.let {
            it + customProperties.map { (key, value) ->
                PropertiesHookOption(
                    value = value,
                    propertiesKey = key
                )
            }
        }
    }


    enum class BuildPropAscription {
        VERSION,
        VERSION_CODES,
        BUILD
    }

    data class PropertiesHookOption(
        val value: Any?,
        /**
         * The field name of [android.os.Build] class. such as `BRAND`, `MODEL`, `PRODUCT`, `DEVICE`
         */
        val fieldName: String? = null,
        /**
         * The property key of system properties. such as `ro.product.brand`, `ro.product.model`, `ro.product.name`, `ro.product.device`
         */
        val propertiesKey: String,
        /**
         *Used to mark the attribution of attributes, for example [android.os.Build], [android.os.Build.VERSION], [android.os.Build.VERSION_CODES]
         */
        val type: BuildPropAscription? = null
    )
}