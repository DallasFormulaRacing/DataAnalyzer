/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer.shortcuts;

import dataanalyzer.DataAnalyzer;
import java.util.ArrayList;

/**
 *
 * @author aribdhuka
 */
public class SaveFileCommand extends Command {
    
    public SaveFileCommand() {
        super("savefile", new ArrayList<String>());
    }
    
    

    @Override
    public void doAction(String[] cmd, DataAnalyzer da) {
        if(da.getChartManager().getDatasets().size() > 1) {
            da.saveFileAssembly(da.getOpenedFilePath());
        } else {
            da.saveFile(da.getOpenedFilePath());
        }
    }
    
}
