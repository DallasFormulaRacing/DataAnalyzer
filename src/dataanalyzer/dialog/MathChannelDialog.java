/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer.dialog;

import dataanalyzer.CategoricalHashMap;
import dataanalyzer.Dataset;
import dataanalyzer.EquationEvaluater;
import dataanalyzer.ScreenLocation;
import dataanalyzer.dialog.MessageBox;
import dataanalyzer.VehicleData;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author aribdhuka
 */
public class MathChannelDialog extends javax.swing.JFrame {

   
    int lastIndex;
    
    LinkedList<Dataset> datasets;
    
    /**
     * Creates new form MathChannelDialog
     */
    public MathChannelDialog(LinkedList<Dataset> datasets) {
        initComponents();
        ScreenLocation.getInstance().calculateCenter(this);
        lastIndex = 0;
        this.datasets = datasets;
        configureVariablesList();
        configureDatasetsList();
        //listen to when the caret is updated
        equationField.addCaretListener((CaretEvent e) -> {
            lastIndex = e.getDot();
        });
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jCheckBox1 = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        channelTitleText = new javax.swing.JTextPane();
        createChannelButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        availableVariablesList = new javax.swing.JList<>();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        equationField = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        minTextField = new javax.swing.JTextPane();
        minRangeLabel = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        maxTextField = new javax.swing.JTextPane();
        maxLabelRange = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        appliedDatasets = new javax.swing.JList<>();
        jLabel3 = new javax.swing.JLabel();
        summationCheckBox = new javax.swing.JCheckBox();
        rateCheckBox = new javax.swing.JCheckBox();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMathChannelMenu = new javax.swing.JMenu();
        editMathChannelMenu = new javax.swing.JMenu();
        insertMathChannelMenu = new javax.swing.JMenu();
        functionOfMenuItem = new javax.swing.JMenuItem();

        jCheckBox1.setText("jCheckBox1");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jScrollPane1.setToolTipText("Channel Name");

        channelTitleText.setToolTipText("Channel Title");
        channelTitleText.setName(""); // NOI18N
        jScrollPane1.setViewportView(channelTitleText);

        createChannelButton.setBackground(new java.awt.Color(0, 122, 255));
        createChannelButton.setText("Create Channel");
        createChannelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createChannelButtonPressed(evt);
            }
        });

        availableVariablesList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(availableVariablesList);

        jLabel1.setText("Variables available to use");

        equationField.setColumns(20);
        equationField.setRows(5);
        jScrollPane3.setViewportView(equationField);

        jLabel2.setText("Channel Title:");

        jScrollPane4.setViewportView(minTextField);

        minRangeLabel.setText("Min:");

        jScrollPane5.setViewportView(maxTextField);

        maxLabelRange.setText("Max:");

        appliedDatasets.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane6.setViewportView(appliedDatasets);

        jLabel3.setText("Applied Datasets");

        summationCheckBox.setText("Summate");
        summationCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                summationCheckBoxItemStateChanged(evt);
            }
        });

        rateCheckBox.setText("Rate");
        rateCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rateCheckBoxItemStateChanged(evt);
            }
        });

        fileMathChannelMenu.setText("File");
        jMenuBar1.add(fileMathChannelMenu);

        editMathChannelMenu.setText("Edit");
        jMenuBar1.add(editMathChannelMenu);

        insertMathChannelMenu.setText("Insert");

        functionOfMenuItem.setText("Change Domain");
        functionOfMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                functionOfMenuItemActionPerformed(evt);
            }
        });
        insertMathChannelMenu.add(functionOfMenuItem);

        jMenuBar1.add(insertMathChannelMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(createChannelButton)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(jScrollPane2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(minRangeLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(maxLabelRange)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(rateCheckBox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(summationCheckBox))
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 398, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(minRangeLabel)
                                    .addComponent(maxLabelRange)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(createChannelButton)
                                        .addComponent(summationCheckBox)
                                        .addComponent(rateCheckBox)))))
                        .addGap(8, 8, 8)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void createChannelButtonPressed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createChannelButtonPressed
        //variable that handles if equation was parsed properly so far
        
        double minBound = Double.MIN_VALUE;
        double maxBound = Double.MAX_VALUE;
        if(channelTitleText.getText().isEmpty()) {
            // error message displayed
            new MessageBox(this, "Error: Equation not parsed properly", true).setVisible(true);
            return;
        }
        if(!minTextField.getText().isEmpty()) {
            try {
                minBound = Double.parseDouble(minTextField.getText());
            }  catch(NumberFormatException e) {
                new MessageBox(this, "Error: Lower bound is not a number!\nPlease enter a number or leave the field blank.", true).setVisible(true);
                return;
            }
        }
        if(!maxTextField.getText().isEmpty()) {
            try {
                maxBound = Double.parseDouble(maxTextField.getText());
            }  catch(NumberFormatException e) {
                new MessageBox(this, "Error: Upper bound is not a number!\nPlease enter a number or leave the field blank.", true).setVisible(true);
                return;
            }
        }
        
        //remove all spaces from string.
        String eq = equationField.getText();
        //Check the validity of the string
        if(minTextField.getText().isEmpty() && maxTextField.getText().isEmpty()) {
            if(summationCheckBox.isSelected()) { 
                for(Dataset dataset : getSelectedDatasets()) {
                    EquationEvaluater.summate(eq, dataset.getDataMap(), dataset.getVehicleData(), channelTitleText.getText());
                }
            }
            else if(rateCheckBox.isSelected()) {
                for(Dataset dataset : getSelectedDatasets()) {
                    EquationEvaluater.rate(eq, dataset.getDataMap(), dataset.getVehicleData(), channelTitleText.getText());
                }
            }
            else {
                for(Dataset dataset : getSelectedDatasets()) {
                    EquationEvaluater.evaluate(eq, dataset.getDataMap(), dataset.getVehicleData(), channelTitleText.getText());
                }
            }
        }
        else {
            if(summationCheckBox.isSelected()) {
                for(Dataset dataset : getSelectedDatasets()) {
                    EquationEvaluater.summate(eq, dataset.getDataMap(), dataset.getVehicleData(), channelTitleText.getText(), minBound, maxBound);
                }
            }
            else if(rateCheckBox.isSelected()) {
                for(Dataset dataset : getSelectedDatasets()) {
                    EquationEvaluater.rate(eq, dataset.getDataMap(), dataset.getVehicleData(), channelTitleText.getText());
                }
            }
            else {
                for(Dataset dataset : getSelectedDatasets()) {
                    EquationEvaluater.evaluate(eq, dataset.getDataMap(), dataset.getVehicleData(), channelTitleText.getText(), minBound, maxBound);
                }
            }
        }
        this.dispose();
    }//GEN-LAST:event_createChannelButtonPressed

    private void functionOfMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_functionOfMenuItemActionPerformed
        //add string for changing function at the end of the current string
        equationField.setText(equationField.getText() + " asFunctionOf(DOMAIN)");
    }//GEN-LAST:event_functionOfMenuItemActionPerformed

    private void summationCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_summationCheckBoxItemStateChanged
        // unselect rate if summation is selected
        if(evt.getStateChange() == ItemEvent.SELECTED) {
            rateCheckBox.setSelected(false);
        }
    }//GEN-LAST:event_summationCheckBoxItemStateChanged

    private void rateCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rateCheckBoxItemStateChanged
        // unselect summation if rate is selected
        if(evt.getStateChange() == ItemEvent.SELECTED) {
            summationCheckBox.setSelected(false);
        }
    }//GEN-LAST:event_rateCheckBoxItemStateChanged

    //Update variables list, handle list onclicks
    private void configureVariablesList() {
        //set the variables list to all the tags from the datamap
        ArrayList<String> variablesList = new ArrayList<>();
        for(Dataset dataset : datasets) {
            variablesList.addAll(dataset.getDataMap().getTags());
            variablesList.addAll(dataset.getVehicleData().getKeySet());
        }
        //find uniques in the list
        List<String> variablesUniqueList = variablesList.stream() 
                                      .distinct() 
                                      .collect(Collectors.toList());
        
        availableVariablesList.setListData(variablesUniqueList.toArray(new String[variablesUniqueList.size()]));
        
        //on click of an item of the variables list
        availableVariablesList.addListSelectionListener((ListSelectionEvent e) -> {
            //if the value is not adjusting elsewhere, and the selected value is not null
            if(!e.getValueIsAdjusting() && availableVariablesList.getSelectedValue() != null) {
                //get the string of the equation field
                String str = equationField.getText();
                if(availableVariablesList.getSelectedValue().contains(","))
                    //insert the variable string into the string
                    str = str.substring(0, lastIndex) + "$(" + availableVariablesList.getSelectedValue() + ")" + str.substring(lastIndex);
                else
                    str = str.substring(0, lastIndex) + "&(" + availableVariablesList.getSelectedValue() + ")" + str.substring(lastIndex);
                //set the text value of the field
                equationField.setText(str);
                //clear the list selection
                availableVariablesList.clearSelection();
                //request focus back to the equation field
                equationField.requestFocus();
            }
        });
    }
    
    private void configureDatasetsList() {
        String[] datasetNames = new String[datasets.size()];
        
        //holds index for array
        int index = 0;
        //for each dataset
        for(Dataset dataset : datasets) {
            datasetNames[index] = dataset.getName();
            index++;
        }
        
        appliedDatasets.setListData(datasetNames);
    }
    
    private LinkedList<Dataset> getSelectedDatasets() {
        LinkedList<Dataset> selectedDatasets = new LinkedList<>();
        int[] selected = appliedDatasets.getSelectedIndices();
        int index = 0;
        for(Dataset dataset : datasets) {
            boolean contains = false;
            
            //see if the current index is in selected
            for(Integer i : selected) {
                if(i == index) {
                    contains = true;
                    break;
                }
            }
            
            //if it is add it to the selected datasets
            if(contains)
                selectedDatasets.add(dataset);
            
            //increment index
            index++;
        }
        
        return selectedDatasets;
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList<String> appliedDatasets;
    private javax.swing.JList<String> availableVariablesList;
    private javax.swing.JTextPane channelTitleText;
    private javax.swing.JButton createChannelButton;
    private javax.swing.JMenu editMathChannelMenu;
    private javax.swing.JTextArea equationField;
    private javax.swing.JMenu fileMathChannelMenu;
    private javax.swing.JMenuItem functionOfMenuItem;
    private javax.swing.JMenu insertMathChannelMenu;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JLabel maxLabelRange;
    private javax.swing.JTextPane maxTextField;
    private javax.swing.JLabel minRangeLabel;
    private javax.swing.JTextPane minTextField;
    private javax.swing.JCheckBox rateCheckBox;
    private javax.swing.JCheckBox summationCheckBox;
    // End of variables declaration//GEN-END:variables
}