/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer.dialog;

import dataanalyzer.dialog.MessageBox;
import dataanalyzer.Referencer;
import dataanalyzer.ScreenLocation;

/**
 * @author Morgan
 * @author Peter
 * @author Preston
 * @author Dante
 */
public class LambdaMapSettings extends javax.swing.JDialog {

    private static int maxRPM;
    private static double targetAFR;
    private static double acceptedError;
    private static double targetCountHigh;
    private static int injectorTimeColorMap;
    private static boolean includeFullyLeanValues;
    private static boolean hideLowDataCountValues;

    /**
     * Creates new form LambdaMapSettings
     */
    public LambdaMapSettings(java.awt.Frame parent, boolean modal, int maxRPM, double targetAFR, double acceptedError, int injectorTimeColorMap, boolean includeFullyLeanValues, boolean hideLowDataCountValues, double targetCountHigh) {
        super(parent, modal);
        this.maxRPM = maxRPM;
        this.targetAFR = targetAFR;
        this.acceptedError = acceptedError;
        this.injectorTimeColorMap = injectorTimeColorMap;
        this.includeFullyLeanValues = includeFullyLeanValues;
        this.targetCountHigh = targetCountHigh;
        this.hideLowDataCountValues = hideLowDataCountValues;
        initComponents();
        includeFullyLeanValuesCheckBox.setSelected(includeFullyLeanValues);
        hideLowDataCountValuesCheckBox.setSelected(hideLowDataCountValues);
        ScreenLocation.getInstance().calculateCenter(this);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        maxRPMLabel = new javax.swing.JLabel();
        maxRpmField = new javax.swing.JTextField();
        targetAFRLabel = new javax.swing.JLabel();
        targetAfrField = new javax.swing.JTextField();
        afrErrorLabel = new javax.swing.JLabel();
        afrOffsetField = new javax.swing.JTextField();
        cancelButton = new javax.swing.JButton();
        applyButton = new javax.swing.JButton();
        injectorTimeLabel = new javax.swing.JLabel();
        injectorTimeComboBox = new javax.swing.JComboBox<>();
        includeFullyLeanValuesCheckBox = new javax.swing.JCheckBox();
        dataCountLabel = new javax.swing.JLabel();
        dataCountField = new javax.swing.JTextField();
        hideLowDataCountValuesCheckBox = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setFocusCycleRoot(false);
        setMaximumSize(new java.awt.Dimension(260, 340));
        setMinimumSize(new java.awt.Dimension(260, 340));

        maxRPMLabel.setText("Max RPM");

        maxRpmField.setText(String.valueOf(maxRPM));
        maxRpmField.setToolTipText("Enter the Max RPM setting of the ECU");

        targetAFRLabel.setText("Target AFR");

        targetAfrField.setText(String.valueOf(targetAFR));
        targetAfrField.setToolTipText("Enter the desired AFR value you are looking for");

        afrErrorLabel.setText("Acceptable AFR Error");

        afrOffsetField.setText(String.valueOf(acceptedError));
        afrOffsetField.setToolTipText("Enter the Accepted error for the desired AFR you entered above");

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        applyButton.setText("Apply");
        applyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settingsApplied(evt);
            }
        });

        injectorTimeLabel.setText("Injector Time Color Map");

        injectorTimeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Lambda Average", "Lambda Max", "Lambda Min"}));
        injectorTimeComboBox.setSelectedIndex(injectorTimeColorMap);

        includeFullyLeanValuesCheckBox.setText("Include Fully Lean Values");
        includeFullyLeanValuesCheckBox.setToolTipText("");

        dataCountLabel.setText("Target Data Count (Low/High)");

        dataCountField.setText(String.valueOf(targetCountHigh));
        dataCountField.setToolTipText("Enter High target for data point amounts");

        hideLowDataCountValuesCheckBox.setText("Hide Low Data Count Values");
        hideLowDataCountValuesCheckBox.setToolTipText("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(hideLowDataCountValuesCheckBox)
                    .addComponent(dataCountLabel)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(includeFullyLeanValuesCheckBox)
                        .addComponent(injectorTimeLabel)
                        .addComponent(afrErrorLabel)
                        .addComponent(targetAFRLabel)
                        .addComponent(maxRPMLabel)
                        .addComponent(maxRpmField)
                        .addComponent(targetAfrField)
                        .addComponent(afrOffsetField)
                        .addComponent(injectorTimeComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(cancelButton)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 71, Short.MAX_VALUE)
                            .addComponent(applyButton))
                        .addComponent(dataCountField)))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(maxRPMLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(maxRpmField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(targetAFRLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(targetAfrField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(afrErrorLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(afrOffsetField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(injectorTimeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(injectorTimeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(includeFullyLeanValuesCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dataCountLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dataCountField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(hideLowDataCountValuesCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(applyButton))
                .addContainerGap())
        );

        maxRpmField.getAccessibleContext().setAccessibleName("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        close();
    }//GEN-LAST:event_cancelButtonActionPerformed

    
    private void settingsApplied(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_settingsApplied
        //checks for valid data in table settings
        try {
            if (Integer.parseInt(maxRpmField.getText()) < 25) {
                throw new NumberFormatException("Max RPM is less than 25!");
            }
            maxRPM = Integer.parseInt(maxRpmField.getText());
            
            if (Double.parseDouble(targetAfrField.getText()) < 10 || Double.parseDouble(targetAfrField.getText()) > 21) {
                throw new NumberFormatException("Target AFR must be between 10 and 21 (inclusive)!");
            }
            targetAFR = Double.parseDouble(targetAfrField.getText());
            
            acceptedError = Double.parseDouble(afrOffsetField.getText());
            acceptedError = Math.abs(acceptedError);
            
            injectorTimeColorMap = injectorTimeComboBox.getSelectedIndex();
            
            includeFullyLeanValues = includeFullyLeanValuesCheckBox.isSelected();
            
            targetCountHigh = Double.parseDouble(dataCountField.getText());
            hideLowDataCountValues = hideLowDataCountValuesCheckBox.isSelected();
            
            close();
        }
        //displays a message box with an error when exceptions are thrown
        catch (NumberFormatException e) {
            new MessageBox(this, "Error validating your settings." + "\n" + "Please make sure your numbers are correct.\n" + e.getMessage(), true).setVisible(true);
        }
    }//GEN-LAST:event_settingsApplied
    
    public static double getTargetCountHigh() {
        return targetCountHigh;
    }

    public static int getMaxRPM() {
        return maxRPM;
    }

    public static double getTargetAFR() {
        return targetAFR;
    }

    public static double getAcceptedError() {
        return acceptedError;
    }

    public static int getInjectorTimeColorMap() {
        return injectorTimeColorMap;
    }

    public static boolean isIncludeFullyLeanValues() {
        return includeFullyLeanValues;
    }
    
    public static boolean isHideLowDataCountValues() {
        return hideLowDataCountValues;
    }
    
    public void close() {
        this.dispose();
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
            java.util.logging.Logger.getLogger(LambdaMapSettings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LambdaMapSettings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LambdaMapSettings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LambdaMapSettings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                LambdaMapSettings dialog = new LambdaMapSettings(new javax.swing.JFrame(), true, maxRPM, targetAFR, acceptedError, injectorTimeColorMap, includeFullyLeanValues, hideLowDataCountValues, targetCountHigh);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel afrErrorLabel;
    private javax.swing.JTextField afrOffsetField;
    private javax.swing.JButton applyButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JTextField dataCountField;
    private javax.swing.JLabel dataCountLabel;
    private javax.swing.JCheckBox hideLowDataCountValuesCheckBox;
    private javax.swing.JCheckBox includeFullyLeanValuesCheckBox;
    private javax.swing.JComboBox<String> injectorTimeComboBox;
    private javax.swing.JLabel injectorTimeLabel;
    private javax.swing.JLabel maxRPMLabel;
    private javax.swing.JTextField maxRpmField;
    private javax.swing.JLabel targetAFRLabel;
    private javax.swing.JTextField targetAfrField;
    // End of variables declaration//GEN-END:variables
}
