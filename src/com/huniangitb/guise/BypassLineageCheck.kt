package com.huniangitb.guise

import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * BypassLineageCheck provides a utility to bypass the lineage environment check.
 * 
 * The functionality uses a shell command pipeline to process system properties using resetprop and awk. 
 * It executes the following command:
 *
 * resetprop | awk -F '\[|\]: \[|\]' '/lineage/ {
 *     key=$2
 *     value=$3
 *     if (key ~ /lineage/) {
 *         system("resetprop --delete \"" key "\"")
 *     } else if (value ~ /lineage/) {
 *         gsub("lineage_?", "", value)
 *         gsub("userdebug", "user", value)
 *         gsub("test-keys", "release-keys", value)
 *         system("resetprop \"" key "\" \"" value "\"")
 *     }
 * }
 *
 * The command clears lineage-related properties or modifies them accordingly. 
 * The execution is only applied if the scope is appropriate, as defined by isWithinScope().
 */
object BypassLineageCheck {

    fun bypass() {
        if (!isWithinScope()) {
            // Not within the intended scope, so do nothing.
            return
        }
        try {
            // Construct the command. Note the escaping required in the shell command.
            val command = arrayOf(
                "sh", "-c",
                "resetprop | awk -F '\\[|\\]: \\[[|\\]' '/lineage/ { " +
                "key=\\$2; value=\\$3; " +
                "if (key ~ /lineage/) { system(\"resetprop --delete \" key \"\") } " +
                "else if (value ~ /lineage/) { " +
                "gsub(\"lineage_?\", \"\", value); " +
                "gsub(\"userdebug\", \"user\", value); " +
                "gsub(\"test-keys\", \"release-keys\", value); " +
                "system(\"resetprop \" key \" \" value \"\") } }"
            )
            
            val process = Runtime.getRuntime().exec(command)
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                println(line)
            }
            process.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Checks whether the current execution context is within the intended scope.
     * This can be customized to perform environment checks, for example using system properties or build configurations.
     * Here, it only proceeds if the system property 'guixposed.bypassLineage' is set to "true".
     */
    private fun isWithinScope(): Boolean {
        return System.getProperty("guixposed.bypassLineage") == "true"
    }
}