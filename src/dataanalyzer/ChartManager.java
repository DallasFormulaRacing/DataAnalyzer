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
import java.util.LinkedList;
import javax.swing.JFrame;
import org.jfree.chart.*;

/**
 *
 * @author aribdhuka
 */
public class ChartManager {
    
    private final JFrame parent;
    
    //status of lapBreakerTool
    private int lapBreakerActive;
    //lap that will be created and applied to lapBreaker list
    private Lap newLap;
    
    //holds all the datasets in this application instance
    LinkedList<Dataset> datasets;
    
    //holds all active charts
    ArrayList<ChartAssembly> charts;
    
    //holds if swapper is active
    int swapActive;
    //holds charts being swapped;
    ChartAssembly first, second;
    
    
    public ChartManager(JFrame parent) {
        this.parent = parent;
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
        return datasets.getFirst().getDataMap();
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
