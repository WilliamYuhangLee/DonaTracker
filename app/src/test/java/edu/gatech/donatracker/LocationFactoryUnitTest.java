package edu.gatech.donatracker;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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

    @Before
    public void setUp() {
        rawListNOTNULL.add(0
                , new String[]{"Key","Name","Latitude","Longitude","Street Address","City","State"
                        ,"Zip", "Type","Phone","Website"});


    }

    @Test(timeout = TIMEOUT)
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
}
