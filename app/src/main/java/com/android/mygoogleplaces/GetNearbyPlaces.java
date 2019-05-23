package com.android.mygoogleplaces;

import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class GetNearbyPlaces extends AsyncTask<Object,String,String> {

private String googleplacedata,url;
private GoogleMap  gmap;
    @Override
    protected String doInBackground(Object... objects) {
        gmap = (GoogleMap) objects[0];
        url = (String) objects[1];
        DownloadUrl downloadUrl= new DownloadUrl();
        try {
            googleplacedata = downloadUrl.ReadTheUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return googleplacedata;
    }
    @Override
    protected void onPostExecute(String s){
        List<HashMap<String,String>> nearbyplaceslist = null;
        DataParser dataParser = new DataParser();
        nearbyplaceslist = dataParser.parse(s);
        DisplayNearbyPlaces(nearbyplaceslist);
    }
    private void DisplayNearbyPlaces(List<HashMap<String,String>> nearbyplaceslist)
    {
        for(int i=0;i<nearbyplaceslist.size();i++){
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String,String> googlenearbyplace = nearbyplaceslist.get(i);
            String  nameofplace = googlenearbyplace.get("place name");
            String  vicinity = googlenearbyplace.get("vicinity");
            double lat =Double.parseDouble(googlenearbyplace.get("lat"));
            double lon =Double.parseDouble(googlenearbyplace.get("lon"));
            LatLng latLng = new LatLng(lat,lon)  ;
            markerOptions.position(latLng);
            markerOptions.title(nameofplace + ":" + vicinity);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker
                    (BitmapDescriptorFactory.HUE_YELLOW));
            gmap.addMarker(markerOptions);
            gmap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            gmap.animateCamera(CameraUpdateFactory.zoomBy(12));
        }
    }
}
