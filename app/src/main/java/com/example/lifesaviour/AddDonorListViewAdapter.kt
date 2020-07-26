package com.example.lifesaviour

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView

class AddDonorListViewAdapter(var mCtx: Context, val resource:Int, val items:List<Donors>)
    : ArrayAdapter<Donors>( mCtx , resource , items ){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)

        val view: View = layoutInflater.inflate(resource, null)
        val text_Name = view.findViewById<TextView>(R.id.Donor_Name)
        val text_Name2 = view.findViewById<TextView>(R.id.address)
        val text_Name3 = view.findViewById<TextView>(R.id.gender)
        val text_Name4 = view.findViewById<TextView>(R.id.bloodgrp)
        val text_Name5 = view.findViewById<TextView>(R.id.Contact)
        val call_donor = view.findViewById<Button>(R.id.call_donor_button)

        val donor: Donors = items[position]

        text_Name.text = donor.Name
        text_Name2.text = donor.Address
        text_Name3.text = donor.Gender
        text_Name4.text = donor.Blood_Group
        text_Name5.text = donor.Contact_No

        call_donor.setOnClickListener{
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel: " + text_Name5.text)
            context.startActivity(intent)
        }


        return view
    }
}