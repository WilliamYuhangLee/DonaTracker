package edu.gatech.donatracker;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import edu.gatech.donatracker.model.Location;
import edu.gatech.donatracker.util.LocationFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class InventoryTest {
    private static final int TIMEOUT = 200;
    private List<String[]> rawListNULL = null;
    private List<String[]> rawListNOTNULL = new ArrayList<>();
    private String[] key = new String[]{"Key","Name","Latitude","Longitude","Street Address"
            ,"City","State","Zip", "Type","Phone","Website"};
    private String[] dummyData = new String[]{"1", "AFD Station 4", "33.75416", "-84.37742"
            , "309 EDGEWOOD AVE SE", "Atlanta", "GA", "30332", "Drop Off", "(404) 555 - 3456"
            , "www.afd04.atl.ga"};


    @Before
    public void setUp() {
        rawListNOTNULL.add(0, key);
        rawListNOTNULL.add(1, dummyData);
    }

    @Test(timeout = TIMEOUT)
    public void containsInventoryTest() {
        List<Location> list = LocationFactory.parseLocations(rawListNOTNULL);
        assertFalse(list.get(0).containsInventory("inventory"));
    }
}
