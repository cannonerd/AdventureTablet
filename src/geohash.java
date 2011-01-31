
import java.net.*;
import java.io.*;
import java.util.regex.*;


class Geohash
{
    double lat;
    double lon;

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
        double lat = location.lat;
        double lon = location.lon;

/*    
        if (lon < -30)
        {
           int td30=0;
        }
        else
        {
            int td30=1;    
        }
        if(lat< 0)
        {
            int south = -1;
        }
        else 
        {
           int south = 1;
        }
        if (lon < 0)
        {
            int west = -1;
        }
        else
        {
            int west = 1;
        }
*/
        int lati = (int)lat;
        String latit = Integer.toString(lati);
        int longi = (int) lon;
        String longit = Integer.toString(longi);
        String djia = "";

        djia = fetchURL("http://atlas.freshlogicstudios.com/Features/Experiments/Xkcd/Xkcd.ashx?Latitude="+latit+"&Longitude="+longit );

        CharSequence inputStr = djia;
        String patternStr = "<point(.*[^>])>(.*)</point>";

        // Compile and use regular expression
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(inputStr);
        boolean matchFound = matcher.find();

        if (!matchFound) {
            throw new Exception("foo!");
        }

        if (matcher.groupCount() != 2)
        {
            throw new Exception("foo!");
        }

        String coordinateString = matcher.group(2);
        String[] coordinateParts = coordinateString.split("\\s+");
        String newlat = coordinateParts[0];
        String newlon = coordinateParts[1];
        Point geohash = new Point(Double.parseDouble(newlon), Double.parseDouble(newlat)); 
        return geohash;
    }
} 
/*    public static void main(String[] args)
    {
        try
        {
            Point hash = Geohash.getGeohash(new Point(60.0, 24.0));
            System.out.println("Your geohash is " + Double.toString(hash.lat) + "," + Double.toString(hash.lon));
        }
        catch (Exception e)
        {
            System.out.println("Fail!");
        }
    }
}

*/
