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
    
    private final DataAnalyzer parent;
    //status of lapBreakerTool
    private int lapBreakerActive;
    //lap that will be created and applied to lapBreaker list
    private Lap newLap;
    
    //status of cut dataTool
    private long cutDataActive;
    
    //holds all the datasets in this application instance
    private LinkedList<Dataset> datasets;
    
    //holds all active charts
    ArrayList<ChartAssembly> charts;
    
    //holds if swapper is active
    int swapActive;
    //holds charts being swapped;
    ChartAssembly first, second;
    
    //dataset size change listener
    private ArrayList<SizeListener> listeners;
    
    
    public ChartManager(DataAnalyzer parent) {
        this.parent = parent;
        lapBreakerActive = -1;
        swapActive = -1;
        cutDataActive = -2;
        newLap = new Lap();
        charts = new ArrayList<>();
        first = null;
        second = null;
        listeners = new ArrayList<>();
        datasets = new LinkedList<>();
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
        chart.getOverlay().rangeMarkersActive = parent.rangeMarkersActive;
        parent.desktop.add(chart.chartFrame);
        charts.add(chart);
        return chart;
    }
    
    public void clearCharts() {
        while(!charts.isEmpty()) {
            ChartAssembly chart = charts.get(0);
            chart.chartFrame.dispose();
            charts.remove(0);
        }
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

    public ArrayList<Integer> getUsedLapNumbers() {
        ArrayList<Integer> usedLapNumbers = new ArrayList<>();
        for(Dataset dataset : datasets) {
            for(Lap l : dataset.getLapBreaker()) {
                if(!usedLapNumbers.contains(l.getLapNumber())) {
                    usedLapNumbers.add(l.getLapNumber());
                }
            }
        }
        
        return usedLapNumbers;
        
    }
    
    public void addDatasetSizeChangeListener(SizeListener sizeListener) {
        listeners.add(sizeListener);
    }
    
    
    public void addDataset(Dataset d) throws DuplicateDatasetNameException {
        for(Dataset dataset : getDatasets()) {
            if(dataset.getName().equals(d.getName())) {
                throw new DuplicateDatasetNameException(d.getName());
            }
        }
        datasets.add(d);
        //on new element entry of dataMap, update the view
        d.getDataMap().addTagSizeChangeListener(new SizeListener() {
            @Override
            public void sizeUpdate() {
                if(!parent.isOpeningAFile()) {
                    Lap.applyToDataset(d.getDataMap(), d.getLapBreaker());

                }
            }
        });
        for(SizeListener l : listeners)
            l.sizeUpdate();
    }
    
    public void removeDataset(String name) {
        //search for dataset by name and remove it
        for(int i = 0; i < datasets.size(); i++) {
            if(datasets.get(i).getName().equals(name)) {
                datasets.remove(i);
                for(SizeListener l : listeners)
                    l.sizeUpdate();
            }
        }
    }
    
    public Dataset getDataset(String name) {
        for(Dataset dataset : datasets) {
            if(dataset.getName().equals(name))
                return dataset;
        }
        
        return null;
    }
    
    public void updateDataset(String name, Dataset updated) {
        for(int i = 0; i < datasets.size(); i++) {
            if(datasets.get(i).getName().equals(name)) {
                datasets.set(i, updated);
                for(SizeListener l : listeners)
                    l.sizeUpdate();
            }
        }
    }
    
    public void triggerChartDomainUpdate() {
        for (ChartAssembly ca : charts) {
            ca.selection.modeUpdate((DomainMode) parent.appParameters.get("domainMode"));
            ca.setChart(ca.selection.getUniqueTags().toArray(new String[ca.selection.getUniqueTags().size()]));
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
    
    public Dataset getMainDataset() {
        return datasets.getFirst();
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

    public DataAnalyzer getParentFrame() {
        return parent;
    }

    public ArrayList<ChartAssembly> getCharts() {
        return charts;
    }
    
    public int getNumberOfCharts() {
        return charts.size();
    }

    public LinkedList<Dataset> getDatasets() {
        return datasets;
    }

    public void setDatasets(LinkedList<Dataset> datasets) {
        this.datasets = datasets;
    }
    
    public long getCutDataActive() {
        return cutDataActive;
    }

    public void setCutDataActive(long cutDataActive) {
        this.cutDataActive = cutDataActive;
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
