/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer.controlbar;

import dataanalyzer.DataAnalyzer;
import dataanalyzer.InfiniteProgressPanel;
import dataanalyzer.LoadingEvent;
import dataanalyzer.LoadingListener;

/**
 *
 * @author aribd
 */
public class LoadingPanel extends javax.swing.JPanel {

    
    private InfiniteProgressPanel ipp;
    
    /**
     * Creates new form LoadingPanel
     */
    public LoadingPanel() {
        initComponents();
        setupListener();
        
        this.setVisible(true);
    }
    
    private void setupListener() {
        DataAnalyzer.addLoadingListener((LoadingEvent le) -> {
            setLoading(le.isLoading(), le.getLoadingText());
        });
    }
    
    public void setLoading(boolean isLoading, String text) {
        if (isLoading) {
            ipp = new InfiniteProgressPanel();
            ipp.setSize(20, 20);
            loadingPanel.add(ipp);
            ipp.start();
            loadingText.setText(text);
        } else {
            if(ipp != null) {
                ipp.interrupt();
                ipp = null;
                loadingPanel.removeAll();
            }
            loadingText.setText(text);
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

        loadingPanel = new javax.swing.JPanel();
        loadingText = new javax.swing.JLabel();

        setMaximumSize(new java.awt.Dimension(400, 20));
        setMinimumSize(new java.awt.Dimension(250, 20));
        setPreferredSize(new java.awt.Dimension(250, 20));

        loadingPanel.setMaximumSize(new java.awt.Dimension(20, 20));
        loadingPanel.setMinimumSize(new java.awt.Dimension(20, 20));
        loadingPanel.setPreferredSize(new java.awt.Dimension(20, 20));

        javax.swing.GroupLayout loadingPanelLayout = new javax.swing.GroupLayout(loadingPanel);
        loadingPanel.setLayout(loadingPanelLayout);
        loadingPanelLayout.setHorizontalGroup(
            loadingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 20, Short.MAX_VALUE)
        );
        loadingPanelLayout.setVerticalGroup(
            loadingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 20, Short.MAX_VALUE)
        );

        loadingText.setText("loading...");
        loadingText.setMaximumSize(new java.awt.Dimension(66, 20));
        loadingText.setMinimumSize(new java.awt.Dimension(66, 20));
        loadingText.setPreferredSize(new java.awt.Dimension(66, 20));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(loadingPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(loadingText, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(loadingPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(loadingText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel loadingPanel;
    private javax.swing.JLabel loadingText;
    // End of variables declaration//GEN-END:variables
}
