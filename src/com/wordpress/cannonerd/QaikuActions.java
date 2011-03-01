package com.wordpress.cannonerd.map;

import java.net.*;
import java.io.*;
import java.util.regex.*;
import java.text.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.http.*;
import org.apache.http.message.*;
import org.apache.http.client.*;
import org.apache.http.util.*;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.client.entity.*;
import java.util.ArrayList;
import java.util.Hashtable;

import android.util.Log;

import com.wordpress.cannonerd.Point;

public class QaikuActions
{
    String apikey;
    double segmentCount = 4.0;
    String source = "AdventureTablet/Android";

    public QaikuActions(String apikey) 
    {
        this.apikey = apikey;
    }
 
    public static String postURL(String url, ArrayList<BasicNameValuePair> data) throws Exception
    {
        HttpClient httpclient = new DefaultHttpClient();   
        HttpPost httppost = new HttpPost(url); 

        try {
            httppost.setEntity(new UrlEncodedFormEntity(data));
            try {
                HttpResponse response = httpclient.execute(httppost);
                return EntityUtils.toString(response.getEntity());
            } catch (ClientProtocolException e) {
                return "";
            } catch (IOException e) {
                return "";
            }
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

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
            String result = fetchURL("http://www.qaiku.com/api/statuses/user_timeline.json?apikey=" + this.apikey);

        } 
        catch (Exception e)             // Your qaiku key is bonkers
        {
            return false;
        }
        return true;
    }

    public String sendAdventure(Point destination) {
        ArrayList<BasicNameValuePair> data = new ArrayList<BasicNameValuePair>(4);
        data.add(new BasicNameValuePair("status", "Geohash for today"));
        data.add(new BasicNameValuePair("source", this.source));
        data.add(new BasicNameValuePair("channel", "adventure"));
        data.add(new BasicNameValuePair("data", Double.toString(destination.lat) + "," + Double.toString(destination.lon)));

        String result;
        try {
            result = postURL("http://www.qaiku.com/api/statuses/update.json?apikey=" + apikey, data);
            try
            {
                JSONObject message = new JSONObject(result);
                return message.getString("id");
            }
            catch (Exception e) {
                return "";
            }
        }
        catch (Exception e) {
            return "";
        }
    }

    public Hashtable getPlayerLocations(String adventureId) {
        String result = "";
        try 
        {
            result = fetchURL("http://www.qaiku.com/api/statuses/replies/" + adventureId + ".json?apikey=" + this.apikey);

        } 
        catch (Exception e)
        {
            // Something went wrong with Qaiku
            Log.d("QaikuActions", "Failed to read replies", e);
            return new Hashtable();
        }

        Hashtable playerLocations = new Hashtable();
        try
        {
            JSONArray messages = new JSONArray(result);
            for (int i = 0; i < messages.length(); ++i) {
                JSONObject message = messages.getJSONObject(i);
                JSONObject player = message.getJSONObject("user");
                if (playerLocations.containsKey(player.getString("screen_name"))) {
                    // We already have latest location of player
                    continue;
                }

                if (message.getString("data").equals("")) {
                    // No QaikuData found, we need this for our adventure
                    Log.d("QaikuActions", "Skipping empty data");
                    continue;
                }

                String[] qaikudata = message.getString("data").split(",");
                if (qaikudata.length != 3) {
                    // Invalid mission coordinates, skip
                    Log.d("QaikuActions", "Skipping data with wrong param count");
                    continue;
                }
                
                Point playerLocation = new Point(Double.parseDouble(qaikudata[0]), Double.parseDouble(qaikudata[1]));
                Log.d("QaikuActions", "Setting player " + player.getString("screen_name") + " location to " + playerLocation.PrettyPrint());
                playerLocations.put(player.getString("screen_name"), playerLocation);
            }
        }
        catch (Exception e)
        {
            // Something went wrong with Qaiku
            Log.d("QaikuActions", "Failed to parse replies", e);
            return new Hashtable();
        }


        return playerLocations;
    }

    public Point[] getAdventures() {
        String result = "";
        try 
        {
            result = fetchURL("http://www.qaiku.com/api/statuses/channel_timeline/adventure.json?apikey=" + this.apikey);

        } 
        catch (Exception e)
        {
            // Something went wrong with Qaiku
            Log.d("QaikuActions", "Failed to read adventures", e);
            return new Point[0];
        }

        try
        {
            JSONArray messages = new JSONArray(result);

            Point[] adventures = new Point[messages.length()];
            int adventuresFound = 0;
            for (int i = 0; i < messages.length(); ++i) {
                JSONObject message = messages.getJSONObject(i);
                if (!message.getString("in_reply_to_status_id").equals(""))
                {
                    // This is a log entry or comment, we're only interested in adventures
                    Log.d("QaikuActions", "Skipping comment to '" + message.getString("in_reply_to_status_id") + "'");
                    continue;
                }

                if (message.getString("data").equals("")) {
                    // No QaikuData found, we need this for our adventure
                    Log.d("QaikuActions", "Skipping empty data");
                    continue;
                }

                String[] qaikudata = message.getString("data").split(",");
                if (qaikudata.length != 2) {
                    // Invalid mission coordinates, skip
                    Log.d("QaikuActions", "Skipping data with wrong param count");
                    continue;
                }
                Log.d("QaikuActions", "Found valid adventure " + message.getString("id"));
                adventures[adventuresFound] = new Point(Double.parseDouble(qaikudata[0]), Double.parseDouble(qaikudata[1]));
                adventures[adventuresFound].setId(message.getString("id"));
                adventuresFound++;
            }
            return adventures;

        }
        catch (Exception e)
        {

            Log.d("QaikuActions", "Failed to parse adventures", e);

            return new Point[0];
        }

    }
 
    public boolean checkArrival(double distance) {
        if (distance <= 0.05) {
            return true;
        }
        return false;
    }

    public double[] getSegments(double distance) {
        int segmentCountInt = new Double(segmentCount).intValue();

        double[] segments = new double[segmentCountInt];
        double segmentLength = distance / segmentCount;
        int segmentsToTraverse = segmentCountInt - 1;
        int i = 0;
        while (segmentsToTraverse >= 0) {
            distance = distance - segmentLength;
            segments[i] = distance;
            i++;
            if (checkArrival(distance)) {
                break;
            }
            segmentsToTraverse--;
            if (segmentsToTraverse == 0) {
                break;
            }
        }
        return segments;
    }
}
