/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer;

import java.awt.Window;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 *
 * @author aribdhuka
 */
public class ScreenLocation {
    
    private static ScreenLocation loc;
    private static boolean instanceExists = false;
    
    private int width;
    private int height;
    private int xPos;
    private int yPos;
    
    public ScreenLocation() {
        width = 0;
        height = 0;
        xPos = 0;
        yPos = 0;
    }
    
    public void update(JFrame frame) {
        width = frame.getWidth();
        height = frame.getHeight();
        xPos = frame.getX();
        yPos = frame.getY();
    }
    
    public void calculateCenter(Window dialog) {
        //calculate centerX, centerY of frame
        int centerX = xPos + (width / 2);
        int centerY = yPos + (height / 2);
        //compensate for dialog height and width
        centerX -= dialog.getWidth() / 2;
        centerY -= dialog.getHeight() / 2;
        
        //set new location
        dialog.setLocation(centerX, centerY);
    }
    
    public static ScreenLocation getInstance() {
        if(!instanceExists) {
            loc = new ScreenLocation();
            instanceExists = true;
        }
        
        return loc;
    }
    
}
