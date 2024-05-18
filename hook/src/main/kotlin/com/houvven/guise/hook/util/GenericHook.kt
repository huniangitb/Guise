package com.houvven.guise.hook.util

import android.util.Log
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.param.PackageParam

fun PackageParam.printCallStack(tag: String, clazz: Class<*>) {
    clazz.method {
        modifiers {
            !isAbstract
        }
    }.hookAll().after {
        val stackTrace = Thread.currentThread().stackTrace
        val methodName = method.toString()
        val argsType = method.parameterTypes.joinToString(", ") { it.simpleName }
        val argsValue = args.joinToString(", ") { it.toString() }
        val resultType = result?.javaClass?.simpleName ?: "null"
        val resultValue = result?.toString() ?: "null"
        val stackTraceString = stackTrace.joinToString("\n") { it.toString() }

        Log.d(tag, "")
        Log.d(tag, buildString {
            appendLine("\n")
            appendLine("====================================================")
            appendLine("Method: $methodName")
            appendLine("Args Type: $argsType")
            appendLine("Args Value: $argsValue")
            appendLine("Result Type: $resultType")
            appendLine("Result Value: $resultValue")
            appendLine("Stack Trace:")
            appendLine(stackTraceString)
            appendLine("====================================================")
            appendLine("\n")
        })
        Log.d(tag, "")
    }
}