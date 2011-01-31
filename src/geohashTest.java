/*
 * Compile:
 * javac -classpath "./:/home/ihmis-suski/Downloads/junit-4.8.2.jar" geohashTest.java 
 * Run:
 * java -classpath "./:/home/ihmis-suski/Downloads/junit-4.8.2.jar" org.junit.runner.JUnitCore geohashTest
 */

import org.junit.*;
import static org.junit.Assert.*;

public class geohashTest {

    @Test
    public void testHashInGraticuleLat() {
        try {
            Point hash = Geohash.getGeohash(new Point(60.0, 24.0));
            assertEquals((int) 24.2505529752829, (int) hash.lat);
        }
        catch (Exception e)
        {
        }
    }

    @Test
    public void testHashInGraticuleLon() {
        try {
            Point hash = Geohash.getGeohash(new Point(60.0, 24.0));
            assertEquals((int) 60.4615111025175, (int) hash.lon);
        }
        catch (Exception e)
        {
        }
    }




} 
