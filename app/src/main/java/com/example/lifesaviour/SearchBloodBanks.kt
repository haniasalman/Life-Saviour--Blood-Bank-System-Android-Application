package com.example.lifesaviour

import android.Manifest
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnSuccessListener
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.net.URL
import java.nio.charset.StandardCharsets


class SearchBloodBanks : AppCompatActivity(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener{

    private lateinit var mMap: GoogleMap
    private lateinit var mGoogleApiClient: GoogleApiClient
    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0
    private lateinit var myLocation: Location
    private val REQUEST_ID_MULTIPLIER_PERMISSION = 1
    private val REQUEST_CHECK_SETTINGS_GPS = 2
    var PROXIMITY_RADIUS = 1000 //10000=10km
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var currentLocationMarker: Marker? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_blood_banks)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        setUPGClient()    //buildGoogleApiClient


    }



    private fun setUPGClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this,0,this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
        mGoogleApiClient.connect()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val isb = LatLng(33.690904, 73.051865)
        mMap.addMarker(MarkerOptions().position(isb).title("Marker in Islamabad"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(isb, 10F))
        mMap.uiSettings.setZoomControlsEnabled(true)
        mMap.uiSettings.setMyLocationButtonEnabled(true)
        mMap.uiSettings.setMapToolbarEnabled(true)


    }

    override fun onConnected(p0: Bundle?) {
        checkPermission() //it will allow us to use location services

        fusedLocationProviderClient?.lastLocation?.addOnSuccessListener(
            this, OnSuccessListener<Location> { location ->
                ShowHospitals(location.latitude, location.longitude)
            })
    }


    ////////////////////////////////////////////////////////////
    private fun getUrl(latitude: Double, longitude: Double, nearbyPlace: String ): String? {

        val googlePlaceUrl = StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?")
        googlePlaceUrl.append("location=").append(latitude).append(",").append(longitude)
        googlePlaceUrl.append("&radius=").append(PROXIMITY_RADIUS)
        googlePlaceUrl.append("&type=$nearbyPlace")
        googlePlaceUrl.append("&sensor=false")
        googlePlaceUrl.append("&key=" + resources.getString(R.string.places_api_key))

        Log.d("NearbyHospitalActivity", "url = $googlePlaceUrl")

        return googlePlaceUrl.toString()


    }
    ////////////////////////////////////////////////////////////


    private fun checkPermission() {
        val permissionLocation: Int =
            ContextCompat.checkSelfPermission(this@SearchBloodBanks, Manifest.permission.ACCESS_FINE_LOCATION)


        if(permissionLocation != PackageManager.PERMISSION_GRANTED)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),  REQUEST_ID_MULTIPLIER_PERMISSION)
            }
        }

        else{
            getMyLocation()
        }


    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val permissionLocation: Int = ContextCompat.checkSelfPermission(this@SearchBloodBanks, Manifest.permission.ACCESS_FINE_LOCATION)

        if(permissionLocation == PackageManager.PERMISSION_GRANTED){

            getMyLocation()
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()

        } else {
            checkPermission()
            Toast.makeText(this, "Permission not granted!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getMyLocation() {
        if (mGoogleApiClient.isConnected) {
            val permissionLocation = ContextCompat.checkSelfPermission(
                this@SearchBloodBanks,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                myLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
                val locationRequest = LocationRequest()
//                locationRequest.interval = 3000
//                locationRequest.fastestInterval = 3000
                locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                val builder = LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest)
                builder.setAlwaysShow(true)
                LocationServices.FusedLocationApi
                    .requestLocationUpdates(mGoogleApiClient, locationRequest, this)
                val result: PendingResult<LocationSettingsResult> = LocationServices.SettingsApi
                    .checkLocationSettings(mGoogleApiClient, builder.build())
                result.setResultCallback { result ->
                    val status: Status = result.status
                    when (status.statusCode) {
                        LocationSettingsStatusCodes.SUCCESS -> {
                            // All location settings are satisfied.
                            // You can initialize location requests here.
                            val permissionLocation = ContextCompat
                                .checkSelfPermission(
                                    this@SearchBloodBanks,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                )
                            if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                myLocation = LocationServices.FusedLocationApi
                                    .getLastLocation(mGoogleApiClient)
                            }
                        }
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
                            try {
                                // Ask to turn on GPS automatically
                                status.startResolutionForResult(
                                    this@SearchBloodBanks,
                                    REQUEST_CHECK_SETTINGS_GPS
                                )
                            } catch (e: SendIntentException) { // Ignore the error.
                            }
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        }
                    }
                }
            }
        }
    }

    override fun onConnectionSuspended(p0: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLocationChanged(location: Location) {
        myLocation = location

        currentLatitude = location.latitude
        currentLongitude = location.longitude

        currentLocationMarker?.remove()


        val latLng = LatLng(currentLatitude, currentLongitude)
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.title("Current Location")
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        //            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.location))
        currentLocationMarker = mMap.addMarker(markerOptions)
//        mMap.addMarker(markerOptions)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap.animateCamera(CameraUpdateFactory.zoomBy(0F))

        LocationServices.FusedLocationApi.removeLocationUpdates( mGoogleApiClient, this)

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        when (id) {
            R.id.maptype_none -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NONE
            }
            R.id.maptype_normal -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            }
            R.id.maptype_satellite -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
            }
            R.id.maptype_terrain -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
            }
            R.id.maptype_hybrid -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
            }
            R.id.current_location ->
//                getCurrentLocation()
                getMyLocation()
        }
        return super.onOptionsItemSelected(item)
    }


    private fun ShowHospitals(latitude: Double, longitude: Double) {

        val dataTransfer = arrayOfNulls<Any>(2)
        val getNearbyPlacesData = GetNearbyPlacesData()
        val url = getUrl(latitude, longitude, "hospital")
        dataTransfer[0] = mMap
        dataTransfer[1] = url
        getNearbyPlacesData.execute(dataTransfer)

        Toast.makeText(this, "Showing Nearby Hospitals", Toast.LENGTH_SHORT).show()
    }

}
