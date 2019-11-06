package com.example.dawn.friendsintheworld;

import android.app.Activity;
import android.content.Context;

import org.json.JSONObject;

import java.util.ArrayList;

public class Controller {
    public TCPConnection connection;

    private static Controller ourInstance = null;

    public static Controller getInstance(ReceiveListener listener) {
        if(ourInstance == null) {
            ourInstance = new Controller();
        }
        ourInstance.connection.setListener(listener);
        return ourInstance;
    }

    public Controller(){
        connection = new TCPConnection("195.178.227.53",7117);
    }

    public void connectClicked() {
        connection.connect();
    }

    public void disconnectClicked() {
        connection.disconnect();
    }

}
