/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer;

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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.general.DatasetUtilities;
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

    // Stores the data set for each data type ( RPM vs Time, Distance vs Time....)
    CategoricalHashMap dataMap;
    
    //Stores all the static markers the user has created
    CategoricalHashTable<CategorizedValueMarker> staticMarkers;
    
    //Stores the vehicle data
    VehicleData vehicleData;
    
    //Stores the array of String in the listview of tags
    String[] titles;
    
    //Stores the current filepath
    private String openedFilePath;
    
    //String array that populates the categories list
    AnalysisCategory[] analysisCategories = new AnalysisCategory[] { 
        new AnalysisCategory("Brakes"), new AnalysisCategory("Coolant"), 
        new AnalysisCategory("Acceleration"), new AnalysisCategory("Endurance"), 
        new AnalysisCategory("Skidpad")};

    public DataAnalyzer() {
        initComponents();

        //disable the layout manager which essentially makes the frame an absolute positioning frame
        this.setLayout(null);
        
        // Create a new hash map
        dataMap = new CategoricalHashMap();
        
        //create a new instance of the vehicle data
        vehicleData = new VehicleData();

        //on new element entry of dataMap, update the view
        dataMap.addTagSizeChangeListener(new HashMapTagSizeListener() {
            @Override
            public void sizeUpdate() {
                fillDataList(dataMap.tags);
            }
        });

        //init the arraylist of static markers
        staticMarkers = new CategoricalHashTable<>();

        // Init the graph with some dummy data until there is data given to read
        showEmptyGraph();
        
        // Create the global object crosshairs
        this.xCrosshair = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
        this.xCrosshair.setLabelVisible(true);
        this.yCrosshair = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
        this.yCrosshair.setLabelVisible(true);
        
        //init the array
        titles = new String[10];
        
        //set the opened file path to empty string to prevent null pointer exceptions
        openedFilePath = "";
        
        //populate category list 
        //TODO: add tags to each list element.
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
        String tag = titleToTag(title);
        //update laps
        XYSeriesCollection data = getHistogramDataCollection(tag, lapList.getSelectedIndices());
        
        // Gets the independent variable from the title of the data
        String yAxis = "Instances";
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
        updateStatistics(tag);
        
    }

    private void setChart(String tag, int[] laps, String title) {

        // Gets the specific data based on what kind of data we want to show for which 
        final XYSeriesCollection data = getDataCollection(tag, laps);

        // Gets the independent variable from the title of the data
        String xAxis = title.split(" vs ")[1];  //split title by vs, we get ["RPM", "Time"] or something like that
        // Gets the dependent variable from the title of the data
        String yAxis = title.split(" vs ")[0];

        // Create a JFreeChart from the Factory, given parameters (Chart Title, Domain name, Range name, series collection, PlotOrientation, show legend, show tooltips, show url)
        JFreeChart chart = ChartFactory.createXYLineChart(
                title,
                xAxis,
                yAxis,
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
        
        //update statistics
        updateStatistics(tag);
        
        //draw markers
        drawMarkers(tag, chart.getXYPlot());
    }

    private XYSeriesCollection getDataCollection(String tag, int[] laps) {

        // XY Series Collection allows there to be multiple data lines on the graph
        final XYSeriesCollection graphData = new XYSeriesCollection();
        // Get the list of data elements based on the tag
        LinkedList<LogObject> data = dataMap.getList(tag);
        // Declare the series to add the data elements to
        final XYSeries series = new XYSeries("");
        
        //if tag contains time then its not a function of another dataset
        if(tag.contains("Time")) {
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

//      Each series in the series array would have the lap data from laps ary
//      for(int i = 0; i < laps.length; i++){
//          XYSeries s = series[i];
//          s.setKey("Lap " + laps[i]);
//          graphData.addSeries(s);
//      }

        // Add the series to the XYCollection
        graphData.addSeries(series);
        // Return the XYCollection
        return graphData;
    }
    
    private XYSeriesCollection getHistogramDataCollection(String tag, int[] laps) {
        //collection to return
        final XYSeriesCollection graphData = new XYSeriesCollection();
        
        //get data from dataset
        LinkedList<LogObject> data = dataMap.getList(tag);
        
        //series that will hold the data
        final XYSeries series = new XYSeries("");
        
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
        
        //holds how many instances occured within this interval
        int counter;
        
        //for each of the 50 intervals
        for(int i = 1; i < 51; i++) {
            //start with 0 count
            counter = 0;
            
            //for each data element
            for(LogObject lo : data) {
                //if its a simple log object its value can be obtained
                if(lo instanceof SimpleLogObject) {
                    //if the value of the current object is between the interval we are searching for
                    if(((SimpleLogObject) lo).getValue() < ((interval * i) + min) && ((SimpleLogObject) lo).getValue() > ((interval * (i-1)) + min)) {
                        //increment the counter
                        counter++;
                    }
                }
            }
            //if the counter is not 0, add the median of the interval we are looking for along with the counter to the series.
            if(counter != 0)
                series.add((((interval * i) + min) + ((interval * i - 1) + min))/2, counter); //TODO, make counter estimate the amount of time spent in this interval.
        }

        
        
        graphData.addSeries(series);
        return graphData;
        
    }

    // When the chart is clicked
    @Override
    public void chartMouseClicked(ChartMouseEvent cme) {
        // Create a static cursor that isnt cleared every time
        ValueMarker marker = new ValueMarker(xCor);
        //static markers are blue
        marker.setPaint(Color.BLUE);
        //calculate the tag
        String tag = cme.getChart().getTitle().getText();
        String[] arr = tag.split(" ");
        //add to the list of static markers
        staticMarkers.put(new CategorizedValueMarker(titleToTag(tag), marker));
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
        plot.clearDomainMarkers();
        // Get the xAxis
        ValueAxis xAxis = plot.getDomainAxis();
        // Get the xCordinate from the xPositon of the mouse
        xCor = xAxis.java2DToValue(cme.getTrigger().getX(), dataArea,
                RectangleEdge.BOTTOM);
        // Find the y cordinate from the plots data set given a x cordinate
        yCor = DatasetUtilities.findYValue(plot.getDataset(), 0, xCor);
        // Create a marker at the x Coordinate with black paint
        ValueMarker marker = new ValueMarker(xCor);
        marker.setPaint(Color.BLACK);
        // Add a marker on the x axis given a marker. This essentially makes the marker verticle
        plot.addDomainMarker(marker);
        
        //calculate the tag
        String tag = titleToTag(chart.getTitle().getText());
        
        //call the method to draw the markers
        drawMarkers(tag, plot);
        

        // String object that holds values for all the series on the plot.
        String yCordss = "";
        // Repeat the loop for each series in the plot
        for (int i = 0; i < plot.getDataset().getSeriesCount(); i++) {
            // Get the collection from the plots data set
            XYSeriesCollection col = (XYSeriesCollection) plot.getDataset();
            // Get the plots name from the series's object
            String plotName = plot.getDataset().getSeriesKey(i).toString();
            // Create a new collection 
            XYSeriesCollection col2 = new XYSeriesCollection();
            // Add the series with the name we found to the other collection
            // We do this because the findYValue() method takes a collection
            col2.addSeries(col.getSeries(plotName));
            // Get the y value for the current series.
            double val = DatasetUtilities.findYValue(col2, 0, xCor);
            // Add the value to the string
            yCordss += String.format("%.2f", val) + "\n";

        }

        // Set the textviews at the bottom of the file.
        xCordLabel.setText(String.format("%.2f", xCor));
        yCordLabel.setText(yCordss);

        // Set this objects crosshair data to the value we have
        this.xCrosshair.setValue(xCor);
        this.yCrosshair.setValue(yCor);
    }
    
    private void drawMarkers(String tag, XYPlot plot) {
        //get the linked list from tag
        LinkedList<CategorizedValueMarker> markers = staticMarkers.getList(tag);
        //position var
        int k = 0;
        //if the linked list is not null
        if(markers != null) {
            //create string array of data
            String[] staticMarkerData = new String[markers.size()];
            //draw every domain marker saved for this chart and add it to an array
            for(CategorizedValueMarker v : markers) {
                plot.addDomainMarker(v.getMarker());
                //create formatted string and insert into current index
                // Repeat the loop for each series in the plot
                for (int i = 0; i < plot.getDataset().getSeriesCount(); i++) {
                    // Get the collection from the plots data set
                    XYSeriesCollection col = (XYSeriesCollection) plot.getDataset();
                    // Get the plots name from the series's object
                    String plotName = plot.getDataset().getSeriesKey(i).toString();
                    // Create a new collection 
                    XYSeriesCollection col2 = new XYSeriesCollection();
                    // Add the series with the name we found to the other collection
                    // We do this because the findYValue() method takes a collection
                    col2.addSeries(col.getSeries(plotName));
                    //insert the marker data into current index
                    staticMarkerData[k] = "(" + String.format("%.2f", markers.get(k).getMarker().getValue()) + ", " +
                            String.format("%.2f", DatasetUtilities.findYValue(col2,0,markers.get(k).getMarker().getValue())) + ")";

                }
                k++;
            }
            //set the data to the list
            staticMarkersList.setListData(staticMarkerData);

        } else {
            staticMarkersList.setListData(new String[0]);
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
        newImportMenuItem = new javax.swing.JMenuItem();
        openCSVBtn = new javax.swing.JMenuItem();
        saveMenuButton = new javax.swing.JMenuItem();
        saveAsMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        addMathChannelButton = new javax.swing.JMenuItem();
        viewMenu = new javax.swing.JMenu();
        histogramMenuItem = new javax.swing.JMenuItem();
        fullscreenMenuItem = new javax.swing.JMenuItem();
        vehicleMenu = new javax.swing.JMenu();
        newVehicleMenuItem = new javax.swing.JMenuItem();
        saveVehicleMenuItem = new javax.swing.JMenuItem();
        importVehicleMenuItem = new javax.swing.JMenuItem();
        editVehicleMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
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
        jScrollPane2.setViewportView(lapList);

        jScrollPane4.setPreferredSize(new java.awt.Dimension(43, 128));
        jScrollPane4.setSize(new java.awt.Dimension(43, 128));

        staticMarkersList.setSize(new java.awt.Dimension(177, 128));
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
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE))))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 145, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addGap(139, 139, 139))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                    .addContainerGap(348, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(26, 26, 26)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 6, 189, 640));

        jLabel1.setText("X Cord:");

        xCordLabel.setText("jLabel2");

        jLabel2.setText("Y Cord:");

        yCordLabel.setText("jLabel2");

        jLabel3.setText("Average:");

        jLabel5.setText("Max: ");

        jLabel6.setText("Min: ");

        maxText.setText("max");

        averageText.setText("acg");

        minText.setText("min");

        javax.swing.GroupLayout statisticsPanelLayout = new javax.swing.GroupLayout(statisticsPanel);
        statisticsPanel.setLayout(statisticsPanelLayout);
        statisticsPanelLayout.setHorizontalGroup(
            statisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statisticsPanelLayout.createSequentialGroup()
                .addGroup(statisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(statisticsPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(6, 6, 6)
                        .addComponent(xCordLabel)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(averageText))
                    .addGroup(statisticsPanelLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(6, 6, 6)
                        .addComponent(yCordLabel)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(statisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(statisticsPanelLayout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(maxText))
                    .addGroup(statisticsPanelLayout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(minText)))
                .addGap(0, 46, Short.MAX_VALUE))
        );
        statisticsPanelLayout.setVerticalGroup(
            statisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statisticsPanelLayout.createSequentialGroup()
                .addGroup(statisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(xCordLabel)
                    .addComponent(jLabel3)
                    .addGroup(statisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(averageText)
                        .addComponent(maxText)))
                .addGap(6, 6, 6)
                .addGroup(statisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(yCordLabel)
                    .addGroup(statisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(minText)))
                .addGap(0, 12, Short.MAX_VALUE))
        );

        averageText.getAccessibleContext().setAccessibleName("");

        getContentPane().add(statisticsPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 600, -1, 50));

        fileMenu.setText("File");

        newImportMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        newImportMenuItem.setText("New Import");
        newImportMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newImportMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(newImportMenuItem);

        openCSVBtn.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        openCSVBtn.setText("Open");
        openCSVBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openCSVBtnClicked(evt);
            }
        });
        fileMenu.add(openCSVBtn);

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

        menuBar.add(fileMenu);

        editMenu.setText("Edit");

        addMathChannelButton.setLabel("Add Math Channel");
        addMathChannelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addMathChannel(evt);
            }
        });
        editMenu.add(addMathChannelButton);

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

    private void openCSVBtnClicked(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openCSVBtnClicked
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
                    } else {
                        // do nothing - that selection should not be approved
                        //TODO: Message Box here
                        this.cancelSelection();
                    }

                }
            }
        };

        // showOpenDialog returns the chosen option and if it as an approve
        //  option then the file should be imported and opened
        int choice = fileChooser.showOpenDialog(null);
        if (choice == JFileChooser.APPROVE_OPTION) {
            String chosenFilePath = fileChooser.getSelectedFile().getAbsolutePath();
            openedFilePath = chosenFilePath;
            openFile(chosenFilePath);
        }
    }//GEN-LAST:event_openCSVBtnClicked

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
                case KeyEvent.VK_BACKSPACE : dataMap.remove(titleToTag(dataList.getSelectedValue())); break;
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
                File chosenFile = getSelectedFile();

                // Make sure that the chosen file exists
                if (chosenFile.exists()) {
                    // Get the file extension to make sure it is .csv
                    String filePath = chosenFile.getAbsolutePath();
                    int lastIndex = filePath.lastIndexOf(".");
                    String fileExtension = filePath.substring(lastIndex,
                            filePath.length());

                    // approve selection if it is a .csv file
                    if (fileExtension.equals(".csv")) {
                        super.approveSelection();
                    } else {
                        // do nothing - that selection should not be approved
                        //TODO: Message Box here
                        this.cancelSelection();
                    }

                }
            }
        };
        
        int choice = fileChooser.showOpenDialog(null);
        if (choice == JFileChooser.APPROVE_OPTION) {
            String chosenFilePath = fileChooser.getSelectedFile().getAbsolutePath();
            openedFilePath = chosenFilePath;
            importCSV(chosenFilePath);
        }
        
        //if nothing was loaded do not try to do math channels
        if(dataMap.isEmpty())
            return;
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
    }//GEN-LAST:event_newImportMenuItemActionPerformed

    private void newVehicleMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newVehicleMenuItemActionPerformed
        //open vehicle data dialog
        new VehicleDataDialog(vehicleData, "Create").setVisible(true);
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
        new VehicleDataDialog(vehicleData, "Apply").setVisible(true);
    }//GEN-LAST:event_editVehicleMenuItemActionPerformed

    private void saveVehicleMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveVehicleMenuItemActionPerformed
        //save vehicle dynamic data
        saveVehicleData("");
    }//GEN-LAST:event_saveVehicleMenuItemActionPerformed

    private void categoryListKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_categoryListKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_categoryListKeyReleased

    private void histogramMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_histogramMenuItemActionPerformed
        showHistogram();
    }//GEN-LAST:event_histogramMenuItemActionPerformed

    private void saveAsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsMenuItemActionPerformed
        //save file with no known file path. Will force method to open file chooser
        saveFile("");
    }//GEN-LAST:event_saveAsMenuItemActionPerformed

    private void importVehicleData(String filepath) {
        //create scanner to read file
        Scanner scanner = null;
        try {
            //try to initiate with given filepath
            scanner = new Scanner(new File(filepath));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DataAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
            //TODO:Message Box
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
    
    //given a chart title or dataList title we can create the tag
    private String titleToTag(String title) {
        String[] split = title.split(" ");
        if(split.length == 3) {
            return split[2] + "," + split[0];
        }
        return "";
    }

    public void importCSV(String filepath) {
        
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
                        if(tag.contains("Time"))
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
            //TODO: Message Box here
        }
        
    }
    
    public void hashMapToCSV(ArrayList<String> tags)
    {
        try {
            // Creates a new csv file to put data into. File is located within 'DataAnalyzer' git folder
            FileOutputStream csv = new FileOutputStream(new File("sample.csv"), true);
            // Allows program to print/write data into file
            PrintWriter pw = new PrintWriter(csv);
            // Allows to change dataset within LinkedList 'dataMap' 
            int count = 0;
            
            // Loop continues based on total number of tags in array 'tags' from importCSV
            for (int i = 0; i < tags.size(); i++){
                // Gets tag for dataset from array 'tags' in importCSV
                String tag = tags.get(count);
                // Creates array of SimpleLogObject that only includes data from 'dataMap' under 'tag'
                ArrayList<SimpleLogObject> data = new ArrayList(dataMap.getList(tag));
                // Prints 'tag' before data is printed
                pw.println(tag);
                
                // Loop that prints data under 'tag' on separate lines
                for (int x = 0; x < data.size(); x++){
                    // Allows for data to be split by comma for placement in csv 
                    final String DELIMITER = ",";
                    // Splits data by commas to be printed into csv file
                    String[] obj = ((data.get(x)).toString()).split(DELIMITER);
                    // Prints each piece of data to a unique cell on one line
                    pw.println(obj[0] + "," + obj[1]);
                    // Sends single data line to print in file
                    pw.flush();
                }
                // Allows for next dataset under the next tag to be extracted and printer
                count++;
            }
            
            System.out.println ("File sample.csv has been created" );
            
        } catch (IOException x) {
            
        }
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
        String toReturn = "";
        
        //for each tag of data
        for(String tag : dataMap.tags) {
            //output the tag
            toReturn += tag + "\n";
            //get the list of data for the current tag
            List<LogObject> data = dataMap.getList(tag);
            //for each data element
            for(LogObject lo : data) {
                //output the data
                toReturn += lo.toString() + "\n";
            }
            //output MARKERS
            toReturn += "MARKERS\n";
            //get the markers for the current tag
            List<CategorizedValueMarker> markers = staticMarkers.getList(tag);
            //if the markers exist
            if(markers != null) {
                //for each marker we have output it
                for(CategorizedValueMarker marker : markers) {
                    toReturn += marker.getMarker().getValue() + "\n";
                }
            }
            
            //output END to signify end of data for this tag.
            toReturn += "END\n";
        }
        
        //return calculated value
        return toReturn;
        
    }
    
    private void fillDataList(ArrayList<String> tags){
        // Use the tags list to get the title for each tag
        titles = new String[tags.size()];

        // Make a list of titles
        // Get (Title)"RPM vs Time" from (Tag)"Time, RPM"
        String str = "";
        for (int i = 0; i < titles.length; i++) {
            str = "";
            str += tags.get(i).split(",")[1];
            str += " vs ";
            str += tags.get(i).split(",")[0];
            titles[i] = str;
        }
        // Add the list of titles to the data List View 
        dataList.setListData(titles);

        // If another item is selected in the data combo box, change the chart
        dataList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent arg0) {
                if (!arg0.getValueIsAdjusting()) {
                    // Passes the data type index, all the laps currently selected, and the data type name
                    if(dataList.getSelectedIndex() != -1)
                        setChart(tags.get(dataList.getSelectedIndex()), lapList.getSelectedIndices(), dataList.getSelectedValue());
                }
            }
        });

        // If a different or another lap is selected, change the graph accordingly
        lapList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent arg0) {
                if (!arg0.getValueIsAdjusting()) {
                    // Passes the data type index, all the laps currently selected, and the data type name
                    setChart(tags.get(dataList.getSelectedIndex()), lapList.getSelectedIndices(), dataList.getSelectedValue());
                }
            }
        });
    }
    
    //updates the statistics panel
    private void updateStatistics(String tag) {
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
        
        //set the text values, format to two decimal places
        averageText.setText(String.format("%.2f", avg));
        maxText.setText(String.format("%.2f", max));
        minText.setText(String.format("%.2f", min));
    }
    
    //save file
    private void saveFile(String filename) {
        //add normal data
        StringBuilder sb = new StringBuilder(getStringOfData());
        //append vehicle dynamic data
        sb.append("VEHICLEDYNAMICDATA");
        sb.append("\n");
        sb.append(vehicleData.getStringOfData());
        
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
                    //exception handling
                    } catch (IOException e) {
                        //TODO: Message Box here
                        System.out.println(e.getMessage());
                    }
                }
                else {
                    //try to open a file writer
                    try(FileWriter fw = new FileWriter(chooser.getSelectedFile())) {
                        //write the data
                        fw.write(sb.toString());
                        //close the file writer
                        fw.close();
                    //exception handling
                    } catch (IOException e) {
                        //TODO: Message Box here
                        System.out.println(e.getMessage());
                    }
                }
            } else {
                //TODO: Message Box here
            }
            
        } else { //if a filename was already provided
            //try to write the file
            try(FileWriter fw = new FileWriter(new File(filename))) {
                fw.write(sb.toString());
                fw.close();
            } catch (IOException e) {
                //TODO: Message Box here
            }
        }
    }
    
    //open file
    private void openFile(String filepath) {
        //Scanner to handle the file
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(filepath));
        } catch(FileNotFoundException e) {
            //TODO: Message Box here
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
                    ValueMarker v = new ValueMarker(Double.parseDouble(line));
                    v.setPaint(Color.BLUE);
                    staticMarkers.put(new CategorizedValueMarker(tag, v));
                }
            }
        }
        
        //string builder for creating string of data
        StringBuilder vd = new StringBuilder("");
        while(scanner.hasNextLine()) {
            //append the next line followed by a new line char
            vd.append(scanner.nextLine());
            vd.append("\n");
        }
        
        //give the data to the vehicleData class to create
        vehicleData.applyVehicleData(vd.toString());
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem addMathChannelButton;
    private javax.swing.JLabel averageText;
    private javax.swing.JList<String> categoryList;
    private javax.swing.JInternalFrame chartFrame;
    private javax.swing.JList<String> dataList;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem editVehicleMenuItem;
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
    private javax.swing.JMenuItem openCSVBtn;
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
    
    /**
     * Class that holds CategorizedValueMarkers
     * Essentially just ValueMarkers and a String that defines which category they belong to
     */
    private class CategorizedValueMarker implements CategoricalHashTableInterface {
        String TAG;
        ValueMarker marker;

        public CategorizedValueMarker() {
            TAG = "";
            marker = null;
        }

        public CategorizedValueMarker(String TAG, ValueMarker marker) {
            this.TAG = TAG;
            this.marker = marker;
        }

        public String getTAG() {
            return TAG;
        }

        public void setTAG(String TAG) {
            this.TAG = TAG;
        }

        public ValueMarker getMarker() {
            return marker;
        }

        public void setMarker(ValueMarker marker) {
            this.marker = marker;
        }

        @Override
        public String hashTag() {
            return TAG;
        }
    }
    
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
        
        public void addTag(String elem) {
            TAG.add(elem);
        }
    }
}
