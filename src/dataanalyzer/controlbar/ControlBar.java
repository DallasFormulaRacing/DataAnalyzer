/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer.controlbar;

import dataanalyzer.CategoricalHashMap;
import dataanalyzer.ChartManager;
import dataanalyzer.DataAnalyzer;
import dataanalyzer.Dataset;
import dataanalyzer.DomainMode;
import dataanalyzer.FileOpenedListener;
import dataanalyzer.InfiniteProgressPanel;
import dataanalyzer.SizeListener;
import dataanalyzer.dialog.VitalsDialog;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIManager;

/**
 *
 * @author aribd
 */
public class ControlBar extends javax.swing.JPanel {
    
    // holds which mode the charts are in
    private DomainMode mode;
    
    DataAnalyzer parent;
    
    /**
     * Creates new form ControlBar
     */
    public ControlBar(DataAnalyzer parent) {
        initComponents();
        this.parent = parent;
        mode = DomainMode.TIME;
        this.parent.getAppParameters().put("domainMode", mode);
        vitalsPanel.setDataAnalyzer(parent);
    }
    
    public DomainMode getMode() {
        return mode;
    }
    
    public void setLoading(boolean isLoading, String text) {
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        domainSlider = new javax.swing.JSlider();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        openFilesPanel1 = new dataanalyzer.controlbar.OpenFilesPanel();
        loadingPanel1 = new dataanalyzer.controlbar.LoadingPanel();
        vitalsPanel = new dataanalyzer.controlbar.VitalsPanel();

        setMaximumSize(new java.awt.Dimension(1100, 20));
        setRequestFocusEnabled(false);

        domainSlider.setMaximum(1);
        domainSlider.setValue(0);
        domainSlider.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                domainSliderMouseClicked(evt);
            }
        });

        jLabel1.setText("Distance");

        jLabel2.setText("Time");

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        loadingPanel1.setMinimumSize(new java.awt.Dimension(250, 20));
        loadingPanel1.setPreferredSize(new java.awt.Dimension(350, 20));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(openFilesPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(loadingPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 304, Short.MAX_VALUE)
                .addComponent(vitalsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(domainSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addGap(11, 11, 11))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                .addComponent(domainSlider, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(openFilesPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(loadingPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(vitalsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void domainSliderMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_domainSliderMouseClicked
        // Toogle domain mode
        if(mode == DomainMode.TIME) {
            domainSlider.setValue(1);
            mode = DomainMode.DISTANCE;
        }
        else {
            domainSlider.setValue(0);
            mode = DomainMode.TIME;
        }
        
        this.parent.getAppParameters().put("domainMode", mode);
        //need to trigger charts to update.
        this.parent.triggerChartDomainUpdate();
    }//GEN-LAST:event_domainSliderMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSlider domainSlider;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private dataanalyzer.controlbar.LoadingPanel loadingPanel1;
    private dataanalyzer.controlbar.OpenFilesPanel openFilesPanel1;
    private dataanalyzer.controlbar.VitalsPanel vitalsPanel;
    // End of variables declaration//GEN-END:variables

}