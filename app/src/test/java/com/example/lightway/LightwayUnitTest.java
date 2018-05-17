package com.example.lightway;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class LightwayUnitTest {

    RegisterActivity registerActivity = new RegisterActivity();

    @Test
    public void incorrectPasswordFormat() throws Exception {
        assertFalse(registerActivity.correctPasswordFormat("test1"));
    }
}