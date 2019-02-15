/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer;

import com.arib.categoricalhashtable.*;
import com.arib.toast.Toast;
import com.sun.glass.events.KeyEvent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;

/**
 *
 * @author aribdhuka
 */
public class DataAnalyzer extends javax.swing.JFrame implements ChartMouseListener {

    // Chartpanel object exists so that it can be accessed in the chartmouselistener methods.
    ChartPanel chartPanel;

    // X and Y crosshairs
    Crosshair xCrosshair;
    Crosshair yCrosshair;
    // X and Y vals
    public double xCor = 0;
    public double yCor = 0;
    //Crosshair
    CrosshairOverlay overlay;

    // Stores the data set for each data type ( RPM vs Time, Distance vs Time....)
    CategoricalHashMap dataMap;
    
    //Stores all the static markers the user has created
    CategoricalHashTable<CategorizedValueMarker> staticMarkers;
    
    //Stores the vehicle data
    VehicleData vehicleData;
    
    //Stores the array of String in the listview of tags
    String[] titles;
    
    //holds the currently selected laps
    int[] selectedLaps;
    
    //Stores the current filepath
    private String openedFilePath;
    
    //stores the laps breaker
    private ArrayList<Lap> lapBreaker;
    //status of lapBreakerTool
    int lapBreakerActive;
    //lap that will be created and applied to lapBreaker list
    Lap newLap;
    
    //holds if file operations are currently ongoing
    private boolean openingAFile;
    
    //String array that populates the categories list
    //TODO: add tags to each list element.
    AnalysisCategory[] analysisCategories = new AnalysisCategory[] { 
        new AnalysisCategory("Brakes").addTag("Time,BrakePressureFront").addTag("Time,BrakePressureRear").addTag("Time,AccelX").addTag("Time,AccelY").addTag("Time,AccelZ"),
        new AnalysisCategory("Brake Balance").addTag("Time,BrakePressureFront").addTag("Time,BrakePressureRear"),
        new AnalysisCategory("Coolant").addTag("Time,Coolant").addTag("Time,RadiatorInlet"), 
        new AnalysisCategory("Acceleration").addTag("Time,AccelX").addTag("Time,AccelY").addTag("Time,AccelZ").addTag("Time,RPM").addTag("Time,WheelspeedFront").addTag("Time,WheelspeedRear"),
        new AnalysisCategory("Endurance"), 
        new AnalysisCategory("Skidpad")};

