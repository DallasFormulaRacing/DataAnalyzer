/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer.dialog;

import dataanalyzer.Dataset;
import dataanalyzer.Lap;
import dataanalyzer.LogObject;
import java.util.ArrayList;
import java.util.LinkedList;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * The selection class handles the result from the tag chooser dialog.  
 * @author aribdhuka
 */
public class Selection {
    
    private LinkedList<DatasetSelection> datasetSelections;
    
    public Selection() {
        datasetSelections = new LinkedList<>();
    }
    
    public void addDatasetSelection(DatasetSelection ds) {
        datasetSelections.add(ds);
    }
    
    public ArrayList<String> getUniqueTags() {
        //TODO: Implement this
        ArrayList<String> uniqueTags = new ArrayList<>();
        for(DatasetSelection ds : datasetSelections) {
            for(String tag : ds.selectedTags) {
                if(!uniqueTags.contains(tag))
                    uniqueTags.add(tag);
            }
        }
        
        return uniqueTags;
    }
    
    public XYSeriesCollection[] getDataCollection() {
        //TODO: Implement this
//        for(uniqueTags)
//	for(selectedDatasets)
//		if(selectedDatasets.contains(uniqueTags)
//			for(selectedDatasets.laps)
//				selectedDataset.datamap.get(tag) where log object contains lap
//                                      

        XYSeriesCollection[] fullCollection = new XYSeriesCollection[getUniqueTags().size()];
        for(String uniqueTag : getUniqueTags()) {
            XYSeriesCollection tagCollection = new XYSeriesCollection();
            for(DatasetSelection ds : datasetSelections) {
                if(ds.selectedTags.contains(uniqueTag)) {
                    for(int lap : ds.selectedLaps) {
                        //find Lap obj
                        Lap l = new Lap();
                        for(Lap tempLap : ds.dataset.getLapBreaker()) {
                            if(tempLap.getLapNumber() == lap)
                                l = tempLap;
                        }
                        XYSeries series = new XYSeries("D:"+ds.dataset.getName() + "-"+uniqueTag+"-" +"L:"+lap);
                        for(LogObject lo : ds.dataset.getDataMap().getList(uniqueTag)) {
                            if(lo.getLaps().contains(lap)) {
                                //Get the x and y values by seprating them by the comma
                                String[] values = lo.toString().split(",");
                                //Add the x and y value to the series
                                series.add(Double.parseDouble(values[0]) - l.getStart(), Double.parseDouble(values[1]));
                            }
                        }
                    }
                    if(ds.selectedLaps == null || ds.selectedLaps.size() == 0) {
                        XYSeries series = new XYSeries("D:"+ds.dataset.getName() + "-"+uniqueTag);
                        for(LogObject lo : ds.dataset.getDataMap().getList(uniqueTag)) {
                                                       //Get the x and y values by seprating them by the comma
                                String[] values = lo.toString().split(",");
                                //Add the x and y value to the series
                                series.add(Double.parseDouble(values[0]), Double.parseDouble(values[1]));
                        }
                    }
                }
            }
        }
        
        return fullCollection;
    }
    
    public SimpleHistogramDataset getHistogramDataCollection() {
        //TODO: Implement this
    }
    
    public int getCollectionLength() {
        int count = 0;
        for(DatasetSelection ds : datasetSelections) {
            count += ds.selectedLaps.size() * ds.selectedTags.size();
        }
        return count;
    }
    
}
