/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.util.Vector;
import javafx.scene.control.SelectionMode;
import javax.swing.DefaultListModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
 * I did work too -Nikhil
 */
public class DataAnalyzer extends javax.swing.JFrame implements ChartMouseListener {

    //chartpanel object
    //exists so that it can be accessed in the chartmouselistener methods.
    ChartPanel chartPanel;
        
    //x and y crosshairs
    Crosshair xCrosshair;
    Crosshair yCrosshair;
    
    //x and y vals
    public double xCor = 0;
    public double yCor = 0;
    
    
    public DataAnalyzer() {
        initComponents();
        
        //create the global object crosshairs
        this.xCrosshair = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
        this.xCrosshair.setLabelVisible(true);
        this.yCrosshair = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
        this.yCrosshair.setLabelVisible(true); 
        
        // Add different types of data that we are collecting and want to show
        String[] data = {"RPM vs Time", "RPM vs Distance", "Wheel speed vs Time", "Coolant Temp vs Time"};
        //Populate the data list view with given data types
        dataList.setListData(data);
        
        //lapList just shows different laps, so we can select and show multiple laps on one graph
        lapList.setListData(new String[] {"Lap 1", "Lap 2"});
        
        //init the graph with the first items in combo box and lap 1
        showEmptyGraph();           
         
        //If another item is selected in the data combo box, change the chart
        dataList.addListSelectionListener(new ListSelectionListener(){
            @Override
            public void valueChanged(ListSelectionEvent arg0) {
                if (!arg0.getValueIsAdjusting()) {
                    // passes the data type index, all the laps currently selected, and the data type name
                    setChart(data[dataList.getSelectedIndex()], lapList.getSelectedIndices());
                }
            }
        });
        
        //If a different or another lap is selected, change the graph accordingly
        lapList.addListSelectionListener(new ListSelectionListener(){
            @Override
            public void valueChanged(ListSelectionEvent arg0) {
                if (!arg0.getValueIsAdjusting()) {
                    // passes the data type index, all the laps currently selected, and the data type name
                    setChart(data[dataList.getSelectedIndex()], lapList.getSelectedIndices());
                }
            }
        });
        
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
              filter();
            }
            public void removeUpdate(DocumentEvent e) {
              filter();
            }
            public void insertUpdate(DocumentEvent e) {
              filter();
            }

