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
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.ArrayList;
import javax.swing.JFileChooser;
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

    public DataAnalyzer() {
        initComponents();

        // Create a new hash map
        dataMap = new CategoricalHashMap();

        // Init the graph with some dummy data until there is data given to read
        showEmptyGraph();
        
        // Create the global object crosshairs
        this.xCrosshair = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
        this.xCrosshair.setLabelVisible(true);
        this.yCrosshair = new Crosshair(Double.NaN, Color.GRAY, new BasicStroke(0f));
        this.yCrosshair.setLabelVisible(true);
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
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));

        // Mouse listener
        chartPanel.addChartMouseListener(this);

        // The form has a subframe inside the mainframe
        // Set the subframe's content to be the chartpanel
        chartFrame.setContentPane(chartPanel);
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
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));

        // Mouse listener
        chartPanel.addChartMouseListener(this);

        // The form has a subframe inside the mainframe
        // Set the subframe's content to be the chartpanel
        chartFrame.setContentPane(chartPanel);
    }

    private XYSeriesCollection getDataCollection(String tag, int[] laps) {

        // XY Series Collection allows there to be multiple data lines on the graph
        final XYSeriesCollection graphData = new XYSeriesCollection();
        // Get the list of data elements based on the tag
        LinkedList<LogObject> data = dataMap.getList(tag);
        // Declare the series to add the data elements to
        final XYSeries series = new XYSeries("");

        // We could make a XYSeries Array if we wanted to show different lap data
        // final XYSeries[] series = new XYSeries[laps.length];  <--- if we wanted to show different laps at the same time
        // Iterate through each data element in the received dataMap LinkedList
        for (LogObject d : data) {
            //Get the x and y values by seprating them by the comma
            String[] values = d.toString().split(",");
            //Add the x and y value to the series
            series.add(Long.parseLong(values[0]), Double.parseDouble(values[1]));
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

    // When the chart is clicked
    @Override
    public void chartMouseClicked(ChartMouseEvent cme) {
        // Create a static cursor that isnt cleared every time
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
        // All the statics that need to be shows should be added to plot

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
            yCordss += val + "\n";
        }

        // Set the textviews at the bottom of the file.
        xCordLabel.setText(xCor + "");
        yCordLabel.setText(yCordss);

        // Set this objects crosshair data to the value we have
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
        dataList = new javax.swing.JList<>();
        searchField = new javax.swing.JTextField();
        chartFrame = new javax.swing.JInternalFrame();
        jLabel1 = new javax.swing.JLabel();
        xCordLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        yCordLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lapList = new javax.swing.JList<>();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        importCSVBtn = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();

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

        jLabel2.setText("Y Cord:");

        yCordLabel.setText("jLabel2");

        jScrollPane2.setViewportView(lapList);

        fileMenu.setText("File");

        importCSVBtn.setText("Import CSV");
        importCSVBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importCSVBtnClicked(evt);
            }
        });
        fileMenu.add(importCSVBtn);

        menuBar.add(fileMenu);

        editMenu.setText("Edit");
        menuBar.add(editMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
                    .addComponent(searchField)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE))
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
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void importCSVBtnClicked(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importCSVBtnClicked
        // TODO add your handling code here:

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
                    }

                }
            }
        };

        // showOpenDialog returns the chosen option and if it as an approve
        //  option then the file should be imported and opened
        int choice = fileChooser.showOpenDialog(null);
        if (choice == JFileChooser.APPROVE_OPTION) {
            String chosenFilePath = fileChooser.getSelectedFile().getAbsolutePath();
            importCSV(chosenFilePath);
        }
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
        // System.out.println("Filepath: " + filepath);
        // Store all the tags in the csv file
        ArrayList<String> tags = new ArrayList<>();
        try {
            // Create a new file from the filepath
            File file = new File(filepath);
            // Scan the file
            Scanner sc = new Scanner(file);
            
            // While there is a next line
            while (sc.hasNextLine()) {
                // Store the line
                String line = sc.nextLine();
                // If the line represents an END of the current tag
                if (line.equals("END")) {
                    // Do nothing
                    // Necessary so that END statements don't get added to 'tags' ArrayList
                } else if (Character.isLetter(line.charAt(0))) {
                    // If the first character is a letter
                    // Then add the line to the tags list
                    tags.add(line);
                } else if (Character.isDigit(line.charAt(0))) {
                    // If the first character is a digit
                    // Then divide the list in 2 values by ,
                    final String DELIMITER = ",";
                    String[] values = line.split(DELIMITER);
                    // And add the values to the hashmap with their correct tag
                    // dataMap.put(new SimpleLogObject(“TAG HERE”, VALUE HERE, TIME VALUE HERE));
                    dataMap.put(new SimpleLogObject((tags.get(tags.size() - 1)), Double.parseDouble(values[1]), Long.parseLong(values[0])));
                
                }
            }
        } catch (FileNotFoundException x) {

        }
        
        // Fill the data list with titles
        fillDataList(tags);
        // Sends ArrayLisy 'tags' to hashMapToCSV
        hashMapToCSV(tags);
        
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
 
    
    
    private void fillDataList(ArrayList<String> tags){
        // Use the tags list to get the title for each tag
        String[] titles = new String[tags.size()];

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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JInternalFrame chartFrame;
    private javax.swing.JList<String> dataList;
    private javax.swing.JMenu editMenu;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuItem importCSVBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList<String> lapList;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JTextField searchField;
    private javax.swing.JLabel xCordLabel;
    private javax.swing.JLabel yCordLabel;
    // End of variables declaration//GEN-END:variables
}
