package com.danielj.priisvergliich.Models;

import junit.framework.TestCase;

public class UserModelTest extends TestCase {

    public void testToString() {
        // First build the example test user
        UserModel user = new UserModel(0, "", 0, 0);
        // Next create expected output string
        String expected = "UserModel{id=0, name='\', " +
                "latitude=0.0, longitude=0.0}";
        assertEquals(expected, user.toString());
    }

    public void testGetId() {
    }

    public void testSetId() {
    }

    public void testGetName() {
    }

    public void testSetName() {
    }

    public void testGetLatitude() {
    }

    public void testSetLatitude() {
    }

    public void testGetLongitude() {
    }

    public void testSetLongitude() {
    }
}