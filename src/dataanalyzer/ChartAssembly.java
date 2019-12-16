/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer;

import dataanalyzer.DataAnalyzer.Theme;
import dataanalyzer.dialog.ApplyFilteringDialog;
import dataanalyzer.dialog.LapDataDialog;
import dataanalyzer.dialog.StaticMarkersFrame;
import dataanalyzer.dialog.StatisticsFrame;
import dataanalyzer.dialog.TagChooserDialog;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.statistics.SimpleHistogramBin;
import org.jfree.data.statistics.SimpleHistogramDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.graphics2d.Args;
import org.jfree.ui.RectangleEdge;
import sun.java2d.pipe.SpanShapeRenderer;

/**
 * 
 * @author aribdhuka
 */
public class ChartAssembly implements ChartMouseListener {
    
    //parent class to access, lapbreaker, staticmarkers, things that belong to all charts
    private ChartManager manager;
    
    //physical componenets
    ChartPanel chartPanel;
    JInternalFrame chartFrame;
    MyCrosshairOverlay overlay;
    
    //variables neccessary
    // X and Y crosshairs
    Crosshair xCrosshair;
    ArrayList<Crosshair> yCrosshairs;
    
    //current selected tags and laps
    String[] selectedTags;
    int[] selectedLaps;
    
    //boolean holding if a histogram is currently being shown
    boolean showingHistogram;
    
    ChartTheme currentTheme;
    
    public ChartAssembly(ChartManager manager) {        
        this.manager = manager;
        selectedTags = new String[1];
        selectedLaps = new int[1];
        showingHistogram = false;
        chartPanel = null;
        chartFrame = new ChartFrame();
        chartFrame.setSize(new Dimension(800,600));
        chartFrame.setResizable(true);
        //set necessary theme
        if(((DataAnalyzer)this.manager.getParentFrame()).currTheme == Theme.DARK) {
            currentTheme = StandardChartTheme.createDarknessTheme();
        } else {
            StandardChartTheme temp = new StandardChartTheme("JFree");
            temp.setPlotBackgroundPaint(Color.WHITE);
            temp.setDomainGridlinePaint(Color.GRAY);
            temp.setRangeGridlinePaint(Color.GRAY);
            currentTheme = temp;   
        }
        addMenuBar();
        showEmptyGraph();
        createOverlay();
    }
    
    /**
     * loads and creates an empty chart panel
     * adds the panel to the frame,
     */
    private void showEmptyGraph() {
        final XYSeriesCollection data = new XYSeriesCollection();

        // Add values of ( Age, Happiness)
        final XYSeries series = new XYSeries("Me");
        Random rand = new Random();
        series.add(0, rand.nextInt(100));
        series.add(5, rand.nextInt(100));
        series.add(10, rand.nextInt(100));
        series.add(16, rand.nextInt(100));
        series.add(18, rand.nextInt(100));
        series.add(20, rand.nextInt(100));
        series.add(22, rand.nextInt(100));
        series.add(25, rand.nextInt(100));
        series.add(30, rand.nextInt(100));
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
        
        //apply the current theme
        currentTheme.apply(chart);

        // Instantiate chart panel object from the object created from ChartFactory
        chartPanel = new ChartPanel(chart);
        // Set the size of the panel
        chartPanel.setSize(new java.awt.Dimension(899, 589));
        //add menu items
        addPopUpMenuItems(chartPanel);
        // Mouse listener
        chartPanel.addChartMouseListener(this);

        // The form has a subframe inside the mainframe
        // Set the subframe's content to be the chartpanel
        chartFrame.setContentPane(chartPanel); 
        chartFrame.setVisible(true);
    }
    
    private void addMenuBar() {
        //830
        JMenuBar frameMenuBar = new JMenuBar();
        //create data menuitem for user to choose data in this chart.
        JMenuItem data = new JMenuItem("Choose Data");
        data.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO: OPEN DIALOG WITH TAGS, CHOOSE TAGS/LAPS, THEN APPLY.
                ArrayList<String> tags = new ArrayList<>();
                ArrayList<Integer> laps = new ArrayList<>();
                TagChooserDialog tcd = new TagChooserDialog(manager.getParentFrame(),
                        manager.getDataMap(), manager.getLapBreaker(), 
                        manager.getStaticMarkers(), tags, laps);
                tcd.setVisible(true);
                
                //wait for the dialog to finish running
                while(tcd.isRunning()) {
                    try {
                        Thread.currentThread().sleep(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(DataAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                //set the chart with new params
                if(!tags.isEmpty() && tags.get(0) != null && !tags.get(0).equals("EVENTCANCELLED")) {
                    selectedTags = new String[tags.size()];
                    for(int i = 0; i < tags.size(); i++) {
                            selectedTags[i] = tags.get(i);
                        }
                    if(!laps.isEmpty()) {
                        selectedLaps = new int[laps.size()];
                        for(int i = 0; i < laps.size(); i++) {
                            selectedLaps[i] = laps.get(i);
                        }
                    } else {
                        selectedLaps = null;
                    }
                    System.out.printf("Tags: %s Laps: %s", Arrays.toString(selectedTags), Arrays.toString(selectedLaps));
                    setChart(selectedTags, selectedLaps);
                }
            }
        });
        data.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SPACE, java.awt.event.InputEvent.ALT_MASK));
        
