/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dataanalyzer;

import dataanalyzer.dialog.MessageBox;
import dataanalyzer.dialog.TagChooserDialog;
import java.util.LinkedList;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYSeriesCollection;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import org.apache.commons.io.FileUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.util.Arrays;

/**
 *
 * @author Ofek Shaltiel
 */

public class SteeringAngleDisplay{
    private ChartManager manager;
    private int numSteeringElements;
    private Selection[] selections;
    JInternalFrame chartFrame;
    private SteeringPanel panel;
    
    public SteeringAngleDisplay(ChartManager manager){
        this.manager = manager;
        panel = new SteeringPanel();
        chartFrame = new ChartFrame();
        chartFrame.setSize(new Dimension(200, 200));
        chartFrame.setResizable(true);
        chartFrame.setContentPane(panel);
        numSteeringElements = 1;
        addMenuBar();
        chartFrame.setVisible(true);
        panel.repaint();
    }
    
    private void addMenuBar() {        
        selections = new Selection[1];
    }
    
     //Creates a hardcoded selection for steering data. 
     protected void setSteering(Dataset dataset) {
        DataAnalyzer da = manager.getParent();
        
        try{  
        DatasetSelection ds = new DatasetSelection(dataset, 
                    new ArrayList<String>(Arrays.asList(new String[] {"Time,SteeringAngle"})), 
                    new ArrayList<>());
        LinkedList<DatasetSelection> dsList = new LinkedList<DatasetSelection>();
        dsList.add(ds);
        selections[0]= new Selection(dsList);
        }catch(Exception e){
         new MessageBox(da, e.getMessage(), true).setVisible(true);
        }
    }
     
    public void updateOverlay(double xCor) {
        panel.setNumSteeringElements(numSteeringElements);
        panel.xCor = xCor;
        panel.repaint();
    }
    
    public class SteeringPanel extends JPanel{
        int numSteeringElements;
        double xCor;
        
        public void setNumSteeringElements(int numSteeringElements){this.numSteeringElements = numSteeringElements;}
        @Override
             public void paint(Graphics g){
           
            double val = 0;
            // Setting up the background image
            Image background = new BufferedImage(panel.getWidth(),panel.getHeight(), 1);
            Graphics bkG = background.getGraphics();
            bkG.setColor(Color.WHITE);
            
            BufferedImage theImage = null;
            // loads an image of the steering wheel to display.
            try {
                File steeringWheelFile = new File("../libs/pictures/Steering Wheel Sprite DFR.png");
                theImage = ImageIO.read(steeringWheelFile);
                 
            } catch (IOException e1) {
                
            }
            
            Image img = new BufferedImage(panel.getWidth(),panel.getHeight(), 1);
            Graphics imgG = img.getGraphics();
            Graphics2D graphics2D = (Graphics2D)g;
            
            // sets image in the foreground.
            imgG.fillRect(0, 0, panel.getWidth(), panel.getHeight());
            // sets background behind image
            bkG.fillRect(0, 0, panel.getWidth(), panel.getHeight());
            g.drawImage(background, 0,0, panel);
            if(numSteeringElements == 1){
                // Rotate steering wheel based on time and steering angle data
                if (selections != null && 0 < selections.length && selections[0] != null){
                    Selection s = selections[0];  
                  
                    // find way to get yval for xcor
                    XYSeriesCollection col = s.getDataCollection()[0];
                    // Get the y value for the current series.
                    try {
                        // checks for mouse input from beyond the left edge of the chart.
                        if(xCor < 0){
                            val = 0;
                        }else
                          val = DatasetUtilities.findYValue(col, 0, xCor);
                        // checks for right end of graph with no data.
                        if(Double.valueOf(val).isNaN()){
                            val = 0;
                        }
                        double angle = val;
                        
                        int imgWidth = 200;
                        int imgHeight = 200;   
                        // rotates the steering wheel about the center of the panel.  
                        graphics2D.rotate(Math.toRadians(angle), panel.getWidth()/2, panel.getHeight()/2);
                    } catch(IllegalArgumentException e2) {

                    }
                }
               }
          
            //scales the steering wheel image so that it resizes with the panel and stays centered.
            double scaleFactor = panel.getWidth()/(2.0*200);  
            if(scaleFactor == 0){
                scaleFactor = 1;
            }
            AffineTransform wheelScalling =  AffineTransform.getScaleInstance(scaleFactor,scaleFactor);
            AffineTransformOp shrinkImage = new AffineTransformOp(wheelScalling,null);
            graphics2D.drawImage(theImage, shrinkImage, (int)(panel.getWidth()/2-((200/2)*scaleFactor)),(int)(panel.getHeight()/2-((200/2)*scaleFactor)));          
        }
}  
}
