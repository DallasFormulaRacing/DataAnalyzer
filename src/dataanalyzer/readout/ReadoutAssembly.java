/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dataanalyzer.readout;

import dataanalyzer.ChartFrame;
import dataanalyzer.ChartManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JInternalFrame;

/**
 *
 * @author arib
 */
public class ReadoutAssembly {
    
    private JInternalFrame chartFrame;
    private ReadoutPanel readoutPanel;
    
    private ChartManager chartManager;
    
    public ReadoutAssembly(ChartManager chartManager) {
        chartFrame = new ChartFrame();
        chartFrame.setSize(new Dimension(200,500));
        chartFrame.setResizable(true);
        readoutPanel = new ReadoutPanel(chartManager);
        chartFrame.setLayout(null);
        chartFrame.add(readoutPanel);
        
        chartFrame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent componentEvent) {
                readoutPanel.setSize(chartFrame.getSize());
            }
        });
        
        this.chartManager = chartManager;
        
        readoutPanel.setVisible(true);
        chartFrame.setVisible(true);
    }

    public JInternalFrame getChartFrame() {
        return chartFrame;
    }

    public void setChartFrame(JInternalFrame chartFrame) {
        this.chartFrame = chartFrame;
    }

    public ReadoutPanel getReadoutPanel() {
        return readoutPanel;
    }

    public void setReadoutPanel(ReadoutPanel readoutPanel) {
        this.readoutPanel = readoutPanel;
    }
    
    
}
