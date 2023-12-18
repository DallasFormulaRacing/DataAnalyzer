/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer;

/**
 * LoadingListener for Objects that need to do something while something is 
 * starting to load, or finished loading. I.e show a spinning wheel.
 * 
 * @author aribdhuka
 */
public interface LoadingListener {
    
    /**
     * Function to be implemented by consumer and triggered when the loading
     * state is changed.
     */
    void loadingEvent(LoadingEvent le);
}
