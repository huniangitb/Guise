package com.houvven.guise.hook.profile

import kotlinx.serialization.Transient

internal interface Profile {

    @Transient
    val isAvailable: Boolean
}