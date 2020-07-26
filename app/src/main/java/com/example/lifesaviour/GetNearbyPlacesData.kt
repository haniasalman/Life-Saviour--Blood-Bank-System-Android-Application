package com.example.lifesaviour

import android.os.AsyncTask
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException


class GetNearbyPlacesData : AsyncTask<Any?, String, String>() {
    //AsyncTask is an abstract class that’s used to perform long operations in the background.

    private var googlePlacesData: String? = null
    private var mMap: GoogleMap? = null
    private var url: String? = null


    // Invoked by execute() method of this object
    override fun doInBackground(vararg objects: Any?): String? {
        mMap = (objects.first() as Array<Object>)[0] as GoogleMap
        url = (objects.first() as Array<Object>)[1].toString()

        val downloadURL = DownloadUrl()
        try{
            googlePlacesData = downloadURL.readUrl(url!!)

        } catch (e: IOException)
        {
            e.printStackTrace();
        }

        return googlePlacesData
    }

    // Executed after the complete execution of doInBackground() method
    override fun onPostExecute(s: String ) {

        val nearbyPlaceList: List<HashMap<String, String>>
        // Start parsing the Google places in JSON format
        // Invokes the "doInBackground()" method of the class ParserTask
        val parser = DataParser()
        nearbyPlaceList = parser.parse(s)
        showNearbyPlaces(nearbyPlaceList)

        Log.d("nearby places data", nearbyPlaceList.toString())
    }


    private fun showNearbyPlaces(nearbyplaces: List<java.util.HashMap<String, String>>)
    {

        for (i in 0 until nearbyplaces.size) {
            val markerOptions = MarkerOptions()
            val googlePlace = nearbyplaces[i]

            val PlaceName = googlePlace["place_name"]
            val vicinity = googlePlace["vicinity"]
            val lat = googlePlace["lat"]!!.toDouble()
            val lng = googlePlace["lng"]!!.toDouble()

            Log.d("Latitude", "$lat")
            Log.d("Longitude", "$lng")
            Log.d("PlaceName", "$PlaceName")

            val latLng = LatLng(lat, lng)
            markerOptions.position(latLng)
            markerOptions.title("$PlaceName : $vicinity")
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))

            mMap?.addMarker(markerOptions)
            mMap?.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            mMap?.animateCamera(CameraUpdateFactory.zoomBy(12f))

        }
        }

}