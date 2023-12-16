/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer.dialog;

import com.arib.toast.Toast;
import dataanalyzer.CategoricalHashMap;
import dataanalyzer.ChartAssembly;
import dataanalyzer.DataAnalyzer;
import dataanalyzer.Dataset;
import dataanalyzer.DatasetSelection;
import dataanalyzer.FunctionOfLogObject;
import dataanalyzer.Installer;
import dataanalyzer.LogObject;
import dataanalyzer.Selection;
import dataanalyzer.ScreenLocation;
import dataanalyzer.SimpleLogObject;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

/**
 *
 * @author aribdhuka
 */
public class VitalsDialog extends javax.swing.JDialog {

    JPanel mainPanel;
    ArrayList<Vital> vitals;
    Dataset dataset;
    DataAnalyzer parent;

    /**
     * Creates new form VitalsDialog
     */
    public VitalsDialog(DataAnalyzer parent, boolean modal, Dataset dataset) {
        super(parent, modal);
        this.parent = parent;
        vitals = new ArrayList<>();
        this.dataset = dataset;
        initComponents();
        ScreenLocation.getInstance().calculateCenter(this);
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        this.setContentPane(mainPanel);
        mainPanel.setVisible(true);
        try {
            loadVitals();
        } catch(FileNotFoundException e) {
            //this should never happen
            Toast.makeToast(this, "Couldn't find vitals file! Starting fresh!", Toast.DURATION_LONG);
        }
        runVitals(dataset.getDataMap());
        setupButtons();
    }
    
    /**
     * Creates new form VitalsDialog
     * WILL CAUSE ERROR WITH EDITING VITALS!
     * @deprecated 
     */
    public VitalsDialog(DataAnalyzer parent, boolean modal, LinkedList<Dataset> datasets) {
        super(parent, modal);
        this.parent = parent;
        vitals = new ArrayList<>();
        initComponents();
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        this.setContentPane(mainPanel);
        mainPanel.setVisible(true);
        try {
            loadVitals();
        } catch(FileNotFoundException e) {
            //this should never happen
            Toast.makeToast(this, "Couldn't find vitals file! Starting fresh!", Toast.DURATION_LONG);
        }
        runVitals(dataset.getDataMap());
        setupButtons();
        
    }
    
    private void loadVitals() throws FileNotFoundException {
        //get filepath from installer
        String vitalsPath = Installer.getHomePath() + "Vitals/";
        
        //open and read file
        Scanner vitalsFile = new Scanner(new File(vitalsPath + "vitals.dfrvit"));
        while(vitalsFile.hasNextLine()) {
            //channel,lowactive,highactive,lowtype,hightype,lowvalue,highvalue
            String[] attributes = vitalsFile.nextLine().split(";");
            
            Vital vital = new Vital();
            vital.setChannel(attributes[0]);
            if(attributes[1].equals("true")) {
                vital.setLowActive(true);
            } else {
                vital.setLowActive(false);
            }
            if(attributes[2].equals("true")) {
                vital.setHighActive(true);
            } else {
                vital.setHighActive(false);
            }
            vital.setLowType(VitalType.valueOf(attributes[3]));
            vital.setHighType(VitalType.valueOf(attributes[4]));
            //these doubles should never be unparsable
            vital.setLowValue(Double.parseDouble(attributes[5]));
            vital.setHighValue(Double.parseDouble(attributes[6]));
            
            this.vitals.add(vital);
        }
        
    }
    
    private void addLog(String text) {
        Box box = Box.createHorizontalBox();
        JLabel image = new JLabel();
        image.setIcon(UIManager.getIcon("OptionPane.informationIcon"));
        box.add(image);
        box.add(new JLabel(text));
        box.setVisible(true);
        box.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                String channel = text.split(" ")[0];
                if(!dataset.getDataMap().getTags().contains(channel)) {
                    return;
                }
                ArrayList<String> selectedTags = new ArrayList<>();
                selectedTags.add(channel);
                DatasetSelection ds = new DatasetSelection(dataset, selectedTags, new ArrayList<Integer>());
                LinkedList<DatasetSelection> dss = new LinkedList<>();
                dss.add(ds);
                Selection selection = new Selection(dss);
                ChartAssembly ca = parent.getChartManager().addChart();
                ca.setSelection(selection);
                ca.setChart(selection.getUniqueTags().toArray(new String[selection.getUniqueTags().size()]));
                try {
                    ca.getChartFrame().setSelected(true);
                } catch (PropertyVetoException ex) {
                    Logger.getLogger(VitalsDialog.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        });
        mainPanel.add(box);
    }

