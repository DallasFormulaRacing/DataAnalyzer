/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer.dialog;

import dataanalyzer.Lap;
import dataanalyzer.ScreenLocation;
import dataanalyzer.dialog.MessageBox;
import java.awt.Dialog;
import java.util.ArrayList;

/**
 *
 * @author aribdhuka
 */
public class LapDataDialog extends javax.swing.JDialog {

    //holds true until the user hits apply and the verification is successful, or cancel
    private boolean running;
    //the lap we will be editing
    private Lap toEdit;
    //lap numbers that are already used
    ArrayList<Integer> usedLaps;
    /**
     * Creates new form LapDataDialog
     * @param parent holds the frame that created this dialog
     * @param modal does this dialog hold the user captive until they finish interacting with it
     */
    public LapDataDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);
        initComponents();
        toEdit = new Lap();
        running = true;
        int[] usedLaps = null;
        ScreenLocation.getInstance().calculateCenter(this);
    }
    
    /**
     * Creates new form LapDataDialog
     * @param parent holds the frame that created this dialog
     * @param modal does this dialog hold the user captive until they finish interacting with it
     * @param toEdit the Lap object we will be interacting and editing
     * @param usedLaps an ArrayList of integers that holds which lap numbers are already used
     */
    public LapDataDialog(java.awt.Frame parent, boolean modal, Lap toEdit, ArrayList<Integer> usedLaps) {
        super(parent, modal);
        this.setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);
        initComponents();
        this.toEdit = toEdit;
        running = true;
        this.usedLaps = usedLaps;
        startTextField.setText(toEdit.getStart() + "");
        stopTextField.setText(toEdit.getStop() + "");
        lapNumberTextField.requestFocus();
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

        lapLabelTextField = new javax.swing.JTextField();
        startTextLabel = new javax.swing.JLabel();
        startTextField = new javax.swing.JTextField();
        stopTextLabel = new javax.swing.JLabel();
        applyButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        stopTextField = new javax.swing.JTextField();
        lapNumberLabel = new javax.swing.JLabel();
        lapNumberTextField = new javax.swing.JTextField();
        lapLabelTextLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        lapLabelTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                lapLabelTextFieldFocusLost(evt);
            }
        });

        startTextLabel.setText("Start:");

        startTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                startTextFieldFocusLost(evt);
            }
        });

        stopTextLabel.setText("Stop:");

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

        stopTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                stopTextFieldFocusLost(evt);
            }
        });

        lapNumberLabel.setText("Lap #:");

        lapNumberTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                lapNumberTextFieldFocusLost(evt);
            }
        });

        lapLabelTextLabel.setText("Lap Label:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(cancelButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(applyButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(startTextLabel, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(stopTextLabel, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lapLabelTextLabel, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lapNumberLabel, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lapLabelTextField)
                            .addComponent(lapNumberTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(stopTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(startTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(startTextLabel)
                    .addComponent(startTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(stopTextLabel)
                    .addComponent(stopTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lapNumberLabel)
                    .addComponent(lapNumberTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lapLabelTextLabel)
                    .addComponent(lapLabelTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(applyButton)
                    .addComponent(cancelButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void applyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyButtonActionPerformed
        //see if the data is valid
        boolean verified = verifyData();
        //if verified stop running and finish
        if(verified) {
            running = false;
            this.dispose();
        }
    }//GEN-LAST:event_applyButtonActionPerformed

    private void startTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_startTextFieldFocusLost
        if(!stopTextField.getText().isEmpty()) {
            long attemptParse;
            try {
                attemptParse = Long.parseLong(startTextField.getText());
                if(attemptParse >= 0) {
                    toEdit.setStart(attemptParse);
                } else {
                    new MessageBox(this, "How can the start of the lap occcur before time. Bloody time travelers. Enter an integer greater than or equal to 0.", true).setVisible(true);
                }
            } catch(NumberFormatException e) {
                new MessageBox(this, "Does that look like a integer to you? Please enter an integer.", true).setVisible(true);
            }
        }
    }//GEN-LAST:event_startTextFieldFocusLost

    private void stopTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_stopTextFieldFocusLost
        if(!stopTextField.getText().isEmpty()) {
            long attemptParse;
            try {
                attemptParse = Long.parseLong(stopTextField.getText());
                if(attemptParse >= 0) {
                    toEdit.setStop(attemptParse);
                } else {
                    new MessageBox(this, "How can the stop of the lap occcur before time. Bloody time travelers. Enter an integer greater than or equal to 0.", true).setVisible(true);
                }
            } catch(NumberFormatException e) {
                new MessageBox(this, "Does that look like a integer to you? Please enter an integer.", true).setVisible(true);
            }
        }
    }//GEN-LAST:event_stopTextFieldFocusLost

    private void lapNumberTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_lapNumberTextFieldFocusLost
        if(!lapNumberTextField.getText().isEmpty()) {
            int attemptParse;
            try {
                attemptParse = Integer.parseInt(lapNumberTextField.getText());
                toEdit.setLapNumber(attemptParse);
            } catch(NumberFormatException e) {
                new MessageBox(this, "Does that look like a integer to you? Please enter an integer.", true).setVisible(true);
            }
        }
    }//GEN-LAST:event_lapNumberTextFieldFocusLost

    private void lapLabelTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_lapLabelTextFieldFocusLost
        toEdit.setLapLabel(lapLabelTextField.getText());
    }//GEN-LAST:event_lapLabelTextFieldFocusLost

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        //lose the data and finish
        toEdit.setLapLabel("!#@$LAPCANCELLED");
        running = false;
        this.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private boolean verifyData() {
        if(startTextField.getText().isEmpty() || stopTextField.getText().isEmpty()) {
            new MessageBox(this, "Please enter when the lap starts and stops. C'mon man.", true).setVisible(true);
            return false;
        }
        
        if(toEdit.getStart() >= toEdit.getStop()) {
            new MessageBox(this, "How can the lap stop before it starts? \nPlease enter an integer for stop time that is larger than the start integer.", true).setVisible(true);
            return false;
        }
                
        if(lapNumberTextField.getText().isEmpty()) {
            new MessageBox(this, "Please enter a lap number that isn't already used.", true).setVisible(true);
            return false;
        }
        
        if(usedLaps != null && usedLaps.contains(toEdit.getLapNumber())) {
            new MessageBox(this, "That lap number is already being used.", true).setVisible(true);
            return false;
        }
        
        return true;
    }
    
    /**
     * getter for running object
     * @return returns true if the dialog is unfinished 
     */
    public boolean isRunning() {
        return running;
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
            java.util.logging.Logger.getLogger(LapDataDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LapDataDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LapDataDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LapDataDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                LapDataDialog dialog = new LapDataDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton applyButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JTextField lapLabelTextField;
    private javax.swing.JLabel lapLabelTextLabel;
    private javax.swing.JLabel lapNumberLabel;
    private javax.swing.JTextField lapNumberTextField;
    private javax.swing.JTextField startTextField;
    private javax.swing.JLabel startTextLabel;
    private javax.swing.JTextField stopTextField;
    private javax.swing.JLabel stopTextLabel;
    // End of variables declaration//GEN-END:variables
}
