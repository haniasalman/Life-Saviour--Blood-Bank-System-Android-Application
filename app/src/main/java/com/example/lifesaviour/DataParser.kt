package com.example.lifesaviour

import android.R.attr.name
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*


class DataParser {

    private fun getPlace(googlePlaceJson:JSONObject):HashMap<String,String>{

        val googlePlacesMap = HashMap<String,String>()
        var placeName = "--NA--"
        var vicinity = "--NA--"
        var latitude = ""
        var longitude = ""
        var reference = ""

        try {
            if(!googlePlaceJson.isNull("name")) {
                placeName = googlePlaceJson.getString("name")
            }
            if(!googlePlaceJson.isNull("vicinity")) {
                vicinity = googlePlaceJson.getString("vicinity")
            }
            latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat")
            longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng")
            reference = googlePlaceJson.getString("reference")

            googlePlacesMap["place_name"] = placeName
            googlePlacesMap.put("vicinity", vicinity)
            googlePlacesMap.put("lat", latitude)
            googlePlacesMap.put("lng", longitude)
            googlePlacesMap.put("reference", reference)

            Log.d("Place Name", placeName)
        }
        catch (e: IOException){
            e.printStackTrace()
        }

        return googlePlacesMap
    }

    private fun getPlaces(jsonArray: JSONArray):List<HashMap<String,String>>{

        val count:Int = jsonArray.length()
        val placesList = ArrayList<HashMap<String,String>>()
        var placeMap: HashMap<String,String>

        for (i in 0 until count){
            try {
                placeMap = getPlace(jsonArray.get(i) as JSONObject)
                placesList.add(placeMap)
            }
            catch (e:JSONException){
                e.printStackTrace()
            }
        }
        return placesList
    }

    fun parse(jsonData: String):List<HashMap<String,String>>{

        var jsonArray:JSONArray? = null
        val jsonObject:JSONObject
//        Receives a JSONObject and returns a list
        try{
            jsonObject = JSONObject(jsonData)
            jsonArray = jsonObject.getJSONArray("results")
        }
        catch (e:JSONException) {
            e.printStackTrace()
        }

        return getPlaces(jsonArray!!)
    }

}