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
import javax.swing.JFrame;
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
    
    //Selection for this assembly
    Selection selection;
    
    //physical componenets
    ChartPanel chartPanel;
    JInternalFrame chartFrame;
    MyCrosshairOverlay overlay;
    
    //variables neccessary
    // X and Y crosshairs
    Crosshair xCrosshair;
    ArrayList<Crosshair> yCrosshairs;
    
    //current selected lists and laps
    LinkedList<LinkedList<LogObject>> selectedLists;
    
    //boolean holding if a histogram is currently being shown
    boolean showingHistogram;
    
    ChartTheme currentTheme;
    
    public ChartAssembly(ChartManager manager) {        
        this.manager = manager;
        selection = new Selection();
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
                TagChooserDialog tcd = new TagChooserDialog(manager.getParentFrame(), manager.getDatasets(), selection);
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
                if(!selection.getUniqueTags().isEmpty())
                    setChart(selection.getUniqueTags().toArray(new String[selection.getUniqueTags().size()]));
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
                    setChart(selection.getUniqueTags().toArray(new String[selection.getUniqueTags().size()]));
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
                    setChart(selection.getUniqueTags().toArray(new String[selection.getUniqueTags().size()]), rc.getCode());
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
                StaticMarkersFrame frame = new StaticMarkersFrame(selection.getAllSelectedDatasets(), selection, manager.getParentFrame(), true);
                frame.setVisible(true);
                drawMarkers(chartPanel.getChart().getXYPlot());
            }
        });
        markers.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.ALT_MASK));

        //create static markers menu item
        JMenuItem statistics = new JMenuItem("Statistics");
        statistics.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new StatisticsFrame(selection.getSelectedLists()).setVisible(true);
            }
        });
        statistics.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_MASK));
        
        //create static markers menu item
        JMenuItem extrude = new JMenuItem("Extrude");
        extrude.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame chartJFrame = new JFrame("Chart");
                chartJFrame.setVisible(true);
                chartJFrame.setContentPane(chartPanel);
                chartJFrame.setSize(640, 480);
            }
        });
        extrude.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.ALT_MASK));
        
        //add created menu items to menu for frame
        frameMenuBar.add(data);
        frameMenuBar.add(histogram);
        frameMenuBar.add(filtering);
        frameMenuBar.add(markers);
        frameMenuBar.add(statistics);
        frameMenuBar.add(extrude);
        
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
                TagChooserDialog tcd = new TagChooserDialog(manager.getParentFrame(), manager.getDatasets(), selection);
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
                if(!selection.getUniqueTags().isEmpty())
                    setChart(selection.getUniqueTags().toArray(new String[selection.getUniqueTags().size()]));
                
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
                    setChart(selection.getUniqueTags().toArray(new String[selection.getUniqueTags().size()]));
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
                    setChart(selection.getUniqueTags().toArray(new String[selection.getUniqueTags().size()]), rc.getCode());
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
                StaticMarkersFrame frame = new StaticMarkersFrame(selection.getAllSelectedDatasets(), selection, manager.getParentFrame(), true);
                frame.setVisible(true);
                drawMarkers(chartPanel.getChart().getXYPlot());
            }
        });
        markers.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.ALT_MASK));

        //create static markers menu item
        JMenuItem statistics = new JMenuItem("Show Statistics");
        statistics.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new StatisticsFrame(selection.getSelectedLists()).setVisible(true);
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
    protected void setChart(String[] tags) {

        // Gets the specific data based on what kind of data we want to show for which 
        XYSeriesCollection[] seriesCollection;
        
        String title = "";
        
        // Store data for each data type in different XYSeriesCollection
        // New title for all the Y-Axis labels added together
        for(int i = 0; i < tags.length; i++){
            title += tags[i].split(",")[1] + " vs ";
        }
        
        seriesCollection = selection.getDataCollection();
        
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
            for(int s = 0; s < seriesCollection[i].getSeriesCount(); s++) {
                //add a crosshair for this tag
                Crosshair yCrosshair = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
                yCrosshair.setValue(1000*s);
                yCrosshair.setLabelVisible(true);
                overlay.addRangeCrosshair(yCrosshair);
                yCrosshairs.add(yCrosshair);
            }
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
        drawMarkers(chart.getXYPlot());
        
        //declare that we are not showing a histogram
        showingHistogram = false;
        
        //set menu item to say histogram
        ((JMenuItem) chartFrame.getJMenuBar().getComponent(1)).setText("Histogram");
    }

    // Displays the data for all selected data types
    protected void setChart(String[] tags, int bucketSize) {

        // Gets the specific data based on what kind of data we want to show for which 
        XYSeriesCollection[] seriesCollection;
        
        String title = "";
        
        // Store data for each data type in different XYSeriesCollection
        // New title for all the Y-Axis labels added together
        for(int i = 0; i < tags.length; i++){
            title += tags[i].split(",")[1] + " vs ";
        }
        seriesCollection = selection.getDataCollection(bucketSize);
        
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
            for(int s = 0; s < seriesCollection[i].getSeriesCount(); s++) {
                Crosshair yCrosshair = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
                yCrosshair.setValue(1000*s);
                yCrosshair.setLabelVisible(true);
                overlay.addRangeCrosshair(yCrosshair);
                yCrosshairs.add(yCrosshair);
            }
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
        drawMarkers(chart.getXYPlot());
        
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
        String[] tags = selection.getUniqueTags().toArray(new String[selection.getUniqueTags().size()]);

        if(tags == null || tags[0] == null)
            return;
        //update laps
        SimpleHistogramDataset data = selection.getHistogramDataCollection();
        
        // Gets the independent variable from the title of the data
        String yAxis = tags[0].split(",")[0];
        // Gets the dependent variable from the title of the data
        String xAxis = tags[0].split(",")[1];  //split title by vs, we get ["RPM", "Time"] or something like that
        
        //creates a custom histogram
        JFreeChart chart = createMyHistogram(chartPanel.getChart().getTitle().getText(), xAxis, yAxis, data, PlotOrientation.VERTICAL, true, true, false);

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
    private void drawMarkers(XYPlot plot) {
        plot.clearDomainMarkers();
        //which dataset we are on
        int count = 0;
        //get the linked list from tag
        LinkedList<LinkedList<CategorizedValueMarker>> markerList = selection.getAllMarkers();
        //if the linked list is not null
        if(markerList != null && !markerList.isEmpty()) {
            for(LinkedList<CategorizedValueMarker> markers : markerList) {
                //draw every domain marker saved for this chart and add it to an array
                for(CategorizedValueMarker v : markers) {
                    v.getMarker().setPaint(getColorFromIndex(count));
                    plot.addDomainMarker(v.getMarker());
                }
                //declare we are moving on to the next tag
                count++;
            }
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

    // When the chart is clicked
    @Override
    public void chartMouseClicked(ChartMouseEvent cme) {
        //TODO: check for left click only
        //if the swap tool is active, notify the manager
        if(manager.swapActive != -1) {
            manager.chartClicked(this);
            return;
        }
        
        //notes to apply to the static marker
        String notes = "";
        //value of the static marker
        double xVal = xCrosshair.getValue();
        /**
         * This is how to do domain regulation
         * getDomain()
         * if(domain is not time)
         * convertXToTime()
         * 
         * on every lap when a lap is selected with a domain other than time, convert the time to the domain
         */
        
        //if we are creating a lap
        if(manager.getLapBreakerActive() >= 0) { 
            //if we are doing the start
            if(manager.getLapBreakerActive() == 0) {
                //round start time
                manager.getNewLap().start = (long) xVal;
                //move to next task
                manager.setLapBreakerActive(manager.getLapBreakerActive() + 1);
                //notes that this marker will become
                notes = "Lap Start";
            }
            //if we are doing the second
            else if(manager.getLapBreakerActive() == 1) {
                //get stop position
                manager.getNewLap().stop = (long) xVal;
                
                //launch Lap confirmation dialog
                //create and run the dialog
                LapDataDialog ldd = new LapDataDialog(manager.getParentFrame(), true, manager.getNewLap(), manager.getUsedLapNumbers());
                ldd.setVisible(true);
                //while the dialog is running
                while(ldd.isRunning()) {
                    try {
                        Thread.currentThread().wait(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(DataAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                //if the lap was cancelled
                if(manager.getNewLap().lapLabel.equals("!#@$LAPCANCELLED")) {
                    manager.setLapBreakerActive(-1);
                    manager.setNewLap(new Lap());
                } else {
                    selection.addLap(manager.getNewLap());
                    manager.setLapBreakerActive(-1);
                    manager.setNewLap(new Lap());
                }
            }
        }
        
        //if we are cutting the data
        if(manager.getCutDataActive() >= -1) {
            if(manager.getCutDataActive() == -1)
                manager.setCutDataActive((long) xVal);
            else {
                selection.cutData(manager.getCutDataActive(), (long) xVal);
                manager.setCutDataActive(-2);
                setChart(selection.getUniqueTags().toArray(new String[selection.getUniqueTags().size()]));

            }
        }
    
        
        ValueMarker marker = new ValueMarker(xVal);
        selection.addMarker(marker, notes);
        
        drawMarkers(chartPanel.getChart().getXYPlot());
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
        // Repeat the loop for each series in the plot
        for (int i = 0; i < plot.getDatasetCount(); i++) {
            //get current data set
            XYSeriesCollection col = (XYSeriesCollection) plot.getDataset(i);
            for(int j = 0; j < plot.getSeriesCount(); j++) {
                // Get the y value for the current series.
                try {
                    double val = DatasetUtilities.findYValue(col, j, xCor);
                    // Add the value to the list
                    yCors.add(val);
                } catch(IllegalArgumentException e) {
                    
                }
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
     * @deprecated 
     * @param TAG TAG of the dataset
     * @param s String collected from list
     * @return CategorizedValueMarker object that has the same domain marker as the string
     */
    private CategorizedValueMarker getMarkerFromString(String TAG, String s) {
        //for each list of static markers associated with a tag
        for(LinkedList<CategorizedValueMarker> tagMarkers : selection.getAllMarkers()) {
            //if the list is not empty and the tag matches with what we are looking for
            if(!tagMarkers.isEmpty() && tagMarkers.getFirst().getTAG().equals(TAG)) {
                //for each marker
                for(CategorizedValueMarker marker : tagMarkers) {
                    //if it matches, return
                    if(String.format("%.2f", marker.getMarker().getValue()).equals(s.substring(1, s.indexOf(','))))
                        return marker;
                }
            }
        }
        
        //else return null
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
        //for each list of static markers associated with a tag
        for(LinkedList<CategorizedValueMarker> tagMarkers : selection.getAllMarkers()) {
            //if the list is not empty and the tag matches with what we are looking for
            if(!tagMarkers.isEmpty() && tagMarkers.getFirst().getTAG().equals(TAG)) {
                //for each marker
                for(CategorizedValueMarker marker : tagMarkers) {
                    //if it matches, return
                    if(marker.getMarker().getValue() == domainValue)
                        return marker;
                }
            }
        }
        
        //else return null
        return null;
    } 
    
    /**
    * Gets the tag from the active chart and formats it into a String array of TAGs
    * @return String of the TAGs of the active charts
    * @deprecated Do not use this anymore, it will fail with multiple datasets.
    */
    private String[] titleToTag() {
        return titleToTag("");
    }
    
    //given a chart title or dataList title we can create the tag
    /**
     * Reformats a title into a String array of TAGs
     * @param title String value of the title of a chart
     * @return String value of the TAGs from the title given
     * @deprecated Do not use this anymore, it will fail with multiple datasets.
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
    
    @Deprecated
    /**
     * Used to take some value i.e. 2324.24 and convert it to the nearest value
     * of the logging rate. This now requires a dataset to choose which logging
     * rate, however it was only used in instances where a dataset was not known.
     * Should still work provided a dataset. (Untested)
     */
    private long getRoundedTime(Dataset dataset, double val) {
        //time to return if its not already a function of time
        long time = -1;
        //get the tag of the first chart
        String TAG = dataset.getDataMap().getTags().get(0);
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
            for(LogObject lo : dataset.getDataMap().getList(finding)) {
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
            if(dataset.getDataMap().tags.contains("Time," + goTo)) {
                //get the tag for the function of time
                String toSearch = "Time," + goTo;
                //for each logobject of the base function
                for(LogObject lo : dataset.getDataMap().getList(toSearch)) {
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
                //holds the current index for the crosshairs
                int crosshairCount = 0;
                //for each dataset
                for(int i = 0; i < plot.getDatasetCount(); i++) {
                    //get the dataset
                    XYSeriesCollection col = (XYSeriesCollection) plot.getDataset(i);
                    //each dataset will have a value axis. get this datasets value axis
                    ValueAxis rAxis = plot.getRangeAxis(i);
                    //get its axis location (left or right)
                    RectangleEdge rAxisEdge = plot.getRangeAxisEdge(i);
                    //for each series in this dataset
                    for(int j = 0; j < col.getSeriesCount(); j++) {
                        //get the cross hair at this location
                        Crosshair c = shadowR.get(crosshairCount);
                        crosshairCount++;
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
            }
            g2.setClip(savedClip);
        }
    }

}
