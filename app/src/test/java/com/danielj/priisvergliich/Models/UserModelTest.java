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
        // Create new user of class UserModel and add default values, take note of the id
        UserModel user = new UserModel(0, "", 0, 0);
        int expected = 0;
        assertEquals(expected, user.getId());
        // Next change the expected value and ensure it's still working
        expected = 22;
        assertFalse(user.getId() == expected);
    }

    public void testSetId() {
        // First initialise empty object of type UserModel
        UserModel user = new UserModel();
        // Attempt to set the Id
        user.setId(5);
        int expected = 5;
        // Utilise the already tested getId method check equality and pass test
        assertEquals(expected, user.getId());
    }

    public void testGetName() {
        // Although name functionality was never implemented, we can test it anyway
        UserModel user = new UserModel(0, "Daniel", 0, 0);
        String expected = "Daniel";
        assertEquals(expected, user.getName());
    }

    public void testSetName() {
        UserModel user = new UserModel();
        user.setName("Thomas");
        String expected = "Thomas";
        // Again utilise the already tested method to ensure accuracy
        assertEquals(expected, user.getName());
    }

    public void testGetLatitude() {
        UserModel user = new UserModel(0, "Joseph", -77.0364, 38.8951);
        double expected = -77.0364;
        assertEquals(expected, user.getLatitude());
    }

    public void testSetLatitude() {
        UserModel user = new UserModel();
        user.setLatitude(-1.0378);
        double expected = -1.0378;
        assertEquals(expected, user.getLatitude());
    }

    public void testGetLongitude() {
        UserModel user = new UserModel(0, "Joseph", -77.0364, 38.8951);
        double expected = 38.8951;
        assertEquals(expected, user.getLongitude());
    }

    public void testSetLongitude() {
        UserModel user = new UserModel();
        user.setLongitude(101.3893);
        double expected = 101.3893;
        assertEquals(expected, user.getLongitude());
    }
}