package com.android.mygoogleplaces;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public
class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient googleapiclient;
    private LocationRequest locationrequest;
    private Location lastlocation;
    private Marker currentlocationusermarker;
    private static final int Request_user_location_code = 99;
    private double latitude,longitude;
    private int ProximityRadius = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            checkuserLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)        buildGoogleApiClient();
        {
buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }
    public boolean checkuserLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_user_location_code);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_user_location_code);

            }
            return false;
        }
        else{
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case Request_user_location_code:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION )== PackageManager.PERMISSION_GRANTED){
if(googleapiclient == null){
    buildGoogleApiClient();
}
mMap.setMyLocationEnabled(true);
                    }
                }else{
                    Toast.makeText(this,"permission denied",Toast.LENGTH_SHORT).show();
                }
        }
    }

    protected synchronized void buildGoogleApiClient() {
        googleapiclient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleapiclient.connect();
    }


    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        lastlocation = location;

        if (currentlocationusermarker != null)
        {
            currentlocationusermarker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("user Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        currentlocationusermarker = mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(12));

        if (googleapiclient != null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleapiclient, this);
        }
    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationrequest = new LocationRequest();
        locationrequest.setInterval(1100);
        locationrequest.setFastestInterval(1100);
        locationrequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleapiclient,
                        locationrequest, this);

            }

}   @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    public void onClick(View view) {

        String hospital = "hospitals",school = "school",Resturants = "resturants";
      Object transferdata[] = new Object[2];
      GetNearbyPlaces getNearbyPlaces  = new GetNearbyPlaces();

        switch (view.getId()){
            case R.id.search__address:
                EditText addressfield =(EditText)findViewById(R.id.location_search);
                String address= addressfield.getText().toString();
                List<Address> addressList = null;
                MarkerOptions usermarkeroption = new MarkerOptions();

        if(!TextUtils.isEmpty(address)){

        Geocoder geocoder =new Geocoder(this);
        try {

        addressList = geocoder.getFromLocationName(address,6);

        if(addressList!=null) {
            for (int i = 0; i < addressList.size(); i++) {
                Address userAddress = addressList.get(i);
                LatLng latLng = new LatLng(userAddress.getLatitude(), userAddress.getLongitude());
                usermarkeroption.position(latLng);
                usermarkeroption.title(address);
                usermarkeroption.icon(BitmapDescriptorFactory.defaultMarker
                        (BitmapDescriptorFactory.HUE_BLUE));
                mMap.addMarker(usermarkeroption);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomBy(10));
            }
        }
        else
        {
            Toast.makeText(this,"Location not found",Toast.LENGTH_SHORT).show();
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

                }

        else {
            Toast.makeText(this,"please enter the address",Toast.LENGTH_SHORT).show();
        }
            break;
            case R.id.hospital:
                mMap.clear();
                String url = geturl(latitude,longitude,hospital);
                transferdata[0] =mMap;
                transferdata[1]=url;
                getNearbyPlaces.execute(transferdata);
                Toast.makeText(this,"Searching nearby hospitals",Toast.LENGTH_SHORT).show();
                Toast.makeText(this,"Showing nearby hospitals",Toast.LENGTH_SHORT).show();
                break;

        case R.id.school:
        mMap.clear();
         url = geturl(latitude,longitude,school);
        transferdata[0] =mMap;
        transferdata[1]=url;
        getNearbyPlaces.execute(transferdata);
        Toast.makeText(this,"Searching nearby school",Toast.LENGTH_SHORT).show();
        Toast.makeText(this,"Showing nearby school",Toast.LENGTH_SHORT).show();
        break;

      case R.id.resturant:
            mMap.clear();
            url = geturl(latitude,longitude,Resturants);
                transferdata[0] =mMap;
                transferdata[1]=url;
                getNearbyPlaces.execute(transferdata);
                Toast.makeText(this,"Searching nearby resturants",Toast.LENGTH_SHORT).show();
                Toast.makeText(this,"Showing nearby resturants",Toast.LENGTH_SHORT).show();
                break;
           }
    }

    private String  geturl (double latitude,double longitude,String nearbyplace){
        StringBuilder googleUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googleUrl.append("location="+ latitude+ ","+longitude);
        googleUrl.append("&radius="+ProximityRadius);
        googleUrl.append("$type"   +nearbyplace);
        googleUrl.append("sensor=true");
        googleUrl.append("&key"+"PlaceAPI key");
        Log.d("GoogleMApsActivity","url = "+ googleUrl.toString());
        return googleUrl.toString();
    }
}
