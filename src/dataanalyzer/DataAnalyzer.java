/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer;

import javax.swing.JInternalFrame;
import dataanalyzer.dialog.VehicleDataDialog;
import dataanalyzer.dialog.AskVehicleDialog;
import dataanalyzer.dialog.MathChannelDialog;
import com.arib.toast.Toast;
import dataanalyzer.dialog.FileNotesDialog;
import dataanalyzer.dialog.LoadingDialog;
import dataanalyzer.dialog.MessageBox;
import dataanalyzer.dialog.VitalsDialog;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import org.jfree.chart.plot.ValueMarker;

/**
 *
 * @author aribdhuka
 */
public class DataAnalyzer extends javax.swing.JFrame {

    //Stores the current filepath
    private String openedFilePath;

    //holds if file operations are currently ongoing
    private boolean openingAFile;
    
    protected boolean rangeMarkersActive;
    
    ChartManager chartManager;
    
    private String fileNotes;
    
    private ArrayList<GPSGraphInternalFrame> trackMaps = new ArrayList<>();
    
    private ArrayList<JInternalFrame> lambdaMaps = new ArrayList<>();

    //holds the current theme
    protected Theme currTheme = Theme.DEFAULT;

    public DataAnalyzer() {
        initComponents();
        
        //Setup directories
        Installer.runInstaller();
        
        //to prevent nulls, start as blank
        fileNotes = "";
        
        //initialize chart manager
        chartManager = new ChartManager(this, trackMaps);
        
        //Set the title of the frame
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
        
        //start with range markers shown
        rangeMarkersActive = true;
        
        //disable the layout manager which essentially makes the frame an absolute positioning frame
        this.setLayout(null);
        
        // Init the graph with some dummy data until there is data given to read
        initializeBasicView();
        
        //set the opened file path to empty string to prevent null pointer exceptions
        openedFilePath = "";
        
        //disable new import button
        newImportMenuItem.setVisible(false);
        
        //on new element entry of dataMap, update the view
        chartManager.getDataMap().addTagSizeChangeListener(new HashMapTagSizeListener() {
            @Override
            public void sizeUpdate() {
                if(!openingAFile)
                    Lap.applyToDataset(chartManager.getDataMap(), chartManager.getLapBreaker());
            }
        });
    }
    
    private void clearAllCharts() {
        ArrayList<ChartAssembly> charts = chartManager.getCharts();
        for(ChartAssembly chart : charts) {
            chart.getChartFrame().dispose();
        }
        charts.clear();
    }
    
    private void clearAllTrackMaps(){
        for(JInternalFrame trackMap : trackMaps){
            trackMap.dispose();
        }
        trackMaps.clear();
    }
    
    private void clearAllLambdaMaps(){
        for(JInternalFrame lambdaMap : lambdaMaps){
            lambdaMap.dispose();
        }
        lambdaMaps.clear();
    }
    
    private void clearAllFrames(){
        clearAllCharts();
        clearAllTrackMaps();
        clearAllLambdaMaps();
    }
    
    private void initializeBasicView() {
        ChartAssembly chart = chartManager.addChart();
        chart.getChartFrame().setLocation(0, 0);
        Dimension frameSize = this.getSize();
        chart.getChartFrame().setSize((frameSize.width/4)*3, frameSize.height - (3 * ((int) menuBar.getSize().getHeight())));
        
        showTrackMapActionPerformed(null);
    }
    
    private void twoVerticalView() {
        Dimension frameSize = this.getSize();
        clearAllFrames();
        ChartAssembly leftChart = chartManager.addChart();
        leftChart.getChartFrame().setLocation(0, 0);
        leftChart.getChartFrame().setSize(((frameSize.width/4)*3)/2, frameSize.height - (3 * ((int) menuBar.getSize().getHeight())));
        
        ChartAssembly rightChart = chartManager.addChart();
        rightChart.getChartFrame().setLocation(((frameSize.width/4)*3)/2 + 1, 0);
        rightChart.getChartFrame().setSize(((frameSize.width/4)*3)/2, frameSize.height - (3 * ((int) menuBar.getSize().getHeight())));
        
        showTrackMapActionPerformed(null);
    }
    
