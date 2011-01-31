/*
 * Compile:
 * javac -classpath "./:/home/ihmis-suski/Downloads/junit-4.8.2.jar" -Xlint:deprecation PointTest.java 
 * Run:
 * java -classpath "./:/home/ihmis-suski/Downloads/junit-4.8.2.jar" org.junit.runner.JUnitCore PointTest
 */

import org.junit.*;
import static org.junit.Assert.*;

public class PointTest {

    Point efhf = new Point(60.254558, 25.042828);
    Point efhk = new Point(60.317222, 24.963333);
    Point fymg = new Point(-22.083332, 17.366667);

    @Test
    public void distance() {
        assertEquals(8.2, efhf.distanceTo(efhk), 0.2);
    }

    @Test
    public void distanceSame() {
        assertEquals(0.0, efhf.distanceTo(efhf), 0.2);
    }

    @Test
    public void distanceBack() {
        assertEquals(efhf.distanceTo(efhk), efhk.distanceTo(efhf), 0.2);
    }

    @Test
    public void distanceLong() {
        assertEquals(9181.6, efhf.distanceTo(fymg), 0.5);
    }

    @Test
    public void bearing() {
        assertEquals(329.0, efhf.bearingTo(efhk), 2.2);
    }

    @Test
    public void bearingSame() {
        assertEquals(0.0, efhf.bearingTo(efhf), 0.2);
    }

    @Test
    public void bearingLong() {
        assertEquals(187, efhf.bearingTo(fymg), 0.2);
    }
}   
