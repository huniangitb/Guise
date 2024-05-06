package com.houvven.guise.util.app

import android.icu.text.Collator


object AppSortComparator {

    @JvmStatic
    val AppNameLocaleComparator = Comparator<App> { o1, o2 ->
        Collator.getInstance().compare(o1.name, o2.name)
    }
}