    private void addError(String text) {
        Box box = Box.createHorizontalBox();
        JLabel image = new JLabel();
        image.setIcon(UIManager.getIcon("OptionPane.errorIcon"));
        box.add(image);
        box.add(new JLabel(text));
        box.setVisible(true);
        box.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                String channel = text.split(" ")[0];
                if(!dataset.getDataMap().getTags().contains(channel)) {
                    return;
                }
                ArrayList<String> selectedTags = new ArrayList<>();
                selectedTags.add(channel);
                DatasetSelection ds = new DatasetSelection(dataset, selectedTags, new ArrayList<Integer>());
                LinkedList<DatasetSelection> dss = new LinkedList<>();
                dss.add(ds);
                Selection selection = new Selection(dss);
                ChartAssembly ca = parent.getChartManager().addChart();
                ca.setSelection(selection);
                ca.setChart(selection.getUniqueTags().toArray(new String[selection.getUniqueTags().size()]));
                try {
                    ca.getChartFrame().setSelected(true);
                } catch (PropertyVetoException ex) {
                    Logger.getLogger(VitalsDialog.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        });
        mainPanel.add(box);
    }
    
    private void addWarning(String text) {
        Box box = Box.createHorizontalBox();
        JLabel image = new JLabel();
        image.setIcon(UIManager.getIcon("OptionPane.warningIcon"));
        box.add(image);
        box.add(new JLabel(text));
        box.setVisible(true);
        box.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                String channel = text.split(" ")[0];
                if(!dataset.getDataMap().getTags().contains(channel)) {
                    return;
                }
                ArrayList<String> selectedTags = new ArrayList<>();
                selectedTags.add(channel);
                DatasetSelection ds = new DatasetSelection(dataset, selectedTags, new ArrayList<Integer>());
                LinkedList<DatasetSelection> dss = new LinkedList<>();
                dss.add(ds);
                Selection selection = new Selection(dss);
                ChartAssembly ca = parent.getChartManager().addChart();
                ca.setSelection(selection);
                ca.setChart(selection.getUniqueTags().toArray(new String[selection.getUniqueTags().size()]));
                try {
                    ca.getChartFrame().setSelected(true);
                } catch (PropertyVetoException ex) {
                    Logger.getLogger(VitalsDialog.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        });
        mainPanel.add(box);
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 498, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    private void runVitals(List<CategoricalHashMap> dataMaps) {
        for(CategoricalHashMap dataMap : dataMaps)
            runVitals(dataMap);
    }
    
    /**
     * Checks all data signals for anomalies and auto diagnoses issues
     * @param parent JFrame to spawn dialog from
     * @param dataMap dataMap to analyze
     */
    private void runVitals(CategoricalHashMap dataMap) {
        boolean clean = true;
        boolean noChannels = true;
        for(Vital vital : vitals) {
            boolean low = false;
            boolean high = false;
            if(dataMap.getTags().contains(vital.getChannel())) {
                noChannels = false;
                for(LogObject lo : dataMap.getList(vital.getChannel())) {
                    if (lo instanceof Valueable) {
                        if(vital.isLowActive()) {
                            if(((Valueable) lo).getValue() < vital.getLowValue()) {
                                low = true;
                            }
                        }
                        if(vital.isHighActive()) {
                            if(((Valueable) lo).getValue() > vital.getHighValue()) {
                                high = true;
                            }
                        }
                    }
                }
                
                if(low) {
                    switch(vital.getLowType()) {
                        case Error:
                            addError(vital.getChannel() + " low!");
                            clean = false;
                            break;
                        case Warn:
                            addWarning(vital.getChannel() + " low!");
                            clean = false;
                            break;
                        default:
                            addLog(vital.getChannel() + " low!");
                            clean = false;
                            break;
                    }
                }
                
                if(high) {
                    switch(vital.getHighType()) {
                        case Error:
                            addError(vital.getChannel() + " high!");
                            clean = false;
                            break;
                        case Warn:
                            addWarning(vital.getChannel() + " high!");
                            clean = false;
                            break;
                        default:
                            addLog(vital.getChannel() + " high!");
                            clean = false;
                            break;
                    }
                }
                
            }
        }
        
        if(noChannels) {
            addError("I couldn't find any data boss!");
        }
        else if(clean) {
            addLog("All channels look clean chief!");
        }
    }
    
    private void setupButtons() {
        Box editBox = Box.createHorizontalBox();
        JButton editButton = new JButton();
        editButton.setText("Edit");
        editBox.add(editButton);
        editBox.setVisible(true);
        
        mainPanel.add(editBox);
        
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //create VitalsEditDialog
                new VitalsEditDialog(VitalsDialog.this, true, vitals, dataset.getDataMap()).setVisible(true);
                //reset main panel
                mainPanel = new JPanel();
                mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
                VitalsDialog.this.setContentPane(mainPanel);
                mainPanel.setVisible(true);
                //redraw components
                runVitals(dataset.getDataMap());
                setupButtons();
                //rebuild window
                VitalsDialog.this.pack();
                VitalsDialog.this.setSize(498, 300);
            }
        });
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
