package com.example.lifesaviour

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*

class AddDonor : AppCompatActivity() {

    private var myRef : DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_donor)

        FirebaseApp.initializeApp(this)

        // Write a message to the database
        val database = FirebaseDatabase.getInstance()
        myRef = database.getReference("Add Donor")

    }

    fun register_button(view: View) {

        val name = findViewById<EditText>(R.id.input_fullName)
        val contact_no = findViewById<EditText>(R.id.inputMobile)
        val address = findViewById<EditText>(R.id.inputAddress)
        val spinnerBG = findViewById<Spinner>(R.id.inputBloodGroup)
        val male = findViewById<RadioButton>(R.id.male_rb)
        val female = findViewById<RadioButton>(R.id.female_rb)



        val key = myRef?.push()?.key
        val pat1 = key?.let { myRef?.child(it) }

        if (name.text.isEmpty()) {
            Toast.makeText(
                applicationContext, "Enter your Name!",
                Toast.LENGTH_LONG
            ).show()
        } else if (contact_no.text.isEmpty()) {
            Toast.makeText(
                applicationContext, "Enter your Contact Number!",
                Toast.LENGTH_LONG
            ).show()
        } else if (address.text.isEmpty()) {
            Toast.makeText(
                applicationContext, "Enter your Address!",
                Toast.LENGTH_LONG
            ).show()
        }else if (spinnerBG.selectedItem == "Select your blood group") {
            Toast.makeText(
                applicationContext, "Enter your Blood Group!",
                Toast.LENGTH_LONG
            ).show()
        }else if (male.text.isEmpty() || female.text.isEmpty() ) {
            Toast.makeText(
                applicationContext, "Select your Gender!",
                Toast.LENGTH_LONG
            ).show()
        }  else {

            pat1?.child("Name")?.setValue(name.text.toString())
            pat1?.child("Contact_No")?.setValue(contact_no.text.toString())
            pat1?.child("Address")?.setValue(address.text.toString())
            pat1?.child("Blood_Group")?.setValue(spinnerBG.selectedItem.toString())

            if (male.isChecked) {
                pat1?.child("Gender")?.setValue(male.text.toString())
            } else {
                pat1?.child("Gender")?.setValue(female.text.toString())
            }


            Toast.makeText(this@AddDonor, "Your Data has been registered", Toast.LENGTH_LONG).show()


            //get value from database
            val query = key?.let { myRef?.child(it) }
            query?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("On Failure:", databaseError.message)
                }

                override fun onDataChange(data: DataSnapshot) {
                    processData()
                }
            })
        }
    }

    private fun processData() {

        val post_intent = Intent(this, FindDonorList::class.java)
        startActivity(post_intent)
    }
}
