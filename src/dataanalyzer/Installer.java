/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer;

import java.io.File;

/**
 *
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
        //TODO: Shadman's code
        String home = System.getProperty("user.home");
        File dataAnalyzer = new File(home + "\\AppData\\Local\\DataAnalyzer\\");
        File vehicleData = new File(home + "\\AppData\\Local\\DataAnalyzer\\VehicleData\\");
        File chartConfig = new File(home + "\\AppData\\Local\\DataAnalyzer\\ChartConfigurations\\");
        File settings = new File(home + "\\AppData\\Local\\DataAnalyzer\\Settings\\");
        
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
        
    }
    
    private static void runLinuxInstaller() {
        String home = System.getProperty("user.home");
        File dataAnalyzer = new File(home + "/Applications/DataAnalyzer/");
        File vehicleData = new File(home + "/Applications/DataAnalyzer/VehicleData/");
        File chartConfig = new File(home + "/Applications/DataAnalyzer/ChartConfigurations/");
        File settings = new File(home + "/Applications/DataAnalyzer/Settings/");

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
        
    }
    
}
