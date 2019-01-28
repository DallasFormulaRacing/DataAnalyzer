/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer;

import com.arib.categoricalhashtable.*;
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
import javax.swing.JFileChooser;
import javax.swing.JList;
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
        new AnalysisCategory("Brakes").addTag("Time,BrakePressureFront").addTag("Time,BrakePressureRear").addTag("Time,AccelX").addTag("Time,AccelY").addTag("Time,AccelZ"),
        new AnalysisCategory("Brake Balance").addTag("Time,BrakePressureFront").addTag("Time,BrakePressureRear"),
        new AnalysisCategory("Coolant").addTag("Time,Coolant").addTag("Time,RadiatorInlet"), 
        new AnalysisCategory("Acceleration").addTag("Time,AccelX").addTag("Time,AccelY").addTag("Time,AccelZ").addTag("Time,RPM").addTag("Time,WheelspeedFront").addTag("Time,WheelspeedRear"),
        new AnalysisCategory("Endurance"), 
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
        
        //extend the statistics panel to the edge
        //get the screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        //get xCordinate of the statisticsPanel
        statisticsPanel.setSize(screenSize.width - statisticsPanel.getX(), statisticsPanel.getHeight());
        
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
        String[] titleSplit = title.split(" vs ");
        String[] tags = new String[titleSplit.length - 1];
        for (int i = 0; i < titleSplit.length - 1; i++) {
            tags[i] = titleSplit[titleSplit.length - 1] + "," + titleSplit[i];
        }
        //update laps
        XYSeriesCollection data = getHistogramDataCollection(tags, lapList.getSelectedIndices());
        
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
        updateStatistics(tags);
        
    }

    // Displays the data for all selected data types
    private void setChart(String[] tags, int[] laps) {

        // Gets the specific data based on what kind of data we want to show for which 
        XYSeriesCollection[] seriesCollection = new XYSeriesCollection[tags.length];
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
        // Get the list of data elements based on the tag
        LinkedList<LogObject> data = dataMap.getList(tag);
        
        // Declare the series to add the data elements to
        final XYSeries series = new XYSeries(tag.split(",")[1]);
        
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
    
    private XYSeriesCollection getHistogramDataCollection(String[] tags, int[] laps) {
        //collection to return
        final XYSeriesCollection graphData = new XYSeriesCollection();
        
        for(String tag : tags) {
        //get data from dataset
            LinkedList<LogObject> data = dataMap.getList(tag);
            //series that will hold the data
            XYSeries series = new XYSeries(tag.split(",")[1]);

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
        }
        return graphData;
        
    }

    // When the chart is clicked
    @Override
    public void chartMouseClicked(ChartMouseEvent cme) {
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
       
        
        //call the method to draw the markers
        drawMarkers(titleToTag(), plot);
        

        // String object that holds values for all the series on the plot.
        String yCordss = "";
        // Repeat the loop for each series in the plot
        for (int i = 0; i < plot.getDatasetCount(); i++) {
            //get current data set
            XYSeriesCollection col = (XYSeriesCollection) plot.getDataset(i);
            // Get the y value for the current series.
            double val = DatasetUtilities.findYValue(col, 0, xCor);
            // Add the value to the string
            yCordss += String.format("%.2f", val) + ", ";

        }
        
        yCordss = yCordss.substring(0, yCordss.length() - 2);

        // Set the textviews at the bottom of the file.
        xCordLabel.setText(String.format("%.2f", xCor));
        yCordLabel.setText(yCordss);

        // Set this objects crosshair data to the value we have
        this.xCrosshair.setValue(xCor);
        this.yCrosshair.setValue(yCor);
    }
    
    private void drawMarkers(String[] tags, XYPlot plot) {
        ArrayList<String> markerList = new ArrayList<>();
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
                    //insert the marker data into current index
                    markerList.add("(" + String.format("%.2f", markers.get(k).getMarker().getValue()) + ", " +
                            String.format("%.2f", DatasetUtilities.findYValue(col,0,markers.get(k).getMarker().getValue())) + 
                            ") " + v.getNotes());
                    k++;
                }

            }
            //move to next dataset
            count++;
        }
        
        if(markerList.isEmpty()) {
            staticMarkersList.setListData(new String[0]);
        } else {
            staticMarkersList.setListData(markerList.toArray(new String[markerList.size()]));
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
        openBtn = new javax.swing.JMenuItem();
        saveMenuButton = new javax.swing.JMenuItem();
        saveAsMenuItem = new javax.swing.JMenuItem();
        exportMenuItem = new javax.swing.JMenuItem();
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
                    } else {
                        // display error message - that selection should not be approve
                        new MessageBox("Error: Selection could not be approved").setVisible(true);
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
                        // display error message - that selection should not be approved
                        new MessageBox("Error: Wrong File Type").setVisible(true);
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
                    if(currMarker != null) {
                        markers.add(currMarker);
                    }
                }
            }
            
            //launch notes dialog
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
    
    //dafault parameters for titleToTag
    private String[] titleToTag() {
        return titleToTag("");
    }
    
    //given a chart title or dataList title we can create the tag
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
                    toReturn += marker.getMarker().getValue() + "," + marker.getNotes() + "\n";
                }
            }
            
            //output END to signify end of data for this tag.
            toReturn += "END\n";
            System.out.println("we running");
        }
        
        //return calculated value
        return toReturn;
        
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

        // If another item is selected in the data combo box, change the chart
        dataList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent arg0) {
                if (!arg0.getValueIsAdjusting()) {
                    // Passes the data type index, all the laps currently selected, and the data type name
                    if(dataList.getSelectedIndex() != -1){
                        int[] selected = dataList.getSelectedIndices();
                        String[] tags = new String[selected.length];
                        for(int i = 0; i < tags.length; i++){
                            tags[i] = allTags.get(selected[i]);
                        }
                        setChart(tags, lapList.getSelectedIndices());
                    }
                }
            }
        });

        // If a different or another lap is selected, change the graph accordingly
        lapList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent arg0) {
                if (!arg0.getValueIsAdjusting()) {
                    // Passes the data type index, all the laps currently selected, and the data type name
                    int[] selected = dataList.getSelectedIndices();
                    String[] tags = new String[selected.length];
                    for(int i = 0; i < tags.length; i++){
                        tags[i] = allTags.get(selected[i]);
                    }
                    setChart(tags, lapList.getSelectedIndices());
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
                        //error message displayed
                        new MessageBox("Error: FileWriter could not be opened").setVisible(true);
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
                        //error message displayed
                        new MessageBox("Error: FileWriter could not be opened ").setVisible(true);
                        System.out.println(e.getMessage());
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
            } catch (IOException e) {
                //error message displayed
                new MessageBox("Error: File could not be written to").setVisible(true);
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
            //append the next line followed by a new line char
            vd.append(scanner.nextLine());
            vd.append("\n");
        }
        
        //give the data to the vehicleData class to create
        vehicleData.applyVehicleData(vd.toString());
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem addMathChannelButton;
    private javax.swing.JLabel averageText;
    private javax.swing.JList<String> categoryList;
    private javax.swing.JInternalFrame chartFrame;
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
