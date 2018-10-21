/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javax.swing.JFileChooser;
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

    //chartpanel object
    //exists so that it can be accessed in the chartmouselistener methods.
    ChartPanel chartPanel;
        
    //x and y crosshairs
    Crosshair xCrosshair;
    Crosshair yCrosshair;
    
    //x and y vals
    public double xCor = 0;
    public double yCor = 0;
    
    
    CategoricalHashMap dataMap;
    public DataAnalyzer() {
        initComponents();
        
        dataMap = new CategoricalHashMap();
        
        
        //create the global object crosshairs
        this.xCrosshair = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
        this.xCrosshair.setLabelVisible(true);
        this.yCrosshair = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
        this.yCrosshair.setLabelVisible(true); 
        
        //set the data for the ListView this will need to become dynamic at somepoint
        listView.setListData(new String[] {"LAP1", "RPM vs Time", "RPM vs Distance", "Wheel speed vs Time", "Coolant Temp vs Time", "LAP2", "RPM vs Time", "RPM vs Distance", "Wheel speed vs Time", "Coolant Temp vs Time"});
        
        //One data series
        final XYSeries series = new XYSeries("Lap 1");
        series.add(1.0, 500.2);
        series.add(5.0, 694.1);
        series.add(4.0, 100.0);
        series.add(12.5, 734.4);
        
        //another data series
        final XYSeries series2 = new XYSeries("Lap 2");
        series2.add(1.0, 400.2);
        series2.add(5.0, 670.1);
        series2.add(4.0, 200.0);
        series2.add(12.5, 734.4);
        
        //csv.open file selected
        //pull data elements from selected listview elements
//        listView.addListSelectionListener()
        //this method is the listener for when the listView has an item selected
        
        //A collection of series.
        final XYSeriesCollection data = new XYSeriesCollection();
        //add the series to the collection
        data.addSeries(series);
        data.addSeries(series2);
        
        //create a JFreeChart from the Factory, given parameters (Chart Title, Domain name, Range name, series collection, PlotOrientation, show legend, show tooltips, show url)
        final JFreeChart chart = ChartFactory.createXYLineChart(
            "XY Series Demo",
            "Distance (miles)", 
            "Speed", 
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
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileChooser = new javax.swing.JFileChooser();
        jScrollPane1 = new javax.swing.JScrollPane();
        listView = new javax.swing.JList<>();
        searchField = new javax.swing.JTextField();
        chartFrame = new javax.swing.JInternalFrame();
        jLabel1 = new javax.swing.JLabel();
        xCordLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        yCordLabel = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        importCSVBtn = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        listView.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(listView);

        searchField.setToolTipText("Search");

        chartFrame.setPreferredSize(new java.awt.Dimension(500, 650));
        chartFrame.setVisible(true);

        javax.swing.GroupLayout chartFrameLayout = new javax.swing.GroupLayout(chartFrame.getContentPane());
        chartFrame.getContentPane().setLayout(chartFrameLayout);
        chartFrameLayout.setHorizontalGroup(
            chartFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 875, Short.MAX_VALUE)
        );
        chartFrameLayout.setVerticalGroup(
            chartFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 543, Short.MAX_VALUE)
        );

        jLabel1.setText("X Cord:");

        xCordLabel.setText("jLabel2");

        jLabel2.setText("X Cord:");

        yCordLabel.setText("jLabel2");

        fileMenu.setText("File");

        importCSVBtn.setText("Import CSV");
        importCSVBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importCSVBtnClicked(evt);
            }
        });
        fileMenu.add(importCSVBtn);

        jMenuBar1.add(fileMenu);

        editMenu.setText("Edit");
        jMenuBar1.add(editMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
                    .addComponent(searchField))
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
                            .addComponent(yCordLabel))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void importCSVBtnClicked(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importCSVBtnClicked
        // TODO add your handling code here:
        
        // Open a separate dialog to select a .csv file
        fileChooser = new JFileChooser() {
            @Override
            public void approveSelection() {
                File chosenFile = getSelectedFile();
                if (chosenFile.exists()) {
                    String filePath = chosenFile.getAbsolutePath();
                    int lastIndex = filePath.lastIndexOf(".");
                    String fileExtension = filePath.substring(lastIndex, 
                            filePath.length());
                    if (fileExtension.equals(".csv")) {
                        super.approveSelection();
                    } else {
                        // do nothing - we don't want to approve that selection
                    }
                        
                }
            }
        };
        
        int choice = fileChooser.showOpenDialog(null);
        System.out.println("choice: " + choice);
        System.out.println("choice == APPROVE_OPTION?: " + 
                (choice == JFileChooser.APPROVE_OPTION));
        File chosenFile = fileChooser.getSelectedFile();
        String chosenFilePath = chosenFile.getAbsolutePath();
        importCSV(chosenFilePath);
        
    }//GEN-LAST:event_importCSVBtnClicked

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
    
    public void importCSV(String filepath) {
        //Ashish's code will go here.
        
        System.out.println("Filepath: " + filepath);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JInternalFrame chartFrame;
    private javax.swing.JMenu editMenu;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuItem importCSVBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList<String> listView;
    private javax.swing.JTextField searchField;
    private javax.swing.JLabel xCordLabel;
    private javax.swing.JLabel yCordLabel;
    // End of variables declaration//GEN-END:variables
}
