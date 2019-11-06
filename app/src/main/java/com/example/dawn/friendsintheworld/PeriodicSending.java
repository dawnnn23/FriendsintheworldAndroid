package com.example.dawn.friendsintheworld;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class PeriodicSending extends Thread {

    private FusedLocationProviderClient mFusedLocationClient;
    private Activity activity;
    private Controller controller;

    public PeriodicSending(Controller controller, Activity activity){

        this.activity=activity;
        this.controller=controller;
        this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);


    }

    public void run(){

        if (ActivityCompat.checkSelfPermission(activity, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{ACCESS_FINE_LOCATION},1);
        }

        while(true){


            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {

                                double latitude= location.getLatitude();
                                double longtitude = location.getLongitude();

                                SharedPreferences sharedPreferences=activity.getSharedPreferences("groupId", Context.MODE_PRIVATE);
                                String groupId=sharedPreferences.getString("groupId","");

                                try {
                                    controller.connection.send(Expression.setPosition(groupId,longtitude,latitude));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
            try {

                Thread.sleep(30000);

            } catch (Exception e) {


            }

        }
    }

}
