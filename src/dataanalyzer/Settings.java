/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Settings class is a singleton object that holds a tree map of the user's
 * settings. It's functions handle opening the file from the user's installation
 * path and saving the file back when changes are made.
 * @author aribdhuka
 */
public class Settings {
    
    //the Settings instance for this window
    private static Settings instance;
    //the treemap that holds a setting name and setting value
    private TreeMap<String, String> map;
    
    /**
     * Instantiated the singleton object if not already done so. This method is
     * private and can only be accessed by the getInstance() method if an
     * instance does not already exist.
     */
    private Settings() {
        //init values
        map = new TreeMap<>();
        //get the path of the settings file from the installer
        String settingsFile = Installer.getSettingsPath(Installer.getOS()) + "userpreferences.conf";
        //open the file
        Scanner scan;
        try {
            scan = new Scanner(new File(settingsFile));
        } catch (FileNotFoundException ex) {
            setDefaults();
            return;
        }
        //for each line
        while(scan.hasNextLine()) {
            //get the line
            String line = scan.nextLine();
            //skip if empty
            if(line.isEmpty())
                continue;
            //add the key value settings pair to the treemap
            String[] split = line.split(":");
            map.put(split[0], split[1]);
        }
    }
    
    //if a file is not found these are default settings values.
    private void setDefaults() {
        map.put("AutoCheckForUpdates", "true");
        map.put("PreferredTheme", "Default");
        map.put("AlwaysApplyPostProcessing", "Ask");
    }
    
    //Gets this processes settings instance
    public static Settings getInstance() {
        if(instance == null)
            instance = new Settings();
        
        return instance;
    }
    
     /**
     * Gets all the settings currently in the map and outputs them to a file
     */
    public void save() {
        //delete the current settings file
        new File(Installer.getSettingsPath() + "userpreferences.conf").delete();
        //Try to open a file writer and write out the current settings to the file
        //ISSUE: An issue that can arise here is that a file could be deleted and a new file fails to write
        try {
            //create writer object
            FileWriter writer = new FileWriter(new File(Installer.getSettingsPath() + "userpreferences.conf"));
            //output each key value pair to the file
            for(String key : map.keySet()) {
                writer.write(key + ":" + map.get(key) + "\n");
            }
            writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
            return;
        } catch (IOException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Gets the settings value given a name.
     * @param settingName the name of the setting we are looking for
     * @return returns the settings value. returns empty string if not found
     */
    public String getSetting(String settingName) {
        return map.get(settingName) == null ? "" : map.get(settingName);
    }
    
    /**
     * Sets the value for a settings
     * @param settingName the setting we are modifying
     * @param settingValue the new value for the settings
     */
    public void setSetting(String settingName, String settingValue) {
        map.put(settingName, settingValue);
    }
}
