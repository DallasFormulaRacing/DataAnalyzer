/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer;

import com.arib.categoricalhashtable.CategoricalHashTable;
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
import java.util.TreeMap;
import org.jfree.chart.plot.ValueMarker;
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
    
    public void clearSelections() {
        datasetSelections = new LinkedList<>();
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
                            if(lo.getLaps().contains(lap)) {
                                data.add(lo);
                            }
                        }
                        double startDomain = 0;
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
                                if(uniqueTag.contains("Time,"))
                                    //add that to series
                                    series.add(x - l.getStart(), avg);
                                else if(uniqueTag.contains("Distance,")) {
                                    if(series.isEmpty()) {
                                        LogObject lo = data.get(0);
                                        if(lo instanceof FunctionOfLogObject)
                                            startDomain = ((FunctionOfLogObject) lo).getX();
                                    }
                                    series.add(x - startDomain, avg);
                                }
                                else
                                    series.add(x, avg);
                            }
                        }
                        //add created series to current tag collection
                        tagCollection.addSeries(series);
                    }
                    //if no laps were selected
                    if(ds.selectedLaps == null || ds.selectedLaps.isEmpty()) {
                        //create a series for this dataset+tag
                        XYSeries series = new XYSeries("D:"+ds.dataset.getName() + "-"+ uniqueTag);
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
        //for each tag
        for(String tag : getUniqueTags()) {
            //holds markers associated with this tag
            LinkedList<CategorizedValueMarker> tagMarkers = new LinkedList<>();
            //for each dataselection, get the static markers associated with this tag
            for(DatasetSelection datasetSelection : datasetSelections) {
                if(datasetSelection.selectedTags.contains(tag)) {
                    if(datasetSelection.dataset.getStaticMarkers().getList(tag) != null)
                        tagMarkers.addAll(datasetSelection.dataset.getStaticMarkers().getList(tag));
                }
            }
            allMarkers.addLast(tagMarkers);
        }
        
        //return the found markers
        return allMarkers;
    }
    
    /**
     * Gets all datasets that were selected
     * @return 
     */
    public LinkedList<Dataset> getAllSelectedDatasets() {
        LinkedList<Dataset> datasets = new LinkedList<>();
        //for each dataset selection, add the dataset
        for(DatasetSelection sel : datasetSelections) {
            datasets.add(sel.dataset);
        }
        //return all added datasets
        return datasets;
    }
    
    public TreeMap<CategorizedValueMarker, Double> getAllValuedMarkers() {
        TreeMap<CategorizedValueMarker, Double> map = new TreeMap<>();
        //for each tag
        for(String tag : getUniqueTags()) {
            //holds markers associated with this tag
            LinkedList<CategorizedValueMarker> tagMarkers = new LinkedList<>();
            //for each dataselection, get the static markers associated with this tag
            for(DatasetSelection datasetSelection : datasetSelections) {
                if(datasetSelection.selectedTags.contains(tag)) {
                    CategoricalHashTable<CategorizedValueMarker> staticMarkers = datasetSelection.dataset.getStaticMarkers();
                    //for each marker, (which should be unique, at least multiple instances of the same data)
                    if(staticMarkers.getList(tag) == null)
                        continue;
                    for(CategorizedValueMarker marker : staticMarkers.getList(tag)) {
                        map.put(marker, getValueAt(marker.getMarker().getValue(), datasetSelection.dataset.getDataMap().getList(tag)));
                    }
                }
            }
        }
        
        return map;
    }
    
    public void addMarker(ValueMarker marker) {
        addMarker(marker, "");
    }
    
    public void addMarker(ValueMarker marker, String notes) {
        for(DatasetSelection ds : datasetSelections) {
            for(String tag : ds.selectedTags) {
                ds.dataset.getStaticMarkers().put(new CategorizedValueMarker(tag, marker, notes));
            }
        }
    }
    
    public void addLap(Lap l) {
        //for each dataset selection
        for(DatasetSelection ds : datasetSelections) {
            if(ds.selectedTags.isEmpty())
                continue;
            Lap myLap = l.clone();
            myLap.start = roundToNearestLogObject(myLap.start, ds.dataset.getDataMap().getList(ds.selectedTags.get(0)));
            myLap.stop = roundToNearestLogObject(myLap.stop, ds.dataset.getDataMap().getList(ds.selectedTags.get(0)));
            //check that the lap is within the limits.
            if(myLap.start < ds.dataset.getDataTimeLength() && myLap.stop < ds.dataset.getDataTimeLength()) {
                //add the created lap
                ds.dataset.getLapBreaker().add(myLap);
                //apply the lap to the dataset
                Lap.applyToDataset(ds.dataset.getDataMap(), ds.dataset.getLapBreaker());
            }
        }
    }
    
    /**
     * Returns the value at a given point.
     * Handles children to return accurate values for simple and functionof objects
     * @param x domain value
     * @param data list of data
     * @return corresponding y value for given x
     */
    private double getValueAt(double x, LinkedList<LogObject> data) {
        double lastDiff = Double.MAX_VALUE;
        double valueToReturn = Double.NaN;
        //for each object, check if this is correct object, if so: return value
        for(LogObject lo : data) {
            if(lo instanceof SimpleLogObject) {
                if(Math.abs(lo.getTime() - x) < lastDiff) {
                    valueToReturn = ((SimpleLogObject) lo).getValue();
                    lastDiff = Math.abs(lo.getTime() - x);
                } else {
                    return valueToReturn;
                }
            } else if(lo instanceof FunctionOfLogObject) {
                if(Math.abs(((FunctionOfLogObject) lo).getX()-x) < lastDiff) { 
                    valueToReturn = ((FunctionOfLogObject) lo).getValue();
                    lastDiff = Math.abs(((FunctionOfLogObject) lo).getX()-x);
                } else {
                    return valueToReturn;
                }
            }
        }
        
        //return NaN for not value found
        return valueToReturn;
    }
    
    /**
     * Cuts the data to the two lengths specified
     * @param start start of data to cut to
     * @param end end of data to cut to
     */
    public void cutData(long start, long end) {
        boolean reApplyPostProcessing = false;
        //check for non Time, to have creation method.
        for(DatasetSelection ds : datasetSelections) {
            for(String tag : ds.dataset.getDataMap().getTags()) {
                if(!tag.startsWith("Time,")) {
                    if(ds.dataset.getDataMap().getList(tag).getFirst().getCreationMethod().isEmpty() || ds.dataset.getDataMap().getList(tag).getFirst().getCreationMethod().equals("Measured")) {
                        //warn user if they want to continue. Warn user of data loss
                        boolean shouldContinue = DataAnalyzer.createConfirmDialog("Loss of data possible! Continue?", "Continuing with cropping the data could cause loss of data. Yell at Arib to fix this. He doesn't want to though.");
                        if(!shouldContinue) {
                            reApplyPostProcessing = DataAnalyzer.createConfirmDialog("Apply Post Processing", "Some data can be restored through reapplying the original post processing. Press Yes to continue with this. Press No to Cancel.");
                            if(!reApplyPostProcessing)
                                return;
                            else
                                break;
                        } else {
                            break;
                        }
                    }
                }
            }
        }
        //for each dataset that is in this selection
        for(DatasetSelection ds : datasetSelections) {
            //if this dataset has something showing up currently on the screen and its not on a lap
            //TODO: implement lap math to cut to a lap
            if(!ds.selectedTags.isEmpty() && ds.selectedLaps.isEmpty()) {
                ds.dataset.cutData(start, end);
                if(reApplyPostProcessing) {
                    DataAnalyzer.applyPE3PostProcessing(ds.dataset);
                    DataAnalyzer.applyPostProcessing(ds.dataset);
                }
                
            }
            
        }
    }
    
    /**
     * Gets the list of objects for the selected tags for each respective dataset
     * @return list of lists of selected tags keyed by name
     */
    public TreeMap<String, LinkedList<LogObject>> getSelectedLists() {
        TreeMap<String, LinkedList<LogObject>> toReturn = new TreeMap<>();
        //for each dataset selection
        for(DatasetSelection ds : datasetSelections) {
            //for each selected tag of this dataset
            for(String tag : ds.selectedTags) {
                //get the data categorized by this tag
                LinkedList<LogObject> all = ds.dataset.getDataMap().getList(tag);
                //if there are no selected laps, then just add the whole data
                if(ds.selectedLaps.isEmpty()) {
                    toReturn.put("D:" + ds.dataset.getName() + "T:"+tag, all);
                } 
                //else add data for each lap
                else {
                    //for each lap in this selection
                    for(Integer l : ds.selectedLaps) {
                        //this holds the list of data objects that belong to this dataset, tag, and lap
                        LinkedList<LogObject> ofLap = new LinkedList<>();
                        //for each object in the complete set for this dataset and tag
                        for(LogObject lo : all) {
                            //if this object belongs to this lap add it
                            if(lo.getLaps().contains(l))
                                ofLap.add(lo);
                        }
                        //when finished add this to the list of list of objects toReturn
                        toReturn.put("D:" + ds.dataset.getName() + " T:" + tag + " L:" + l, ofLap);
                    }
                }
            }
        }
        
        //return the generated map
        return toReturn;
    }
    
    private long roundToNearestLogObject(long val, LinkedList<LogObject> los) {
        long valueToReturn = 0;
        long diff = Long.MAX_VALUE;
        for(LogObject lo : los) {
            if(Math.abs(lo.getTime() - val) < diff) {
                diff = Math.abs(lo.getTime() - val);
                valueToReturn = lo.getTime();
            }
            else
                break;
        }
        
        return valueToReturn;
    }
}