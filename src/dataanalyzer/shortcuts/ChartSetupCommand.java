/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer.shortcuts;

import dataanalyzer.DataAnalyzer;
import java.util.ArrayList;
import java.util.Arrays;

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
        
    }
    
    
    
}
