/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;

/**
 *
 * @author Preston Baxter
 */
public class GPSGraphPanel extends JPanel{
    
    public GPSGraphPanel(){
        setPreferredSize(new Dimension(600,400));
    }
    
    @Override
    public void paintComponent(Graphics g){
        
        g.drawRect(0,0,100,100);
        
    }
    
}