        //create histogram menuitem to filter current chart
        JMenuItem histogram = new JMenuItem("Histogram");
        histogram.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!showingHistogram)
                    showHistogram();
                else
                    setChart(selectedTags, selectedLaps);
            }
        });
        histogram.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.ALT_MASK));
        
        //create filtering menu to filter current chart
        JMenuItem filtering = new JMenuItem("Filtering");
        filtering.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //dialog for bucket size
                ReturnCode rc = new ReturnCode();
                ApplyFilteringDialog afd = new ApplyFilteringDialog(manager.getParentFrame(), true, rc);
                afd.setVisible(true);
                while(rc.getCode() == 0) {
                    try {
                        Thread.currentThread().sleep(100);
                    } catch(InterruptedException ex) {
                        Logger.getLogger(DataAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                //apply filtering
                if(rc.getCode() != -1) {
                    setChart(titleToTag(), selectedLaps, rc.getCode());
                }
            }
        });
        filtering.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.ALT_MASK));
        
        //create static markers menu item
        JMenuItem markers = new JMenuItem("Static Markers");
        markers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Launch mini window that shows all static markers for this tag
                StaticMarkersFrame frame = new StaticMarkersFrame(manager.getDataMap(), selectedTags, manager.getStaticMarkers(), manager.getParentFrame(), true);
                frame.setVisible(true);
                drawMarkers(selectedTags, chartPanel.getChart().getXYPlot());
            }
        });
        markers.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.ALT_MASK));

        //create static markers menu item
        JMenuItem statistics = new JMenuItem("Statistics");
        statistics.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new StatisticsFrame(manager.getDataMap(), selectedTags, selectedLaps).setVisible(true);
            }
        });
        statistics.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_MASK));
        
        //add created menu items to menu for frame
        frameMenuBar.add(data);
        frameMenuBar.add(histogram);
        frameMenuBar.add(filtering);
        frameMenuBar.add(markers);
        frameMenuBar.add(statistics);
        
        //set created menu to frame
        chartFrame.setJMenuBar(frameMenuBar);
    }
    
    /**
    * changes the popup menu for a given chart
    * @param chart Chart that owns popupmenu
    */
    private void addPopUpMenuItems(ChartPanel chart) {
        JPopupMenu menu = chart.getPopupMenu();
        //create data menuitem for user to choose data in this chart.
        JMenuItem data = new JMenuItem("Choose Data");
        data.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO: OPEN DIALOG WITH TAGS, CHOOSE TAGS/LAPS, THEN APPLY.
                ArrayList<String> tags = new ArrayList<>();
                ArrayList<Integer> laps = new ArrayList<>();
                TagChooserDialog tcd = new TagChooserDialog(manager.getParentFrame(),
                        manager.getDataMap(), manager.getLapBreaker(), 
                        manager.getStaticMarkers(), tags, laps);
                tcd.setVisible(true);
                
                //wait for the dialog to finish running
                while(tcd.isRunning()) {
                    try {
                        Thread.currentThread().sleep(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(DataAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                //set the chart with new params
                if(!tags.isEmpty() && tags.get(0) != null && !tags.get(0).equals("EVENTCANCELLED")) {
                    selectedTags = new String[tags.size()];
                    for(int i = 0; i < tags.size(); i++) {
                            selectedTags[i] = tags.get(i);
                        }
                    if(!laps.isEmpty()) {
                        selectedLaps = new int[laps.size()];
                        for(int i = 0; i < laps.size(); i++) {
                            selectedLaps[i] = laps.get(i);
                        }
                    } else {
                        selectedLaps = null;
                    }
                    System.out.printf("Tags: %s Laps: %s", Arrays.toString(selectedTags), Arrays.toString(selectedLaps));
                    setChart(selectedTags, selectedLaps);
                }
            }
        });
        data.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SPACE, java.awt.event.InputEvent.ALT_MASK));
        
        //create histogram menuitem to filter current chart
        JMenuItem histogram = new JMenuItem("Toggle Histogram");
        histogram.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!showingHistogram)
                    showHistogram();
                else
                    setChart(selectedTags, selectedLaps);
            }
        });
        histogram.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.ALT_MASK));
        
        //create filtering menu to filter current chart
        JMenuItem filtering = new JMenuItem("Apply Filtering");
        filtering.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //dialog for bucket size
                ReturnCode rc = new ReturnCode();
                ApplyFilteringDialog afd = new ApplyFilteringDialog(manager.getParentFrame(), true, rc);
                afd.setVisible(true);
                while(rc.getCode() == 0) {
                    try {
                        Thread.currentThread().sleep(100);
                    } catch(InterruptedException ex) {
                        Logger.getLogger(DataAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                //apply filtering
                if(rc.getCode() != -1) {
                    setChart(titleToTag(), selectedLaps, rc.getCode());
                }
            }
        });
        filtering.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.ALT_MASK));
        
        //create static markers menu item
        JMenuItem markers = new JMenuItem("Show Static Markers");
        markers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Launch mini window that shows all static markers for this tag
                StaticMarkersFrame frame = new StaticMarkersFrame(manager.getDataMap(), selectedTags, manager.getStaticMarkers(), manager.getParentFrame(), true);
                frame.setVisible(true);
                drawMarkers(selectedTags, chartPanel.getChart().getXYPlot());
            }
        });
        markers.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.ALT_MASK));

        //create static markers menu item
        JMenuItem statistics = new JMenuItem("Show Statistics");
        statistics.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new StatisticsFrame(manager.getDataMap(), selectedTags, selectedLaps).setVisible(true);
            }
        });
        statistics.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_MASK));

        //add created menu items to current menu
        menu.add(data);
        menu.add(histogram);
        menu.add(filtering);
        menu.add(markers);
        menu.add(statistics);
        
        //set new popup menu to chart
        chart.setPopupMenu(menu);
    }
    
    private void createOverlay() {
        // Create the global object crosshairs
        yCrosshairs = new ArrayList<>();
        overlay = new MyCrosshairOverlay(this);
        this.xCrosshair = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
        this.xCrosshair.setLabelVisible(true);
        overlay.addDomainCrosshair(xCrosshair);
        Crosshair yCrosshair = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
        yCrosshair.setLabelVisible(true);
        overlay.addRangeCrosshair(yCrosshair);
        yCrosshairs.add(yCrosshair);
        chartPanel.addOverlay(overlay);
    }
    
    // Displays the data for all selected data types
    protected void setChart(String[] tags, int[] laps) {

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
        
        //reset overlays
        overlay = new MyCrosshairOverlay(this);
        overlay.rangeMarkersActive = ((DataAnalyzer) manager.getParentFrame()).rangeMarkersActive;
        this.xCrosshair = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
        this.xCrosshair.setLabelVisible(true);
        overlay.addDomainCrosshair(xCrosshair);
        yCrosshairs.clear();
        
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
            //add a crosshair for this tag
            Crosshair yCrosshair = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
            yCrosshair.setLabelVisible(true);
            overlay.addRangeCrosshair(yCrosshair);
            yCrosshairs.add(yCrosshair);
        }
        
        // Just show the X-Axis data type (Time, Distance, etc)
        plot.setDomainAxis(new NumberAxis(tags[0].split(",")[0]));

        // Create a new JFreeChart with the XYPlot
        JFreeChart chart = new JFreeChart(title, chartFrame.getFont(), plot, true);
        chart.setBackgroundPaint(Color.WHITE);
        
        //only apply the theme if its dark
        currentTheme.apply(chart);
        
        // Instantiate chart panel object from the object created from ChartFactory
        chartPanel = new ChartPanel(chart);
        chartPanel.addOverlay(overlay);
        // Set the size of the panel
        chartPanel.setSize(new java.awt.Dimension(800, 600));
        //add menu items
        addPopUpMenuItems(chartPanel);
        // Mouse listener
        chartPanel.addChartMouseListener(this);
        // The form has a subframe inside the mainframe
        // Set the subframe's content to be the chartpanel
        chartFrame.setContentPane(chartPanel);
        
        //draw markers
        drawMarkers(tags, chart.getXYPlot());
        
        //declare that we are not showing a histogram
        showingHistogram = false;
        
        //set menu item to say histogram
        ((JMenuItem) chartFrame.getJMenuBar().getComponent(1)).setText("Histogram");
    }

    // Displays the data for all selected data types
    protected void setChart(String[] tags, int[] laps, int bucketSize) {

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
            seriesCollection[i] = getDataCollection(tags[i], laps, bucketSize);
            title += tags[i].split(",")[1] + " vs ";
        }
        
        //add domain
        title += tags[0].split(",")[0];
        
        //reset overlays
        overlay = new MyCrosshairOverlay(this);
        this.xCrosshair = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
        this.xCrosshair.setLabelVisible(true);
        overlay.addDomainCrosshair(xCrosshair);
        yCrosshairs.clear();
        
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
            //add a crosshair for this tag
            Crosshair yCrosshair = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
            yCrosshair.setLabelVisible(true);
            overlay.addRangeCrosshair(yCrosshair);
            yCrosshairs.add(yCrosshair);
        }
        
        // Just show the X-Axis data type (Time, Distance, etc)
        plot.setDomainAxis(new NumberAxis(tags[0].split(",")[0]));

        // Create a new JFreeChart with the XYPlot
        JFreeChart chart = new JFreeChart(title, chartFrame.getFont(), plot, true);
        chart.setBackgroundPaint(Color.WHITE);
        
        //apply current theme
        currentTheme.apply(chart);
        
        // Instantiate chart panel object from the object created from ChartFactory
        chartPanel = new ChartPanel(chart);
        chartPanel.addOverlay(overlay);
        // Set the size of the panel
        chartPanel.setSize(new java.awt.Dimension(800, 600));
        //add menu items
        addPopUpMenuItems(chartPanel);
        // Mouse listener
        chartPanel.addChartMouseListener(this);

        // The form has a subframe inside the mainframe
        // Set the subframe's content to be the chartpanel
        chartFrame.setContentPane(chartPanel);
        
        //draw markers
        drawMarkers(tags, chart.getXYPlot());
        
        //declare that we are not showing a histogram
        showingHistogram = false;
        
        //set menu item to say histogram
        ((JMenuItem) chartFrame.getJMenuBar().getComponent(1)).setText("Histogram");
    }
    
    /**
     * Creates a custom histogram that uses a interval dataset
     * @param title Title of the graph
     * @param xAxisLabel label for x axis
     * @param yAxisLabel label for y axis
     * @param dataset The Interval dataset
     * @param orientation orientation of the graph
     * @param legend show legend
     * @param tooltips show tooltips
     * @param urls show urls
     * @return JFreeChart object with data parameters
     */
    public static JFreeChart createMyHistogram(String title,
            String xAxisLabel, String yAxisLabel, IntervalXYDataset dataset,
            PlotOrientation orientation, boolean legend, boolean tooltips,
            boolean urls) {

        //ensure an orientation is given
        Args.nullNotPermitted(orientation, "orientation");
        //apply labels
        NumberAxis xAxis = new NumberAxis(xAxisLabel);
        xAxis.setAutoRangeIncludesZero(false);
        ValueAxis yAxis = new NumberAxis(yAxisLabel);

        //create the item renderer
        XYItemRenderer renderer = new XYBarRenderer(-10);
        if (urls) {
            renderer.setURLGenerator(new StandardXYURLGenerator());
        }

        //create the plot
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
        plot.setOrientation(orientation);
        plot.setDomainZeroBaselineVisible(true);
        plot.setRangeZeroBaselineVisible(true);
        //create the chart
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                plot, legend);
        return chart;

    }
    
    private void showHistogram() {
        Dimension currSize = chartPanel.getSize();
        String title = chartPanel.getChart().getTitle().getText();
        String[] titleSplit = title.split(" vs ");
        String[] tags = new String[titleSplit.length - 1];
        for (int i = 0; i < titleSplit.length - 1; i++) {
            tags[i] = titleSplit[titleSplit.length - 1] + "," + titleSplit[i];
        }
        //update laps
        SimpleHistogramDataset data = getHistogramDataCollection(tags, selectedLaps);
        
        // Gets the independent variable from the title of the data
        String yAxis = "Milliseconds";
        // Gets the dependent variable from the title of the data
        String xAxis = title.split(" vs ")[0];  //split title by vs, we get ["RPM", "Time"] or something like that
        
        //creates a custom histogram
        JFreeChart chart = createMyHistogram(title, xAxis, yAxis, data, PlotOrientation.VERTICAL, true, true, false);
        
        //apply current theme
        currentTheme.apply(chart);

        //apply histogram to chart panel
        chartPanel = new ChartPanel(chart);
        
        //add pop up menu items so we can exit
        addPopUpMenuItems(chartPanel);
        
        //set size
        chartPanel.setSize(currSize);
        
        //set frame content
        chartFrame.setContentPane(chartPanel);
        
        //declare that we are showing a histogram
        showingHistogram = true;
        
        //set menu item to say chart
        ((JMenuItem) chartFrame.getJMenuBar().getComponent(1)).setText("Chart");
    }
    
    //draw the static markers on the screen
    private void drawMarkers(String[] tags, XYPlot plot) {
        plot.clearDomainMarkers();
        //which dataset we are on
        int count = 0;
        for(String tag : tags) {
            //get the linked list from tag
            LinkedList<CategorizedValueMarker> markers = manager.getStaticMarkers().getList(tag);
            //if the linked list is not null
            if(markers != null) {
                //draw every domain marker saved for this chart and add it to an array
                for(CategorizedValueMarker v : markers) {
                    v.getMarker().setPaint(getColorFromIndex(count));
                    plot.addDomainMarker(v.getMarker());
                }

            }
            //move to next dataset
            count++;
        }      
        
    }
     
    // Creates a new render for a new series or data type. Gives a new color
    private XYSplineRenderer getNewRenderer(int index) {
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
                sx.setSeriesFillPaint(index, Color.CYAN); //TODO: CHECK IF THIS FIXED THIS SHITTY YELLOW PIECE OF SHIT COLOR
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
    
    /**
     * Generates a XYSeriesCollection based on a list retrieved from a tag
     * @param tag the tag of the dataset, used to get the data from the CategoricalHashMap
     * @param laps the laps the user wants to see
     * @return 
     */
    private XYSeriesCollection getDataCollection(String tag, int[] laps) {

        // XY Series Collection allows there to be multiple data lines on the graph
        XYSeriesCollection graphData = new XYSeriesCollection();

        //if laps were not provided show whole dataset
        if(laps == null || laps.length == 0) {
            // Get the list of data elements based on the tag
            LinkedList<LogObject> data = manager.getDataMap().getList(tag);

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
                for(Lap lap : manager.getLapBreaker()) {
                    if(lap.lapNumber == laps[i]) {
                        currLap = lap;
                    }
                }
                //create a series with the tag and lap #
                XYSeries series = new XYSeries(tag.split(",")[1] + "Lap " + laps[i]);
                //if its a base dataset
                if(tag.contains("Time,")) {
                    //for each log object if the log object belongs in this lap add it to the series
                    for(LogObject lo : manager.getDataMap().getList(tag)) {
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
                    for(LogObject lo : manager.getDataMap().getList(tag)) {
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
    
    private XYSeriesCollection getDataCollection(String tag, int[] laps, int bucketSize) {

        // XY Series Collection allows there to be multiple data lines on the graph
        XYSeriesCollection graphData = new XYSeriesCollection();

        //if laps were not provided show whole dataset
        if(laps == null || laps.length == 0) {
            // Get the list of data elements based on the tag
            LinkedList<LogObject> dataLinked = manager.getDataMap().getList(tag);
            //copy to a arraylist for a better runtime letter
            ArrayList<LogObject> data = new ArrayList<>(dataLinked.size());
            for(LogObject lo : dataLinked) {
                data.add(lo);
            }

            // Declare the series to add the data elements to
            final XYSeries series = new XYSeries(tag.split(",")[1]);

            //if tag contains time then its not a function of another dataset
            if(tag.contains("Time,")) {
                // We could make a XYSeries Array if we wanted to show different lap data
                // final XYSeries[] series = new XYSeries[laps.length];  <--- if we wanted to show different laps at the same time
                // Iterate through each data element in the received dataMap LinkedList
                
                //for each element
                for(int i = 0; i < data.size(); i++) {
                    //modifes the index
                    int modifier = ((bucketSize - 1) / 2) * -1;
                    //holds current avg
                    double avg = 0;
                    //x to add to series
                    long x = 0;
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
                            x = Long.parseLong(data.get(i + modifier).toString().split(",")[0]);
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
            } else {
                // We could make a XYSeries Array if we wanted to show different lap data
                // final XYSeries[] series = new XYSeries[laps.length];  <--- if we wanted to show different laps at the same time
                // Iterate through each data element in the received dataMap LinkedList
                
                //for each element
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
            }
            //add series to collection
            graphData.addSeries(series);
        } else { //if lap data was provided
            // Get the list of data elements based on the tag
            LinkedList<LogObject> rawdata = manager.getDataMap().getList(tag);
            //for each lap
            for(int l = 0; l < laps.length; l++) {
                Lap currLap = new Lap(0, 0);
                for(Lap lap : manager.getLapBreaker()) {
                    if(lap.lapNumber == laps[l]) {
                        currLap = lap;
                    }
                }
                
                //create a dataset of items only of the current lap.
                LinkedList<LogObject> data = new LinkedList<>();
                for(LogObject lo : rawdata) {
                    if(lo.laps.contains(laps[l]))
                        data.add(lo);
                }
                //create a series with the tag and lap #
                XYSeries series = new XYSeries(tag.split(",")[1] + "Lap " + laps[l]);
                //if its a base dataset
                if(tag.contains("Time,")) {
                    //for each log object if the log object belongs in this lap add it to the series
                    for(int i = 0; i < data.size(); i++) {
                        //modifes the index
                        int modifier = ((bucketSize - 1) / 2) * -1;
                        //holds current avg
                        double avg = 0;
                        //x to add to series
                        long x = 0;
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
                                x = Long.parseLong(data.get(i + modifier).toString().split(",")[0]);
                            }
                            modifier++;
                        }

                        if(usedIndecies != 0) {
                            //calculate average
                            avg /= usedIndecies;
                            //add that to series
                            series.add(x - currLap.start, avg);
                        }
                    }
                //else its function of another dataset
                } else {
                    //for each element
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
                            series.add(x - currLap.start, avg);
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
    
    /**
     * 
     * @param tags
     * @param laps
     * @return 
     */
    private SimpleHistogramDataset getHistogramDataCollection(String[] tags, int[] laps) {
        //collection to return
        SimpleHistogramDataset dataset = new SimpleHistogramDataset("time");
        dataset.setAdjustForBinSize(false);

        
        for(String tag : tags) {
        //get data from dataset
            LinkedList<LogObject> data = manager.getDataMap().getList(tag);
            
            //if asked for data with laps, complete for each lap
            if(laps != null) {
                for(int l = 0; l < laps.length; l++) {
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


                    //for each of the 50 intervals
                    for(int i = 1; i < 51; i++) {
                        SimpleHistogramBin bin = new SimpleHistogramBin(interval*(i-1) + min, interval*(i) + min - .000001);


                        //for each data element
                        for(LogObject lo : data) {
                            if(lo.getLaps().contains(laps[l])) {
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
                        }
                        dataset.addBin(bin);
                    }



                }
            } else {
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
                    dataset.addBin(bin);

                }



            }
        }
        return dataset;
        
    }
    
    // When the chart is clicked
    @Override
    public void chartMouseClicked(ChartMouseEvent cme) {
        //if the swap tool is active, notify the manager
        if(manager.swapActive != -1) {
            manager.chartClicked(this);
        }
        //else handle normal chart clicking duties
        else {
            //if the lap breaker hasn't been activiates
            if(manager.getLapBreakerActive() < 0 && !SwingUtilities.isRightMouseButton(cme.getTrigger())) {
                // Create a static cursor that isnt cleared every time
                ValueMarker marker = new ValueMarker(xCrosshair.getValue());
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
                    if(manager.getStaticMarkers().get(new CategorizedValueMarker(tag, marker)) == null)
                        manager.getStaticMarkers().put(new CategorizedValueMarker(tag, marker));
                }

                //draw markers
                drawMarkers(titleToTag(), chartPanel.getChart().getXYPlot());
            } else {
                //if lapbreaker has just started
                if(manager.getLapBreakerActive() == 0) {
                    //get clicked position and set it as start
                    manager.getNewLap().start = getRoundedTime(xCrosshair.getValue());
                    //move to next task
                    manager.setLapBreakerActive(manager.getLapBreakerActive()+1);

                    ValueMarker startMarker = new ValueMarker(manager.getNewLap().start);
                    for(String tag : manager.getDataMap().tags) {
                        if(manager.getStaticMarkers().get(new CategorizedValueMarker(tag, startMarker, "Start Lap" + manager.getNewLap().lapNumber)) == null)
                            manager.getStaticMarkers().put(new CategorizedValueMarker(tag, startMarker, "Start Lap" + manager.getNewLap().lapNumber));
                    }

                    //draw markers
                    drawMarkers(titleToTag(), chartPanel.getChart().getXYPlot());
                //if the start has already been defined
                } else if(manager.getLapBreakerActive() == 1) {
                    //define the next click as a stop
                    manager.getNewLap().stop = getRoundedTime(xCrosshair.getValue());

                    //hold the laps start and stop, so we have the value in case its lost
                    long oldStartTime = manager.getNewLap().start;
                    long oldStopTime = manager.getNewLap().stop;

                    ValueMarker stopMarker = new ValueMarker(manager.getNewLap().stop);

                    //apply marker to all datasets.
                    for(String tag : manager.getDataMap().tags) {
                        //add to the list of static markers
                        if(manager.getStaticMarkers().get(new CategorizedValueMarker(tag, stopMarker, "End Lap" + manager.getNewLap().lapNumber)) == null)
                            manager.getStaticMarkers().put(new CategorizedValueMarker(tag, stopMarker, "End Lap" + manager.getNewLap().lapNumber));
                    }

                    //draw markers
                    drawMarkers(titleToTag(), chartPanel.getChart().getXYPlot());

                    //get the used lap numbers
                    ArrayList<Integer> usedLaps = new ArrayList<>();
                    for(Lap l : manager.getLapBreaker()) {
                        usedLaps.add(l.lapNumber);
                    }
                    //create and run the dialog
                    LapDataDialog ldd = new LapDataDialog(manager.getParentFrame(), true, manager.getNewLap(), usedLaps);
                    ldd.setVisible(true);
                    //while the dialog is running
                    while(ldd.isRunning()) {
                        try {
                            Thread.currentThread().wait(100);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(DataAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                    if(!manager.getNewLap().lapLabel.equals("!#@$LAPCANCELLED")) {
                        //remove old start and stop markers and add new ones with values from text box
                        ValueMarker startMarker = new ValueMarker(manager.getNewLap().start);
                        stopMarker = new ValueMarker(manager.getNewLap().stop);

                        for(String tag : manager.getDataMap().tags) {
                            manager.getStaticMarkers().remove(getMarkerFromDomainValue(tag, oldStartTime));
                            manager.getStaticMarkers().remove(getMarkerFromDomainValue(tag, oldStopTime));
                            if(manager.getStaticMarkers().get(new CategorizedValueMarker(tag, startMarker, "Start Lap" + manager.getNewLap().lapNumber)) == null)
                                manager.getStaticMarkers().put(new CategorizedValueMarker(tag, startMarker, "Start Lap" + manager.getNewLap().lapNumber));
                            if(manager.getStaticMarkers().get(new CategorizedValueMarker(tag, stopMarker, "End Lap" + manager.getNewLap().lapNumber)) == null)
                                manager.getStaticMarkers().put(new CategorizedValueMarker(tag, stopMarker, "End Lap" + manager.getNewLap().lapNumber));
                        }
                        //add that to the list of laps
                        manager.getLapBreaker().add(manager.getNewLap());
                        //apply the lap data to the datasets
                        Lap.applyToDataset(manager.getDataMap(), manager.getLapBreaker());
                        //reset the lapbreaker
                        manager.setLapBreakerActive(-1);

                        //reset the new lap
                        manager.setNewLap(new Lap());

                    } else {
                        //delete previous markers
                        for(String tag : manager.getDataMap().tags) {
                            manager.getStaticMarkers().remove(getMarkerFromDomainValue(tag, oldStartTime));
                            manager.getStaticMarkers().remove(getMarkerFromDomainValue(tag, oldStopTime));
                        }
                        drawMarkers(titleToTag(), chartPanel.getChart().getXYPlot());
                    }
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
        double xCor = xAxis.java2DToValue(cme.getTrigger().getX(), dataArea,
                RectangleEdge.BOTTOM);
        updateOverlay(xCor);
        manager.updateOverlays(xCor, this);
    }
    
    public void updateOverlay(double xCor) {
        XYPlot plot = (XYPlot) chartPanel.getChart().getPlot();
        // Find the y cordinate from the plots data set given a x cordinate and store to list
        ArrayList<Double> yCors = new ArrayList<>();
        String[] titles = titleToTag();
        // Repeat the loop for each series in the plot
        for (int i = 0; i < plot.getDatasetCount(); i++) {
            //get current data set
            XYSeriesCollection col = (XYSeriesCollection) plot.getDataset(i);
            for(int j = 0; j < plot.getSeriesCount(); j++) {
                // Get the y value for the current series.
                double val = DatasetUtilities.findYValue(col, j, xCor);
                // Add the value to the list
                yCors.add(val);
            }
        }

        // Set this objects crosshair data to the value we have
        this.xCrosshair.setValue(xCor);
        int curr = 0;
        for(Crosshair crosshair : yCrosshairs) {
            crosshair.setValue(yCors.get(curr));
            curr++;
        }
    }
    
    /**
     * 
     * @param TAG TAG of the dataset
     * @param s String collected from list
     * @return CategorizedValueMarker object that has the same domain marker as the string
     */
    private CategorizedValueMarker getMarkerFromString(String TAG, String s) {
        for(CategorizedValueMarker marker : manager.getStaticMarkers().getList(TAG)) {
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
        for(CategorizedValueMarker marker : manager.getStaticMarkers().getList(TAG)) {
            if(marker.getMarker().getValue() == domainValue)
                return marker;
        }
        return null;
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
            for(LogObject lo : manager.getDataMap().getList(finding)) {
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
            if(manager.getDataMap().tags.contains("Time," + goTo)) {
                //get the tag for the function of time
                String toSearch = "Time," + goTo;
                //for each logobject of the base function
                for(LogObject lo : manager.getDataMap().getList(toSearch)) {
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

    public ChartPanel getChartPanel() {
        return chartPanel;
    }

    public void setChartPanel(ChartPanel chartPanel) {
        this.chartPanel = chartPanel;
    }

    public JInternalFrame getChartFrame() {
        return chartFrame;
    }

    public void setChartFrame(JInternalFrame chartFrame) {
        this.chartFrame = chartFrame;
    }
    
    /**
     * Applies a new theme manually, which is called when the overall theme is changed
     * @param theme new theme to change to
     */
    public void applyNewTheme(Theme theme) {
        //if its a dark theme, apply jfreechart's dark theme
        if(theme == Theme.DARK) {
            currentTheme = StandardChartTheme.createDarknessTheme();
            currentTheme.apply(chartPanel.getChart());
        } else {
            //else apply its standard light theme
            StandardChartTheme temp = new StandardChartTheme("JFree");
            temp.setPlotBackgroundPaint(Color.WHITE);
            temp.setDomainGridlinePaint(Color.GRAY);
            temp.setRangeGridlinePaint(Color.GRAY);
            currentTheme = temp;
            currentTheme.apply(chartPanel.getChart());
        }
    }

    public MyCrosshairOverlay getOverlay() {
        return overlay;
    }

    public void setOverlay(MyCrosshairOverlay overlay) {
        this.overlay = overlay;
    }
    
    /**
     * Class that overwrites a few aspects of crosshair overlay to keep crosshairs more accessible
     * Overwrites paint to paint the range marker dependent on dataset/number axis
     */
    public class MyCrosshairOverlay extends CrosshairOverlay {
        /*
        The domain and range crosshair lists in superclass are private,
        so we shadow them. See addDomainCrosshair and addRangeCrosshair
        */
        private final List<Crosshair> shadowD, shadowR;
        protected boolean rangeMarkersActive;
        //holds the parent chartassembly
        private final ChartAssembly parent;

        /**
         * @deprecated Please provide a parent
         */
        public MyCrosshairOverlay() {
            super();
            shadowD = new ArrayList<>();
            shadowR = new ArrayList<>();
            rangeMarkersActive = true;
            parent = null;
        }
        
        /**
         * Constructors an overlay object with a parent that it belongs to
         * @param parent 
         */
        public MyCrosshairOverlay(ChartAssembly parent) {
            super();
            this.parent = parent;
            shadowD = new ArrayList<>();
            shadowR = new ArrayList<>();
            rangeMarkersActive = true;
        }
        
        /**
         * @deprecated Please provide a parent
         * @param range provide if range markers are already disabled
         */
        public MyCrosshairOverlay(boolean range) {
            super();
            shadowD = new ArrayList<>();
            shadowR = new ArrayList<>();
            rangeMarkersActive = range;
            parent = null;
        }

        @Override
        public void addDomainCrosshair(Crosshair crosshair) {
            super.addDomainCrosshair( crosshair );
            shadowD.add( crosshair );
        }

        @Override
        public void addRangeCrosshair(Crosshair crosshair) {
            super.addRangeCrosshair( crosshair );
            shadowR.add( crosshair );
        }
        
        public void invertRangeActive() {
            rangeMarkersActive = !rangeMarkersActive;
        }

        /**
        * Paints the crosshairs in the layer.
        *
        * @param g2 the graphics target.
        * @param chartPanel the chart panel.
        */
        @Override
        public void paintOverlay(Graphics2D g2, ChartPanel chartPanel) {
            Shape savedClip = g2.getClip();
            Rectangle2D dataArea = chartPanel.getScreenDataArea();
            g2.clip(dataArea);
            JFreeChart chart = chartPanel.getChart();
            XYPlot plot = (XYPlot) chart.getPlot();
            ValueAxis dAxis = plot.getDomainAxis();
            RectangleEdge dAxisEdge = plot.getDomainAxisEdge();

            for( Crosshair c : shadowD ) {
                if( !c.isVisible() )
                    continue;
                //set the color to white if we transition to dark theme
                if(((DataAnalyzer)this.parent.manager.getParentFrame()).currTheme == Theme.DARK)
                    c.setLabelBackgroundPaint(Color.WHITE);
                double x = c.getValue();
                double xx = dAxis.valueToJava2D(x, dataArea, dAxisEdge);
                if (plot.getOrientation() == PlotOrientation.VERTICAL) {
                    drawVerticalCrosshair(g2, dataArea, xx, c );
                }
                else {
                    drawHorizontalCrosshair(g2, dataArea, xx, c );
                }
            }
            if(rangeMarkersActive) {
                int N = plot.getRangeAxisCount();
                for( int i = 0; i < N; i++ ) {
                    ValueAxis rAxis = plot.getRangeAxis(i);
                    RectangleEdge rAxisEdge = plot.getRangeAxisEdge(i);
                    Crosshair c = shadowR.get(i);
                    if( !c.isVisible() )
                        continue;
                    //set the color to white if we transition to dark theme
                    if(((DataAnalyzer)this.parent.manager.getParentFrame()).currTheme == Theme.DARK)
                        c.setLabelBackgroundPaint(Color.WHITE);
                    double y = c.getValue();
                    double yy = rAxis.valueToJava2D(y, dataArea, rAxisEdge);
                    if (plot.getOrientation() == PlotOrientation.VERTICAL) {
                        drawHorizontalCrosshair(g2, dataArea, yy, c );
                    }
                    else {
                        drawVerticalCrosshair(g2, dataArea, yy, c );
                    }
                }
            }
            g2.setClip(savedClip);
        }
    }

    
}
