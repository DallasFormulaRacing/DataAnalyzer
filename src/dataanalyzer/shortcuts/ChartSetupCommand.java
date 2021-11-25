/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer.shortcuts;

import dataanalyzer.ChartConfiguration;
import dataanalyzer.DataAnalyzer;
import dataanalyzer.Installer;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import org.json.simple.parser.ParseException;

/**
 *
 * @author aribdhuka
 */
public class ChartSetupCommand extends Command {
    
    public ChartSetupCommand() {
        super("chartsetup", new ArrayList<String>(Arrays.asList(new String[] {"$chartsetupname"})));
    }

    @Override
    public void doAction(String[] cmd, DataAnalyzer da) {
        if(cmd.length > 1) {
            String chartName = Installer.getInstallationPath()+ "ChartConfigurations/" + cmd[1] + ".dfrchartconfig";
            try {
                ChartConfiguration.openChartConfiguration(chartName, da, da.getChartManager());
            } catch (FileNotFoundException e) {
                System.err.println("Couldn't find the chart config you were looking for!");
            } catch (IOException | ParseException e) {
                System.err.println("Couldn't find the chart config you were looking for!");
            }
        }
    }
    
    
    
}
