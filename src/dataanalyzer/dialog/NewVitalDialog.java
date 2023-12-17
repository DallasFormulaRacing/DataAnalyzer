/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer.dialog;

import com.arib.toast.Toast;
import dataanalyzer.CategoricalHashMap;
import dataanalyzer.Dataset;
import dataanalyzer.Referencer;
import java.awt.Dialog;
import java.lang.ref.Reference;
import java.util.ArrayList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author aribdhuka
 */
public class NewVitalDialog extends javax.swing.JDialog {

    private Vital currVital;
    CategoricalHashMap dataMap;
    Referencer<Boolean> isCanceled;
    /**
     * Creates new form NewVitalDialog
     * @param parent which component is this dialog bound to
     * @param newVital the vital to write to
     */
    public NewVitalDialog(Dialog parent, Vital newVital, Referencer<Boolean> isCanceled, CategoricalHashMap dataMap) {
        super(parent, true); //hold user captive
        initComponents();
        currVital = newVital;
        this.isCanceled = isCanceled;
        this.dataMap = dataMap;
        populateFields();
    }
    
    private void populateFields() {
        //build list of channels
        ArrayList<String> tags = dataMap.getTags();
        String[] tagsArray = new String[tags.size()];
        tags.toArray(tagsArray);
        channelList.setListData(tagsArray);
        
        channelList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(!e.getValueIsAdjusting()) {
                    if(channelList.getSelectedIndex() != -1) {
                        String listelem = tagsArray[channelList.getSelectedIndex()];
                        channelField.setText(listelem);
                    }
                }
            }
        });
        
        
        channelField.setText(currVital.getChannel());
        channelList.setSelectedValue(currVital.getChannel(), true);
        
        if(currVital.getLowValue() != 0) {
            lowValueField.setText(currVital.getLowValue() + "");
        }
        
        if(currVital.getHighValue() != 0) {
            highValueField.setText(currVital.getHighValue() + "");
        }
        
        switch(currVital.getLowType()) {
            case Error: lowValueErrorRadioButton.setSelected(true); break;
            case Warn: lowValueWarnRadioButton.setSelected(true); break;
            case Info: lowValueInfoRadioButton.setSelected(true); break;
        }
        
        switch(currVital.getHighType()) {
            case Error: highValueErrorRadioButton.setSelected(true); break;
            case Warn: highValueWarnRadioButton.setSelected(true); break;
            case Info: highValueInfoRadioButton.setSelected(true); break;
        }
        
        if(currVital.isLowActive()) {
            lowValueActive.setSelected(true);
        }
        
        if(currVital.isHighActive()) {
            highValueActive.setSelected(true);
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

        lowValueButtonGroup = new javax.swing.ButtonGroup();
        highValueButtonGroup = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        channelField = new javax.swing.JTextPane();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        channelList = new javax.swing.JList<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lowValueActive = new javax.swing.JCheckBox();
        highValueActive = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        lowValueField = new javax.swing.JTextPane();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        highValueField = new javax.swing.JTextPane();
        lowValueInfoRadioButton = new javax.swing.JRadioButton();
        lowValueWarnRadioButton = new javax.swing.JRadioButton();
        lowValueErrorRadioButton = new javax.swing.JRadioButton();
        highValueInfoRadioButton = new javax.swing.JRadioButton();
        highValueWarnRadioButton = new javax.swing.JRadioButton();
        highValueErrorRadioButton = new javax.swing.JRadioButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        applyButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jScrollPane1.setViewportView(channelField);

        jLabel1.setText("Channel");

        channelList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        channelList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        channelList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                channelListMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(channelList);

        jLabel2.setText("Low Value Warning");

        jLabel3.setText("High Value Warning");

        lowValueActive.setText("Activate");

        highValueActive.setText("Activate");

        jLabel4.setText("Enter value:");

        jScrollPane3.setViewportView(lowValueField);

        jLabel5.setText("Enter value:");

        jScrollPane4.setViewportView(highValueField);

        lowValueButtonGroup.add(lowValueInfoRadioButton);
        lowValueInfoRadioButton.setText("Inform");

        lowValueButtonGroup.add(lowValueWarnRadioButton);
        lowValueWarnRadioButton.setText("Warn");

        lowValueButtonGroup.add(lowValueErrorRadioButton);
        lowValueErrorRadioButton.setText("Error");

        highValueButtonGroup.add(highValueInfoRadioButton);
        highValueInfoRadioButton.setText("Inform");

        highValueButtonGroup.add(highValueWarnRadioButton);
        highValueWarnRadioButton.setText("Warn");

        highValueButtonGroup.add(highValueErrorRadioButton);
        highValueErrorRadioButton.setText("Error");

        jLabel6.setText("Set Vital Level");

        jLabel7.setText("Set Vital Level");

        applyButton.setBackground(new java.awt.Color(0, 122, 255));
        applyButton.setText("Apply");
        applyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyButtonActionPerformed(evt);
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
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPane2)
                            .addComponent(jScrollPane1))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lowValueActive)
                            .addComponent(jLabel4)
                            .addComponent(lowValueInfoRadioButton)
                            .addComponent(lowValueWarnRadioButton)
                            .addComponent(lowValueErrorRadioButton)
                            .addComponent(jLabel7)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel6)
                            .addComponent(highValueWarnRadioButton)
                            .addComponent(highValueErrorRadioButton)
                            .addComponent(highValueActive)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5)
                            .addComponent(highValueInfoRadioButton)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 385, Short.MAX_VALUE)
                        .addComponent(cancelButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(applyButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addGap(188, 188, 188))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel7))
                                .addGap(3, 3, 3)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(lowValueInfoRadioButton)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lowValueWarnRadioButton)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lowValueErrorRadioButton)
                                        .addGap(18, 18, 18)
                                        .addComponent(lowValueActive))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(highValueInfoRadioButton)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(highValueWarnRadioButton)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(highValueErrorRadioButton)
                                        .addGap(18, 18, 18)
                                        .addComponent(highValueActive)))))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(applyButton)
                    .addComponent(cancelButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        // TODO add your handling code here:
        isCanceled.set(true);
        this.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void applyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyButtonActionPerformed
        //channel
        currVital.setChannel(channelField.getText());
        
        //low value
        if(lowValueField.getText().isEmpty() && lowValueActive.isSelected()) {
            Toast.makeToast(this, "Low value empty and low vital active!", Toast.DURATION_LONG);
            return;
        }
        if(lowValueActive.isSelected()) {
            try{
                currVital.setLowValue(Double.parseDouble(lowValueField.getText()));
            } catch (NumberFormatException e) {
                Toast.makeToast(this, "Couldn't parse low value and low vital active!", ERROR);
                return;
            }
        }
        if(lowValueField.getText().isEmpty() && !lowValueActive.isSelected()) {
            currVital.setLowValue(0);
        }
        
        //highvalue
        if(highValueField.getText().isEmpty() && highValueActive.isSelected()) {
            Toast.makeToast(this, "Low value empty and low vital active!", Toast.DURATION_LONG);
            return;
        }
        if(highValueActive.isSelected()) {
            try{
                currVital.setHighValue(Double.parseDouble(highValueField.getText()));
            } catch (NumberFormatException e) {
                Toast.makeToast(this, "Couldn't parse low value and low vital active!", ERROR);
                return;
            }
        }
        if(highValueField.getText().isEmpty() && !highValueActive.isSelected()) {
            currVital.setHighValue(0);
        }
        
        //low value type
        if(lowValueInfoRadioButton.isSelected()) {
            currVital.setLowType(VitalType.Info);
        } else if(lowValueWarnRadioButton.isSelected()) {
            currVital.setLowType(VitalType.Warn);
        } else if(lowValueErrorRadioButton.isSelected()) {
            currVital.setLowType(VitalType.Error);
        }
        
        //high value type
        if(highValueInfoRadioButton.isSelected()) {
            currVital.setHighType(VitalType.Info);
        } else if(highValueWarnRadioButton.isSelected()) {
            currVital.setHighType(VitalType.Warn);
        } else if(highValueErrorRadioButton.isSelected()) {
            currVital.setHighType(VitalType.Error);
        }
        
        //low active
        if(lowValueActive.isSelected()){
            currVital.setLowActive(true);
        } else {
            currVital.setLowActive(false);
        }
        
        //high active
        if(highValueActive.isSelected()){
            currVital.setHighActive(true);
        } else {
            currVital.setHighActive(false);
        }
        
        this.dispose();
        
    }//GEN-LAST:event_applyButtonActionPerformed

    private void channelListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_channelListMouseClicked
        // TODO add your handling code here:
        
    }//GEN-LAST:event_channelListMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton applyButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JTextPane channelField;
    private javax.swing.JList<String> channelList;
    private javax.swing.JCheckBox highValueActive;
    private javax.swing.ButtonGroup highValueButtonGroup;
    private javax.swing.JRadioButton highValueErrorRadioButton;
    private javax.swing.JTextPane highValueField;
    private javax.swing.JRadioButton highValueInfoRadioButton;
    private javax.swing.JRadioButton highValueWarnRadioButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JCheckBox lowValueActive;
    private javax.swing.ButtonGroup lowValueButtonGroup;
    private javax.swing.JRadioButton lowValueErrorRadioButton;
    private javax.swing.JTextPane lowValueField;
    private javax.swing.JRadioButton lowValueInfoRadioButton;
    private javax.swing.JRadioButton lowValueWarnRadioButton;
    // End of variables declaration//GEN-END:variables
}