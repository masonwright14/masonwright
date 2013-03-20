package edu.vanderbilt.hw3.driver;

import edu.vanderbilt.hw3.controllers.MasterController;

/**
 * Starts up the project.
 * 
 * @author Mason Wright
 */
public abstract class Driver {

    /**
     * Starts the project.
     * 
     * @param args unused
     */
    public static void main(final String[] args) {
        new MasterController().startUp();
    }
}
