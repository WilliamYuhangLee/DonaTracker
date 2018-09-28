package edu.gatech.donatracker.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yuhang Li on 2018/09/28
 *
 * This is our facade to the Model.
 * We use a singleton design pattern to provide access to the model from each
 * controller.
 *
 */

public class Model {
    // Singleton instance
    private static final Model instance = new Model();
    public static Model getModel() {return instance;}

    // Holds the list of all Users
    private List<User> users;

    // Constructor
    private Model() {
        users = new ArrayList<>();

        // for debugging, delete when project is finished
        loadDummyUsers();
    }

    /**
     * Populate the model with dummy users.
     *
     * When the app is completed, delete this method.
     *
     */
    private void loadDummyUsers() {

    }

    
}
