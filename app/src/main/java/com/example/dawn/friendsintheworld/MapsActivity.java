package com.example.dawn.friendsintheworld;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONObject;

import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private LatLng userLocation;
    private Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        controller = Controller.getInstance(new MapsActivityListener());
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION},1);
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            userLocation = new LatLng(location.getLatitude(),location.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(userLocation).title("It's Me!"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));

                        }
                    }
                });

    }

    public class MapsActivityListener implements ReceiveListener {
        public void newMessage(final String answer) {
            runOnUiThread(new Runnable() {
                public void run() {

                    try {
                        JSONObject jsonObj = new JSONObject(answer);
                        String type = jsonObj.getString("type");
                        if(type.equals("groups")) {
                            ArrayList<String> groupsArray = new ArrayList<>();
                            int i = 0;
                            while (jsonObj.getJSONArray("groups").getJSONObject(i).getString("group") != null) {
                                groupsArray.add(jsonObj.getJSONArray("groups").getJSONObject(i).getString("group"));
                                i++;
                            }

                        }else if(type.equals("register")){
                            String groupName = jsonObj.getString("group");
                            String id = jsonObj.getString("id");
                        } else if(type.equals("unregister")) {
                            String id = jsonObj.getString("id");
                        }else if(type.equals("members")) {
                            String groupName = jsonObj.getString("group");
                            ArrayList<String> membersArray = new ArrayList<>();
                            int i = 0;
                            while (jsonObj.getJSONArray("groups").getJSONObject(i).getString("group") != null) {
                                membersArray.add(jsonObj.getJSONArray("members").getJSONObject(i).getString("member"));
                                i++;
                            }
                        } else if(type.equals("locations")){
                            //Check if this is correct
                            String groupName = jsonObj.getString("group");
                            ArrayList<User> usersLocationsArray = new ArrayList<>();
                            int i = 0;
                            while(jsonObj.getJSONArray("location").getJSONObject(i).getString("member") != null){
                                String memberName = jsonObj.getJSONArray("location").getJSONObject(i).getString("member");
                                String memberLatitude = jsonObj.getJSONArray("location").getJSONObject(i).getString("latitude");
                                String memberLongtitude = jsonObj.getJSONArray("location").getJSONObject(i).getString("longtitude");
                                User user = new User(memberName,memberLongtitude,memberLatitude);
                                usersLocationsArray.add(user);
                                i++;
                            }
                        } else if(type.equals("exception")){
                            String message = jsonObj.getString("message");
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }}

}

