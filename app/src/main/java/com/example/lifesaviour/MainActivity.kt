package com.example.lifesaviour

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val request_blood_imageView = findViewById<ImageView>(R.id.request_blood_button)
        val add_donor_imageView = findViewById<ImageView>(R.id.add_donor_button)
        val share_app_imageView = findViewById<ImageView>(R.id.share_app_button)
        val search_blood_banks_imageView = findViewById<ImageView>(R.id.search_blood_banks_button)
        val find_donor_imageView = findViewById<ImageView>(R.id.find_donor_button)
        val view_bloodrqst_imageView = findViewById<ImageView>(R.id.view_bloodrqst_button)

        //Request Blood Activity

        request_blood_imageView.setOnClickListener {

            val request_blood_intent = Intent(this, RequestBlood::class.java)
            startActivity(request_blood_intent)
        }

        //Add Donor Activity

        add_donor_imageView.setOnClickListener {

            val add_donor_intent = Intent(this, AddDonor::class.java)
            startActivity(add_donor_intent)
        }


        //Share app Activity
        share_app_imageView.setOnClickListener {
            val share_app_intent = Intent(this, ShareApp::class.java)
            startActivity(share_app_intent)
        }


        //Search Blood Banks Activity
        search_blood_banks_imageView.setOnClickListener {
            val search_blood_banks_intent = Intent(this, SearchBloodBanks::class.java)
            startActivity(search_blood_banks_intent)

//            initGoogleMap()
        }

        //Find Donor Activity
        find_donor_imageView.setOnClickListener {
            val find_donor_intent = Intent(this, FindDonorList::class.java)
            startActivity(find_donor_intent)

        }

        //View Blood Requests Activity
        view_bloodrqst_imageView.setOnClickListener {
            val view_bloodrqst_intent = Intent(this, RequestBloodList::class.java)
            startActivity(view_bloodrqst_intent)

        }

    }
}
