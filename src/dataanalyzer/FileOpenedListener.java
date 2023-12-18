/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer;

/**
 * FileOpenedListener for Objects that need to update when a new dataset file
 * or any other file (VehicleData) is opened.
 * 
 * @author aribdhuka
 */
public interface FileOpenedListener {
    
    /**
     * Function to be implemented by consumer and triggered when a file is opened.
     * @param o Resulting Object from file opening
     */
    void fileOpened(Object o);
}
