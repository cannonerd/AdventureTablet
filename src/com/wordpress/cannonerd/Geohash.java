package com.wordpress.cannonerd;

import java.net.*;
import java.io.*;
import java.util.regex.*;
import java.text.*;
import java.util.Date;
import net.exclaimindustries.tools.HexFraction;
import net.exclaimindustries.tools.MD5Tools;

public class Geohash
{
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
    
    public static Point getGeohash(Point location) throws Exception
    {
        DateFormat urlDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        DateFormat hashDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();

        String djia = fetchURL("http://irc.peeron.com/xkcd/map/data/" + urlDateFormat.format(today));

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
