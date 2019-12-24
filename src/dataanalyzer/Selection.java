/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer;

import dataanalyzer.DatasetSelection;
import dataanalyzer.CategorizedValueMarker;
import dataanalyzer.Dataset;
import dataanalyzer.FunctionOfLogObject;
import dataanalyzer.Lap;
import dataanalyzer.LogObject;
import dataanalyzer.SimpleLogObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import org.jfree.data.statistics.SimpleHistogramBin;
import org.jfree.data.statistics.SimpleHistogramDataset;
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
    
    public Selection(LinkedList<DatasetSelection> datasetSelections) {
        this.datasetSelections = datasetSelections;
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
        return getDataCollection(1);
    }
    
    public XYSeriesCollection[] getDataCollection(int bucketSize) {
        //the full collection
        XYSeriesCollection[] fullCollection = new XYSeriesCollection[getUniqueTags().size()];
        //holds the index to add collection to in above array
        int index = 0;
        //for each unique tag
        for(String uniqueTag : getUniqueTags()) {
            //Create a collection for this tag
            XYSeriesCollection tagCollection = new XYSeriesCollection();
            //for each dataset
            for(DatasetSelection ds : datasetSelections) {
                //if it contains this tag
                if(ds.selectedTags.contains(uniqueTag)) {
                    //iterate through all its selected laps
                    for(int lap : ds.selectedLaps) {
                        //find Lap obj
                        Lap l = new Lap();
                        //for each lap in the lap breaker, find the correct lap obj
                        for(Lap tempLap : ds.dataset.getLapBreaker()) {
                            if(tempLap.getLapNumber() == lap)
                                l = tempLap;
                        }
                        //create a series for this instance of Dataset+Tag+Lap
                        XYSeries series = new XYSeries("D:"+ds.dataset.getName() + "-"+uniqueTag+"-" +"L:"+lap);
                        //for each log object
                        LinkedList<LogObject> dataLinked = ds.dataset.getDataMap().getList(uniqueTag);
                        //copy to a arraylist for a better runtime letter
                        ArrayList<LogObject> data = new ArrayList<>(ds.dataset.getDataMap().getList(uniqueTag).size());
                        for(LogObject lo : dataLinked) {
                            if(lo.getLaps().contains(lap))
                                data.add(lo);
                        }
                        //for each item left in the data
                        for(int i = 0; i < data.size(); i++) {
                            //modifes the index
                            int modifier = ((bucketSize - 1) / 2) * -1;
                            //holds current avg
                            double avg = 0;
                            //x to add to series
                            double x = 0;
                            //how many indecies we actually used
                            int usedIndecies = 0;
                            //while we have not accounted for each item in the bucket
                            while(modifier <= ((bucketSize - 1) / 2)) {
                                //make sure the index is correct
                                if(i + modifier > -1 && i + modifier < data.size()) {
                                    //add this index value's object's value
                                    avg += Double.parseDouble(data.get(i + modifier).toString().split(",")[1]);
                                    //increment usedIndevies
                                    usedIndecies++;
                                }
                                if(modifier == 0) {
                                    x = Double.parseDouble(data.get(i + modifier).toString().split(",")[0]);
                                }
                                modifier++;
                            }

                            if(usedIndecies != 0) {
                                //calculate average
                                avg /= usedIndecies;
                                //add that to series
                                series.add(x - l.getStart(), avg);
                            }
                        }
                        //add created series to current tag collection
                        tagCollection.addSeries(series);
                    }
                    //if no laps were selected
                    if(ds.selectedLaps == null || ds.selectedLaps.isEmpty()) {
                        //create a series for this dataset+tag
                        XYSeries series = new XYSeries("D:"+ds.dataset.getName() + "-"+uniqueTag);
                        //for each log object
                        LinkedList<LogObject> dataLinked = ds.dataset.getDataMap().getList(uniqueTag);
                        //copy to a arraylist for a better runtime letter
                        ArrayList<LogObject> data = new ArrayList<>(ds.dataset.getDataMap().getList(uniqueTag).size());
                        for(LogObject lo : dataLinked) {
                            data.add(lo);
                        }
                        //for each item left in the data
                        for(int i = 0; i < data.size(); i++) {
                            //modifes the index
                            int modifier = ((bucketSize - 1) / 2) * -1;
                            //holds current avg
                            double avg = 0;
                            //x to add to series
                            double x = 0;
                            //how many indecies we actually used
                            int usedIndecies = 0;
                            //while we have not accounted for each item in the bucket
                            while(modifier <= ((bucketSize - 1) / 2)) {
                                //make sure the index is correct
                                if(i + modifier > -1 && i + modifier < data.size()) {
                                    //add this index value's object's value
                                    avg += Double.parseDouble(data.get(i + modifier).toString().split(",")[1]);
                                    //increment usedIndevies
                                    usedIndecies++;
                                }
                                if(modifier == 0) {
                                    x = Double.parseDouble(data.get(i + modifier).toString().split(",")[0]);
                                }
                                modifier++;
                            }

                            if(usedIndecies != 0) {
                                //calculate average
                                avg /= usedIndecies;
                                //add that to series
                                series.add(x, avg);
                            }
                        }
                        //add created series to current tag collection
                        tagCollection.addSeries(series);
                    }
                }
            }
            //add this tags collection to the list of all collections
            fullCollection[index] = tagCollection;
            //increment the index
            index++;
        }
        
        return fullCollection;
    }
    
    public SimpleHistogramDataset getHistogramDataCollection() {
        //collection to return
        SimpleHistogramDataset histogramDataset = new SimpleHistogramDataset("time");
        histogramDataset.setAdjustForBinSize(false);

        //We are currently only capable of showing one histogram per chart assembly
        //get data from dataset
        LinkedList<LogObject> data = datasetSelections.get(0).dataset.getDataMap().getList(getUniqueTags().get(0));
        
        //calculate min and max value of the data 
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for(LogObject lo : data) {
            if(lo instanceof SimpleLogObject) {
                if(((SimpleLogObject) lo).getValue() > max)
                    max = ((SimpleLogObject) lo).getValue();
                if(((SimpleLogObject) lo).getValue() < min)
                    min = ((SimpleLogObject) lo).getValue();
            }
        }

        //get the intervals to work with
        double interval = max - min;
        interval /= 50;

        //for each of the 50 intervals
        for(int i = 1; i < 51; i++) {
            //A bin for this section
            SimpleHistogramBin bin = new SimpleHistogramBin(interval*(i-1) + min, interval*(i) + min - .000001);

            //for each data element
            for(LogObject lo : data) {
                //if its a simple log object its value can be obtained
                if(lo instanceof SimpleLogObject) {
                    //if the value of the current object is between the interval we are searching for
                    if(((SimpleLogObject) lo).getValue() < ((interval * i) + min) && ((SimpleLogObject) lo).getValue() > ((interval * (i-1)) + min)) {
                        //increment the counter
                            bin.setItemCount(bin.getItemCount() + 50);
                    }
                } else if(lo instanceof FunctionOfLogObject) {
                    if(((FunctionOfLogObject) lo).getValue() < ((interval * i) + min) && ((FunctionOfLogObject) lo).getValue() > ((interval * (i-1)) + min)) {
                        //increment the counter
                            bin.setItemCount(bin.getItemCount() + 50);
                    }
                }
            }
            //if the counter is not 0, add the median of the interval we are looking for along with the counter to the series.
            histogramDataset.addBin(bin);

        }
        return histogramDataset;
     
    }
    
    public int getCollectionLength() {
        int count = 0;
        for(DatasetSelection ds : datasetSelections) {
            count += ds.selectedLaps.size() * ds.selectedTags.size();
        }
        return count;
    }
   
    public LinkedList<LinkedList<CategorizedValueMarker>> getAllMarkers() {
        
        //for each dataset, get its selection tags, and get its markers
        LinkedList<LinkedList<CategorizedValueMarker>> allMarkers = new LinkedList<>();
        for(String tag : getUniqueTags()) {
            LinkedList<CategorizedValueMarker> tagMarkers = new LinkedList<>();
            for(DatasetSelection datasetSelection : datasetSelections) {
                if(datasetSelection.selectedTags.contains(tag)) {
                    tagMarkers.addAll(datasetSelection.dataset.getStaticMarkers().getList(tag));
                }
            }
            allMarkers.addLast(tagMarkers);
        }
        
        //return the found markers
        return allMarkers;
    }
    
}
