package com.wordpress.cannonerd.map;

import java.net.*;
import java.io.*;
import java.util.regex.*;
import java.text.*;
import android.util.Log;
import android.os.Bundle;


public class QaikuActions
{
    String apikey;

    public QaikuActions(String apikey) {
        this.apikey = apikey;
    }
    private static final String TAG = "AdventureTablet";

    public static String fetchURL(String url) throws Exception 
    {
        URL address = new URL(url);
        BufferedReader in = new BufferedReader(
        new InputStreamReader(
        address.openStream()));

        String inputLine;
        String result = "";

        while ((inputLine = in.readLine()) != null) 
        {
            result +=inputLine;
        }

        in.close();
        return result;
    }

    public boolean checkApiKey()
    {
        try 
        {
            Log.d(TAG, "true");
            String result = fetchURL("http://www.qaiku.com/api/statuses/user_timeline.json?apikey=" + this.apikey);

        } 
        catch (Exception e) 
        {
            Log.d(TAG, "false");
            // Your qaiku key is bonkers
            return false;
        }
        return true;
    } 
}
