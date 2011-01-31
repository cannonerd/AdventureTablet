class Point
{
    double lat;
    double lon;

    public Point(double latitude, double longitude)
    {
        this.lat = latitude;
        this.lon = longitude;
    }

    private double toRadians(double degrees)
    {
        return (degrees * Math.PI / 180.0);
    }

	public double distanceTo(Point to)
    {
        double startLon = toRadians(lon); //start_long = math.radians(self.lon)
        double startLat = toRadians(lat);//start_latt = math.radians(self.lat)
        double endLon  = toRadians(to.lon);
        double endLat = toRadians(to.lat);//end_latt = math.radians(point.lat)

        double dLat = endLat - startLat;//d_latt = end_latt - start_latt
        double dLon = endLon - startLon;//d_long = end_long - start_long
        double a = Math.pow(Math.sin(dLat/2.0), 2) + Math.cos(startLat) * Math.cos(endLat) * Math.pow(Math.sin(dLon/2.0),2);
        //a = math.sin(d_latt/2)**2 + math.cos(start_latt) * math.cos(end_latt) * math.sin(d_long/2)**2
        double c = 2.0 * Math.atan2(Math.sqrt(a),  Math.sqrt(1.0-a));     
        //c = 2 * math.atan2(math.sqrt(a),  math.sqrt(1-a))
        return 6371.0 * c;

    }


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
/*
        double arad = 
Math.acos((Math.sin(Math.toRadians(to.lat)) -    //acos((sin(deg2rad($to->latitude)) -
 Math.sin(Math.toRadians(lat)) *                 //sin(deg2rad($this->latitude)) * 
 Math.cos(Math.toRadians(distance / 60.0))) /    // cos(deg2rad($dist / 60))) / 
(Math.sin(Math.toRadians(distance / 60.0)) *     //(sin(deg2rad($dist / 60)) * 
Math.cos(Math.toRadians(lat))));                 //cos(deg2rad($this->latitude))));
        System.out.println("arad " + Double.toString(arad));
        double bearing = arad * 180.0 / Math.PI; //$bearing = $arad * 180 / pi();
        if (Math.sin(Math.toRadians(to.lon - lon)) < 0.0) //if (sin(deg2rad($to->longitude - $this->longitude)) < 0)
        {
            bearing = 360.0 - bearing; //$bearing = 360 - $bearing;
        }
        
        return bearing;
*/

    }
}	



