package com.android.mygoogleplaces;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser {
    private HashMap<String,String>getsinglenearbyPlace(JSONObject googleplaceJson){
        HashMap<String,String > googleplacemap = new HashMap<>();
        String NameofPlace = "-NA-";
        String Vicinity = "-NA-";
        String Latitude = "";
        String Longitude = "";
        String reference = "";
        try {
           if(!googleplaceJson.isNull("name")){
               NameofPlace = googleplaceJson.getString("name");
           }
           if(!googleplaceJson.isNull("vicinity")) {
               Vicinity = googleplaceJson.getString("vicinity");
           }
           Latitude = googleplaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            Longitude = googleplaceJson.getJSONObject("geometry").getJSONObject("location").getString("lon");
            reference = googleplaceJson.getString("reference");

            googleplacemap.put("place name",NameofPlace);
            googleplacemap.put("vicinity",Vicinity);
            googleplacemap.put("lat",Latitude);
            googleplacemap.put("lon",Longitude);
            googleplacemap.put("reference",reference);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return googleplacemap;
    }
    private List<HashMap<String,String>> getAllnearbyplaces(JSONArray jsonarray){
        int counter = jsonarray.length();
        List<HashMap<String,String>> NearbyPlacesList = new ArrayList<>();
        HashMap<String,String> NearbyPlaceMAp = null;

        for(int i=0;i<counter;i++)
        {

            try {
                NearbyPlaceMAp = getsinglenearbyPlace((JSONObject)jsonarray.get(i));
                NearbyPlacesList.add(NearbyPlaceMAp);

            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return NearbyPlacesList;
    }

    public List<HashMap<String,String>>parse (String jsonData){
        JSONArray jsonArray = null;
        JSONObject jsonObject;
        try{
            jsonObject = new JSONObject(jsonData);
             jsonArray = jsonObject.getJSONArray("results");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getAllnearbyplaces(jsonArray);
    }
}
