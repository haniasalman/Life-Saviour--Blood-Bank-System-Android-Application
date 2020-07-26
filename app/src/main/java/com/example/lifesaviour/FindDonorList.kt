package com.example.lifesaviour

import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*


class FindDonorList : AppCompatActivity() {
    lateinit var ref: DatabaseReference
    lateinit var donorList: MutableList<Donors>
    lateinit var listview: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_donor)

        donorList = mutableListOf()
        listview = findViewById(R.id.listView)


            ref = FirebaseDatabase.getInstance().reference.child("Add Donor")

            ref.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Log.d("Donor Error", p0.message)
                }


                override fun onDataChange(p0: DataSnapshot) {

                    if (p0.exists()) {

                        for (e in p0.children) {
                            val donors = e.getValue(Donors::class.java)
                            donorList.add(donors!!)
                        }
                        val adapter = AddDonorListViewAdapter(
                            this@FindDonorList,
                            R.layout.row_data_add_donor_list,
                            donorList
                        )
                        listview.adapter = adapter

                    } else {
                        Toast.makeText(
                           this@FindDonorList, "Database is empty!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

            })
        }
    }



