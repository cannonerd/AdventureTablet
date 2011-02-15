package com.wordpress.cannonerd;

import java.net.*;
import java.io.*;
import java.util.regex.*;
import java.text.*;
import java.util.Date;
import net.exclaimindustries.tools.HexFraction;
import net.exclaimindustries.tools.MD5Tools;
import java.util.TimeZone;
/**
 * Calculates Geohash for users location
 * 
 *
 * @param   users location, DJIA opening
 * 
 * @return point Geohash
 */
public class Geohash
{
/**
 * Prepares URL fetching
 * 
 *
 * @param   URL   automatically read information
 * 
 * @return url
 */
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
/**
 * Calculates the Geohash point using djia opening ands players location 
 * 
 *
 * @param   getGeohash   Users location from GPS
 * 
 * @return Point Geohash
 */
    
    public static Point getGeohash(Point location) throws Exception
    {
        DateFormat urlDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        DateFormat hashDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //urlDateFormat.setTimeZone(TimeZone.getTimeZone("US/Eastern"));
       // hashDateFormat.setTimeZone(TimeZone.getTimeZone("US/Eastern"));
        Date today = new Date();

        int td30 = 0;
        if (location.lon > -30.0) {
            td30 = 1000 * 60 * 60 * 24;
        }
       
        String djia = fetchURL("http://irc.peeron.com/xkcd/map/data/" + urlDateFormat.format(today.getTime() - td30));

        String hash = MD5Tools.MD5hash(hashDateFormat.format(today) + "-" + djia);
        double hashLat = HexFraction.calculate(hash.substring(0, 16));
        double hashLon = HexFraction.calculate(hash.substring(16, 32));

        return new Point
        (
            Math.floor(location.lat) + hashLat,
            Math.floor(location.lon) + hashLon
        );
    }
} 
