package com.example.lightway;

import android.content.Context;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class RegisterActivityTest {

    Context mMockContext;
    RegisterActivity registerActivity = new RegisterActivity();

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void correctPasswordTest(){
        assertTrue(registerActivity.correctPasswordFormat("Test123"));
    }

    @Test
    public void incorrectPasswordTest(){
        assertFalse(registerActivity.correctPasswordFormat("test1"));
    }

    @After
    public void tearDown() throws Exception {
    }
}