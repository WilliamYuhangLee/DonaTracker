package edu.gatech.donatracker;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UnitTest {

    private static final int TIMEOUT = 200;

    @Before
    public void setUp() {

    }

    @Test(timeout = TIMEOUT)
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
}
