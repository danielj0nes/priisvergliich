package com.danielj.priisvergliich.Controllers;

import android.view.View;
import android.widget.ListView;

import com.danielj.priisvergliich.Activities.MainActivity;
import com.danielj.priisvergliich.R;

import junit.framework.TestCase;

public class RequestsControllerTest extends TestCase {

    public void testGetMigrosProducts() {
        // First create the requests controller
        RequestsController rc = new RequestsController();
        // Prepare a query
        String query = "Coke";
        // Run the function and assert true within the callback as long as the result is not null
        rc.getMigrosProducts(query, resultMigros -> assertTrue(resultMigros != null));
    }

    public void testGetCoopProducts() {
        // First create the requests controller
        RequestsController rc = new RequestsController();
        // Prepare a query
        String query = "Zweifel";
        rc.getCoopProducts(query, resultCoop -> assertTrue(resultCoop != null));
    }
}