/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer.shortcuts;

import com.arib.toast.Toast;
import dataanalyzer.ChartAssembly;
import dataanalyzer.DataAnalyzer;
import dataanalyzer.Dataset;
import dataanalyzer.DatasetSelection;
import dataanalyzer.Selection;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aribdhuka
 */
public class AddChartCommand extends Command {

    
    public AddChartCommand() {
        super("addchart", new ArrayList<String>(Arrays.asList(new String[] {"$channel"})));
    }
    
    @Override
    public void doAction(String[] cmd, DataAnalyzer da) {
        ChartAssembly ca = da.getChartManager().addChart();
        Selection selection = ca.getSelection();
        //get all unique datasets needed
        ArrayList<Dataset> datasets = new ArrayList<>();
        for(int i = 1; i < cmd.length; i++) {
            if(!cmd[i].contains(".")){
                break;
            }
            String datasetName = cmd[i].split(".")[0];
            boolean exists = false;
            for(Dataset dataset : datasets) {
                if(dataset.getName().equals(datasetName)) {
                    exists = true;
                    break;
                }
            }
            if(exists == false) {
                Dataset d = da.getChartManager().getDataset(datasetName);
                if(d == null)
                    Toast.makeToast(da, "Dataset " + datasetName + " not found!", Toast.DURATION_MEDIUM);
                else
                    datasets.add(d);
            }
        }
        //get all tags
        ArrayList<String> selectedTags = new ArrayList<>();
        for(int i = 1; i < cmd.length; i++) {
            selectedTags.add(cmd[i]);
        }
        
        if(datasets.size() > 1) {
            for(Dataset dataset : datasets) {
                ArrayList<String> seltagsds = new ArrayList<>();
                ArrayList<Integer> laps = new ArrayList<>();
                for(String channel : selectedTags) {
                    if(channel.matches(dataset.getName() + ".*")) {
                        seltagsds.add(channel.split(".")[1]);
                    }
                }
                selection.addDatasetSelection(new DatasetSelection(dataset, seltagsds, laps));
                
            }
        } else {
            selection.addDatasetSelection(new DatasetSelection(da.getChartManager().getMainDataset(), selectedTags, new ArrayList<Integer>()));
        }
        
        ca.setChart();
        
        try {
            ca.getChartFrame().setSelected(true);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(AddChartCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    
    
}
