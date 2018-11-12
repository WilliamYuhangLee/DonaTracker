package edu.gatech.donatracker;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import edu.gatech.donatracker.model.Location;
import edu.gatech.donatracker.util.LocationFactory;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */


public class LocationFactoryUnitTest {

    private static final int TIMEOUT = 200;
    private List<String[]> rawListNULL = null;
    private List<String[]> rawListNOTNULL = new ArrayList<>();
    private String[] keys = new String[]{"Key","Name","Latitude","Longitude","Street Address"
            ,"City","State","Zip", "Type","Phone","Website"};
    private String[] dummyData = new String[]{"1", "AFD Station 4", "33.75416", "-84.37742"
            , "309 EDGEWOOD AVE SE", "Atlanta", "GA", "30332", "Drop Off", "(404) 555 - 3456"
            , "www.afd04.atl.ga"};


    @Before
    public void setUp() {
        rawListNOTNULL.add(0, keys);
        rawListNOTNULL.add(1, dummyData);
    }

    @Test(timeout = TIMEOUT)
    public void listNotNullTest() {
        List<Location> list = LocationFactory.parseLocations(rawListNOTNULL);
        assertEquals(list.get(0).getKey(), Integer.parseInt(dummyData[0]));
        assertEquals(list.get(0).getName(), dummyData[1]);
        assertEquals(list.get(0).getLatitude(), Double.parseDouble(dummyData[2]), 0);
        assertEquals(list.get(0).getLongitude(), Double.parseDouble(dummyData[3]), 0);
        assertEquals(list.get(0).getAddress(), dummyData[4]);
        assertEquals(list.get(0).getCity(), dummyData[5]);
        assertEquals(list.get(0).getState(), dummyData[6]);
        assertEquals(list.get(0).getZip(), Integer.parseInt(dummyData[7]));
        assertEquals(list.get(0).getType(), dummyData[8]);
        assertEquals(list.get(0).getPhone(), dummyData[9]);
        assertEquals(list.get(0).getWebsite(), dummyData[10]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void listNullTest() {
        LocationFactory.parseLocations(rawListNULL);
    }
}
