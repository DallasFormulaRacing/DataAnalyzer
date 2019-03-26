/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer.dialog;

import dataanalyzer.VehicleData;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Set;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author aribdhuka
 */
public class VehicleDataDialog extends javax.swing.JDialog {

    
    private final static String[] neededVariables = {"Mass", "MotionRatio", "RollingResistance",
        "AerodynamicDrag", "1stGear", "2ndGear", "3rdGear", "4thGear", "5thGear", "6thGear",
        "FinalDrive", "TireRadius"};
    
    //vehicle data object that will be held by the program.
    VehicleData vehicleData;
    
    //holds the last index of the caret
    int lastIndex;
    /**
     * Creates new form VehicleDataDialog
     */
    public VehicleDataDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        //create new vehicle data if not provided
        vehicleData = new VehicleData();
        //set last index visited to 0
        lastIndex = 0;
        //set the list data
        neededVariablesList.setListData(neededVariables);
        //configure listview and caret listeners
        configureListeners();
    }
    
    public VehicleDataDialog(java.awt.Frame parent, boolean modal, VehicleData vehicleData, String buttonName) {
        super(parent, modal);
        initComponents();
        //set last index visited to 0
        lastIndex = 0;
        //set the button name
        mainButton.setText(buttonName);
        //set the vehicle data object
        this.vehicleData = vehicleData;
        if(buttonName.equals("Apply")) {
            //update the text field
            updateTextFieldFromData();
        }
        //update list of needed variables
        updateNeededVarsList();
        //configure listview and caret listeners
        configureListeners();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        dataTextArea = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        neededVariablesList = new javax.swing.JList<>();
        mainButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        dataTextArea.setColumns(20);
        dataTextArea.setRows(5);
        dataTextArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                dataTextAreaKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(dataTextArea);

        neededVariablesList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(neededVariablesList);

        mainButton.setBackground(new java.awt.Color(0, 122, 255));
        mainButton.setText("jButton1");
        mainButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mainButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 312, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(cancelButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mainButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mainButton)
                    .addComponent(cancelButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mainButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mainButtonActionPerformed
        //clear the data and update
        vehicleData.clearData();
        //apply the data to the VehicleData class
        vehicleData.applyVehicleData(dataTextArea.getText());
        //close dialog
        this.dispose();
    }//GEN-LAST:event_mainButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        //close this dialog
        this.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void dataTextAreaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dataTextAreaKeyReleased
        //handle key releases in the data text area
        int keyCode = evt.getKeyCode();
        switch(keyCode) {
            //if its enter, see if any of the needed variables have been applied
            case KeyEvent.VK_ENTER : updateNeededVarsList(); break;
        }
    }//GEN-LAST:event_dataTextAreaKeyReleased

    
    //updates dataTextArea from the data already present in vehicle data
    private void updateTextFieldFromData() {
        //set the dataText areas text to the string we built.
        dataTextArea.setText(vehicleData.getStringOfData());
    }
    
    //updates the neededVariablesList of variables that are still needed
    private void updateNeededVarsList() {
        //split the lines on the newlines
        String[] lines = dataTextArea.getText().split("\n");
        
        //holds variables we have used
        ArrayList<String> usedVars = new ArrayList<>();
        //for each line
        for(String line : lines) {
            //get the var name
            String varName;
            //make sure the line is varname = value
            if(line.matches("^[0-9a-zA-Z]+[ ]*=[ ]*[0-9.]+")) {
                //if so get the var name from a substring
                varName = line.substring(0, line.indexOf('='));
                //remove spaces
                varName = varName.replace(" ", "");
            } else  //if not continue
                continue;
            //for each of the variables we need
            for(String var : neededVariables) {
                //if the variable we need is the one we wrote
                if(varName.contains(var)) {
                    //update the list of variables we have used
                    usedVars.add(var);
                }
            }
        }
        
        //holds variables we still need
        ArrayList<String> stillNeeded = new ArrayList<>();
        //for each variables we still need
        for(String var : neededVariables) {
            //if we have not used it
            if(!usedVars.contains(var))
                //add it to the list of variables we need
                stillNeeded.add(var);
        }
        
        String[] arr = new String[stillNeeded.size()];
        //set the list data
        neededVariablesList.setListData(stillNeeded.toArray(arr));
    }
    
    private void configureListeners() {
        //on caret changed update last place it was
        dataTextArea.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                lastIndex = e.getDot();
            }
        });
        
        //on item clicked
        neededVariablesList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                //if the values is final and not null
                if(!e.getValueIsAdjusting() && neededVariablesList.getSelectedValue() != null) {
                    //get the string of the equation field
                    String str = dataTextArea.getText();
                    //insert the variable
                    str = str.substring(0, lastIndex) + neededVariablesList.getSelectedValue() + " = " + str.substring(lastIndex);
                    //set the text value of the field
                    dataTextArea.setText(str);
                    //clear the list selection
                    neededVariablesList.clearSelection();
                    //request focus back to the equation field
                    dataTextArea.requestFocus();
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JTextArea dataTextArea;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton mainButton;
    private javax.swing.JList<String> neededVariablesList;
    // End of variables declaration//GEN-END:variables
}