            public void filter() {
                String input = searchField.getText().toString();
                Vector<String> filteredList = new Vector<>();
                for(String str : data){
                    if(str.toUpperCase().contains(input.toUpperCase())){
                        filteredList.add(str);
                    }
                }
                dataList.setListData(filteredList);
            }
        });
    }
    
    private void showEmptyGraph(){
        final XYSeriesCollection data = new XYSeriesCollection();
        
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

        // create a JFreeChart from the Factory, given parameters (Chart Title, Domain name, Range name, series collection, PlotOrientation, show legend, show tooltips, show url)
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
        
        //instantiate chart panel object from the object created from ChartFactory
        chartPanel = new ChartPanel(chart);
        //set the size of the panel
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600)); 
        
        //mouse listener
        chartPanel.addChartMouseListener(this);

        //The form has a subframe inside the mainframe
        //set the subframe's content to be the chartpanel
        chartFrame.setContentPane(chartPanel);
    }
    
    private void setChart(String title, int[] laps){
        
        //Gets the specific data based on what kind of data we want to show for which 
        final XYSeriesCollection data = getDataCollection(title, laps);
        
        // Gets the independent variable from the title of the data
        String xAxis = title.split(" vs ")[1];  //split title by vs, we get ["RPM", "Time"] or something like that
        // Gets the dependent variable from the title of the data
        String yAxis = title.split(" vs ")[0];

        // create a JFreeChart from the Factory, given parameters (Chart Title, Domain name, Range name, series collection, PlotOrientation, show legend, show tooltips, show url)
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
        
        //instantiate chart panel object from the object created from ChartFactory
        chartPanel = new ChartPanel(chart);
        //set the size of the panel
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600)); 
        
        //mouse listener
        chartPanel.addChartMouseListener(this);

        //The form has a subframe inside the mainframe
        //set the subframe's content to be the chartpanel
        chartFrame.setContentPane(chartPanel);
    }
    
    //CHANGE DATA BASED ON TITLE===================================================================================================================
    private XYSeriesCollection getDataCollection(String title, int[] laps){
        final XYSeriesCollection data = new XYSeriesCollection();
        for(int i = 0; i < laps.length; i++){
            switch(laps[i]){
                case 0:
                    final XYSeries series = new XYSeries("Lap 1");
                    series.add(1.0, 500.2);
                    series.add(5.0, 694.1);
                    series.add(4.0, 100.0);
                    series.add(12.5, 734.4);
                    data.addSeries(series);
                    break;
                
                case 1:
                    final XYSeries series2 = new XYSeries("Lap 2");
                    series2.add(1.0, 400.2);
                    series2.add(5.0, 670.1);
                    series2.add(4.0, 200.0);
                    series2.add(12.5, 734.4);
                    data.addSeries(series2);
                    break;
            }
        }
        
        return data;
    }
    
    // When the chart is clicked
    @Override
    public void chartMouseClicked(ChartMouseEvent cme) {
        //create a static cursor that isnt cleared every time
    }

    //when the mouse moves over the chart
    @Override
    public void chartMouseMoved(ChartMouseEvent cme) {
        
        //the data area of where the chart is.
        Rectangle2D dataArea = this.chartPanel.getScreenDataArea();
        //get the chart from the chart mouse event
        JFreeChart chart = cme.getChart();
        //Get the xy plot object from the chart
        XYPlot plot = (XYPlot) chart.getPlot();
        //clear all markers
        //this will be a problem for static markers we want to create
        plot.clearDomainMarkers();
        //get the xAxis
        ValueAxis xAxis = plot.getDomainAxis();
        //get the xCordinate from the xPositon of the mouse
        xCor = xAxis.java2DToValue(cme.getTrigger().getX(), dataArea, 
                RectangleEdge.BOTTOM);
        //find the y cordinate from the plots data set given a x cordinate
        yCor = DatasetUtilities.findYValue(plot.getDataset(), 0, xCor);
        //create a marker at the x Coordinate with black paint
        ValueMarker marker = new ValueMarker(xCor);
        marker.setPaint(Color.BLACK);
        //add a marker on the x axis given a marker. This essentially makes the marker verticle
        plot.addDomainMarker(marker);
        //all the statics that need to be shows should be added to plot
        
        //string object that holds values for all the series on the plot.
        String yCordss = "";
        //repeat the loop for each series in the plot
        for(int i = 0; i < plot.getDataset().getSeriesCount(); i++) {
            //get the collection from the plots data set
            XYSeriesCollection col = (XYSeriesCollection) plot.getDataset();
            //get the plots name from the series's object
            String plotName = plot.getDataset().getSeriesKey(i).toString();
            //create a new collection 
            XYSeriesCollection col2 = new XYSeriesCollection();
            //add the series with the name we found to the other collection
            //we do this because the findYValue() method takes a collection
            col2.addSeries(col.getSeries(plotName));
            //get the y value for the current series.
            double val = DatasetUtilities.findYValue(col2, 0, xCor);
            //add the name and value to the string
            yCordss += plotName + "\n" + val + "\n\n\n";
        }
        
        //set the textviews at the bottom of the file.
        xCordLabel.setText(xCor + "");
        yCordLabel.setText(yCordss);
        
        //set this objects crosshair data to the value we have
        this.xCrosshair.setValue(xCor);
        this.yCrosshair.setValue(yCor);
    }

    private void searchFieldActionPerformed(java.awt.event.ActionEvent evt) {                                            
        // TODO add your handling code here:
    }                                           


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        dataList = new javax.swing.JList<>();
        searchField = new javax.swing.JTextField();
        chartFrame = new javax.swing.JInternalFrame();
        jLabel1 = new javax.swing.JLabel();
        xCordLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        yCordLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lapList = new javax.swing.JList<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jScrollPane1.setViewportView(dataList);

        searchField.setToolTipText("Search");

        chartFrame.setPreferredSize(new java.awt.Dimension(500, 650));
        chartFrame.setVisible(true);

        javax.swing.GroupLayout chartFrameLayout = new javax.swing.GroupLayout(chartFrame.getContentPane());
        chartFrame.getContentPane().setLayout(chartFrameLayout);
        chartFrameLayout.setHorizontalGroup(
            chartFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 883, Short.MAX_VALUE)
        );
        chartFrameLayout.setVerticalGroup(
            chartFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 559, Short.MAX_VALUE)
        );

        jLabel1.setText("X Cord:");

        xCordLabel.setText("jLabel2");

        jLabel2.setText("X Cord:");

        yCordLabel.setText("jLabel2");

        jScrollPane2.setViewportView(lapList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
                    .addComponent(searchField)
                    .addComponent(jScrollPane2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chartFrame, javax.swing.GroupLayout.PREFERRED_SIZE, 899, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(xCordLabel))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(yCordLabel))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(chartFrame, javax.swing.GroupLayout.PREFERRED_SIZE, 589, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(xCordLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(yCordLabel)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JInternalFrame chartFrame;
    private javax.swing.JList<String> dataList;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList<String> lapList;
    private javax.swing.JTextField searchField;
    private javax.swing.JLabel xCordLabel;
    private javax.swing.JLabel yCordLabel;
    // End of variables declaration//GEN-END:variables
}
