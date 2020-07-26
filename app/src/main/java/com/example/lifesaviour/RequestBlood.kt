package com.example.lifesaviour

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import java.util.*


class RequestBlood : AppCompatActivity() {
    private var myRef : DatabaseReference? = null
    lateinit var notification_manager: NotificationManager
    lateinit var notification_channel: NotificationChannel
    lateinit var notification_builder: Notification.Builder
    private val channelId = "com.example.lifesaviour"
    private val description = " "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_blood)

        val mPickTimeBtn = findViewById<ImageButton>(R.id.date_Picker)
        val textView     = findViewById<TextView>(R.id.inputDate)
         val notification_button = findViewById<Button>(R.id.button_post)
        notification_manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager



        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        mPickTimeBtn.setOnClickListener {

            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, month, day ->
                // Display Selected date in TextView
                textView.text = " " + day + "/" + (month+1) + "/" + year
            }, year, month, day)
            dpd.show()

        }

        FirebaseApp.initializeApp(this)

        // Write a message to the database
        val database = FirebaseDatabase.getInstance()
        myRef = database.getReference("Request Blood")

    }

    fun post_button(view: View) {
        //Store value in database
        val name = findViewById<EditText>(R.id.input_fullName)
        val blood_units = findViewById<EditText>(R.id.input_blood_unit)
        val details = findViewById<EditText>(R.id.inputDetail)
        val contact_no = findViewById<EditText>(R.id.inputMobile)
        val location = findViewById<EditText>(R.id.inputLocation)
        val date = findViewById<TextView>(R.id.inputDate)
        val spinnerBG = findViewById<Spinner>(R.id.inputBloodGroup)

        val key = myRef?.push()?.key
        val pat1 = key?.let { myRef?.child(it) }

        if (name.text.isEmpty()) {
            Toast.makeText(
                applicationContext, "Enter your Name!",
                Toast.LENGTH_LONG
            ).show()
        } else if (blood_units.text.isEmpty()) {
            Toast.makeText(
                applicationContext, "Enter Blood Units required!",
                Toast.LENGTH_LONG
            ).show()
        } else if (contact_no.text.isEmpty()) {
            Toast.makeText(
                applicationContext, "Enter your Contact Number!",
                Toast.LENGTH_LONG
            ).show()
        } else if (location.text.isEmpty()) {
            Toast.makeText(
                applicationContext, "Enter your Hospital Location!",
                Toast.LENGTH_LONG
            ).show()
        }else if (date.text.isEmpty()) {
            Toast.makeText(
                applicationContext, "Select the date!",
                Toast.LENGTH_LONG
            ).show()
        } else if (spinnerBG.selectedItem == "Select your blood group") {
            Toast.makeText(
                applicationContext, "Enter your Blood Group!",
                Toast.LENGTH_LONG
            ).show()
        }
        else {

            pat1?.child("Name")?.setValue(name.text.toString())
            pat1?.child("Blood_Units")?.setValue(blood_units.text.toString())
            pat1?.child("Details")?.setValue(details.text.toString())
            pat1?.child("Contact_No")?.setValue(contact_no.text.toString())
            pat1?.child("Location")?.setValue(location.text.toString())
            pat1?.child("Posted_Date")?.setValue(date.text.toString())
            pat1?.child("Blood_Group")?.setValue(spinnerBG.selectedItem.toString())


            Toast.makeText(
                this@RequestBlood,
                "Your post has been created successfully",
                Toast.LENGTH_LONG
            ).show()


            //get value from database
            val query = key?.let { myRef?.child(it) }
            query?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                Log.e("On Failure:", databaseError.message)
                }

                override fun onDataChange(data: DataSnapshot) {
                    processData(data)

                }

            })


            //Notification Code
            val noti_intent = Intent(this, RequestBloodList::class.java)
            val pendingIntent =
                PendingIntent.getActivity(this, 0, noti_intent, PendingIntent.FLAG_UPDATE_CURRENT)

            val notiCustomView = RemoteViews(packageName, R.layout.notification_layout)
            notiCustomView.setTextViewText(R.id.noti_title, "Life Saviour")
            val noti_text = "Requesting " + spinnerBG.selectedItem.toString() +
                    " for a patient at " + location.text.toString()
            notiCustomView.setTextViewText(R.id.noti_info, noti_text)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                notification_channel =
                    NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
                notification_channel.enableLights(true)
                notification_channel.lightColor = Color.RED
                notification_channel.enableVibration(true)
                notification_manager.createNotificationChannel(notification_channel)


                notification_builder = Notification.Builder(this, channelId)
                    .setContent(notiCustomView)
                    .setSmallIcon(R.drawable.slifetext)
                    .setLargeIcon(
                        BitmapFactory.decodeResource(
                            this.resources,
                            R.drawable.slifetext
                        )
                    )
                    .setContentIntent(pendingIntent)
            } else {
                notification_builder = Notification.Builder(this)
                    .setContent(notiCustomView)
                    .setSmallIcon(R.drawable.slifetext)
                    .setLargeIcon(
                        BitmapFactory.decodeResource(
                            this.resources,
                            R.drawable.slifetext
                        )
                    )
                    .setContentIntent(pendingIntent)
            }

            notification_manager.notify(1234, notification_builder.build())
        }
    }


    private fun processData(data: DataSnapshot) {

        val post_intent = Intent(this, RequestBloodList::class.java)
        startActivity(post_intent)


    }
}
