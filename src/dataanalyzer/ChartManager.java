/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer;

import com.arib.categoricalhashtable.CategoricalHashTable;
import com.arib.toast.Toast;
import dataanalyzer.dialog.MessageBox;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import org.jfree.chart.*;

/**
 *
 * @author aribdhuka
 */
public class ChartManager {
    
    private final JFrame parent;
    private ArrayList<GPSGraphInternalFrame> trackMaps;
    // Stores the data set for each data type ( RPM vs Time, Distance vs Time....)
    private CategoricalHashMap dataMap;
    
    //stores the static markers
    private CategoricalHashTable<CategorizedValueMarker> staticMarkers;
    
    //holds vehicle parameters
    private VehicleData vehicleData;
    
    //stores the laps breaker
    private ArrayList<Lap> lapBreaker;
    //status of lapBreakerTool
    private int lapBreakerActive;
    //lap that will be created and applied to lapBreaker list
    private Lap newLap;
    
    //holds all active charts
    ArrayList<ChartAssembly> charts;
    
    //holds if swapper is active
    int swapActive;
    //holds charts being swapped;
    ChartAssembly first, second;
    
    
    public ChartManager(JFrame parent, ArrayList<GPSGraphInternalFrame> trackMaps) {
        this.parent = parent;
        this.trackMaps = trackMaps;
        dataMap = new CategoricalHashMap();
        staticMarkers = new CategoricalHashTable<>();
        vehicleData = new VehicleData();
        lapBreaker = new ArrayList<>();
        lapBreakerActive = -1;
        swapActive = -1;
        newLap = new Lap();
        charts = new ArrayList<>();
        first = null;
        second = null;
        
    }
    
    public void updateChartZooms(ChartPanel chartPanel) {
        chartPanel.getZoomInFactor();
    }
    
    public void updateOverlays(double xCor, ChartAssembly requestFrom) {
        for(ChartAssembly chart : charts) {
            if(chart == requestFrom)
                continue;
            chart.updateOverlay(xCor);
        }
    }
    
    //adds a new chart
    public ChartAssembly addChart() {
        ChartAssembly chart = new ChartAssembly(this);
        chart.getOverlay().rangeMarkersActive = ((DataAnalyzer) parent).rangeMarkersActive;
        parent.add(chart.chartFrame);
        charts.add(chart);
        return chart;
    }
    
    /**
     * Provides ability to move charts around the window without dragging.
     * Trades two charts by swapping the charts position and size.
     * @param first
     * @param second 
     */
    protected void swapCharts(ChartAssembly first, ChartAssembly second) {
        //check to make sure they aren't the same first
        if(first == second)
            return;
        //store location and size so that we don't lose the data
        Dimension tempSize = first.getChartFrame().getSize();
        Point tempLocation = first.getChartFrame().getLocation();
        //set first's attributes to seconds
        first.getChartFrame().setSize(second.getChartFrame().getSize());
        first.getChartFrame().setLocation(second.getChartFrame().getLocation());
        //set second's attributes to first's old attributes
        second.getChartFrame().setSize(tempSize);
        second.getChartFrame().setLocation(tempLocation);
    }
    
    //called when a chart is clicked
    protected void chartClicked(ChartAssembly ca) {
        //if first chart is still to be defined
        if(swapActive == 0) {
            //store clicked chart
            first = ca;
            
            //notify click
            Toast.makeToast(ca.getChartFrame(), "First chart chosen!", Toast.DURATION_SHORT);
            
            //move to next swap step
            swapActive++;
        }
        //else if second chart still needs to be defined
        else if (swapActive == 1) {
            //store second chart
            second = ca;
            
            //swap charts
            swapCharts(first, second);
            
            //reset swapper
            swapActive = -1;
            first = null;
            second = null;
        }
    }
    
    
    
    
    /**
     * 
     * 
     * 
     *  GETTERS AND SETTERS
     * 
     * 
     * 
     */
    
    public CategoricalHashMap getDataMap() {
        return dataMap;
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

    public int getLapBreakerActive() {
        return lapBreakerActive;
    }

    public void setLapBreakerActive(int lapBreakerActive) {
        this.lapBreakerActive = lapBreakerActive;
    }

    public Lap getNewLap() {
        return newLap;
    }

    public void setNewLap(Lap newLap) {
        this.newLap = newLap;
    }

    public JFrame getParentFrame() {
        return parent;
    }
    
    public ArrayList<GPSGraphInternalFrame> getTrackMaps() {
        return trackMaps;
    }
    
    public void setTrackMaps(ArrayList<GPSGraphInternalFrame> trackMaps) {
        this.trackMaps = trackMaps;
    }

    public ArrayList<ChartAssembly> getCharts() {
        return charts;
    }
    
    public int getNumberOfCharts() {
        return charts.size();
    }
    
    //enables or disables the swapper
    public void toggleSwapper() {
        //if swapper is disabled
        if(swapActive == -1) {
            //enable it
            swapActive = 0;
            
            //show instructions
            new MessageBox(this.parent, "Click two charts.\nBehold the magic.", false).setVisible(true);
        } else {
            //disable it
            swapActive = -1;
            //clear chosen charts
            first = null;
            second = null;
        }
    }
    
}