    public DataAnalyzer() {
        initComponents();
        
        this.setTitle("DataAnalyzer");
        
        //set window listener
        DataAnalyzer curr = this;
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                int promptResult = JOptionPane.showConfirmDialog(curr, 
                    "Would you like to save before closing this window?", "Save Before Close?", 
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
                
                switch(promptResult) {
                    case JOptionPane.YES_OPTION : saveFile(openedFilePath); curr.dispose(); break;
                    case JOptionPane.NO_OPTION : curr.dispose(); break;
                    case JOptionPane.CANCEL_OPTION : break;
                }
            }
        });
        
        //start with not currently opening a file
        openingAFile = false;
        
        //disable the layout manager which essentially makes the frame an absolute positioning frame
        this.setLayout(null);
        
        // Create a new hash map
        dataMap = new CategoricalHashMap();
        
        //create a new instance of the vehicle data
        vehicleData = new VehicleData();
        
        //start with empty lap data
        lapBreaker = new ArrayList<>();
        //start with the lapBreaker disables
        lapBreakerActive = -1;
        //initialize new Lap
        newLap = new Lap();
        
        //start with no laps selected
        selectedLaps = null;

        //on new element entry of dataMap, update the view
        dataMap.addTagSizeChangeListener(new HashMapTagSizeListener() {
            @Override
            public void sizeUpdate() {
                fillDataList(dataMap.tags);
                if(!openingAFile)
                    Lap.applyToDataset(dataMap, lapBreaker);
            }
        });

        //init the arraylist of static markers
        staticMarkers = new CategoricalHashTable<>();
        
        // Init the graph with some dummy data until there is data given to read
        showEmptyGraph();
        
        //extend the statistics panel to the edge
        //get the screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        //get xCordinate of the statisticsPanel
        statisticsPanel.setSize(screenSize.width - statisticsPanel.getX(), statisticsPanel.getHeight());
        
        // Create the global object crosshairs
        overlay = new CrosshairOverlay();
        this.xCrosshair = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
        this.xCrosshair.setLabelVisible(true);
        overlay.addDomainCrosshair(xCrosshair);
        chartPanel.addOverlay(overlay);
        
        //init the array
        titles = new String[10];
        
        //set the opened file path to empty string to prevent null pointer exceptions
        openedFilePath = "";
        
        //populate category list 
        String[] categoryListData = new String[analysisCategories.length];
        for(int i = 0; i < analysisCategories.length; i++) {
            categoryListData[i] = analysisCategories[i].getTitle();
        }
        categoryList.setListData(categoryListData);
    }

    private void showEmptyGraph() {
        final XYSeriesCollection data = new XYSeriesCollection();

        // Add values of (Age, Happiness)
        final XYSeries series = new XYSeries("Me");
        series.add(0, 70);
        series.add(5, 80);
        series.add(10, 60);
        series.add(16, 50);
        series.add(18, 40);
        series.add(20, 20);
        series.add(22, 5);
        series.add(25, 1);
        series.add(30, 0.1);
        data.addSeries(series);

        // Create a JFreeChart from the Factory, given parameters (Chart Title, Domain name, Range name, series collection, PlotOrientation, show legend, show tooltips, show url)
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Happiness vs Age",
                "Age",
                "Happiness",
                data,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // Instantiate chart panel object from the object created from ChartFactory
        chartPanel = new ChartPanel(chart);
        // Set the size of the panel
        chartPanel.setSize(new java.awt.Dimension(800, 600));

        // Mouse listener
        chartPanel.addChartMouseListener(this);

        // The form has a subframe inside the mainframe
        // Set the subframe's content to be the chartpanel
        chartFrame.setContentPane(chartPanel);
    }
    
    private void showHistogram() {
        String title = chartPanel.getChart().getTitle().getText();
        String[] titleSplit = title.split(" vs ");
        String[] tags = new String[titleSplit.length - 1];
        for (int i = 0; i < titleSplit.length - 1; i++) {
            tags[i] = titleSplit[titleSplit.length - 1] + "," + titleSplit[i];
        }
        //update laps
        XYSeriesCollection data = getHistogramDataCollection(tags, lapList.getSelectedIndices());
        
        // Gets the independent variable from the title of the data
        String yAxis = "Milliseconds";
        // Gets the dependent variable from the title of the data
        String xAxis = title.split(" vs ")[0];  //split title by vs, we get ["RPM", "Time"] or something like that
        
        //create histogram
        JFreeChart chart = ChartFactory.createHistogram(
                title,
                xAxis,
                yAxis,
                data,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        
        
        //apply histogram to chart panel
        chartPanel = new ChartPanel(chart);
        
        //set size
        chartPanel.setSize(new java.awt.Dimension(800, 600));
        
        //set frame content
        chartFrame.setContentPane(chartPanel);
        
        //update statistics panel
        updateStatistics(tags);
        
    }

    // Displays the data for all selected data types
    private void setChart(String[] tags, int[] laps) {

        // Gets the specific data based on what kind of data we want to show for which 
        XYSeriesCollection[] seriesCollection;
        if(laps == null || laps.length == 0) {
            seriesCollection = new XYSeriesCollection[tags.length];
        } else {
            seriesCollection = new XYSeriesCollection[tags.length * laps.length];
        }
        
        String title = "";
        
        // Store data for each data type in different XYSeriesCollection
        // New title for all the Y-Axis labels added together
        for(int i = 0; i < tags.length; i++){
            seriesCollection[i] = getDataCollection(tags[i], laps);
            title += tags[i].split(",")[1] + " vs ";
        }
        
        //add domain
        title += tags[0].split(",")[0];
        
        // Use XYPlot in JFreeChart to draw the data
        XYPlot plot = new XYPlot();
        for(int i = 0; i < tags.length; i++){
            // Assign each dataset at some index
            plot.setDataset(i, seriesCollection[i]);
            // Label the Y-Axis for specific data set
            plot.setRangeAxis(i, new NumberAxis(tags[i].split(",")[1]));
            // Give the line a different color
            plot.setRenderer(i, getNewRenderer(i));
            // Makes sure that the Y-values for each dataset are proportional to the data inside it
            plot.mapDatasetToRangeAxis(i, i);
        }
        // Just show the X-Axis data type (Time, Distance, etc)
        plot.setDomainAxis(new NumberAxis(tags[0].split(",")[0]));

        // Create a new JFreeChart with the XYPlot
        JFreeChart chart = new JFreeChart(title, getFont(), plot, true);
        chart.setBackgroundPaint(Color.WHITE);
        
        // Instantiate chart panel object from the object created from ChartFactory
        chartPanel = new ChartPanel(chart);
        chartPanel.addOverlay(overlay);
        // Set the size of the panel
        chartPanel.setSize(new java.awt.Dimension(800, 600));

        // Mouse listener
        chartPanel.addChartMouseListener(this);

        // The form has a subframe inside the mainframe
        // Set the subframe's content to be the chartpanel
        chartFrame.setContentPane(chartPanel);
        
        //update statistics
        updateStatistics(titleToTag());
        
        //draw markers
        drawMarkers(tags, chart.getXYPlot());
    }

    // Creates a new render for a new series or data type. Gives a new color
    private XYSplineRenderer getNewRenderer(int index){
        Random r = new Random();
        int rand = r.nextInt(5);
        XYSplineRenderer sx = new XYSplineRenderer();
        
        switch (rand){
            case 0:
                sx.setSeriesFillPaint(index, Color.BLUE);
                break;
            case 1:
                sx.setSeriesFillPaint(index, Color.GREEN);
                break;
            case 2:
                sx.setSeriesFillPaint(index, Color.YELLOW);
                break;
            case 3:
                sx.setSeriesFillPaint(index, Color.BLACK);
                break;
            case 4:
                sx.setSeriesFillPaint(index, Color.ORANGE);
                break;
        }        
        return sx;
    }
    
    private XYSeriesCollection getDataCollection(String tag, int[] laps) {

        // XY Series Collection allows there to be multiple data lines on the graph
        XYSeriesCollection graphData = new XYSeriesCollection();

        //if laps were not provided show whole dataset
        if(laps == null || laps.length == 0) {
            // Get the list of data elements based on the tag
            LinkedList<LogObject> data = dataMap.getList(tag);

            // Declare the series to add the data elements to
            final XYSeries series = new XYSeries(tag.split(",")[1]);

            //if tag contains time then its not a function of another dataset
            if(tag.contains("Time,")) {
                // We could make a XYSeries Array if we wanted to show different lap data
                // final XYSeries[] series = new XYSeries[laps.length];  <--- if we wanted to show different laps at the same time
                // Iterate through each data element in the received dataMap LinkedList
                for (LogObject d : data) {
                    //Get the x and y values by seprating them by the comma
                    String[] values = d.toString().split(",");
                    //Add the x and y value to the series
                    series.add(Long.parseLong(values[0]), Double.parseDouble(values[1]));
                }
            } else {
                // We could make a XYSeries Array if we wanted to show different lap data
                // final XYSeries[] series = new XYSeries[laps.length];  <--- if we wanted to show different laps at the same time
                // Iterate through each data element in the received dataMap LinkedList
                for (LogObject d : data) {
                    //Get the x and y values by seprating them by the comma
                    String[] values = d.toString().split(",");
                    //Add the x and y value to the series
                    series.add(Double.parseDouble(values[0]), Double.parseDouble(values[1]));
                }
            }
            //add series to collection
            graphData.addSeries(series);
        } else { //if lap data was provided
            //for each lap
            for(int i = 0; i < laps.length; i++) {
                Lap currLap = new Lap(0, 0);
                for(Lap lap : lapBreaker) {
                    if(lap.lapNumber == laps[i]) {
                        currLap = lap;
                    }
                }
                //create a series with the tag and lap #
                XYSeries series = new XYSeries(tag.split(",")[1] + "Lap " + laps[i]);
                //if its a base dataset
                if(tag.contains("Time,")) {
                    //for each log object if the log object belongs in this lap add it to the series
                    for(LogObject lo : dataMap.getList(tag)) {
                        if(lo.getLaps().contains(laps[i])) {
                            //Get the x and y values by seprating them by the comma
                            String[] values = lo.toString().split(",");
                            //Add the x and y value to the series
                            series.add(Long.parseLong(values[0]) - currLap.start, Double.parseDouble(values[1]));
                        }
                    }
                //else its function of another dataset
                } else {
                    //for each log object if the log object belongs in this lap, add it to the series
                    for(LogObject lo : dataMap.getList(tag)) {
                        if(lo.getLaps().contains(laps[i])) {
                            //Get the x and y values by seprating them by the comma
                            String[] values = lo.toString().split(",");
                            //Add the x and y value to the series
                            series.add(Double.parseDouble(values[0]) - currLap.start, Double.parseDouble(values[1]));
                        }
                    }
                }
                
                //add to collection
                graphData.addSeries(series);
            }
        }


        // Return the XYCollection
        return graphData;
    }
    
    private XYSeriesCollection getHistogramDataCollection(String[] tags, int[] laps) {
        //collection to return
        final XYSeriesCollection graphData = new XYSeriesCollection();
        
        for(String tag : tags) {
        //get data from dataset
            LinkedList<LogObject> data = dataMap.getList(tag);
            
            for(int l = 0; l < laps.length; l++) {
                //series that will hold the data
                XYSeries series = new XYSeries(tag.split(",")[1] + "Laps " + laps[l]);
                //calculate min and max value of the data 
                double min = Double.MAX_VALUE;
                double max = Double.MIN_VALUE;
                for(LogObject lo : data) {
                    if(lo.getLaps().contains(laps[l])) {
                        if(lo instanceof SimpleLogObject) {
                            if(((SimpleLogObject) lo).getValue() > max)
                                max = ((SimpleLogObject) lo).getValue();
                            if(((SimpleLogObject) lo).getValue() < min)
                                min = ((SimpleLogObject) lo).getValue();
                        }
                    }
                }

                //get the intervals to work with
                double interval = max - min;
                interval /= 50;

                //holds how many instances occured within this interval
                int counter;

                //for each of the 50 intervals
                for(int i = 1; i < 51; i++) {
                    //start with 0 count
                    counter = 0;

                    //for each data element
                    for(LogObject lo : data) {
                        if(lo.getLaps().contains(laps[l])) {
                            //if its a simple log object its value can be obtained
                            if(lo instanceof SimpleLogObject) {
                                //if the value of the current object is between the interval we are searching for
                                if(((SimpleLogObject) lo).getValue() < ((interval * i) + min) && ((SimpleLogObject) lo).getValue() > ((interval * (i-1)) + min)) {
                                    //increment the counter
                                    counter++;
                                }
                            } else if(lo instanceof FunctionOfLogObject) {
                                if(((FunctionOfLogObject) lo).getValue() < ((interval * i) + min) && ((FunctionOfLogObject) lo).getValue() > ((interval * (i-1)) + min)) {
                                    //increment the counter
                                    counter++;
                                }
                            }
                        }
                    }
                    //if the counter is not 0, add the median of the interval we are looking for along with the counter to the series.
                    if(counter != 0)
                        series.add((((interval * i) + min) + ((interval * i - 1) + min))/2, counter*50);
                }



                graphData.addSeries(series);
            }
        }
        return graphData;
        
    }

    // When the chart is clicked
    @Override
    public void chartMouseClicked(ChartMouseEvent cme) {
        //if the lap breaker hasn't been activiates
        if(lapBreakerActive < 0) {
            // Create a static cursor that isnt cleared every time
            ValueMarker marker = new ValueMarker(xCor);
            //calculate the tag
            String title = cme.getChart().getTitle().getText();
            //create array of tags
            String[] titleSplit = title.split(" vs ");
            String[] tags = new String[titleSplit.length - 1];
            for (int i = 0; i < titleSplit.length - 1; i++) {
                tags[i] = titleSplit[titleSplit.length - 1] + "," + titleSplit[i];
            }

            for(String tag : tags) {
                //add to the list of static markers
                if(staticMarkers.get(new CategorizedValueMarker(tag, marker)) == null)
                    staticMarkers.put(new CategorizedValueMarker(tag, marker));
            }

            //draw markers
            drawMarkers(titleToTag(), chartPanel.getChart().getXYPlot());
        } else {
            //if lapbreaker has just started
            if(lapBreakerActive == 0) {
                //get clicked position and set it as start
                newLap.start = getRoundedTime(xCor);
                //move to next task
                lapBreakerActive++;
                
                ValueMarker startMarker = new ValueMarker(newLap.start);
                for(String tag : dataMap.tags) {
                    if(staticMarkers.get(new CategorizedValueMarker(tag, startMarker, "Start Lap" + newLap.lapNumber)) == null)
                        staticMarkers.put(new CategorizedValueMarker(tag, startMarker, "Start Lap" + newLap.lapNumber));
                }
                
                //draw markers
                drawMarkers(titleToTag(), chartPanel.getChart().getXYPlot());
            //if the start has already been defined
            } else if(lapBreakerActive == 1) {
                //define the next click as a stop
                newLap.stop = getRoundedTime(xCor);
                
                //hold the laps start and stop, so we have the value in case its lost
                long oldStartTime = newLap.start;
                long oldStopTime = newLap.stop;
                
                ValueMarker stopMarker = new ValueMarker(newLap.stop);

                //apply marker to all datasets.
                for(String tag : dataMap.tags) {
                    //add to the list of static markers
                    if(staticMarkers.get(new CategorizedValueMarker(tag, stopMarker, "End Lap" + newLap.lapNumber)) == null)
                        staticMarkers.put(new CategorizedValueMarker(tag, stopMarker, "End Lap" + newLap.lapNumber));
                }
                
                //draw markers
                drawMarkers(titleToTag(), chartPanel.getChart().getXYPlot());
                
                //get the used lap numbers
                ArrayList<Integer> usedLaps = new ArrayList<>();
                for(Lap l : lapBreaker) {
                    usedLaps.add(l.lapNumber);
                }
                //create and run the dialog
                LapDataDialog ldd = new LapDataDialog(this, true, newLap, usedLaps);
                ldd.setVisible(true);
                //while the dialog is running
                while(ldd.isRunning()) {
                    try {
                        Thread.currentThread().wait(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(DataAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                if(!newLap.lapLabel.equals("!#@$LAPCANCELLED")) {
                    //remove old start and stop markers and add new ones with values from text box
                    ValueMarker startMarker = new ValueMarker(newLap.start);
                    stopMarker = new ValueMarker(newLap.stop);

                    for(String tag : dataMap.tags) {
                        staticMarkers.remove(getMarkerFromDomainValue(tag, oldStartTime));
                        staticMarkers.remove(getMarkerFromDomainValue(tag, oldStopTime));
                        if(staticMarkers.get(new CategorizedValueMarker(tag, startMarker, "Start Lap" + newLap.lapNumber)) == null)
                            staticMarkers.put(new CategorizedValueMarker(tag, startMarker, "Start Lap" + newLap.lapNumber));
                        if(staticMarkers.get(new CategorizedValueMarker(tag, stopMarker, "End Lap" + newLap.lapNumber)) == null)
                            staticMarkers.put(new CategorizedValueMarker(tag, stopMarker, "End Lap" + newLap.lapNumber));
                    }
                    //add that to the list of laps
                    lapBreaker.add(newLap);
                    //apply the lap data to the datasets
                    Lap.applyToDataset(dataMap, lapBreaker);
                    //reset the lapbreaker
                    lapBreakerActive = -1;
                    
                    //reset the new lap
                    newLap = new Lap();
                    
                    //fill lap list
                    fillDataList(dataMap.tags);
                } else {
                    //delete previous markers
                    for(String tag : dataMap.tags) {
                        staticMarkers.remove(getMarkerFromDomainValue(tag, oldStartTime));
                        staticMarkers.remove(getMarkerFromDomainValue(tag, oldStopTime));
                    }
                    drawMarkers(titleToTag(), chartPanel.getChart().getXYPlot());
                }
            }
        }
    }

    //when the mouse moves over the chart
    @Override
    public void chartMouseMoved(ChartMouseEvent cme) {
        // The data area of where the chart is.
        Rectangle2D dataArea = this.chartPanel.getScreenDataArea();
        // Get the chart from the chart mouse event
        JFreeChart chart = cme.getChart();
        // Get the xy plot object from the chart
        XYPlot plot = (XYPlot) chart.getPlot();
        // Clear all markers
        // This will be a problem for static markers we want to create
        // Get the xAxis
        ValueAxis xAxis = plot.getDomainAxis();
        // Get the xCordinate from the xPositon of the mouse
        xCor = xAxis.java2DToValue(cme.getTrigger().getX(), dataArea,
                RectangleEdge.BOTTOM);
        // Find the y cordinate from the plots data set given a x cordinate
        yCor = DatasetUtilities.findYValue(plot.getDataset(), 0, xCor);

        String[] titles = titleToTag();
        int index = 0;
        // String object that holds values for all the series on the plot.
        String yCordss = "";
        // Repeat the loop for each series in the plot
        for (int i = 0; i < plot.getDatasetCount(); i++) {
            //get current data set
            XYSeriesCollection col = (XYSeriesCollection) plot.getDataset(i);
            for(int j = 0; j < plot.getSeriesCount(); j++) {
                // Get the y value for the current series.
                double val = DatasetUtilities.findYValue(col, j, xCor);
                // Add the value to the string
                yCordss += (titles[index].substring(titles[index].indexOf(',')+1)) + ":" + String.format("%.2f", val) + ", ";
            }
            index++;
        }
        
        yCordss = yCordss.substring(0, yCordss.length() - 2);

        // Set the textviews at the bottom of the file.
        xCordLabel.setText(String.format("%.2f", xCor));
        yCordLabel.setText(yCordss);

        // Set this objects crosshair data to the value we have
        this.xCrosshair.setValue(xCor);
    }
    
    private void drawMarkers(String[] tags, XYPlot plot) {
        ArrayList<String> lapMarkers = new ArrayList<>();
        ArrayList<String> markerList = new ArrayList<>();
        plot.clearDomainMarkers();
        //which dataset we are on
        int count = 0;
        for(String tag : tags) {
            //get the linked list from tag
            LinkedList<CategorizedValueMarker> markers = staticMarkers.getList(tag);
            //position var
            int k = 0;
            //if the linked list is not null
            if(markers != null) {
                //draw every domain marker saved for this chart and add it to an array
                for(CategorizedValueMarker v : markers) {
                    v.getMarker().setPaint(getColorFromIndex(count));
                    plot.addDomainMarker(v.getMarker());
                    XYSeriesCollection col = (XYSeriesCollection) plot.getDataset(count);
                    //if its a lap marker, show all other markers first
                    if(v.getNotes().matches("Start Lap[0-9]+") || v.getNotes().matches("End Lap[0-9]+")) {
                        lapMarkers.add("(" + String.format("%.2f", markers.get(k).getMarker().getValue()) + ", " +
                            String.format("%.2f", DatasetUtilities.findYValue(col,0,markers.get(k).getMarker().getValue())) + 
                            ") " + v.getNotes());
                    } else {
                        markerList.add("(" + String.format("%.2f", markers.get(k).getMarker().getValue()) + ", " +
                            String.format("%.2f", DatasetUtilities.findYValue(col,0,markers.get(k).getMarker().getValue())) + 
                            ") " + v.getNotes());
                    }
                    k++;
                }

            }
            //move to next dataset
            count++;
        }
        
        if(markerList.isEmpty() && lapMarkers.isEmpty()) {
            staticMarkersList.setListData(new String[0]);
        } else {
            ArrayList<String> compile = new ArrayList<>();
            compile.addAll(markerList);
            compile.addAll(lapMarkers);
            staticMarkersList.setListData(compile.toArray(new String[compile.size()]));
        }
        
        
    }
 
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileChooser = new javax.swing.JFileChooser();
        chartFrame = new javax.swing.JInternalFrame();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        dataList = new javax.swing.JList<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        lapList = new javax.swing.JList<>();
        jScrollPane4 = new javax.swing.JScrollPane();
        staticMarkersList = new javax.swing.JList<>();
        searchField = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        categoryList = new javax.swing.JList<>();
        statisticsPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        xCordLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        yCordLabel = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        maxText = new javax.swing.JLabel();
        averageText = new javax.swing.JLabel();
        minText = new javax.swing.JLabel();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        newWindowMenuItem = new javax.swing.JMenuItem();
        newImportMenuItem = new javax.swing.JMenuItem();
        openBtn = new javax.swing.JMenuItem();
        saveMenuButton = new javax.swing.JMenuItem();
        saveAsMenuItem = new javax.swing.JMenuItem();
        exportMenuItem = new javax.swing.JMenuItem();
        closeMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        addMathChannelButton = new javax.swing.JMenuItem();
        addLapConditionMenuItem = new javax.swing.JMenuItem();
        viewMenu = new javax.swing.JMenu();
        histogramMenuItem = new javax.swing.JMenuItem();
        fullscreenMenuItem = new javax.swing.JMenuItem();
        vehicleMenu = new javax.swing.JMenu();
        newVehicleMenuItem = new javax.swing.JMenuItem();
        saveVehicleMenuItem = new javax.swing.JMenuItem();
        importVehicleMenuItem = new javax.swing.JMenuItem();
        editVehicleMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1100, 700));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        chartFrame.setPreferredSize(new java.awt.Dimension(899, 589));
        chartFrame.setVisible(true);

        javax.swing.GroupLayout chartFrameLayout = new javax.swing.GroupLayout(chartFrame.getContentPane());
        chartFrame.getContentPane().setLayout(chartFrameLayout);
        chartFrameLayout.setHorizontalGroup(
            chartFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 897, Short.MAX_VALUE)
        );
        chartFrameLayout.setVerticalGroup(
            chartFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 566, Short.MAX_VALUE)
        );

        getContentPane().add(chartFrame, new org.netbeans.lib.awtextra.AbsoluteConstraints(201, 6, -1, -1));

        jPanel1.setMaximumSize(new java.awt.Dimension(177, 32767));
        jPanel1.setPreferredSize(new java.awt.Dimension(177, 608));

        jLabel4.setText("Static Markers:");

        jScrollPane1.setMaximumSize(new java.awt.Dimension(0, 0));
        jScrollPane1.setMinimumSize(new java.awt.Dimension(0, 0));

        dataList.setSize(new java.awt.Dimension(177, 298));
        dataList.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                dataListKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(dataList);

        lapList.setSize(new java.awt.Dimension(177, 128));
        lapList.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                lapListKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(lapList);

        jScrollPane4.setPreferredSize(new java.awt.Dimension(43, 128));
        jScrollPane4.setSize(new java.awt.Dimension(43, 128));

        staticMarkersList.setSize(new java.awt.Dimension(177, 128));
        staticMarkersList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                staticMarkersListMouseClicked(evt);
            }
        });
        staticMarkersList.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                staticMarkersListKeyReleased(evt);
            }
        });
        jScrollPane4.setViewportView(staticMarkersList);

        searchField.setToolTipText("Search");
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchFieldKeyReleased(evt);
            }
        });

        jScrollPane3.setMaximumSize(new java.awt.Dimension(0, 0));
        jScrollPane3.setMinimumSize(new java.awt.Dimension(0, 0));

        categoryList.setSize(new java.awt.Dimension(177, 298));
        categoryList.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                categoryListKeyReleased(evt);
            }
        });
        jScrollPane3.setViewportView(categoryList);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(searchField)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addContainerGap(90, Short.MAX_VALUE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addGap(139, 139, 139))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                    .addContainerGap(506, Short.MAX_VALUE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 6, 189, 640));

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel1.setText("X Cord:");

        xCordLabel.setText("jLabel2");

        jLabel2.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel2.setText("Y Cord:");

        yCordLabel.setText("jLabel2");

        jLabel3.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel3.setText("Average:");

        jLabel5.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel5.setText("Max: ");

        jLabel6.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel6.setText("Min: ");

        maxText.setText("max");

        averageText.setText("acg");

        minText.setText("min");

        javax.swing.GroupLayout statisticsPanelLayout = new javax.swing.GroupLayout(statisticsPanel);
        statisticsPanel.setLayout(statisticsPanelLayout);
        statisticsPanelLayout.setHorizontalGroup(
            statisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statisticsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(statisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(statisticsPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(6, 6, 6)
                        .addComponent(xCordLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addGap(6, 6, 6)
                        .addComponent(yCordLabel))
                    .addGroup(statisticsPanelLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(averageText)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(maxText)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(minText)))
                .addGap(0, 544, Short.MAX_VALUE))
        );
        statisticsPanelLayout.setVerticalGroup(
            statisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statisticsPanelLayout.createSequentialGroup()
                .addGroup(statisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addGroup(statisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(averageText)
                        .addComponent(maxText)
                        .addComponent(jLabel6)
                        .addComponent(minText)))
                .addGap(6, 6, 6)
                .addGroup(statisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(yCordLabel)
                    .addComponent(jLabel1)
                    .addComponent(xCordLabel))
                .addGap(0, 12, Short.MAX_VALUE))
        );

        averageText.getAccessibleContext().setAccessibleName("");

        getContentPane().add(statisticsPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 600, 780, 50));

        fileMenu.setText("File");

        newWindowMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.CTRL_MASK));
        newWindowMenuItem.setText("New Window");
        newWindowMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newWindowMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(newWindowMenuItem);

        newImportMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        newImportMenuItem.setText("New Import");
        newImportMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newImportMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(newImportMenuItem);

        openBtn.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        openBtn.setText("Open");
        openBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openBtnClicked(evt);
            }
        });
        fileMenu.add(openBtn);

        saveMenuButton.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveMenuButton.setText("Save");
        saveMenuButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveMenuButtonClicked(evt);
            }
        });
        fileMenu.add(saveMenuButton);

        saveAsMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        saveAsMenuItem.setText("Save As");
        saveAsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveAsMenuItem);

        exportMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        exportMenuItem.setText("Export");
        exportMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exportMenuItem);

        closeMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        closeMenuItem.setText("Exit");
        closeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(closeMenuItem);

        menuBar.add(fileMenu);

        editMenu.setText("Edit");

        addMathChannelButton.setLabel("Add Math Channel");
        addMathChannelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addMathChannel(evt);
            }
        });
        editMenu.add(addMathChannelButton);

        addLapConditionMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        addLapConditionMenuItem.setText("Add Lap Condition");
        addLapConditionMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addLapConditionMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(addLapConditionMenuItem);

        menuBar.add(editMenu);

        viewMenu.setText("View");

        histogramMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.CTRL_MASK));
        histogramMenuItem.setText("Histogram");
        histogramMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                histogramMenuItemActionPerformed(evt);
            }
        });
        viewMenu.add(histogramMenuItem);

        fullscreenMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
        fullscreenMenuItem.setText("Fullscreen");
        fullscreenMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fullscreenMenuItemActionPerformed(evt);
            }
        });
        viewMenu.add(fullscreenMenuItem);

        menuBar.add(viewMenu);

        vehicleMenu.setText("Vehicle");

        newVehicleMenuItem.setText("New Vehicle");
        newVehicleMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newVehicleMenuItemActionPerformed(evt);
            }
        });
        vehicleMenu.add(newVehicleMenuItem);

        saveVehicleMenuItem.setText("Save Vehicle");
        saveVehicleMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveVehicleMenuItemActionPerformed(evt);
            }
        });
        vehicleMenu.add(saveVehicleMenuItem);

        importVehicleMenuItem.setText("Import Vehicle");
        importVehicleMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importVehicleMenuItemActionPerformed(evt);
            }
        });
        vehicleMenu.add(importVehicleMenuItem);

        editVehicleMenuItem.setText("Edit Vehicle");
        editVehicleMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editVehicleMenuItemActionPerformed(evt);
            }
        });
        vehicleMenu.add(editVehicleMenuItem);

        menuBar.add(vehicleMenu);

        setJMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void openBtnClicked(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openBtnClicked
        // Open a separate dialog to select a .csv file
        fileChooser = new JFileChooser() {

            // Override approveSelection method because we only want to approve
            //  the selection if its is a .csv file.
            @Override
            public void approveSelection() {
                File chosenFile = getSelectedFile();

                // Make sure that the chosen file exists
                if (chosenFile.exists()) {
                    // Get the file extension to make sure it is .csv
                    String filePath = chosenFile.getAbsolutePath();
                    int lastIndex = filePath.lastIndexOf(".");
                    String fileExtension = filePath.substring(lastIndex,
                            filePath.length());

                    // approve selection if it is a .csv file
                    if (fileExtension.equals(".dfr")) {
                        super.approveSelection();
                        setTitle("DataAnalyzer - " + filePath.substring(filePath.lastIndexOf('/')));
                    } else {
                        // display error message - that selection should not be approve
                        new MessageBox("Error: Selection could not be approved").setVisible(true);
                        this.cancelSelection();
                    }

                }
            }
        };

        // showOpenDialog returns the chosen option and if it as an approve
        fileChooser.setMultiSelectionEnabled(false);
        //  option then the file should be imported and opened
        int choice = fileChooser.showOpenDialog(null);
        if (choice == JFileChooser.APPROVE_OPTION) {
            String chosenFilePath = fileChooser.getSelectedFile().getAbsolutePath();
            openedFilePath = chosenFilePath;
            openFile(chosenFilePath);
        }
    }//GEN-LAST:event_openBtnClicked

    private void addMathChannel(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addMathChannel
        new MathChannelDialog(dataMap, vehicleData).setVisible(true);
    }//GEN-LAST:event_addMathChannel

    private void searchFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchFieldKeyReleased
        //if the key is alphabetic
        if(Character.isAlphabetic(evt.getKeyChar())) {
            //if the titles array is not null 
            if(titles != null) {
                //create array list of new titles that will hold all matches
                ArrayList<String> newTitles = new ArrayList<>();
                //for each array element of titles array
                for(String s : titles) {
                    //if the element contains the search box text
                    if(s.contains(searchField.getText())) {
                        //add it to the array list
                        newTitles.add(s);
                    }
                }

                //set the data list view to all the elements of the array list
                dataList.setListData(newTitles.toArray(new String[newTitles.size()]));
            }
        }
        
        //if the search field becomes empty, set the data list to the original list
        if(searchField.getText().isEmpty()) {
            dataList.setListData(titles);
        }
    }//GEN-LAST:event_searchFieldKeyReleased

    private void saveMenuButtonClicked(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMenuButtonClicked
        saveFile(openedFilePath);
    }//GEN-LAST:event_saveMenuButtonClicked

    private void fullscreenMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fullscreenMenuItemActionPerformed
        //get the dimensions of the screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        
        //if fullscreen
        if(this.getSize().width == screenSize.width && this.getSize().height == screenSize.height) {
            //set these sizes
            this.setSize(1100, 700);
            fullscreenMenuItem.setText("Fullscreen");
            chartFrame.setSize(new Dimension(899, 589));
            chartPanel.setSize(new java.awt.Dimension(899, 589));
            statisticsPanel.setSize(this.getWidth() - statisticsPanel.getX(), statisticsPanel.getHeight());
            int x = chartFrame.getX();
            int y = chartFrame.getY() + chartFrame.getHeight();
            statisticsPanel.setLocation(x, y);
        }
        //if we are not already full screen
        else {
            //set these sizes
            this.setSize(screenSize.width, screenSize.height);
            fullscreenMenuItem.setText("Minimize");
            chartPanel.setSize(new Dimension(screenSize.width - chartPanel.getX(), (screenSize.width - chartPanel.getX()) / 16 * 9));
            chartFrame.setSize(new Dimension(screenSize.width - chartFrame.getX(), (screenSize.width - chartFrame.getX()) / 16 * 9));
            statisticsPanel.setSize(screenSize.width - statisticsPanel.getX(), statisticsPanel.getHeight());
            int x = chartFrame.getX();
            int y = chartFrame.getY() + chartFrame.getHeight();
            statisticsPanel.setLocation(x, y);
            chartFrame.setContentPane(chartPanel);
        }
    }//GEN-LAST:event_fullscreenMenuItemActionPerformed

    private void dataListKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dataListKeyReleased
        //if the dataList has focus and a key is pressed
        //get the key code
        int code = evt.getKeyCode();
        //if the data list has an index that is selected
        if(dataList.getSelectedIndex() > -1) {
            //depending on the code
            switch(code) {
                //if its backspace, remove the item from the datamap
                case KeyEvent.VK_DELETE :
                case KeyEvent.VK_BACKSPACE : dataMap.remove(titleToTag(dataList.getSelectedValue())[0]); break;
            }
        }
    }//GEN-LAST:event_dataListKeyReleased

    private void newImportMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newImportMenuItemActionPerformed
        // Open a separate dialog to select a .csv file
        fileChooser = new JFileChooser() {

            // Override approveSelection method because we only want to approve
            //  the selection if its is a .csv file.
            @Override
            public void approveSelection() {
//                File chosenFile = getSelectedFile();
                File[] chosenFiles = getSelectedFiles();
                boolean toApprove = true;
                for(File chosenFile : chosenFiles) {
                    // Make sure that the chosen file exists
                    if (chosenFile.exists()) {
                        // Get the file extension to make sure it is .csv
                        String filePath = chosenFile.getAbsolutePath();
                        int lastIndex = filePath.lastIndexOf(".");
                        String fileExtension = filePath.substring(lastIndex,
                                filePath.length());

                        // approve selection if it is a .csv file
                        if (fileExtension.equals(".csv") || fileExtension.equals(".txt")) {
//                            setTitle("DataAnalyzer - " + filePath.substring(filePath.lastIndexOf('/')));
//                            super.approveSelection();
                        } else {
                            toApprove = false;
                            // display error message - that selection should not be approved
                            new MessageBox("Error: Wrong File Type").setVisible(true);
                            this.cancelSelection();
                        }

                    }
                }
                
                if(toApprove) {
                    if(chosenFiles.length > 0) {
                        setTitle("DataAnalyzer - " + fileChooser.getSelectedFiles()[0]
                                .getAbsolutePath().substring(fileChooser.getSelectedFiles()[0].getAbsolutePath().lastIndexOf('/')));
                        super.approveSelection();
                    }
                }
            }
        };
        
        //see if approved
        fileChooser.setMultiSelectionEnabled(true);
        int choice = fileChooser.showOpenDialog(null);
        //if approved
        if (choice == JFileChooser.APPROVE_OPTION) {
            //ask the user to import a vehicle. if any but cancel pressed continue
            boolean shouldContinue = askForVehicle();
            if(shouldContinue) {
                //get array of chosenFiles
                File[] chosenFiles = fileChooser.getSelectedFiles();
                //should we create a new window?
                boolean toCreateNewWindow = false;
                //holds new window number opened
                int windowCount = 0;
                //for each file
                for(File chosenFile : chosenFiles) {
                    //if we need to create a new window
                    if(toCreateNewWindow) {
                        //new window object
                        DataAnalyzer da = new DataAnalyzer();
                        //set vehicle data
                        da.vehicleData = this.vehicleData; //May need to clone
                        //get file path
                        String chosenFilePath = chosenFile.getAbsolutePath();
                        //set the file path for that object
                        da.openedFilePath = chosenFilePath;
                        //get index of the last .
                        int lastIndex = openedFilePath.lastIndexOf(".");
                        //get file extension
                        String fileExtension = openedFilePath.substring(lastIndex, openedFilePath.length());
                        //if its a csv
                        if(fileExtension.equals(".csv")) {
                            //make the new window import a CSV
                            da.importCSV(chosenFilePath);
                        //else if its a TXT make the new window import a CSV
                        } else if (fileExtension.equals(".txt")) {
                            da.importTXT(chosenFilePath);
                        }
                        da.applyPostProcessing();
                        da.setVisible(true);
                        da.setTitle("DataAnalyzer - " + chosenFilePath.substring(chosenFilePath.lastIndexOf('/')));
                        da.setLocation(100*windowCount, 100*windowCount);
                    //if we are not to create a new window
                    } else {
                        //get file path
                        String chosenFilePath = chosenFile.getAbsolutePath();
                        //set this windows last opened filepath to the current filepath
                        openedFilePath = chosenFilePath;
                        //get the index of last .
                        int lastIndex = openedFilePath.lastIndexOf(".");
                        //get file extension
                        String fileExtension = openedFilePath.substring(lastIndex, openedFilePath.length());
                        //if CSV
                        if(fileExtension.equals(".csv")) {
                            //import CSV
                            importCSV(chosenFilePath);
                        //if TXT
                        } else if (fileExtension.equals(".txt")) {
                            //import TXT
                            importTXT(chosenFilePath);
                        }
                        applyPostProcessing();
                        toCreateNewWindow = true;
                    }
                    windowCount++;
                }
            }
//            String chosenFilePath = fileChooser.getSelectedFile().getAbsolutePath();
//            openedFilePath = chosenFilePath;
//            boolean shouldContinue = askForVehicle();
//            if(shouldContinue) {
//                int lastIndex = openedFilePath.lastIndexOf(".");
//                String fileExtension = openedFilePath.substring(lastIndex, openedFilePath.length());
//                if(fileExtension.equals(".csv")) {
//                    importCSV(chosenFilePath);
//                } else if (fileExtension.equals(".txt")) {
//                    importTXT(chosenFilePath);
//                }
//            }
        }
    }//GEN-LAST:event_newImportMenuItemActionPerformed

    private void newVehicleMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newVehicleMenuItemActionPerformed
        //open vehicle data dialog
        new VehicleDataDialog(this, true, vehicleData, "Create").setVisible(true);
    }//GEN-LAST:event_newVehicleMenuItemActionPerformed

    private void importVehicleMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importVehicleMenuItemActionPerformed
        //open file for vehicleData
        // Open a separate dialog to select a .csv file
        fileChooser = new JFileChooser() {

            // Override approveSelection method because we only want to approve
            //  the selection if its is a .csv file.
            @Override
            public void approveSelection() {
                File chosenFile = getSelectedFile();

                // Make sure that the chosen file exists
                if (chosenFile.exists()) {
                    // Get the file extension to make sure it is .csv
                    String filePath = chosenFile.getAbsolutePath();
                    int lastIndex = filePath.lastIndexOf(".");
                    String fileExtension = filePath.substring(lastIndex,
                            filePath.length());

                    // approve selection if it is a .csv file
                    if (fileExtension.equals(".vd")) {
                        super.approveSelection();
                    } else {
                        // do nothing - that selection should not be approved
                    }

                }
            }
        };

        // showOpenDialog returns the chosen option and if it as an approve
        //  option then the file should be imported and opened
        int choice = fileChooser.showOpenDialog(null);
        if (choice == JFileChooser.APPROVE_OPTION) {
            String chosenFilePath = fileChooser.getSelectedFile().getAbsolutePath();
            importVehicleData(chosenFilePath);
        }
    }//GEN-LAST:event_importVehicleMenuItemActionPerformed

    private void editVehicleMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editVehicleMenuItemActionPerformed
        //open VehicleDataDialog
        new VehicleDataDialog(this, true, vehicleData, "Apply").setVisible(true);
    }//GEN-LAST:event_editVehicleMenuItemActionPerformed

    private void saveVehicleMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveVehicleMenuItemActionPerformed
        //save vehicle dynamic data
        saveVehicleData("");
    }//GEN-LAST:event_saveVehicleMenuItemActionPerformed

    private void categoryListKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_categoryListKeyReleased
    }//GEN-LAST:event_categoryListKeyReleased

    private void histogramMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_histogramMenuItemActionPerformed
        showHistogram();
    }//GEN-LAST:event_histogramMenuItemActionPerformed

    private void saveAsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsMenuItemActionPerformed
        //save file with no known file path. Will force method to open file chooser
        saveFile("");
    }//GEN-LAST:event_saveAsMenuItemActionPerformed

    private void staticMarkersListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_staticMarkersListMouseClicked
        //Determine how many clicks, and if double click: open notes dialog
        //get list
        JList list = (JList)evt.getSource();
        //if two clicks
        if (SwingUtilities.isRightMouseButton(evt)) {
            //get the list model to get element at index
            ListModel model = list.getModel();
            //get indices of items selected
            int[] selected = list.getSelectedIndices();
            //holds how many different domains are in selected
            int domainCount = 0;
            //check each element, if its a duplicate
            for(int i = 0; i < selected.length; i++) {
                boolean wasBefore = false;
                //check every element before
                for(int j = i - 1; j > 0; j--) {
                    //if prev element and curr element match: fail
                    String prev = ("" + model.getElementAt(selected[j])).substring(1, ("" + model.getElementAt(selected[j])).indexOf(','));
                    String curr = ("" + model.getElementAt(selected[i])).substring(1, ("" + model.getElementAt(selected[i])).indexOf(','));
                    if(prev.equals(curr)) {
                        wasBefore = true;
                        break;
                    }
                }
                
                //if we didnt find the same element before, 
                if(!wasBefore)
                    domainCount++;
            }
            
            //
            int[] selectedDomains = new int[domainCount];
            int k = 0;
            //get only 
            for(int i = 0; i < selected.length; i++) {
                boolean wasBefore = false;
                //check every element before
                for(int j = i - 1; j > 0; j--) {
                    //if prev element and curr element match: fail
                    //if prev element and curr element match: fail
                    String prev = ("" + model.getElementAt(selected[j])).substring(1, ("" + model.getElementAt(selected[j])).indexOf(','));
                    String curr = ("" + model.getElementAt(selected[i])).substring(1, ("" + model.getElementAt(selected[i])).indexOf(','));
                    if(prev.equals(curr)) {
                        wasBefore = true;
                        break;
                    }
                }
                
                //if we didnt find the same element before, 
                if(!wasBefore) {
                    selectedDomains[k] = selected[i];
                    k++;
                }
            }
            //get tags currently visible
            String[] tags = titleToTag();
            //create same length array of CategorizedValueMarkers
            ArrayList<CategorizedValueMarker> markers = new ArrayList<>();
            //for each domain selected
            for(int i = 0; i < selectedDomains.length; i++) {
                //get the corresponding CategorizedValueMarker
                for(String tag : tags) {
                    CategorizedValueMarker currMarker = getMarkerFromString(tag, "" + model.getElementAt(selectedDomains[i]));
                    if(currMarker != null && !(currMarker.getNotes().matches("Start Lap[0-9]+") || currMarker.getNotes().matches("End Lap[0-9]+"))) {
                        markers.add(currMarker);
                    }
                }
            }

            //launch notes dialog
            if(markers.size() > 0)
                new MarkerNotesDialog(markers.toArray(new CategorizedValueMarker[markers.size()])).setVisible(true);
        }
        
    }//GEN-LAST:event_staticMarkersListMouseClicked

    private void staticMarkersListKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_staticMarkersListKeyReleased
        //if the dataList has focus and a key is pressed
        //get the key code
        int code = evt.getKeyCode();
        //if the data list has an index that is selected
        if(staticMarkersList.getSelectedIndex() > -1) {
            //get list and model
            JList list = (JList) evt.getSource();
            ListModel model = list.getModel();
            //depending on the code
            switch(code) {
                //if its backspace or delete, remove the item from the datamap
                case KeyEvent.VK_DELETE :
                case KeyEvent.VK_BACKSPACE : 
                    String[] tags = titleToTag();
                    for(String tag : tags) {
                        staticMarkers.remove(getMarkerFromString(tag, "" +
                                model.getElementAt(staticMarkersList.getSelectedIndex())));
                    }
                    drawMarkers(titleToTag(), chartPanel.getChart().getXYPlot()); 
                    break;
            }
        }
    }//GEN-LAST:event_staticMarkersListKeyReleased

    //Export the data into a CSV file to use with other programs.
    private void exportMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportMenuItemActionPerformed
        String data = hashMapToCSV(dataMap.tags);
        //open the file choser
        JFileChooser chooser = new JFileChooser();
        //set the directory
        chooser.setCurrentDirectory(new File(""));
        //variable that holds result
        int retrival = chooser.showSaveDialog(null);
        //if its approved
        if (retrival == JFileChooser.APPROVE_OPTION) {
            //if the selected file is a .csv file
            if(!chooser.getSelectedFile().toString().contains(".csv")){
                //try to open a file writer
                try(FileWriter fw = new FileWriter(chooser.getSelectedFile() + ".csv")) {
                    //write the data
                    fw.write(data);
                    //close the file writer
                    fw.close();
                //exception handling
                } catch (IOException e) {
                    new MessageBox(e.toString()).setVisible(true);
                }
             //if its not a csv file
            } else {
                //try to write a file without an extension, it will not be openable unless converted later
                try(FileWriter fw = new FileWriter(chooser.getSelectedFile())) {
                    //write the data
                    fw.write(data);
                    //close the writer
                    fw.close();
                //exception handling
                } catch (IOException e) {
                    new MessageBox(e.toString()).setVisible(true);
                }
            }
            
        }
    }//GEN-LAST:event_exportMenuItemActionPerformed

    //begin the lapbreaker
    private void addLapConditionMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addLapConditionMenuItemActionPerformed
        //if the lapbreaker is not already active
        if(lapBreakerActive == -1) {
            newLap = new Lap();
            //set the lapBreaker to active, this changes the functionality of clicking on the chart
            lapBreakerActive = 0;
            //Display message box with instructions
            new MessageBox("Use the reticle to find the start of the lap.\nClick where the lap starts.\nClick again where the lap stops.").setVisible(true);
        } else {
            //replace with toast
            new MessageBox("Adding of Lap cancelled.").setVisible(true);
            lapBreakerActive = -1;
        }
    }//GEN-LAST:event_addLapConditionMenuItemActionPerformed

    private void lapListKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lapListKeyReleased
        //if the lapList has focus and a key is pressed
        //get the key code
        int code = evt.getKeyCode();
        //if the lap list has an index that is selected
        if(lapList.getSelectedIndex() > -1) {
            //depending on the code
            switch(code) {
                //if its backspace or delete, remove the item from the lapBreaker and update the Lap for the data objects.
                case KeyEvent.VK_DELETE :
                case KeyEvent.VK_BACKSPACE : lapBreaker.remove(getLapFromLapNumber(Integer.parseInt(lapList.getSelectedValue().charAt(0) + ""))); fillDataList(dataMap.tags); Lap.applyToDataset(dataMap, lapBreaker); break;
            }
        }
        
    }//GEN-LAST:event_lapListKeyReleased

    private void newWindowMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newWindowMenuItemActionPerformed
        DataAnalyzer da = new DataAnalyzer();
        da.setVisible(true);
        da.setLocation(100, 100);
    }//GEN-LAST:event_newWindowMenuItemActionPerformed

    private void closeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeMenuItemActionPerformed
        if (JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to close the program? This will close all windows.", "Close Window?", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
    }//GEN-LAST:event_closeMenuItemActionPerformed

    private void importVehicleData(String filepath) {
        //create scanner to read file
        Scanner scanner = null;
        try {
            //try to initiate with given filepath
            scanner = new Scanner(new File(filepath));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DataAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
            // error message displayed
            new MessageBox("Error: File not found").setVisible(true);
        }
        
        //if failed to load, leave method
        if(scanner == null)
            return;
        
        //string builder for creating string of data
        StringBuilder sb = new StringBuilder("");
        while(scanner.hasNextLine()) {
            //append the next line followed by a new line char
            sb.append(scanner.nextLine());
            sb.append("\n");
        }
        
        //give the data to the vehicleData class to create
        vehicleData.applyVehicleData(sb.toString());
        
    }
    
    private void saveVehicleData(String filename) {
        //get the string of the data
        String sb = vehicleData.getStringOfData();
        //open the file choser
        JFileChooser chooser = new JFileChooser();
        //set the directory
        chooser.setCurrentDirectory(new File(filename));
        //variable that holds result
        int retrival = chooser.showSaveDialog(null);
        //if its approved
        if (retrival == JFileChooser.APPROVE_OPTION) {
            //if the selected file is a .csv file
            if(!chooser.getSelectedFile().toString().contains(".vd")){
                //try to open a file writer
                try(FileWriter fw = new FileWriter(chooser.getSelectedFile() + ".vd")) {
                    //write the data
                    fw.write(sb);
                    //close the file writer
                    fw.close();
                //exception handling
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
             //if its not a csv file
            } else {
                //try to write a file without an extension, it will not be openable unless converted later
                try(FileWriter fw = new FileWriter(chooser.getSelectedFile())) {
                    //write the data
                    fw.write(sb);
                    //close the writer
                    fw.close();
                //exception handling
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
            
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DataAnalyzer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DataAnalyzer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DataAnalyzer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DataAnalyzer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DataAnalyzer().setVisible(true);
            }
        });
    }
    
    /**
     * Gets the tag from the active chart and formats it into a String array of TAGs
     * @return String of the TAGs of the active charts
     */
    private String[] titleToTag() {
        return titleToTag("");
    }
    
    //given a chart title or dataList title we can create the tag
    /**
     * Reformats a title into a String array of TAGs
     * @param title String value of the title of a chart
     * @return String value of the TAGs from the title given
     */
    private String[] titleToTag(String title) {
        //if empty get from chart
        if(title.isEmpty()) {
            title = chartPanel.getChart().getTitle().getText();
        }
        
        //create array of tags
        String[] titleSplit = title.split(" vs ");
        String[] tags = new String[titleSplit.length - 1];
        for (int i = 0; i < titleSplit.length - 1; i++) {
            tags[i] = titleSplit[titleSplit.length - 1] + "," + titleSplit[i];
        }
        
        return tags;
    }

    public void importTXT(String filepath) {
        openingAFile = true;
        TXTParser.parse(dataMap, filepath);
    }
    
    public void importCSV(String filepath) {
        //begin file operations
        openingAFile = true;
        String tag = "";
        try {
            // Create a new file from the filepath
            File file = new File(filepath);
            // Scan the file
            Scanner sc = new Scanner(file);
            
            boolean isMarker = false;
            // While there is a next line
            while (sc.hasNextLine()) {
                // Store the line
                String line = sc.nextLine();
                // If the line represents an END of the current tag
                if (line.equals("END")) {
                    isMarker = false;
                    // Necessary so that END statements don't get added to 'tags' ArrayList
                } else if(line.equals("MARKERS")) {
                    isMarker = true;
                } else if (Character.isLetter(line.charAt(0))) {
                    // If the first character is a letter
                    // Then add the line to the tags list
                    tag = line;
                } else if (Character.isDigit(line.charAt(0))) {
                    if(!isMarker) {
                        // If the first character is a digit
                        // Then divide the list in 2 values by ,
                        final String DELIMITER = ",";
                        String[] values = line.split(DELIMITER);
                        // And add the values to the hashmap with their correct tag
                        // dataMap.put(new SimpleLogObject(“TAG HERE”, VALUE HERE, TIME VALUE HERE));
                        if(tag.contains("Time,"))
                            dataMap.put(new SimpleLogObject(tag, Double.parseDouble(values[1]), Long.parseLong(values[0])));
                        else
                            dataMap.put(new FunctionOfLogObject(tag, Double.parseDouble(values[1]), Double.parseDouble(values[0])));
                    } else {
                        ValueMarker v = new ValueMarker(Double.parseDouble(line));
                        v.setPaint(Color.BLUE);
                        staticMarkers.put(new CategorizedValueMarker(tag, v));
                    }
                }
            }
        } catch (FileNotFoundException x) {
            // Error message displayed
            new MessageBox("Error: File not found").setVisible(true);
        }
        
        
    }
    
    public void applyPostProcessing() {
        //if nothing was loaded do not try to do math channels
        if(dataMap.isEmpty())
            return;
        
        //load wheelspeed averages
        
        //calculate front wheel speed averages
        if(dataMap.tags.contains("Time,WheelspeedFR") && dataMap.tags.contains("Time,WheelspeedFL"))
            EquationEvaluater.evaluate("($(Time,WheelspeedFR)) * ($(Time,WheelspeedFL)) / 2", dataMap, "Time,WheelspeedFront");
        
        //calculate rear wheel speed averages
        if(dataMap.tags.contains("Time,WheelspeedRR") && dataMap.tags.contains("Time,WheelspeedRL"))
            EquationEvaluater.evaluate("($(Time,WheelspeedRR)) * ($(Time,WheelspeedRL)) / 2", dataMap, "Time,WheelspeedRear");
        
        //calculate full average
        if(dataMap.tags.contains("Time,WheelspeedRear") && dataMap.tags.contains("Time,WheelspeedFront"))
            EquationEvaluater.evaluate("($(Time,WheelspeedRear)) * ($(Time,WheelspeedFront)) / 2", dataMap, "Time,WheelspeedAvg");
        
        //Create time vs distance
        if(dataMap.tags.contains("Time,WheelspeedFront"))
            EquationEvaluater.evaluate("($(Time,WheelspeedFront) * (2 * 3.14159 * 10.2)", dataMap, "Time,Distance");
        
        //Create sucky sucky in asain accent
        if(dataMap.tags.contains("Time,Barometer") && dataMap.tags.contains("Time,MAP")) {
            EquationEvaluater.evaluate("($(Time,Barometer)) - ($(Time,MAP))", dataMap, "Time,SuckySucky");
        }
        
        //Create Average of Analog in 5v form
        if(dataMap.tags.contains("Time,Analog3") && dataMap.tags.contains("Time,Analog4")) {
            EquationEvaluater.evaluate("(($(Time,Analog3) + ($(Time,Analog4)))/2)", dataMap, "Time,Lamda5VAveraged");
        }
        
        //average the 5V output to AFR
        //convert to AFR
        if(dataMap.tags.contains("Time,Lamda5VAveraged")) {
            EquationEvaluater.evaluate("2 * $(Time,Lamda5VAveraged) + 10", dataMap, "Time,AFRAveraged");
        }
        
        if(dataMap.tags.contains("Time,Analog1")) {
            EquationEvaluater.evaluate("(($(Time,Analog1)-.5)*(5000-0))/(4.5-.5)", dataMap, "Time,BrakePressureFront");
        }
        if(dataMap.tags.contains("Time,Analog2")) {
            EquationEvaluater.evaluate("(($(Time,Analog2)-.5)*(5000-0))/(4.5-.5)", dataMap, "Time,BrakePressureRear");
        }
        if(dataMap.tags.contains("Time,BrakePressureRear") && dataMap.tags.contains("Time,BrakePressureRear")) {
            //calculate force on caliper pistons
            EquationEvaluater.evaluate("($(Time,BrakePressureFront)*(3.14*.00090792))", dataMap, "Time,ForceOnCaliperPistonFront");
            EquationEvaluater.evaluate("($(Time,BrakePressureRear)*(3.14*.000706858))", dataMap, "Time,ForceOnCaliperPistonRear");
            
            //calcuate torque
            EquationEvaluater.evaluate("($(Time,ForceOnCaliperPistonFront)*.106588*2)", dataMap, "Time,EffectiveBrakeTorqueFront");
            EquationEvaluater.evaluate("($(Time,ForceOnCaliperPistonRear)*.0823*2)", dataMap, "Time,EffectiveBrakeTorqueRear");
            
        }
        //TODO: what if no brakes are applied, divide by 0 error. above 5.1
        if(dataMap.tags.contains("Time,EffectiveBrakeTorqueFront") && dataMap.tags.contains("Time,EffectiveBrakeTorqueRear")) {
            EquationEvaluater.evaluate("$(Time,EffectiveBrakeTorqueFront)/($(Time,EffectiveBrakeTorqueFront) + $(Time,EffectiveBrakeTorqueRear))", dataMap, "Time,BrakeBalance", 0, 1);
        }

        //Perform Operations
        //TODO: FILTERING
        EquationEvaluater.evaluate("($(Time,Coolant)-32)*(5/9)", dataMap, "CoolantCelcius");
        
        //Create Distance Channels for all datasets that do not contain "Time"
        for(int i = 0; i < dataMap.table.length; i++) {
            if(dataMap.table[i] != null && !dataMap.table[i].isEmpty() && dataMap.table[i].getFirst().getTAG().contains("Time")) {
                if(!dataMap.table[i].getFirst().getTAG().equals("Time,Distance"))
                    EquationEvaluater.evaluate("$(" + dataMap.table[i].getFirst().getTAG() + ") asFunctionOf($(Time,Distance))", dataMap, dataMap.table[i].getFirst().getTAG().substring(dataMap.table[i].getFirst().getTAG().indexOf(",") + 1, dataMap.table[i].getFirst().getTAG().length()));
            }
        }
        
        //finish file operations
        openingAFile = false;
    }
    
    public String hashMapToCSV(ArrayList<String> tags)
    {
        //output String
        StringBuilder out = new StringBuilder();
        //for each tag
        for(String tag : tags) {
            //get the tags data
            LinkedList<LogObject> los = dataMap.getList(tag);
            //append the tag and a new line
            out.append(tag);
            out.append("\n");
            //append all of the data objects
            for(LogObject lo : los) {
                out.append(lo.toString());
                out.append("\n");
            }
            //append END with new line
            out.append("END");
            out.append("\n");
        }
        
        return out.toString();
    }
    
    //saves the file to the disk
    private void saveCSV(String filename) {
        //get the string of the data
        String sb = getStringOfData();
        //open the file choser
        JFileChooser chooser = new JFileChooser();
        //set the directory
        chooser.setCurrentDirectory(new File(filename));
        //variable that holds result
        int retrival = chooser.showSaveDialog(null);
        //if its approved
        if (retrival == JFileChooser.APPROVE_OPTION) {
            //if the selected file is a .csv file
            if(!chooser.getSelectedFile().toString().contains(".csv")){
                //try to open a file writer
                try(FileWriter fw = new FileWriter(chooser.getSelectedFile() + ".csv")) {
                    //write the data
                    fw.write(sb);
                    //close the file writer
                    fw.close();
                //exception handling
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
             //if its not a csv file
            } else {
                //try to write a file without an extension, it will not be openable unless converted later
                try(FileWriter fw = new FileWriter(chooser.getSelectedFile())) {
                    //write the data
                    fw.write(sb);
                    //close the writer
                    fw.close();
                //exception handling
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
            
        }
        
    }
    
    private String getStringOfData() {
        StringBuilder toReturn = new StringBuilder();
        
        //for each tag of data
        for(String tag : dataMap.tags) {
            //output the tag
            toReturn.append(tag);
            toReturn.append("\n");
            //get the list of data for the current tag
            List<LogObject> data = dataMap.getList(tag);
            //for each data element
            for(LogObject lo : data) {
                //output the data
                toReturn.append(lo.toString());
                toReturn.append("\n");
            }
            //output MARKERS
            toReturn.append("MARKERS\n");
            //get the markers for the current tag
            List<CategorizedValueMarker> markers = staticMarkers.getList(tag);
            //if the markers exist
            if(markers != null) {
                //for each marker we have output it
                for(CategorizedValueMarker marker : markers) {
                    String toAdd = marker.getMarker().getValue() + "," + marker.getNotes() + "\n";
                    toReturn.append(toAdd);
                }
            }
            
            //output END to signify end of data for this tag.
            toReturn.append("END\n");
        }
        
        //return calculated value
        return toReturn.toString();
        
    }
    
    private void fillDataList(ArrayList<String> allTags){
        // Use the tags list to get the title for each tag
        titles = new String[allTags.size()];

        // Make a list of titles
        // Get (Title)"RPM vs Time" from (Tag)"Time, RPM"
        String str = "";
        for (int i = 0; i < titles.length; i++) {
            str = "";
            str += allTags.get(i).split(",")[1];
            str += " vs ";
            str += allTags.get(i).split(",")[0];
            titles[i] = str;
        }
        // Add the list of titles to the data List View 
        dataList.setListData(titles);
        
        //fill lap data
        String[] lapData = new String[lapBreaker.size()];
        for(int i = 0; i < lapData.length; i++) {
            lapData[i] = lapBreaker.get(i).toString();
        }
        lapList.setListData(lapData);
        
        //allow multiple selections and deselect
        lapList.setSelectionModel(new DefaultListSelectionModel() {
            private static final long serialVersionUID = 1L;

            boolean gestureStarted = false;

            @Override
            public void setSelectionInterval(int index0, int index1) {
                if(!gestureStarted){
                    if (isSelectedIndex(index0)) {
                        super.removeSelectionInterval(index0, index1);
                    } else {
                        super.addSelectionInterval(index0, index1);
                    }
                }
                gestureStarted = true;
            }

            @Override
            public void setValueIsAdjusting(boolean isAdjusting) {
                if (isAdjusting == false) {
                    gestureStarted = false;
                }
            }
        });

        // If another item is selected in the data combo box, change the chart
        dataList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent arg0) {
                if (!arg0.getValueIsAdjusting()) {
                    if(dataList.getSelectedIndex() != -1) {
                        int[] selected = dataList.getSelectedIndices();
                        String[] tags = new String[selected.length];
                        for(int i = 0; i < tags.length; i++){
                            tags[i] = allTags.get(selected[i]);
                        }
                        int[] laps;
                        if(lapList.getSelectedIndex() != -1) {
                            selected = lapList.getSelectedIndices();
                            laps = new int[selected.length];
                            if(laps.length > 0) {
                                ArrayList<String> selectedLaps = (ArrayList) lapList.getSelectedValuesList();
                                for(int i = 0; i < laps.length; i++) {
                                    laps[i] = Integer.parseInt(selectedLaps.get(i).charAt(0) + "");
                                }
                            }
                        } else {
                            laps = null;
                        }
                        //update global var that holds which laps are selected
                        selectedLaps = laps;
                        setChart(tags, laps);
                    }
                }
            }
        });

        // If a different or another lap is selected, change the graph accordingly
        lapList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent arg0) {
                if (!arg0.getValueIsAdjusting()) {
                    if(dataList.getSelectedIndex() != -1) {
                        int[] selected = dataList.getSelectedIndices();
                        String[] tags = new String[selected.length];
                        for(int i = 0; i < tags.length; i++){
                            tags[i] = allTags.get(selected[i]);
                        }
                        int[] laps;
                        if(lapList.getSelectedIndex() != -1) {
                            selected = lapList.getSelectedIndices();
                            laps = new int[selected.length];
                            if(laps.length > 0) {
                                ArrayList<String> selectedLaps = (ArrayList) lapList.getSelectedValuesList();
                                for(int i = 0; i < laps.length; i++) {
                                    laps[i] = Integer.parseInt(selectedLaps.get(i).charAt(0) + "");
                                }
                            }
                        } else {
                            laps = null;
                        }
                        //update global var that holds which laps are selected
                        selectedLaps = laps;
                        setChart(tags, laps);
                    }
                }
            }
        });
    }
    
    //updates the statistics panel
    private void updateStatistics(String[] tags) {
        //holds the final strings
        String avgStr = "";
        String minStr = "";
        String maxStr = "";
        //for each tag
        for(String tag : tags) {
            //get the data list thats showing
            List<LogObject> data = dataMap.getList(tag);
            //variables that hold average, min, and max
            double avg = 0;
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;
            //for each logobject in the list we got
            for(LogObject lo : data) {
                //if the LogObject is an instance of a SimpleLogObject
                if(lo instanceof SimpleLogObject) {
                    //add all the values to average
                    avg += ((SimpleLogObject) lo).getValue();
                    //if the current object is less than the current min, update min
                    if(((SimpleLogObject) lo).getValue() < min)
                        min = ((SimpleLogObject) lo).getValue();
                    //if the current object is greater than the current max, update max
                    if(((SimpleLogObject) lo).getValue() > max)
                        max = ((SimpleLogObject) lo).getValue();
                }
            }
            //divide average by number of objects we added
            avg /= data.size();
            //append the string
            avgStr += String.format("%.2f", avg) + ", ";
            minStr += String.format("%.2f", min) + ", ";
            maxStr += String.format("%.2f", max) + ", ";
        }
        
        //remove the last ", "
        avgStr = avgStr.substring(0, avgStr.length() - 2);
        minStr = minStr.substring(0, minStr.length() - 2);
        maxStr = maxStr.substring(0, maxStr.length() - 2);
        
        //set the text values, format to two decimal places
        averageText.setText(avgStr);
        maxText.setText(maxStr);
        minText.setText(minStr);
    }
    
    //save file
    private void saveFile(String filename) {
        //add normal data
        StringBuilder sb = new StringBuilder(getStringOfData());
        //append vehicle dynamic data
        sb.append("VEHICLEDYNAMICDATA");
        sb.append("\n");
        sb.append(vehicleData.getStringOfData());
        sb.append("LAPDATA");
        sb.append("\n");
        sb.append(Lap.getStringOfData(lapBreaker));
        
        //if a filename was not provided
        if(filename.isEmpty() || !filename.contains(".dfr")) {
            //open the filechooser at the default directory
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File(filename));
            
            //result code
            int result = chooser.showSaveDialog(null);
            
            //if approved
            if(result == JFileChooser.APPROVE_OPTION) {
                //if the file chosen is missing the .dfr file extension, add then save
                if(!chooser.getSelectedFile().toString().contains(".dfr")) {
                    //try to open a file writer
                    try(FileWriter fw = new FileWriter(chooser.getSelectedFile() + ".dfr")) {
                        //write the data
                        fw.write(sb.toString());
                        //close the file writer
                        fw.close();
                        //Display success Toast
                        Toast.makeToast(this, "Saved as: " + chooser.getSelectedFile().getName(), Toast.DURATION_LONG);
                    //exception handling
                    } catch (IOException e) {
                        //error message displayed
                        new MessageBox("Error: FileWriter could not be opened").setVisible(true);
                    }
                }
                else {
                    //try to open a file writer
                    try(FileWriter fw = new FileWriter(chooser.getSelectedFile())) {
                        //write the data
                        fw.write(sb.toString());
                        //close the file writer
                        fw.close();
                        //Display Success Toast
                        Toast.makeToast(this, "Saved as: " + chooser.getSelectedFile().getName(), Toast.DURATION_LONG);
                    //exception handling
                    } catch (IOException e) {
                        //error message displayed
                        new MessageBox("Error: FileWriter could not be opened ").setVisible(true);
                    }
                }
            } else {
                //error message displayed
                new MessageBox("Error: File could not be approved").setVisible(true);
            }
            
        } else { //if a filename was already provided
            //try to write the file
            try(FileWriter fw = new FileWriter(new File(filename))) {
                fw.write(sb.toString());
                fw.close();
                //Display Success Toast
                Toast.makeToast(this, "Saved file", Toast.DURATION_LONG);
            } catch (IOException e) {
                //error message displayed
                new MessageBox("Error: File could not be written to").setVisible(true);
            }
        }
    }
    
    //open file
    private void openFile(String filepath) {
        //begin file operation
        openingAFile = true;
        //Scanner to handle the file
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(filepath));
        } catch(FileNotFoundException e) {
            //error message displayed
            new MessageBox("Error: File could not be opened").setVisible(true);
        }
        
        //if we failed to open the file exit
        if(scanner == null) {
            return;
        }
        
        //is the current item a marker
        boolean isMarker = false;
        //current tag
        String tag = "";
        // While there is a next line
        //handles csv data and markers
        while (scanner.hasNextLine()) {
            // Store the line
            String line = scanner.nextLine();
            // If the line represents an END of the current tag
            if (line.equals("END")) {
                isMarker = false;
                // Necessary so that END statements don't get added to 'tags' ArrayList
            } else if(line.isEmpty()) {
                continue;
            }
            else if(line.equals("MARKERS")) {
                isMarker = true;
            } else if (line.equals("VEHICLEDYNAMICDATA")) {
                break;
            } else if (Character.isLetter(line.charAt(0))) {
                // If the first character is a letter
                // Then add the line to the tags list
                tag = line;
            } else if (Character.isDigit(line.charAt(0))) {
                if(!isMarker) {
                    // If the first character is a digit
                    // Then divide the list in 2 values by ,
                    final String DELIMITER = ",";
                    String[] values = line.split(DELIMITER);
                    // And add the values to the hashmap with their correct tag
                    // dataMap.put(new SimpleLogObject(“TAG HERE”, VALUE HERE, TIME VALUE HERE));
                    if(tag.contains("Time"))
                        dataMap.put(new SimpleLogObject(tag, Double.parseDouble(values[1]), Long.parseLong(values[0])));
                    else
                        dataMap.put(new FunctionOfLogObject(tag, Double.parseDouble(values[1]), Double.parseDouble(values[0])));
                } else {
                    String[] split = line.split(",");
                    if(split.length == 2) {
                        ValueMarker v = new ValueMarker(Double.parseDouble(split[0]));
                        v.setPaint(Color.BLUE);
                        staticMarkers.put(new CategorizedValueMarker(tag, v, split[1]));
                    } else if(split.length == 1) {
                        ValueMarker v = new ValueMarker(Double.parseDouble(split[0]));
                        v.setPaint(Color.BLUE);
                        staticMarkers.put(new CategorizedValueMarker(tag, v));
                    }
                }
            }
        }
        
        //string builder for creating string of data
        StringBuilder vd = new StringBuilder("");
        while(scanner.hasNextLine()) {
            //get next line
            String line = scanner.nextLine();
            if(line.equals("LAPDATA"))
                break;
            //append the next line followed by a new line char
            vd.append(line);
            vd.append("\n");
        }
        
        //for all the lines for lapdata
        while(scanner.hasNextLine()) {
            //get the next line
            String line = scanner.nextLine();
            if(line.isEmpty())
                continue;
            
            //holds the data
            long lapStart;
            long lapStop;
            int lapNumber;
            String lapLabel;
            //parse data from string
            lapNumber = Integer.parseInt(line.substring(0, line.indexOf('(')));
            lapStart = Integer.parseInt(line.substring(line.indexOf('(')+1, line.indexOf(',')));
            lapStop = Integer.parseInt(line.substring(line.indexOf(',')+1, line.indexOf(')')));
            lapLabel = line.substring(line.indexOf(')')+1);
            //save data
            if (lapLabel.trim().length() > 0)
                lapBreaker.add(new Lap(lapStart, lapStop, lapNumber, lapLabel));
            else
                lapBreaker.add(new Lap(lapStart, lapStop, lapNumber));
        }
        
        //give the data to the vehicleData class to create
        vehicleData.applyVehicleData(vd.toString());
        
        //we are finished with file operation
        openingAFile = false;
        
        //update lap data
        Lap.applyToDataset(dataMap, lapBreaker);
        fillDataList(dataMap.tags);
    }
    
    /**
     * 
     * @param TAG TAG of the dataset
     * @param s String collected from list
     * @return CategorizedValueMarker object that has the same domain marker as the string
     */
    private CategorizedValueMarker getMarkerFromString(String TAG, String s) {
        for(CategorizedValueMarker marker : staticMarkers.getList(TAG)) {
            if(String.format("%.2f", marker.getMarker().getValue()).equals(s.substring(1, s.indexOf(','))))
                return marker;
        }
        return null;
    }
    
    /**
     * 
     * @param TAG TAG of the dataset
     * @param domainValue the domain value where the marker is as a long
     * @return CategorizedValueMarker object that has the same domain marker as the provided value
     */
    private CategorizedValueMarker getMarkerFromDomainValue(String TAG, long domainValue) {
        return getMarkerFromDomainValue(TAG, (double) domainValue);
    }
    
    /**
     * 
     * @param TAG TAG of the dataset
     * @param domainValue the domain value where the marker is as a double
     * @return CategorizedValueMarker object that has the same domain marker as the provided value
     */
    private CategorizedValueMarker getMarkerFromDomainValue(String TAG, double domainValue) {
        for(CategorizedValueMarker marker : staticMarkers.getList(TAG)) {
            if(marker.getMarker().getValue() == domainValue)
                return marker;
        }
        return null;
    } 
       
    /**
     * Gets a color from an index. Given an index, returns the corresponding color
     * that is shown on an XYPlot
     * @param index Dataset index
     * @return Color of that dataset's line
     */
    private Color getColorFromIndex(int index) {
        switch(index) {
            case 0 : return Color.RED;
            case 1 : return Color.BLUE;
            case 2 : return Color.GREEN;
            case 3 : return Color.YELLOW;
            case 4 : return Color.CYAN;
            case 5 : return Color.PINK;
            default : return Color.BLACK;
        }
    }
    
    private long getRoundedTime(double val) {
        //time to return if its not already a function of time
        long time = -1;
        //get the tag of the first chart
        String TAG = titleToTag()[0];
        //if its a function of time, find nearest 50ms point
        if(TAG.contains("Time,")) {
            //get mod of value
            double mod = val % 50;
            //if value is less than 25 round down
            if(mod < 25) {
                return (long) (val - mod);
            //else round up
            } else {
                return (long) (val + (50 - mod));
            }
        //find base function
        } else {
            //Round to nearest domain value for the tag we are looking at
            String finding = TAG;
            //holds the closest value, holds value and object
            double closestVal = Double.MAX_VALUE;
            //for each logobject of the current tag
            for(LogObject lo : dataMap.getList(finding)) {
                //if its a functionoflogobject which it should be
                if(lo instanceof FunctionOfLogObject) {
                    //calculate the difference between this objects domain and the value the user clicked
                    if(Math.abs(((FunctionOfLogObject) lo).getX() - val) < closestVal) {
                        //if its closer, save this as closest
                        closestVal = Math.abs(((FunctionOfLogObject) lo).getX() - val);
                    }
                }
            }
            //find what its domain is
            String goTo = finding.substring(0,finding.indexOf(','));
            //see if there is a Time, with that domain as its range
            if(dataMap.tags.contains("Time," + goTo)) {
                //get the tag for the function of time
                String toSearch = "Time," + goTo;
                //for each logobject of the base function
                for(LogObject lo : dataMap.getList(toSearch)) {
                    if(lo instanceof SimpleLogObject) {
                        if(((SimpleLogObject) lo).getValue() == closestVal) {
                            return lo.getTime();
                        }
                    }
                }
            }
        }
        //return what we stored as time
        //this value is either -1 for no value found or the time realted to the other domain the user clicked
        return time;
    }
    
    /**
     * Returns the Lap object from the lapBreaker list given a lapNumber
     * @param lapNumber the lapNumber we are looking for
     * @return the Lap with the given lapNumber, null if not found
     */
    private Lap getLapFromLapNumber(int lapNumber) {
        Lap toReturn = null;
        for(Lap l : lapBreaker) {
            if(l.lapNumber == lapNumber)
                toReturn = l;
            break;
        }
        return toReturn;
    }
    
    /**
     * Ask the user if they want to create a vehicle before the auto dataset creation takes place
     * @return returns true if not cancel false if cancel was pressed.
     */
    private boolean askForVehicle() {
        //holds the return code by reference
        int[] returnCode = new int[1];
        //create the Dialog to ask the user
        AskVehicleDialog avd = new AskVehicleDialog(this, true, returnCode);
        avd.setVisible(true);
        //wait for a return code
        while(returnCode[0] == 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(DataAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        //the boolean to return.
        boolean toReturn = true;
        
        //create a vehicle data dialog just in case we need it, for proper encapsulation
        VehicleDataDialog vdd = new VehicleDataDialog(this, true, vehicleData, "Create");
        //depending on our return code
        switch(returnCode[0]) {
            //if the user cancelled, display the cancel message
            case AskVehicleDialog.OPTION_CANCEL : Toast.makeToast(this, "Opening File Cancelled.", Toast.DURATION_MEDIUM); toReturn = false; break;
            //if the user said they would import a vehicle open the file chooser. 
            case AskVehicleDialog.OPTION_IMPORT : importVehicleMenuItemActionPerformed(null); break;
            //if the user said they would create a new vehicle, show the vehicle data dialog
            case AskVehicleDialog.OPTION_NEW    : vdd.setVisible(true); break;
            //if the user said no, import the file
            case AskVehicleDialog.OPTION_NO     : break;
        }
        
        return toReturn;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem addLapConditionMenuItem;
    private javax.swing.JMenuItem addMathChannelButton;
    private javax.swing.JLabel averageText;
    private javax.swing.JList<String> categoryList;
    private javax.swing.JInternalFrame chartFrame;
    private javax.swing.JMenuItem closeMenuItem;
    private javax.swing.JList<String> dataList;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem editVehicleMenuItem;
    private javax.swing.JMenuItem exportMenuItem;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuItem fullscreenMenuItem;
    private javax.swing.JMenuItem histogramMenuItem;
    private javax.swing.JMenuItem importVehicleMenuItem;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JList<String> lapList;
    private javax.swing.JLabel maxText;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JLabel minText;
    private javax.swing.JMenuItem newImportMenuItem;
    private javax.swing.JMenuItem newVehicleMenuItem;
    private javax.swing.JMenuItem newWindowMenuItem;
    private javax.swing.JMenuItem openBtn;
    private javax.swing.JMenuItem saveAsMenuItem;
    private javax.swing.JMenuItem saveMenuButton;
    private javax.swing.JMenuItem saveVehicleMenuItem;
    private javax.swing.JTextField searchField;
    private javax.swing.JList<String> staticMarkersList;
    private javax.swing.JPanel statisticsPanel;
    private javax.swing.JMenu vehicleMenu;
    private javax.swing.JMenu viewMenu;
    private javax.swing.JLabel xCordLabel;
    private javax.swing.JLabel yCordLabel;
    // End of variables declaration//GEN-END:variables
    
    private class AnalysisCategory {
        String title;
        ArrayList<String> TAG;
        
        public AnalysisCategory() {
            title = "";
            TAG = new ArrayList<>();
        }
        
        public AnalysisCategory(String title) {
            this.title = title;
            TAG = new ArrayList<String>();
        }
        
        public AnalysisCategory(String title, ArrayList<String> TAG) {
            this.title = title;
            this.TAG = TAG;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public ArrayList<String> getTAG() {
            return TAG;
        }

        public void setTAG(ArrayList<String> TAG) {
            this.TAG = TAG;
        }
        
        public AnalysisCategory addTag(String elem) {
            TAG.add(elem);
            return this;
        }
    }
}
