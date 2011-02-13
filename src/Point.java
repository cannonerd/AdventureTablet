

class Point
{
    double lat;
    double lon;


    public Point(double latitude, double longitude)
    {
        this.lat = latitude;
        this.lon = longitude;
    }

/**
 * Metodi korvaamaan Math.luokan samaa, katoaa tod nak myohemmin.
 * 
 *
 * @param   toRadians  degrees
 * 
 * @return degrees in radians 
 */

    private double toRadians(double degrees)
    {
        return (degrees * Math.PI / 180.0);
    }

/**
 * Mettodi joka kertoo etaisyyden kahden pisteen valilla
 * 
 *
 * @param   distanceTo   location, latitude and longitude
 * 
 * @return Distance in 
 */

	public double distanceTo(Point to)
    {
        double startLon = toRadians(lon); 
        double startLat = toRadians(lat);
        double endLon  = toRadians(to.lon);
        double endLat = toRadians(to.lat);

        double dLat = endLat - startLat;
        double dLon = endLon - startLon;
        double a = Math.pow(Math.sin(dLat/2.0), 2) + Math.cos(startLat) * Math.cos(endLat) * Math.pow(Math.sin(dLon/2.0),2);
        double c = 2.0 * Math.atan2(Math.sqrt(a),  Math.sqrt(1.0-a));     
       return 6371.0 * c;

    }

/**
 * Metodi kertoo kompassisuuntiman lahtopisteesta loppupisteeseen. Sijainnin siirtyessa suuntima 
 * lasketaan uudelleen.
 *
 * @param   bearingTo location and end point latitudes and longitudes
 * 
 * @return bearing in degrees 
 */
    public double bearingTo(Point to)
    {
        double distance = distanceTo(to);    
        if (distance == 0.0)
        {
            return 0.0;
        }

        double deltaLong = Math.toRadians(to.lon - lon);

        double lat1 = Math.toRadians(lat);
        double lat2 = Math.toRadians(to.lat);

        double y = Math.sin(deltaLong) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(deltaLong);
        double result = Math.toDegrees(Math.atan2(y, x));
        return (result + 360.0) % 360.0;


    }
	
    public String PrettyPrintCoordinate(double coordinate)
    {
        double deg = Math.abs(coordinate);
        int degrees = (int) Math.floor(deg);
        double min = 60.0 * (deg - degrees);
        int minutes = (int) min;
        double sec = 60 *  (min - minutes);
        int seconds = (int) sec;
        String degr  = Integer.toString(degrees);
        String minu  = Integer.toString(minutes);
        String seco  = Integer.toString(seconds);
        String coordinates = degrees +" "+ minutes +" "+seconds;  
        return coordinates;
    }

    /**
     * Could just be toString?
     */
    public String PrettyPrint()
    {
        String lati = PrettyPrintCoordinate(lat);
        String longi = PrettyPrintCoordinate(lon);
        
        if (lat > 0.0)
        {
            lati = lati + " N";
        }
        else
        {
            lati = lati + " S";
        }
        if (lon > 0.0)
        {
            longi = longi + " E";
        }
        else
        {
            longi = longi + " W";
        }
        String total = new String(lati +" "+ longi); 
       return total;
   }
}