    private void twoHorizontalView() {
        Dimension frameSize = this.getSize();
        clearAllFrames();
        
        ChartAssembly topChart = chartManager.addChart();
        topChart.getChartFrame().setLocation(0,0);
        topChart.getChartFrame().setSize((frameSize.width/4)*3, frameSize.height / 2 - (3 * ((int) menuBar.getSize().getHeight()))/2);
        
        ChartAssembly bottomChart = chartManager.addChart();
        bottomChart.getChartFrame().setLocation(0,frameSize.height/2 - (3 * ((int) menuBar.getSize().getHeight()))/2);
        bottomChart.getChartFrame().setSize((frameSize.width/4)*3, frameSize.height / 2 - (3 * ((int) menuBar.getSize().getHeight()))/2);
        
        showTrackMapActionPerformed(null);
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
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        newWindowMenuItem = new javax.swing.JMenuItem();
        newImportMenuItem = new javax.swing.JMenuItem();
        importECUDataMenuItem = new javax.swing.JMenuItem();
        openBtn = new javax.swing.JMenuItem();
        saveMenuButton = new javax.swing.JMenuItem();
        saveAsMenuItem = new javax.swing.JMenuItem();
        exportMenuItem = new javax.swing.JMenuItem();
        resetMenuItem = new javax.swing.JMenuItem();
        closeMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        addMathChannelButton = new javax.swing.JMenuItem();
        addLapConditionMenuItem = new javax.swing.JMenuItem();
        addNotesMenuItem = new javax.swing.JMenuItem();
        checkVitals_menuitem = new javax.swing.JMenuItem();
        viewMenu = new javax.swing.JMenu();
        fullscreenMenuItem = new javax.swing.JMenuItem();
        showRangeMarkersMenuItem = new javax.swing.JMenuItem();
        singleViewMenuItem = new javax.swing.JMenuItem();
        twoVerticalMenuItem = new javax.swing.JMenuItem();
        twoHorizontalMenuItem = new javax.swing.JMenuItem();
        swapChartsMenuItem = new javax.swing.JMenuItem();
        addChartMenuItem = new javax.swing.JMenuItem();
        trackMapMenuItem = new javax.swing.JMenuItem();
        defaultTheme_menuitem = new javax.swing.JMenuItem();
        systemTheme_menuitem = new javax.swing.JMenuItem();
        darkTheme_menuitem = new javax.swing.JMenuItem();
        vehicleMenu = new javax.swing.JMenu();
        newVehicleMenuItem = new javax.swing.JMenuItem();
        saveVehicleMenuItem = new javax.swing.JMenuItem();
        importVehicleMenuItem = new javax.swing.JMenuItem();
        editVehicleMenuItem = new javax.swing.JMenuItem();
        engineMenu = new javax.swing.JMenu();
        engineChartSetup = new javax.swing.JMenuItem();
        showLambdaMap = new javax.swing.JMenuItem();

        jMenuItem2.setText("jMenuItem2");

        jMenuItem1.setText("jMenuItem1");

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1220, 720));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        fileMenu.setText("File");

        newWindowMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.CTRL_MASK));
        newWindowMenuItem.setText("New Window");
        newWindowMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newWindowMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(newWindowMenuItem);

        newImportMenuItem.setText("New Import");
        newImportMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newImportMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(newImportMenuItem);

        importECUDataMenuItem.setText("Import PE3 data");
        importECUDataMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importECUDataMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(importECUDataMenuItem);

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

        resetMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        resetMenuItem.setText("Reset");
        resetMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(resetMenuItem);

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

        addNotesMenuItem.setText("Add Notes");
        addNotesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNotesMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(addNotesMenuItem);

        checkVitals_menuitem.setText("Check Vitals");
        checkVitals_menuitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkVitals_menuitemActionPerformed(evt);
            }
        });
        editMenu.add(checkVitals_menuitem);

        menuBar.add(editMenu);

        viewMenu.setText("View");

        fullscreenMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
        fullscreenMenuItem.setText("Fullscreen");
        fullscreenMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fullscreenMenuItemActionPerformed(evt);
            }
        });
        viewMenu.add(fullscreenMenuItem);

        showRangeMarkersMenuItem.setText("Hide Range Markers");
        showRangeMarkersMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showRangeMarkersMenuItemActionPerformed(evt);
            }
        });
        viewMenu.add(showRangeMarkersMenuItem);

        singleViewMenuItem.setText("Single View");
        singleViewMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                singleViewMenuItemActionPerformed(evt);
            }
        });
        viewMenu.add(singleViewMenuItem);

        twoVerticalMenuItem.setText("Two Vertical");
        twoVerticalMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                twoVerticalMenuItemActionPerformed(evt);
            }
        });
        viewMenu.add(twoVerticalMenuItem);

        twoHorizontalMenuItem.setText("Two Horizontal");
        twoHorizontalMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                twoHorizontalMenuItemActionPerformed(evt);
            }
        });
        viewMenu.add(twoHorizontalMenuItem);

        swapChartsMenuItem.setText("Swap Charts");
        swapChartsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                swapChartsMenuItemActionPerformed(evt);
            }
        });
        viewMenu.add(swapChartsMenuItem);

        addChartMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        addChartMenuItem.setText("Add Chart");
        addChartMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addChartMenuItemActionPerformed(evt);
            }
        });
        viewMenu.add(addChartMenuItem);
        trackMapMenuItem.setText("Add Track Map");
        trackMapMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showTrackMapActionPerformed(evt);
            }
        });
        viewMenu.add(trackMapMenuItem);
        defaultTheme_menuitem.setText("Default");
        defaultTheme_menuitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                defaultTheme_menuitemActionPerformed(evt);
            }
        });
        viewMenu.add(defaultTheme_menuitem);

        systemTheme_menuitem.setText("System");
        systemTheme_menuitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                systemTheme_menuitemActionPerformed(evt);
            }
        });
        viewMenu.add(systemTheme_menuitem);

        darkTheme_menuitem.setText("Dark");
        darkTheme_menuitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                darkTheme_menuitemActionPerformed(evt);
            }
        });
        viewMenu.add(darkTheme_menuitem);

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

        engineMenu.setText("Engine");

        engineChartSetup.setText("Setup Charts");
        engineChartSetup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                engineChartSetupActionPerformed(evt);
            }
        });
        engineMenu.add(engineChartSetup);

        showLambdaMap.setText("Show Lambda Map");
        showLambdaMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showLambdaMapActionPerformed(evt);
            }
        });
        engineMenu.add(showLambdaMap);

        menuBar.add(engineMenu);

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
                //get all selected files
                File[] chosenFiles = getSelectedFiles();
                
                //check to see if all files are legal
                boolean toApprove = true;
                for(File chosenFile : chosenFiles) {
                    if(chosenFile.exists()) {
                        // Get the file extension to make sure it is .csv
                        String filePath = chosenFile.getAbsolutePath();
                        int lastIndex = filePath.lastIndexOf(".");
                        String fileExtension = filePath.substring(lastIndex,
                                filePath.length());

                        // approve selection if it is a .csv file
                        if (!(fileExtension.equals(".dfr") || fileExtension.equals(".csv") || fileExtension.equals(".txt"))) {
                            toApprove = false;
                            // display error message - that selection should not be approve
                            new MessageBox(DataAnalyzer.this, "Error: Selection could not be approved", true).setVisible(true);
                            this.cancelSelection();
                        }
                    } else {
                        toApprove = false;
                    }
                }
                
                //if all files are legal
                if(toApprove) {
                    if(chosenFiles.length > 0) {
                        if(chosenFiles[0].exists()) {
                            String filePath = chosenFiles[0].getAbsolutePath();
                            
                            if(filePath.lastIndexOf('/') != -1) {
                                setTitle("DataAnalyzer - " + filePath.substring(filePath.lastIndexOf('/')));
                            } else if(filePath.lastIndexOf('\\') != -1) {
                                setTitle("DataAnalyzer - " + filePath.substring(filePath.lastIndexOf('\\')));
                            } else {
                                setTitle("DataAnalyzer - " + filePath);
                            }
                        }
                    }
                    super.approveSelection();
                }

            }
        };

        // showOpenDialog returns the chosen option and if it as an approve
        fileChooser.setMultiSelectionEnabled(true);
        //  option then the file should be imported and opened
        int choice = fileChooser.showOpenDialog(null);
        if (choice == JFileChooser.APPROVE_OPTION) {
            boolean onlyDFRFiles = true;
            for(File file : fileChooser.getSelectedFiles()) {
                String filePath = file.getAbsolutePath();
                int lastIndex = filePath.lastIndexOf(".");
                String fileExtension = filePath.substring(lastIndex,
                        filePath.length());
                if(!fileExtension.equals(".dfr")) {
                    onlyDFRFiles = false;
                    break;
                }
                        
            }
            
            boolean applyPostProcessing = false;
            
            if(!onlyDFRFiles) {
                //ask for post processing
                applyPostProcessing = askForPostProcessing();

                //ask the user to import a vehicle. if any but cancel pressed continue
                boolean shouldContinue = askForVehicle();
                if(!shouldContinue)
                    return;
            }
            
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
                    da.getChartManager().setVehicleData(chartManager.getVehicleData()); //TODO: May need to clone
                    //get file path
                    String chosenFilePath = chosenFile.getAbsolutePath();
                    //set the file path for that object
                    da.openedFilePath = chosenFilePath;
                    //get index of the last.
                    int lastIndex = chosenFilePath.lastIndexOf(".");
                    //get file extension
                    String fileExtension = chosenFilePath.substring(lastIndex, chosenFilePath.length());
                    
                    //if its a created file
                    if(fileExtension.equals(".dfr")) {
                        da.openFile(chosenFilePath);
                    }
                    //if its a new import
                    else {
                        //if its a csv
                        if(fileExtension.equals(".csv")) {
                            //make the new window import a CSV
                            da.importCSV(chosenFilePath);
                        //else if its a TXT make the new window import a CSV
                        } else if (fileExtension.equals(".txt")) {
                            da.importTXT(chosenFilePath);
                        }
                        if(applyPostProcessing)
                            da.applyPostProcessing();
                    }
                    
                    da.setVisible(true);
                    if(chosenFilePath.lastIndexOf('/') != -1) {
                        da.setTitle("DataAnalyzer - " + chosenFilePath.substring(chosenFilePath.lastIndexOf('/')));
                    } else {
                        da.setTitle("DataAnalyzer - " + chosenFilePath);
                    }
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
                    
                    //if its a created file
                    if(fileExtension.equals(".dfr")) {
                        openFile(chosenFilePath);
                    }
                    //if its a new import
                    else {
                        //if its a csv
                        if(fileExtension.equals(".csv")) {
                            //make the new window import a CSV
                            importCSV(chosenFilePath);
                        //else if its a TXT make the new window import a CSV
                        } else if (fileExtension.equals(".txt")) {
                            importTXT(chosenFilePath);
                        }
                        if(applyPostProcessing)
                            applyPostProcessing();
                    }
                    toCreateNewWindow = true;
                }
                windowCount++;
            }
        
        }
    }//GEN-LAST:event_openBtnClicked

    private void addMathChannel(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addMathChannel
        new MathChannelDialog(chartManager.getDataMap(), chartManager.getVehicleData()).setVisible(true);
    }//GEN-LAST:event_addMathChannel

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
            if(chartManager.getNumberOfCharts() == 1) {
                Dimension frameSize = this.getSize();
                chartManager.getCharts().get(0).getChartFrame().setSize(frameSize.width, frameSize.height - (2 * ((int) menuBar.getSize().getHeight())));
                chartManager.getCharts().get(0).getChartFrame().setLocation(0, 0);

            }
        }
        //if we are not already full screen
        else {
            //set these sizes
            this.setSize(screenSize.width, screenSize.height);
            this.setLocation(0, 0);
            fullscreenMenuItem.setText("Restore Down");
            if(chartManager.getNumberOfCharts() == 1) {
                Dimension frameSize = this.getSize();
                chartManager.getCharts().get(0).getChartFrame().setSize(screenSize.width, screenSize.height - (2 * ((int) menuBar.getSize().getHeight())));
                chartManager.getCharts().get(0).getChartFrame().setLocation(0, 0);

            }
        }
    }//GEN-LAST:event_fullscreenMenuItemActionPerformed

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
                            new MessageBox(DataAnalyzer.this, "Error: Wrong File Type", true).setVisible(true);
                            this.cancelSelection();
                        }

                    }
                }
                
                if(toApprove) {
                    if(chosenFiles.length > 0) {
                        if(fileChooser.getSelectedFiles()[0].getAbsolutePath().lastIndexOf('/') != -1) {
                            setTitle("DataAnalyzer - " + fileChooser.getSelectedFiles()[0]
                                    .getAbsolutePath().substring(fileChooser.getSelectedFiles()[0].getAbsolutePath().lastIndexOf('/')));
                        } else {
                            setTitle("DataAnalyzer - " + fileChooser.getSelectedFiles()[0].getAbsolutePath());
                        }
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
            //ask for post processing
            boolean applyPostProcessing = askForPostProcessing();
            
            //ask the user to import a vehicle. if any but cancel pressed continue
            boolean shouldContinue = askForVehicle();
            if(shouldContinue) {
                //get array of chosenFiles
                File[] chosenFiles = fileChooser.getSelectedFiles();
                if(chosenFiles.length > 1) {
                    //TODO: show progress bar
                }
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
                        da.getChartManager().setVehicleData(chartManager.getVehicleData()); //TODO: May need to clone
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
                        if(applyPostProcessing)
                            da.applyPostProcessing();
                        da.setVisible(true);
                        da.setTitle("DataAnalyzer - " + chosenFilePath.substring(chosenFilePath.lastIndexOf('/')));
                        if(chosenFilePath.lastIndexOf('/') != -1) {
                            da.setTitle("DataAnalyzer - " + chosenFilePath.substring(chosenFilePath.lastIndexOf('/')));
                        } else {
                            da.setTitle("DataAnalyzer - " + chosenFilePath);
                        }
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
                        if(applyPostProcessing)
                            applyPostProcessing();
                        toCreateNewWindow = true;
                    }
                    windowCount++;
                }
            }
        }
    }//GEN-LAST:event_newImportMenuItemActionPerformed

    private void newVehicleMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newVehicleMenuItemActionPerformed
        //open vehicle data dialog
        new VehicleDataDialog(this, true, chartManager.getVehicleData(), "Create").setVisible(true);
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
        new VehicleDataDialog(this, true, chartManager.getVehicleData(), "Apply").setVisible(true);
    }//GEN-LAST:event_editVehicleMenuItemActionPerformed

    private void saveVehicleMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveVehicleMenuItemActionPerformed
        //save vehicle dynamic data
        saveVehicleData("");
    }//GEN-LAST:event_saveVehicleMenuItemActionPerformed

    private void saveAsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsMenuItemActionPerformed
        //save file with no known file path. Will force method to open file chooser
        saveFile("");
    }//GEN-LAST:event_saveAsMenuItemActionPerformed

    //Export the data into a CSV file to use with other programs.
    private void exportMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportMenuItemActionPerformed
        String data = hashMapToCSV(chartManager.getDataMap().getTags());
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
                    new MessageBox(this, e.toString(), true).setVisible(true);
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
                    new MessageBox(this, e.toString(), true).setVisible(true);
                }
            }
            
        }
    }//GEN-LAST:event_exportMenuItemActionPerformed

    //begin the lapbreaker
    private void addLapConditionMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addLapConditionMenuItemActionPerformed
        //if the lapbreaker is not already active
        if(chartManager.getLapBreakerActive() == -1) {
            chartManager.setNewLap(new Lap());
            //set the lapBreaker to active, this changes the functionality of clicking on the chart
            chartManager.setLapBreakerActive(0);
            //Display message box with instructions
            new MessageBox(this, "Use the reticle to find the start of the lap.\nClick where the lap starts.\nClick again where the lap stops.", false).setVisible(true);
        } else {
            //display message.
            Toast.makeToast(this, "Adding of Lap cancelled.", Toast.DURATION_MEDIUM);
            chartManager.setLapBreakerActive(-1);
        }
    }//GEN-LAST:event_addLapConditionMenuItemActionPerformed

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

    private void twoVerticalMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_twoVerticalMenuItemActionPerformed
        twoVerticalView();
    }//GEN-LAST:event_twoVerticalMenuItemActionPerformed

    private void twoHorizontalMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_twoHorizontalMenuItemActionPerformed
        twoHorizontalView();
    }//GEN-LAST:event_twoHorizontalMenuItemActionPerformed

    private void showRangeMarkersMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showRangeMarkersMenuItemActionPerformed
        invertRangeMarkersActive();
    }//GEN-LAST:event_showRangeMarkersMenuItemActionPerformed

    private void resetMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetMenuItemActionPerformed
        //Ensure that the user wants to reset
        boolean confirmed = createConfirmDialog("Continue?", "This will reset the window. Any unsaved progress will be lost.");
        if(!confirmed)
            return;
        
        //ask to save
        boolean confirmSave = createConfirmDialog("Save?", "Would you like to save before exit?");
        //save if yes
        if(confirmSave) {
            saveFile(openedFilePath);
        }
        
        DataAnalyzer da = new DataAnalyzer();
        da.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_resetMenuItemActionPerformed

    private void addChartMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addChartMenuItemActionPerformed
        chartManager.addChart();
    }//GEN-LAST:event_addChartMenuItemActionPerformed

    private void swapChartsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_swapChartsMenuItemActionPerformed
        chartManager.toggleSwapper();
    }//GEN-LAST:event_swapChartsMenuItemActionPerformed

    private void addNotesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addNotesMenuItemActionPerformed
        Referencer<String> reference = new Referencer<>(fileNotes);
        new FileNotesDialog(reference, this, true).setVisible(true);
        fileNotes = reference.get();
    }//GEN-LAST:event_addNotesMenuItemActionPerformed

    private void importECUDataMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importECUDataMenuItemActionPerformed
        //open file for vehicleData
        // Open a separate dialog to select a .csv file
        fileChooser = new JFileChooser() {

            // Override approveSelection method because we only want to approve
            //  the selection if its is a .csv file.
            @Override
            public void approveSelection() {
                File[] chosenFiles = getSelectedFiles();

                // Make sure that the chosen file exists
                if (allExist(chosenFiles)) {

                    // approve selection if it is a .csv file
                    if (allAreCSV(chosenFiles)) {
                        super.approveSelection();
                    } else {
                        //inform the user of a selection error.
                        Toast.makeToast(DataAnalyzer.this, "Not a CSV file!", Toast.DURATION_MEDIUM);
                    }

                } else {
                    Toast.makeToast(DataAnalyzer.this, "How in the hell did you choose a file that doesnt exist?", Toast.DURATION_LONG);
                }
            }
            
            private boolean allExist(File[] files) {
                for (File file : files) {
                    if(!file.exists())
                        return false;
                }
                return true;
            }
            
            private boolean allAreCSV(File[] files) {
                for(File file : files) {
                    String filePath = file.getAbsolutePath();
                    int lastIndex = filePath.lastIndexOf(".");
                    if(lastIndex == -1)
                        return false;
                    String fileExtension = filePath.substring(lastIndex,
                            filePath.length());
                    
                    if(!fileExtension.equals(".csv"))
                        return false;
                }
                
                return true;
            }
        };

        fileChooser.setMultiSelectionEnabled(true);
        // showOpenDialog returns the chosen option and if it as an approve
        //  option then the file should be imported and opened
        int choice = fileChooser.showOpenDialog(null);
        if (choice == JFileChooser.APPROVE_OPTION) {
            try {
                openPE3Files(fileChooser.getSelectedFiles());
            } catch (FileNotFoundException ex) {
                Logger.getLogger(DataAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_importECUDataMenuItemActionPerformed

    private void singleViewMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_singleViewMenuItemActionPerformed
        clearAllFrames();
        
        //reinitialize the initial basic view.
        initializeBasicView();
    }//GEN-LAST:event_singleViewMenuItemActionPerformed

    private void engineChartSetupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_engineChartSetupActionPerformed
        //delete all current charts
        clearAllFrames();
        
        //create main graph which will show overlay between RPM, TPS, and Lambda
        Dimension frameSize = this.getSize();
        ChartAssembly main = chartManager.addChart();
        main.getChartFrame().setSize(frameSize.width / 2, frameSize.height / 3 * 2 - 22);
        main.getChartFrame().setLocation(0, 0);
        
        //create fuel open time below main frame
        ChartAssembly fot = chartManager.addChart();
        fot.getChartFrame().setSize(frameSize.width / 2, frameSize.height / 3 - 22);
        fot.getChartFrame().setLocation(0, frameSize.height / 3 * 2 - 22 + 1);
        
        //create RPM chart
        ChartAssembly rpm = chartManager.addChart();
        rpm.getChartFrame().setSize(frameSize.width / 2, frameSize.height / 3 - 11);
        rpm.getChartFrame().setLocation(frameSize.width / 2 + 1, 0);
        
        //create TPS chart
        ChartAssembly tps = chartManager.addChart();
        tps.getChartFrame().setSize(frameSize.width / 2, frameSize.height / 3 - 11);
        tps.getChartFrame().setLocation(frameSize.width / 2 + 1, frameSize.height / 3 - 11 + 1);
        
        //create Lambda chart
        ChartAssembly lambda = chartManager.addChart();
        lambda.getChartFrame().setSize(frameSize.width / 2, frameSize.height / 3 - 22);
        lambda.getChartFrame().setLocation(frameSize.width / 2 + 1, frameSize.height / 3 * 2 - 22 + 1);
        
        //set charts data

        //if the datamap contains AFR data, RPM, and TPS, put them on the main chart
        if(chartManager.getDataMap().getTags().contains("Time,AFRAveraged") && 
                chartManager.getDataMap().getTags().contains("Time,TPS") && 
                chartManager.getDataMap().getTags().contains("Time,RPM")) {
            
            main.selectedTags = new String[] {"Time,RPM", "Time,TPS", "Time,AFRAveraged"};
            main.selectedLaps = new int[0];
            
            main.setChart(main.selectedTags, main.selectedLaps);
        }
        
        //if RPM data exists, put it on the rpm chart
        if(chartManager.getDataMap().getTags().contains("Time,RPM")) {
            rpm.selectedTags = new String[] {"Time,RPM"};
            rpm.selectedLaps = new int[0];
            
            rpm.setChart(rpm.selectedTags, rpm.selectedLaps);
        }
       
        //if tps data exists, put it on the tps chart
        if(chartManager.getDataMap().getTags().contains("Time,TPS")) {
            tps.selectedTags = new String[] {"Time,TPS"};
            tps.selectedLaps = new int[0];
            
            tps.setChart(tps.selectedTags, tps.selectedLaps);
        }
        
        //if AFR data exists, put it on the lambda chart
        if(chartManager.getDataMap().getTags().contains("Time,AFRAveraged")) {
            lambda.selectedTags = new String[] {"Time,AFRAveraged"};
            lambda.selectedLaps = new int[0];
            
            lambda.setChart(lambda.selectedTags, lambda.selectedLaps);
        }
        
        //if fuel open time data exists, put it on the fuel open time chart
        if(chartManager.getDataMap().getTags().contains("Time,FuelOpenTime")) {
            fot.selectedTags = new String[] {"Time,FuelOpenTime"};
            fot.selectedLaps = new int[0];
            
            fot.setChart(fot.selectedTags, fot.selectedLaps);
        }
        
        
    }//GEN-LAST:event_engineChartSetupActionPerformed

    private void showLambdaMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showLambdaMapActionPerformed
        JInternalFrame lambdaMapInternalFrame;
        clearAllFrames();
        //calls the correct constructor based on wheather data has been loaded
        if(chartManager.getDataMap().isEmpty()){
            lambdaMapInternalFrame = new LambdaMapInternalFrame(this);
        } else {
            lambdaMapInternalFrame = new LambdaMapInternalFrame(this, chartManager.getDataMap());
        }
        lambdaMapInternalFrame.setVisible(true);
        
        //adds the trackMapInternalFrame to a list, to keep track of them
        lambdaMaps.add(lambdaMapInternalFrame);
        
        //sets the location and size of the trackMapInternalFrame
        Dimension frameSize = this.getSize();
        lambdaMapInternalFrame.setLocation(0, 0);
        lambdaMapInternalFrame.setSize(frameSize.width-15, frameSize.height-70);
        
        //adds the trackMapInternalFrame to the DataAnalyzer frame
        getContentPane().add(lambdaMapInternalFrame);
    }//GEN-LAST:event_showLambdaMapActionPerformed

    private void showTrackMapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showTrackMapActionPerformed
        GPSGraphInternalFrame trackMapInternalFrame;
        
        //calls the correct constructor based on wheather data has been loaded
        if(chartManager.getDataMap().isEmpty()){
            trackMapInternalFrame = new GPSGraphInternalFrame(this);
        } else {
            trackMapInternalFrame = new GPSGraphInternalFrame(this, chartManager.getDataMap());
        }
        trackMapInternalFrame.setVisible(true);
        
        //adds the trackMapInternalFrame to a list, to keep track of them
        trackMaps.add(trackMapInternalFrame);
        //sets the location and size of the trackMapInternalFrame
        Dimension frameSize = this.getSize();
        trackMapInternalFrame.setLocation((frameSize.width/4)*3, 0);
        trackMapInternalFrame.setSize((frameSize.width/4)-18, (int) (frameSize.height/2.5));
        
        //adds the trackMapInternalFrame to the DataAnalyzer frame
        getContentPane().add(trackMapInternalFrame);
    }//GEN-LAST:event_showTrackMapActionPerformed

    /**
     * Apply the default theme
     * @param evt 
     */
    private void defaultTheme_menuitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_defaultTheme_menuitemActionPerformed
        currTheme = Theme.DEFAULT;
        //light theme parameters
        UIManager.put( "control", new Color(214,217,223) );
        UIManager.put( "info", new Color(242,242,189) );
        UIManager.put( "nimbusBase", new Color(51,98,140) );
        UIManager.put( "nimbusAlertYellow", new Color(255,220,35) );
        UIManager.put( "nimbusDisabledText", new Color(142,143,145) );
        UIManager.put( "nimbusFocus", new Color(115,164,209));
        UIManager.put( "nimbusGreen", new Color(176,179,50) );
        UIManager.put( "nimbusInfoBlue", new Color(47,92,180));
        UIManager.put( "nimbusLightBackground", new Color(255,255,255));
        UIManager.put( "nimbusOrange", new Color(191,98,4) );
        UIManager.put( "nimbusRed", new Color(169,46,34) );
        UIManager.put( "nimbusSelectedText", new Color( 255, 255, 255) );
        UIManager.put( "nimbusSelectionBackground", new Color(57,105,138) );
        UIManager.put( "text", new Color(0,0,0) );
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
        SwingUtilities.updateComponentTreeUI(this);
        //apply new theme everywhere
        for(ChartAssembly ca : chartManager.getCharts())
            ca.applyNewTheme(currTheme);
    }//GEN-LAST:event_defaultTheme_menuitemActionPerformed

    /**
     * Apply the system default theme
     * @param evt 
     */
    private void systemTheme_menuitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_systemTheme_menuitemActionPerformed
        currTheme = Theme.SYSTEM;
        try {
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DataAnalyzer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DataAnalyzer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DataAnalyzer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DataAnalyzer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        SwingUtilities.updateComponentTreeUI(this);
        //apply new theme everywhere
        for(ChartAssembly ca : chartManager.getCharts())
            ca.applyNewTheme(currTheme);
    }//GEN-LAST:event_systemTheme_menuitemActionPerformed

    /**
     * Apply the dark theme
     * @param evt 
     */
    private void darkTheme_menuitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_darkTheme_menuitemActionPerformed
        currTheme = Theme.DARK;
        //dark
        UIManager.put( "control", new Color( 128, 128, 128));
        UIManager.put( "info", new Color(128,128,128));
        UIManager.put( "nimbusBase", new Color( 18, 30, 49));
        UIManager.put( "nimbusAlertYellow", new Color( 248, 187, 0));
        UIManager.put( "nimbusDisabledText", new Color( 128, 128, 128));
        UIManager.put( "nimbusFocus", new Color(115,164,209));
        UIManager.put( "nimbusGreen", new Color(176,179,50));
        UIManager.put( "nimbusInfoBlue", new Color( 66, 139, 221));
        UIManager.put( "nimbusLightBackground", new Color( 18, 30, 49));
        UIManager.put( "nimbusOrange", new Color(191,98,4));
        UIManager.put( "nimbusRed", new Color(169,46,34));
        UIManager.put( "nimbusSelectedText", new Color( 255, 255, 255));
        UIManager.put( "nimbusSelectionBackground", new Color( 104, 93, 156));
        UIManager.put( "text", new Color( 230, 230, 230));
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
        SwingUtilities.updateComponentTreeUI(this);
        //apply new theme everywhere
        for(ChartAssembly ca : chartManager.getCharts())
            ca.applyNewTheme(currTheme);
    }//GEN-LAST:event_darkTheme_menuitemActionPerformed

    private void checkVitals_menuitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkVitals_menuitemActionPerformed
        new VitalsDialog(this, true, chartManager.getDataMap()).setVisible(true);
    }//GEN-LAST:event_checkVitals_menuitemActionPerformed
  
    public void invertRangeMarkersActive() {
        //invert showing range markers
        rangeMarkersActive = !rangeMarkersActive;
        //change text
        if(showRangeMarkersMenuItem.getText().equals("Show Range Markers")) {
            showRangeMarkersMenuItem.setText("Hide Range Markers");
        } else {
            showRangeMarkersMenuItem.setText("Show Range Markers");
        }
        
        //for each chart already active, flip the range active
        for(ChartAssembly chart : chartManager.getCharts()) {
            chart.getOverlay().invertRangeActive();
        }
    }
    
    private void importVehicleData(String filepath) {
        //create scanner to read file
        Scanner scanner = null;
        try {
            //try to initiate with given filepath
            scanner = new Scanner(new File(filepath));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DataAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
            // error message displayed
            new MessageBox(this, "Error: File not found", true).setVisible(true);
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
        chartManager.getVehicleData().applyVehicleData(sb.toString());
        
    }
    
    private void saveVehicleData(String filename) {
        //get the string of the data
        String sb = chartManager.getVehicleData().getStringOfData();
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

    public void importTXT(String filepath) {
        
        openingAFile = true;
        
        //show loading screen
        LoadingDialog loading = new LoadingDialog();
        loading.setVisible(true);
        
        SwingWorker worker = new SwingWorker<Void, Void>() {
            
            public Void doInBackground() {
                TXTParser.parse(chartManager.getDataMap(), chartManager.getStaticMarkers(), filepath, 0);
                return null;
            }

            @Override
            public void done() {
                openingAFile = false;
                loading.stop();
            }
        };
        
        worker.execute();
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
                            chartManager.getDataMap().put(new SimpleLogObject(tag, Double.parseDouble(values[1]), Long.parseLong(values[0])));
                        else
                            chartManager.getDataMap().put(new FunctionOfLogObject(tag, Double.parseDouble(values[1]), Double.parseDouble(values[0])));
                    } else {
                        ValueMarker v = new ValueMarker(Double.parseDouble(line));
                        v.setPaint(Color.BLUE);
                        chartManager.getStaticMarkers().put(new CategorizedValueMarker(tag, v));
                    }
                }
                
            }
        } catch (FileNotFoundException x) {
            // Error message displayed
            new MessageBox(this, "Error: File not found", true).setVisible(true);
        }
        
        
    }
    
    public void applyPE3PostProcessing() {
        //Change PE3 -> our standards. (So fuel mapper and such work)
        
        int origsize = chartManager.getDataMap().getTags().size();
        ArrayList<String> origtags = new ArrayList<>(); 
        origtags.addAll(chartManager.getDataMap().getTags());
        
        for(int i = 0; i < origsize; i++) {
            String[] split = origtags.get(i).split(",");
            String newTag;
            if(split[1].indexOf('[', 2) != -1)
                newTag = split[1].substring(1, split[1].indexOf('[', 2));
            else
                newTag = split[1].substring(1, split[1].length() - 1);
            EquationEvaluater.evaluate("$(" + origtags.get(i) + ")", chartManager.getDataMap(), newTag);
            
        }
        
        for(String tag : origtags) {
            chartManager.getDataMap().remove(tag);
        }
        
        if(chartManager.getDataMap().tags.contains("Time,MeasuredAFR#1") && chartManager.getDataMap().tags.contains("Time,MeasuredAFR#2")) {
            EquationEvaluater.evaluate("($(Time,MeasuredAFR#1) + $(Time,MeasuredAFR#2)) / 2 ", chartManager.getDataMap(), "Time,AFRAveraged");
            EquationEvaluater.evaluate("$(Time,AFRAveraged) / 14.7", chartManager.getDataMap(), "Time,Lambda");
        }
        
        if(chartManager.getDataMap().tags.contains("Time,Analog#5")) {
            EquationEvaluater.evaluate("100 * ($(Time,Analog#5) - .5) / (4.5 - .5)", chartManager.getDataMap(), "Time,OilPressure");
        }
        
        if(chartManager.getDataMap().tags.contains("Time,Analog#6")) {
            EquationEvaluater.evaluate("((($(Time,Analog#6) + .055) / .55) - 3) * (0 - 1.818) * (0 - 1)", chartManager.getDataMap(), "Time,yAccel");
        }
        
        if(chartManager.getDataMap().tags.contains("Time,Analog#7")) {
            EquationEvaluater.evaluate("((($(Time,Analog#7) + .04) / .55) - 3) * (0 - 1.1724) * (0 - 1)", chartManager.getDataMap(), "Time,xAccel");

        }
        if(chartManager.getDataMap().tags.contains("Time,Analog#8")) {
            EquationEvaluater.evaluate("((($(Time,Analog#8) + .83) / .55) - 3) * 3.7037", chartManager.getDataMap(), "Time,zAccel");
        }
        
        //now we have unoriented xyz 
        LinkedList<LogObject> rotXAccel = new LinkedList<>();
        LinkedList<LogObject> rotYAccel = new LinkedList<>();
        LinkedList<LogObject> rotZAccel = new LinkedList<>();
        
        //get x y z data as arrays
        ArrayList<LogObject> x,y,z;
        x = new ArrayList<>(chartManager.getDataMap().getList("Time,xAccel"));
        y = new ArrayList<>(chartManager.getDataMap().getList("Time,yAccel"));
        z = new ArrayList<>(chartManager.getDataMap().getList("Time,zAccel"));
        
        //desired calibration
        double[] desired = new double[3];
        desired[0] = 0;
        desired[1] = 0;
        desired[2] = 1;

        //current rotation
        double[] have = new double[3];
        have[0] = -.21316;
        have[1] = .116;
        have[2] = .91;
        LinkedList<LogObject> newX = new LinkedList<>();
        LinkedList<LogObject> newY = new LinkedList<>();
        LinkedList<LogObject> newZ = new LinkedList<>();
        
        //get rotation matrix
        double[][] rot = Mathematics.rotationMatrix3(have, desired);
        
        //for each accel value apply rotation matrix
        for(int i = 0; i < x.size(); i++) {
            //get current accel values
            double xVal = 0, yVal = 0, zVal = 0;
            if(x.get(i) instanceof SimpleLogObject) {
                xVal = ((SimpleLogObject)x.get(i)).getValue();
            }
            if(y.get(i) instanceof SimpleLogObject) {
                yVal = ((SimpleLogObject)y.get(i)).getValue();
            }
            if(z.get(i) instanceof SimpleLogObject) {
                zVal = ((SimpleLogObject)z.get(i)).getValue();
            }
            //apply rotation
            double[] rotated = Mathematics.multiplyVector3(new double[] {xVal, yVal, zVal}, rot);
            //add to new list
            newX.add(new SimpleLogObject("Time,rotX", rotated[0], x.get(i).getTime()));
            newY.add(new SimpleLogObject("Time,rotY", rotated[1], y.get(i).getTime()));
            newZ.add(new SimpleLogObject("Time,rotZ", rotated[2], z.get(i).getTime()));
        }
        
        //save to dataset
        chartManager.getDataMap().put(newX);
        chartManager.getDataMap().put(newY);
        chartManager.getDataMap().put(newZ);
        
    }
    
    private double[][] getRotMax() {
        //https://books.google.ie/books?id=VTy6BQAAQBAJ&pg=PA7&lpg=PA7&dq=pre-rotation,+tilt+post-rotation+matrices&source=bl&ots=Py9GXtE7Io&sig=xfur3P7sv_XaR9ihOAsPXvgGiWw&hl=en&sa=X&ved=0ahUKEwiCmc3V0YfLAhXFPRoKHZuODpMQ6AEIKDAC#v=onepage&q&f=false
        //inputs xyz
        //input as a array
        double x = -.21316;
        double y = .11;
        double z = -.94276;
        double[] input = new double[] {x, y, z};
        //theta = cos^-1(z)
        double theta = 1/Math.cos(z);
        //phi = tan^-1(x/y)
        double phi = 1/Math.tan(x/y);
        //rTheta mat
        double[][] rTheta = {
            {Math.cos(theta), Math.sin(theta), 0},
            {-1 * Math.sin(theta), Math.cos(theta), 0},
            {0, 0, 1}
        };
        //rPhi mat
        double[][] rPhi = {
            {Math.cos(phi), 0, -Math.sin(phi)},
            {0, 1, 0},
            {Math.sin(phi), 0, Math.cos(phi)}
        };
        //do rTheta * rPhi * input
        double[][] rThetaCrossrPhi = Mathematics.multiplyMatrices3(rTheta, rPhi);
        double[] primes = Mathematics.multiplyVector3(input, rThetaCrossrPhi);
        //get alpha from primes calculated
        double alpha = 1/Math.tan(primes[1]/primes[0]);
        //create rAlpha mat
        double[][] rAlpha = {
            {Math.cos(alpha), 0, -Math.sin(alpha)},
            {0, 1, 0},
            {Math.sin(alpha), 0, Math.cos(alpha)}
        };
        
        //do rAlpha * rTheta * rPhi which gives rotation matrix
        return Mathematics.multiplyMatrices3(Mathematics.multiplyMatrices3(rAlpha, rTheta), rPhi);
        
    }
   
    public void applyPostProcessing() {
        //if nothing was loaded do not try to do math channels
        if(chartManager.getDataMap().isEmpty())
            return;
        
        //load wheelspeed averages
        
        //calculate front wheel speed averages
        if(chartManager.getDataMap().tags.contains("Time,WheelspeedFR") && chartManager.getDataMap().tags.contains("Time,WheelspeedFL"))
            EquationEvaluater.evaluate("($(Time,WheelspeedFR)) + ($(Time,WheelspeedFL)) / 2", chartManager.getDataMap(), "Time,WheelspeedFront");
        
        //calculate rear wheel speed averages
        if(chartManager.getDataMap().tags.contains("Time,WheelspeedRR") && chartManager.getDataMap().tags.contains("Time,WheelspeedRL"))
            EquationEvaluater.evaluate("($(Time,WheelspeedRR)) + ($(Time,WheelspeedRL)) / 2", chartManager.getDataMap(), "Time,WheelspeedRear");
        
        //calculate full average
        if(chartManager.getDataMap().tags.contains("Time,WheelspeedRear") && chartManager.getDataMap().tags.contains("Time,WheelspeedFront"))
            EquationEvaluater.evaluate("($(Time,WheelspeedRear)) + ($(Time,WheelspeedFront)) / 2", chartManager.getDataMap(), "Time,WheelspeedAvg");
        
        //Create time vs distance
        if(chartManager.getDataMap().tags.contains("Time,WheelspeedFront"))
            EquationEvaluater.evaluate("($(Time,WheelspeedFront) + (2 * 3.14159 * 10.2)", chartManager.getDataMap(), "Time,Distance");
        
        //Create sucky sucky in asain accent
        if(chartManager.getDataMap().tags.contains("Time,Barometer") && chartManager.getDataMap().tags.contains("Time,MAP")) {
            EquationEvaluater.evaluate("($(Time,Barometer)) - ($(Time,MAP))", chartManager.getDataMap(), "Time,SuckySucky");
        }
        
        //Create Average of Analog in 5v form
        if(chartManager.getDataMap().tags.contains("Time,Analog3") && chartManager.getDataMap().tags.contains("Time,Analog4")) {
            EquationEvaluater.evaluate("(($(Time,Analog3) + ($(Time,Analog4)))/2)", chartManager.getDataMap(), "Time,Lamda5VAveraged");
        }
        
        if(chartManager.getDataMap().tags.contains("Time,TransmissionTeeth")) {
            EquationEvaluater.evaluate("($(Time,TransmissionTeeth)/23)*(23/27)*60", chartManager.getDataMap(), "Time,TransRPM");
        }
        
        if(chartManager.getDataMap().tags.contains("Time,TransRPM") && chartManager.getDataMap().tags.contains("Time,RPM")) {
            EquationEvaluater.evaluate("($(Time,RPM)/1.822) / $(Time,TransRPM)", chartManager.getDataMap(), "Time,GearRatio", 0, 10);
        }
        
        
        //average the 5V output to AFR
        //convert to AFR
        if(chartManager.getDataMap().tags.contains("Time,Lamda5VAveraged")) {
            EquationEvaluater.evaluate("2 * $(Time,Lamda5VAveraged) + 10", chartManager.getDataMap(), "Time,AFRAveraged");
        }
        
        if(chartManager.getDataMap().tags.contains("Time,Analog1")) {
            EquationEvaluater.evaluate("(($(Time,Analog1)-.5)*(5000-0))/(4.5-.5)", chartManager.getDataMap(), "Time,BrakePressureFront");
        }
        if(chartManager.getDataMap().tags.contains("Time,Analog2")) {
            EquationEvaluater.evaluate("(($(Time,Analog2)-.5)*(5000-0))/(4.5-.5)", chartManager.getDataMap(), "Time,BrakePressureRear");
        }
        //TODO: REDO ALL OF THESE VALUES.
        if(chartManager.getDataMap().tags.contains("Time,BrakePressureRear") && chartManager.getDataMap().tags.contains("Time,BrakePressureRear")) {
            //calculate force on caliper pistons
            EquationEvaluater.evaluate("($(Time,BrakePressureFront)*(3.14*.00090792))", chartManager.getDataMap(), "Time,ForceOnCaliperPistonFront");
            EquationEvaluater.evaluate("($(Time,BrakePressureRear)*(3.14*.000706858))", chartManager.getDataMap(), "Time,ForceOnCaliperPistonRear");
            
            //calcuate torque
            EquationEvaluater.evaluate("($(Time,ForceOnCaliperPistonFront)*.106588*2)", chartManager.getDataMap(), "Time,EffectiveBrakeTorqueFront");
            EquationEvaluater.evaluate("($(Time,ForceOnCaliperPistonRear)*.0823*2)", chartManager.getDataMap(), "Time,EffectiveBrakeTorqueRear");
            
        }
        //TODO: what if no brakes are applied, divide by 0 error. above 5.1
        if(chartManager.getDataMap().tags.contains("Time,EffectiveBrakeTorqueFront") && chartManager.getDataMap().tags.contains("Time,EffectiveBrakeTorqueRear")) {
            EquationEvaluater.evaluate("$(Time,EffectiveBrakeTorqueFront)/($(Time,EffectiveBrakeTorqueFront) + $(Time,EffectiveBrakeTorqueRear))", chartManager.getDataMap(), "Time,BrakeBalance", 0, 1);
        }

        //Perform Operations
        //TODO: FILTERING
        if(chartManager.getDataMap().tags.contains("Time,Coolant")) {
            EquationEvaluater.evaluate("($(Time,Coolant)-32)*(5/9)", chartManager.getDataMap(), "CoolantCelcius");
        }
        
        //Create Distance Channels for all datasets that do not contain "Time"
        for(int i = 0; i < chartManager.getDataMap().table.length; i++) {
            if(chartManager.getDataMap().table[i] != null && !chartManager.getDataMap().table[i].isEmpty() && chartManager.getDataMap().table[i].getFirst().getTAG().contains("Time")) {
                if(!chartManager.getDataMap().table[i].getFirst().getTAG().equals("Time,Distance"))
                    EquationEvaluater.evaluate("$(" + chartManager.getDataMap().table[i].getFirst().getTAG() + ") asFunctionOf($(Time,Distance))", 
                            chartManager.getDataMap(), 
                            chartManager.getDataMap().table[i].getFirst().getTAG().substring(chartManager.getDataMap().table[i].getFirst().getTAG().indexOf(",") + 1, 
                            chartManager.getDataMap().table[i].getFirst().getTAG().length()));
            }
        }
        
        //finish file operations
        openingAFile = false;
    }
    
    //TODO: REDO TO MATCH PE3 STYLE
    public String hashMapToCSV(ArrayList<String> tags) {
        //output String
        StringBuilder out = new StringBuilder();
        //for each tag
        for(String tag : tags) {
            //get the tags data
            LinkedList<LogObject> los = chartManager.getDataMap().getList(tag);
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
        for(String tag : chartManager.getDataMap().tags) {
            //output the tag
            toReturn.append(tag);
            toReturn.append("\n");
            //get the list of data for the current tag
            List<LogObject> data = chartManager.getDataMap().getList(tag);
            //for each data element
            for(LogObject lo : data) {
                //output the data
                toReturn.append(lo.toString());
                toReturn.append("\n");
            }
            //output MARKERS
            toReturn.append("MARKERS\n");
            //get the markers for the current tag
            List<CategorizedValueMarker> markers = chartManager.getStaticMarkers().getList(tag);
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
    
    //save file
    private void saveFile(String filename) {
        //add normal data
        StringBuilder sb = new StringBuilder(getStringOfData());
        //append vehicle dynamic data
        sb.append("VEHICLEDYNAMICDATA");
        sb.append("\n");
        sb.append(chartManager.getVehicleData().getStringOfData());
        //append lap data
        sb.append("LAPDATA");
        sb.append("\n");
        sb.append(Lap.getStringOfData(chartManager.getLapBreaker()));
        if(!fileNotes.isEmpty()) {
            sb.append("FILENOTES\n");
            sb.append(fileNotes);
        }
        
        String chosenFileName = "";
        
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
                        this.setTitle("DataAnalyzer - " + chooser.getSelectedFile().getName());
                    //exception handling
                    } catch (IOException e) {
                        //error message displayed
                        new MessageBox(this, "Error: FileWriter could not be opened", true).setVisible(true);
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
                        this.setTitle("DataAnalyzer - " + chooser.getSelectedFile().getName());
                    //exception handling
                    } catch (IOException e) {
                        //error message displayed
                        new MessageBox(this, "Error: FileWriter could not be opened", true).setVisible(true);
                    }
                }
            } else {
                //error message displayed
                new MessageBox(this, "Error: File could not be approved", true).setVisible(true);
            }
            
        } else { //if a filename was already provided
            //try to write the file
            try(FileWriter fw = new FileWriter(new File(filename))) {
                fw.write(sb.toString());
                fw.close();
                //Display Success Toast
                Toast.makeToast(this, "Saved file", Toast.DURATION_LONG);
                this.setTitle("DataAnalyzer - " + filename);
            } catch (IOException e) {
                //error message displayed
                new MessageBox(this, "Error: File could not be written to", true).setVisible(true);
            }
        }
    }
        
    private void openFile(String filepath) {
        openFile(new String[] {filepath});
        clearAllTrackMaps();
        showTrackMapActionPerformed(null);
    }
    //open file
    private void openFile(String[] filepaths) {
        
        //show loading screen
        LoadingDialog loading = new LoadingDialog();
        loading.setVisible(true);
        
        //holds the context to give into the swing worker
        DataAnalyzer me = this;
       
        SwingWorker worker = new SwingWorker<Void, Void>() {
            public Void doInBackground() {
                boolean first = true;
                for(String filepath : filepaths) {
                    if(!first) {
                        DataAnalyzer da = new DataAnalyzer();
                        //begin file operation
                        openingAFile = true;
                        //Scanner to handle the file
                        Scanner scanner = null;
                        try {
                            scanner = new Scanner(new File(filepath));
                        } catch(FileNotFoundException e) {
                            //error message displayed
                            new MessageBox(me, "Error: File could not be opened", true).setVisible(true);
                        }

                        //if we failed to open the file exit
                        if(scanner == null) {
                            return null;
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
                                        chartManager.getDataMap().put(new SimpleLogObject(tag, Double.parseDouble(values[1]), Long.parseLong(values[0])));
                                    else
                                        chartManager.getDataMap().put(new FunctionOfLogObject(tag, Double.parseDouble(values[1]), Double.parseDouble(values[0])));
                                } else {
                                    String[] split = line.split(",");
                                    if(split.length == 2) {
                                        ValueMarker v = new ValueMarker(Double.parseDouble(split[0]));
                                        v.setPaint(Color.BLUE);
                                        chartManager.getStaticMarkers().put(new CategorizedValueMarker(tag, v, split[1]));
                                    } else if(split.length == 1) {
                                        ValueMarker v = new ValueMarker(Double.parseDouble(split[0]));
                                        v.setPaint(Color.BLUE);
                                        chartManager.getStaticMarkers().put(new CategorizedValueMarker(tag, v));
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
                                chartManager.getLapBreaker().add(new Lap(lapStart, lapStop, lapNumber, lapLabel));
                            else
                                chartManager.getLapBreaker().add(new Lap(lapStart, lapStop, lapNumber));
                        }

                        //give the data to the vehicleData class to create
                        chartManager.getVehicleData().applyVehicleData(vd.toString());

                        //we are finished with file operation
                        openingAFile = false;

                        //update lap data
                        Lap.applyToDataset(chartManager.getDataMap(), chartManager.getLapBreaker());
                    } else {
                        //begin file operation
                        openingAFile = true;
                        //Scanner to handle the file
                        Scanner scanner = null;
                        try {
                            scanner = new Scanner(new File(filepath));
                        } catch(FileNotFoundException e) {
                            //error message displayed
                            new MessageBox(me, "Error: File could not be opened", true).setVisible(true);
                        }

                        //if we failed to open the file exit
                        if(scanner == null) {
                            return null;
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
                                        chartManager.getDataMap().put(new SimpleLogObject(tag, Double.parseDouble(values[1]), Long.parseLong(values[0])));
                                    else
                                        chartManager.getDataMap().put(new FunctionOfLogObject(tag, Double.parseDouble(values[1]), Double.parseDouble(values[0])));
                                } else {
                                    String[] split = line.split(",");
                                    if(split.length == 2) {
                                        ValueMarker v = new ValueMarker(Double.parseDouble(split[0]));
                                        v.setPaint(Color.BLUE);
                                        chartManager.getStaticMarkers().put(new CategorizedValueMarker(tag, v, split[1]));
                                    } else if(split.length == 1) {
                                        ValueMarker v = new ValueMarker(Double.parseDouble(split[0]));
                                        v.setPaint(Color.BLUE);
                                        chartManager.getStaticMarkers().put(new CategorizedValueMarker(tag, v));
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

                            if(line.equals(("FILENOTES"))) {
                                break;
                            }

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
                                chartManager.getLapBreaker().add(new Lap(lapStart, lapStop, lapNumber, lapLabel));
                            else
                                chartManager.getLapBreaker().add(new Lap(lapStart, lapStop, lapNumber));
                        }

                        //either we have alre ady reached the end of the file, or we break the last loop at "FILENOTES"
                        while(scanner.hasNextLine()) {
                            fileNotes += scanner.nextLine();
                        }

                        //give the data to the vehicleData class to create
                        chartManager.getVehicleData().applyVehicleData(vd.toString());

                        //we are finished with file operation
                        openingAFile = false;

                        //update lap data
                        Lap.applyToDataset(chartManager.getDataMap(), chartManager.getLapBreaker());
                    }
                }
                
                return null;
            }

            @Override
            public void done() {
                loading.stop();
            }
        };
        
        worker.execute();
        
        
    }
    
    /**
     * Opens files that are formatted in PE3 style.
     * @param filepaths 
     */
    private void openPE3Files(File[] files) throws FileNotFoundException {
        
        //ask for post processing
        boolean applyPostProcessing = askForPostProcessing();
        
        LoadingDialog loading = new LoadingDialog();
        loading.setVisible(true);
        
        SwingWorker worker = new SwingWorker<Void, Void>() {
            
            public Void doInBackground() throws FileNotFoundException {
                //handle the first file to not open a new screen
                boolean first = true;

                //holds number of files opened
                int num = 0;

                //for each file
                for(File file : files) {
                    //if its the first file we don't need to do this in a new window.
                    if(first) {
                        //Create way to read file
                        Scanner scan = new Scanner(file);
                        //get the first line which tells us the order of parameters
                        String header = scan.nextLine();
                        //store these as an array of keys
                        String[] keys = header.split(",");
                        //for each remaining line
                        while(scan.hasNextLine()) {
                            //get the next line
                            String line = scan.nextLine();
                            //if its empty move forward which will skip corrupted lines or end
                            if(line.isEmpty())
                                continue;

                            //all the data should be split by commas in the same order as the header
                            String[] data = line.split(",");
                            //if line does not match the format of the header, skip
                            if(data.length != keys.length)
                                continue;
                            //the first element is time
                            double timeInSeconds = Double.parseDouble(data[0]);

                            long time = (long) (timeInSeconds*1000);
                            //for each of the remaining columns
                            for(int i = 1; i < data.length; i++) {
                                //add this element to the datamap
                                chartManager.getDataMap().put(new SimpleLogObject(("Time,(" + keys[i] + ")").replace("(", "[").replace(")", "]").replace(" ", ""), Double.parseDouble(data[i]), time));
                            }

                        }

                        //set title
                        setTitle("DataAnalyzer - " + file.getName());

                        //no longer first
                        first = false;
                        num++;

                        //apply post processing
                        if(applyPostProcessing) {
                            applyPE3PostProcessing();
                            applyPostProcessing();
                        }

                    }
                    else {
                        DataAnalyzer dataAnalyzer = new DataAnalyzer();
                        //Create way to read file
                        Scanner scan = new Scanner(file);
                        //get the first line which tells us the order of parameters
                        String header = scan.nextLine();
                        //store these as an array of keys
                        String[] keys = header.split(",");
                        //for each remaining line
                        while(scan.hasNextLine()) {
                            //get the next line
                            String line = scan.nextLine();
                            //if its empty move forward which will skip corrupted lines or end
                            if(line.isEmpty())
                                continue;

                            //all the data should be split by commas in the same order as the header
                            String[] data = line.split(",");
                            //the first element is time
                            double timeInSeconds = Double.parseDouble(data[0]);

                            long time = (long) (timeInSeconds*1000);
                            //for each of the remaining columns
                            for(int i = 1; i < data.length; i++) {
                                //add this element to the datamap
                                dataAnalyzer.chartManager.getDataMap().put(new SimpleLogObject(("Time,(" + keys[i] + ")").replace("(", "[").replace(")", "]").replace(" ", ""), Double.parseDouble(data[i]), time));
                            }

                        }

                        //set title
                        dataAnalyzer.setTitle("DataAnalyzer - " + file.getName());

                        //apply post processing
                        if(applyPostProcessing) {
                            dataAnalyzer.applyPE3PostProcessing();
                            dataAnalyzer.applyPostProcessing();
                        }

                        //set visible and location
                        dataAnalyzer.setVisible(true);
                        dataAnalyzer.setLocation(100 * num, 100 * num);

                        //opened another file
                        num++;
                    }

                }
                
                return null;
            }

            public void done() {
                //Destroy the Loading Dialog
                loading.stop();
            }
        };
        
        worker.execute();
        
    }
    
    //returns chartManager
    public ChartManager getChartManager() {
        return chartManager;
    }
    
    /**
     * Ask the user if they want to create a vehicle before the auto dataset creation takes place
     * @return returns true if not cancel false if cancel was pressed.
     */
    private boolean askForVehicle() {
        //holds the return code by reference
        ReturnCode returnCode = new ReturnCode();
        //create the Dialog to ask the user
        AskVehicleDialog avd = new AskVehicleDialog(this, true, returnCode);
        avd.setVisible(true);
        //wait for a return code
        while(returnCode.getCode() == 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(DataAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        //the boolean to return.
        boolean toReturn = true;
        
        //create a vehicle data dialog just in case we need it, for proper encapsulation
        VehicleDataDialog vdd = new VehicleDataDialog(this, true, chartManager.getVehicleData(), "Create");
        //depending on our return code
        switch(returnCode.getCode()) {
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
    
    /**
     * Displays a confirm dialog to apply post processing, useful for unsynced data
     * @return true if yes is pressed, false if no.
     */
    private boolean askForPostProcessing() {
        return createConfirmDialog("Post Processing", "Would you like to apply post processing? Click no for unsynced data.");
    }
    
    /**
     * Creates a confirm dialog with given parameters, returns which button was pressed
     * @param title title of the dialog
     * @param message body message of the dialog
     * @return true if yes is pressed, false if no.
     */
    public static boolean createConfirmDialog(String title, String message) {
        if (JOptionPane.showConfirmDialog(null, message, title,
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            return true;
        } else {
            return false;
        }
    }
    
    //returns trackMaps
    public ArrayList<GPSGraphInternalFrame> getTrackMap() {
        return trackMaps;
    }
    
    //return lambdaMaps
    public ArrayList<JInternalFrame> getLambdaMap() {
        return lambdaMaps;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem addChartMenuItem;
    private javax.swing.JMenuItem addLapConditionMenuItem;
    private javax.swing.JMenuItem addMathChannelButton;
    private javax.swing.JMenuItem addNotesMenuItem;
    private javax.swing.JMenuItem checkVitals_menuitem;
    private javax.swing.JMenuItem closeMenuItem;
    private javax.swing.JMenuItem darkTheme_menuitem;
    private javax.swing.JMenuItem defaultTheme_menuitem;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem editVehicleMenuItem;
    private javax.swing.JMenuItem engineChartSetup;
    private javax.swing.JMenu engineMenu;
    private javax.swing.JMenuItem exportMenuItem;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuItem fullscreenMenuItem;
    private javax.swing.JMenuItem importECUDataMenuItem;
    private javax.swing.JMenuItem importVehicleMenuItem;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem newImportMenuItem;
    private javax.swing.JMenuItem newVehicleMenuItem;
    private javax.swing.JMenuItem newWindowMenuItem;
    private javax.swing.JMenuItem openBtn;
    private javax.swing.JMenuItem resetMenuItem;
    private javax.swing.JMenuItem saveAsMenuItem;
    private javax.swing.JMenuItem saveMenuButton;
    private javax.swing.JMenuItem saveVehicleMenuItem;
    private javax.swing.JMenuItem showLambdaMap;
    private javax.swing.JMenuItem showRangeMarkersMenuItem;
    private javax.swing.JMenuItem singleViewMenuItem;
    private javax.swing.JMenuItem swapChartsMenuItem;
    private javax.swing.JMenuItem trackMapMenuItem;
    private javax.swing.JMenuItem systemTheme_menuitem;
    private javax.swing.JMenuItem twoHorizontalMenuItem;
    private javax.swing.JMenuItem twoVerticalMenuItem;
    private javax.swing.JMenu vehicleMenu;
    private javax.swing.JMenu viewMenu;
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
    
    //Enumeration definition for Theme
    public enum Theme {
        DEFAULT, SYSTEM, DARK;
    }
}
