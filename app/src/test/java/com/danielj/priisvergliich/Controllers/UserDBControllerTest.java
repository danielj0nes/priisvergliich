package com.danielj.priisvergliich.Controllers;

import android.content.Context;

import com.danielj.priisvergliich.Models.UserModel;

import junit.framework.TestCase;

public class UserDBControllerTest extends TestCase {

    Context testContext = null;
    public void testModifyUser() {
        // First initialise the controller and pass in a null context for test purposes
        UserDBController dbc = new UserDBController(testContext);
        // Next initialise a UserModel class as a valid input
        UserModel user = new UserModel(0, "Billy", 0, 0);
        // Attempt to insert record into the database
        Boolean check = dbc.modifyUser(user);
        assertTrue(check == true); // On success; returns true
        // Now modify the user object and attempt again
        user.setName("James");
        user.setLatitude(-40.128);
        check = dbc.modifyUser(user);
        assertTrue(check == true);


    }
}