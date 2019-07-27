/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer;

import javax.swing.JInternalFrame;

/**
 *
 * @author aribdhuka
 */
public class ChartFrame extends JInternalFrame {
    
    public ChartFrame() {
        initComponent();
    }
    
    public void initComponent() {
        this.setResizable(true);
        this.setClosable(true);
        this.setMaximizable(true);
        this.setIconifiable(true);
    }
    
}
