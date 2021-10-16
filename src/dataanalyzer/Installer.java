/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer;

import java.io.File;

/**
 * this checks the os type then creates if they don't already exist
 * @author aribdhuka
 */
public class Installer {
           
    public static void runInstaller() {
        String OS = System.getProperty("os.name");
        
        if(OS.startsWith("Windows")){
            runWindowsInstaller();
        }
        if(OS.startsWith("Mac")){
            runLinuxInstaller();
        }
        if(OS.startsWith("Linux")) {
            runLinuxInstaller();
        }
    }
    
    private static void runWindowsInstaller() {
        String home = System.getProperty("user.home");
        File dataAnalyzer = new File(home + "\\AppData\\Local\\DataAnalyzer\\");
        File vehicleData = new File(home + "\\AppData\\Local\\DataAnalyzer\\VehicleData\\");
        File chartConfig = new File(home + "\\AppData\\Local\\DataAnalyzer\\ChartConfigurations\\");
        File settings = new File(home + "\\AppData\\Local\\DataAnalyzer\\Settings\\");
        File temp = new File(home + "\\AppData\\Local\\DataAnalyzer\\Temp\\");
        
        if (!dataAnalyzer.isDirectory()) {
           dataAnalyzer.mkdirs();

        }   
        if (!vehicleData.isDirectory()){
             vehicleData.mkdirs();

        }
        if (!chartConfig.isDirectory()){
            chartConfig.mkdirs();

        }
        if (!settings.isDirectory()){
            settings.mkdirs();
        }
        
        if (!temp.isDirectory()){
            temp.mkdirs();
        }
    }
    
    private static void runLinuxInstaller() {
        String home = System.getProperty("user.home");
        File applicationsFolder = new File("/Applications/");
        File dataAnalyzer = new File("/Applications/DataAnalyzer/");
        File vehicleData = new File("/Applications/DataAnalyzer/VehicleData/");
        File chartConfig = new File("/Applications/DataAnalyzer/ChartConfigurations/");
        File settings = new File("/Applications/DataAnalyzer/Settings/");
        File temp = new File("/Applications/DataAnalyzer/Temp/");
        
        if(!applicationsFolder.isDirectory()) {
            boolean created = applicationsFolder.mkdir();
            //TODO: Could create a dialog to ask the user to create directory as admin
            //commands to do as follows
            //mkdir /Applications/
            //name=$(whoami)
            //sudo chmod a+wrx Applications/
            //above command should hopefully prompt OS to show insert password dialog to run. Needs to be tested.
            if(created == false) {
                System.err.println("Errror in installer could not continue!!! Try creating /Applications/ and giving permissions.");
                System.exit(1);
            }
        }
        
        if (!dataAnalyzer.isDirectory()) {
           dataAnalyzer.mkdir();

        }   
        if (!vehicleData.isDirectory()){
             vehicleData.mkdir();

        }
        if (!chartConfig.isDirectory()){
            chartConfig.mkdir();

        }
        if (!settings.isDirectory()){
            settings.mkdir();
        }
        
        if (!temp.isDirectory()){
            temp.mkdirs();
        }
    }
    
    /**
     * Gets the operating system the user is using
     * @return Cleaned up string of the users operating system
     */
    public static String getOS() {
        //get the os name
        String OS = System.getProperty("os.name");

        //clean up name and return
        if(OS.startsWith("Windows")){
            return "Windows";
        }
        if(OS.startsWith("Mac")){
            return "Mac";
        }
        if(OS.startsWith("Linux")) {
            return "Linux";
        }
        
        return "Other";
    }
    
    /**
     * Helper function that auto declares OS from getOS() method
     * @return path of settings file
     */
    public static String getSettingsPath() {
        return getSettingsPath(getOS());
    }
    
    /**
     * Get path for settings
     * @param os given an operating system
     * @return path of settings file
     */
    public static String getSettingsPath(String os) {
        if(os.equals("Windows")) {
            String home = System.getProperty("user.home");
            return home + "\\AppData\\Local\\DataAnalyzer\\Settings\\";
        } else {
            return "/Applications/DataAnalyzer/Settings/";
        }
    }
    
}
