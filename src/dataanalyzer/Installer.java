/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer;

import dataanalyzer.dialog.MessageBox;
import dataanalyzer.dialog.Vital;
import dataanalyzer.dialog.VitalType;
import java.awt.Frame;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    }
    
    private static void runWindowsInstaller() {
        String home = System.getProperty("user.home");
        File dataAnalyzer = new File(home + "\\AppData\\Local\\DataAnalyzer\\");
        File vehicleData = new File(home + "\\AppData\\Local\\DataAnalyzer\\VehicleData\\");
        File chartConfig = new File(home + "\\AppData\\Local\\DataAnalyzer\\ChartConfigurations\\");
        File settings = new File(home + "\\AppData\\Local\\DataAnalyzer\\Settings\\");
        File temp = new File(home + "\\AppData\\Local\\DataAnalyzer\\Temp\\");
        File vitals = new File(home + "\\AppData\\Local\\DataAnalyzer\\Vitals\\");
        
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
        
        if (!vitals.isDirectory()) {
            vitals.mkdirs();
            setupDefaultVitals();
        }
    }
    
    private static void runLinuxInstaller() {
        String home = System.getProperty("user.home");
        File dataAnalyzer = new File("/Applications/DataAnalyzer/");
        File vehicleData = new File("/Applications/DataAnalyzer/VehicleData/");
        File chartConfig = new File("/Applications/DataAnalyzer/ChartConfigurations/");
        File settings = new File("/Applications/DataAnalyzer/Settings/");
        File temp = new File("/Applications/DataAnalyzer/Temp/");
        File vitals = new File ("/Applications/DataAnalyzer/Vitals/");
        
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
        
        if (!vitals.isDirectory()) {
            vitals.mkdirs();
            setupDefaultVitals();
        }
    }
    
    private static void setupDefaultVitals() {
        try {
            FileOutputStream fos = new FileOutputStream(new File(getHomePath() + "Vitals/vitals.dfrvit"));
            
            //oil pressure vital
            Vital oilpressure = new Vital("Time,OilPressure[psi]",true,true,VitalType.Error,VitalType.Warn,45.0,90.0);
            Vital coolant = new Vital("Time,CoolantTemp[F]",false,true,VitalType.Info,VitalType.Warn,0.0,220.0);
            Vital battery = new Vital("Time,BatteryVolt[V]",true,true,VitalType.Warn,VitalType.Warn,12.0,15.0);
            Vital RPM = new Vital("Time,RPM",true,true,VitalType.Warn,VitalType.Error,0.0,12500.0);
            Vital ignition = new Vital("Time,IgnitionAngle[DBTDC]",true,true,VitalType.Warn,VitalType.Warn,10,35.0);
            Vital tps = new Vital("Time,TPS[%]",true,true,VitalType.Warn,VitalType.Warn,0.0,100.0);
            Vital afr = new Vital("Time,AFR",true,true,VitalType.Warn,VitalType.Warn,11.0,17.0);
            
            fos.write(oilpressure.toString().getBytes());
            fos.write("\n".getBytes());
            fos.write(coolant.toString().getBytes());
            fos.write("\n".getBytes());
            fos.write(battery.toString().getBytes());
            fos.write("\n".getBytes());
            fos.write(RPM.toString().getBytes());
            fos.write("\n".getBytes());
            fos.write(ignition.toString().getBytes());
            fos.write("\n".getBytes());
            fos.write(tps.toString().getBytes());
            fos.write("\n".getBytes());
            fos.write(afr.toString().getBytes());
            fos.write("\n".getBytes());
            
            fos.close();
            
            
        } catch (FileNotFoundException e) {
            new MessageBox(new Frame(), "Couldn't setup inital vitals file! You will not have default vitals setup!", false).setVisible(true);
        } catch (IOException ex) {
            new MessageBox(new Frame(), "Couldn't setup inital vitals file! You will not have default vitals setup!", false).setVisible(true);
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
    
    /**
     * Helper function that automatically checks and provides OS
     * @return String of the home path for installation directory
     */
    public static String getHomePath() {
        return getHomePath(getOS());
    }
    
    /**
     * Provides the home path of installation for DataAnalyzer
     * @param os the current operating system
     * @return String of the home path for the installation directory
     */
    public static String getHomePath(String os) {
        if(os.equals("Windows")) {
            String home = System.getProperty("user.home");
            return home + "\\AppData\\Local\\DataAnalyzer\\";
        } else {
            return "/Applications/DataAnalyzer/";
        }
    }
    
}
