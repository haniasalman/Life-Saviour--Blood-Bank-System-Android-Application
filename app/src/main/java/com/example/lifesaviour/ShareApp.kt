package com.example.lifesaviour

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class ShareApp : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_app)

        val share_app = findViewById<Button>(R.id.shareapp_button)

        share_app.setOnClickListener {

            val appPackageName = BuildConfig.APPLICATION_ID
            val appName = this.getString(R.string.app_name);
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            val shareBodyText = "https://play.google.com/store/apps/details?id=$appPackageName"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, appName)
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareBodyText)
            startActivity(Intent.createChooser(shareIntent, "Share this App: " ))

        }
    }
}
