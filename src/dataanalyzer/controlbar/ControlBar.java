/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer.controlbar;

import dataanalyzer.CategoricalHashMap;
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
    
    private int openedFiles = 0;
    private InfiniteProgressPanel ipp;
    
    /**
     * Creates new form ControlBar
     */
    public ControlBar(DataAnalyzer parent) {
        initComponents();
        this.parent = parent;
        mode = DomainMode.TIME;
        this.parent.getAppParameters().put("domainMode", mode);
        loadingText.setText("");
    
        setupListeners();

    }
    
    public DomainMode getMode() {
        return mode;
    }
    
    private void setupListeners() {
        parent.getChartManager().addDatasetSizeChangeListener(new SizeListener() {
            @Override
            public void sizeUpdate(int newSize) {
                openedFiles = newSize;
                filesOpenLabel.setText(""+openedFiles);
            }
        });
        
        DataAnalyzer.addFileOpenedListener(new FileOpenedListener() {
            @Override
            public void fileOpened(Object o) {
                System.out.println(".fileOpened()");
                setVitals();
            }
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
    
    public void setVitals() {
        
        VitalsDialog vd = new VitalsDialog(parent, false, parent.getChartManager().getMainDataset());
            ArrayList<CategoricalHashMap> maps = new ArrayList<>();
        for (Dataset ds : parent.getChartManager().getDatasets()) {
            maps.add(ds.getDataMap());
        }
        String ewis = vd.runVitals(maps);
        vd.dispose();
        vitalsTextDialog.setText(ewis);
        vitalsImageDialog.setText("");
        if(Integer.parseInt(""+ewis.charAt(0)) > 0) {
            vitalsImageDialog.setIcon(rescaleIcon(UIManager.getIcon("OptionPane.errorIcon")));
        } else if(Integer.parseInt(""+ewis.charAt(2)) > 0) {
            vitalsImageDialog.setIcon(rescaleIcon(UIManager.getIcon("OptionPane.warningIcon")));
        } else {
            vitalsImageDialog.setIcon(rescaleIcon(UIManager.getIcon("OptionPane.informationIcon")));
        }
    }
    
    private ImageIcon rescaleIcon(Icon icon) {
        BufferedImage bufferedImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB); 
        Graphics2D g = bufferedImage.createGraphics(); 
        icon.paintIcon(null, g, 0, 0); 
        g.dispose(); 
        return new ImageIcon(bufferedImage.getScaledInstance(20, 20, Image.SCALE_SMOOTH));
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
        jLabel3 = new javax.swing.JLabel();
        filesOpenLabel = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        loadingPanel = new javax.swing.JPanel();
        loadingText = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        vitalsTextDialog = new javax.swing.JLabel();
        vitalsImageDialog = new javax.swing.JLabel();

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

        jLabel3.setText("Files Open:");

        filesOpenLabel.setText("0");

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

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
            .addGap(0, 0, Short.MAX_VALUE)
        );

        loadingText.setText("jLabel4");

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        vitalsTextDialog.setText("0E0W0I");
        vitalsTextDialog.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        vitalsImageDialog.setText("I");
        vitalsImageDialog.setMaximumSize(new java.awt.Dimension(20, 20));
        vitalsImageDialog.setMinimumSize(new java.awt.Dimension(5, 5));
        vitalsImageDialog.setPreferredSize(new java.awt.Dimension(20, 20));
        vitalsImageDialog.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                vitalsImageDialogMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filesOpenLabel)
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(loadingPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(loadingText)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 583, Short.MAX_VALUE)
                .addComponent(vitalsImageDialog, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(vitalsTextDialog)
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
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(domainSlider, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(loadingPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(filesOpenLabel))
                    .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(vitalsTextDialog, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(loadingText)
                            .addComponent(vitalsImageDialog, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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

    private void vitalsImageDialogMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_vitalsImageDialogMouseReleased
        // Text shown is summation, but click will always take you to first dataset
        if(parent.getChartManager().getMainDataset() != null) {
            VitalsDialog vd = new VitalsDialog(parent, true, parent.getChartManager().getMainDataset());
            vd.setVisible(true);
        }
    }//GEN-LAST:event_vitalsImageDialogMouseReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSlider domainSlider;
    private javax.swing.JLabel filesOpenLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JPanel loadingPanel;
    private javax.swing.JLabel loadingText;
    private javax.swing.JLabel vitalsImageDialog;
    private javax.swing.JLabel vitalsTextDialog;
    // End of variables declaration//GEN-END:variables

}
