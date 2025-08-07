package com.huniangitb.guise

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager

class BypassLineageActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var enableButton: Button
    private var selectedAppPackage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bypass_lineage)

        listView = findViewById(R.id.app_list)
        enableButton = findViewById(R.id.enable_button)
        val appList = loadInstalledApps()

        // Display app names using an ArrayAdapter.
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, appList.map { it.first })
        listView.adapter = adapter
        listView.choiceMode = ListView.CHOICE_MODE_SINGLE

        listView.setOnItemClickListener { _, _, position, _ ->
            selectedAppPackage = appList[position].second
        }

        enableButton.setOnClickListener {
            if (selectedAppPackage == null) {
                Toast.makeText(this, "Please select an app.", Toast.LENGTH_SHORT).show()
            } else {
                // For demonstration purposes, we set the system property to enable bypass.
                System.setProperty("guixposed.bypassLineage", "true")
                BypassLineageCheck.bypass()
                Toast.makeText(this, "Bypass feature enabled for $selectedAppPackage", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadInstalledApps(): List<Pair<String, String>> {
        val pm = packageManager
        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        // Optionally, filter out system apps and show only non-system apps.
        return apps.filter { (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 }
            .map { Pair(pm.getApplicationLabel(it).toString(), it.packageName) }
            .sortedBy { it.first }
    }
}