/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer;

import com.arib.categoricalhashtable.CategoricalHashTable;
import java.util.ArrayList;

/**
 * The Dataset class is a list of dataMaps. This will provide functionality to
 * view data from multiple datasets. So if more than one file is opened within a
 * single DataAnalyzer app, we can separate these two datasets.
 * @author aribdhuka
 */
public class Dataset {
    
    //A dataset need a name
    String name;
    
    // Stores the data set for each data type ( RPM vs Time, Distance vs Time....)
    private CategoricalHashMap dataMap;
    
    //stores the static markers
    private CategoricalHashTable<CategorizedValueMarker> staticMarkers;
    
    //holds vehicle parameters
    private VehicleData vehicleData;
    
    //stores the laps breaker
    private ArrayList<Lap> lapBreaker;
    
    /**
     * Constructor that initializes array and appends an item
     */
    public Dataset() {
        name = "A dataset has no name";
        dataMap = new CategoricalHashMap(80);
        staticMarkers = new CategoricalHashTable<>();
        vehicleData = new VehicleData();
        lapBreaker = new ArrayList<>();
    }
    
    /**
     * Constructor that initializes array and appends an item
     */
    public Dataset(String name) {
        this.name = name;
        dataMap = new CategoricalHashMap();
        staticMarkers = new CategoricalHashTable<>();
        vehicleData = new VehicleData();
        lapBreaker = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public CategoricalHashMap getDataMap() {
        return dataMap;
    }

    public void setDataMap(CategoricalHashMap dataMap) {
        this.dataMap = dataMap;
    }

    public CategoricalHashTable<CategorizedValueMarker> getStaticMarkers() {
        return staticMarkers;
    }

    public void setStaticMarkers(CategoricalHashTable<CategorizedValueMarker> staticMarkers) {
        this.staticMarkers = staticMarkers;
    }

    public VehicleData getVehicleData() {
        return vehicleData;
    }

    public void setVehicleData(VehicleData vehicleData) {
        this.vehicleData = vehicleData;
    }

    public ArrayList<Lap> getLapBreaker() {
        return lapBreaker;
    }

    public void setLapBreaker(ArrayList<Lap> lapBreaker) {
        this.lapBreaker = lapBreaker;
    }
    
    /**
     * returns the last time stamp recorded
     * @return 
     */
    public long getDataTimeLength() {
        return dataMap.getList("Time,RPM").getLast().time;
    }
    
    /**
     * TODO: Some notes for me to spitball and list
     * 
     * [] Datasets will need names.
     * 
     * [] ChartManager.getDataMap() needs to give the main dataset
     * 
     * [X] Okay so the ChartAssembly currently just uses a String[] of selectedTags and int[] of laps 
     * this won't work because we could have dataset1.RPMvsTime and dataset2.RPMvsTime, just holding RPMvsTime is ambiguous.
     * This list needs to become references to the actual LinkedList of logobjects.
     *      Thought: Here is a possible solution, although we will need to look further
     *      So, instead of the choose data dialog returning the selected tags and laps by the user we return the formatted lists and save them as local lists in chartassembly.
     *      Now we have access to the data the user has selected for statistics, histogram, creating laps from it (because we should have time data as well), filter it (which will require a change)?, creating markers. Can't think of anything that will hold this idea back.
     * 
     * [X] All references to getting the title and then referencing the main dataset also need to be changed
     * 
     * [X] The get statistics need the lists of logobjects to get statistics from instead of what theyre doing now.
     * 
     * [] For the choose data dialog, each on click needs to append to a list of strings of chosen tags (which will also need to know which datasets theyre from)
     * The whole UI for choosing the data dialog needs to be rethought. What's the most intuitive way of showing multiple datasets to the user.
     * 
     * [X] Laps may need a reference to this list as well. Does each lap-set belong to a dataset? No, because then it may be weird to view two different datasets.
     *      Rethought: what are the downsides of laps belonging to datasets? There are almost never a case where the time for a lap follows over to a whole different dataset. Each dataset has a list of laps. 
     *      Implementation: Laps are held within dataset and used to calculate collections, but the chartassembly has no access to them. The Selection object will handle all conversion and manipulation of data.
     * 
     * [] So what happens when i delete a list?? Does it end up as nullpointers in the ChartAssemblies and Laps that hold these references??
     * It may need to propagate through and delete those lists and such.
     * 
     * 
     * [] Well also don't forget that you need to change save/open/open
     * 
     * [] for open > open as dataset or open as multiple windows
     * 
     * [] MathChannels need functionality to apply to one dataset or multiple. This will prob be easy, loop through all the selected datasets that the user wants to apply to. Auto select the main dataset.
     */ 
    
}
