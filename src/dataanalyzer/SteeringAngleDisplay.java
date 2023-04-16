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
            int xOffset = 0;
            int yOffset = 0;
            Image img = new BufferedImage(panel.getWidth(),panel.getHeight(), 1);
            Image background = new BufferedImage(panel.getWidth(),panel.getHeight(), 1);
            Graphics imgG = img.getGraphics();
            Graphics2D graphics2D = (Graphics2D)g;
            
            
            BufferedImage theImage = null;
            // loads an image of the steering wheel to display.
            try {
                File steeringWheelFile = new File("../libs/pictures/Steering Wheel Sprite DFR.png");
                theImage = ImageIO.read(steeringWheelFile);
                 
            } catch (IOException e1) {
                
            }

            // sets the steering components images into the foreground.
            imgG.fillRect(0, 0, panel.getWidth(), panel.getHeight());
            // sets background behind the steering component images.
            imgG.setColor(Color.WHITE);
            imgG.fillRect(0, 0, panel.getWidth(), panel.getHeight());
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
                        
                        
                        xOffset = panel.getWidth()/2;
                        yOffset = panel.getHeight()/2;
                        int imgWidth = 200;
                        int imgHeight = 200;   
                        int steeringSliderStartPos= panel.getWidth()/2-panel.getWidth()/20;
                        double steeringSliderFunction = steeringSliderStartPos +angle*((0.41*panel.getWidth())/360.0);
                        imgG.setColor(Color.GREEN);
                        // larger rectangle of steering slider.
                        imgG.fillRect(xOffset/2,7*panel.getHeight()/8,xOffset,panel.getHeight()/5);
                        imgG.setColor(Color.ORANGE);
                        /*
                        * makes a moving rectangle that has the function of xPosition = midPanel + 82 /360 * angle within the above rectangle for panel width 200.
                        * This moving rectangle mirrors the steering wheel movements from -180 degrees to +180 degrees.
                        */
                        imgG.fillRect((int)steeringSliderFunction,7*panel.getHeight()/8,panel.getWidth()/10,panel.getHeight()/5);
                        g.drawImage(img,0,0,panel);
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
            // Symetric scaling of the steering wheel based on the panel size
            AffineTransform wheelScalling =  AffineTransform.getScaleInstance(scaleFactor,scaleFactor);
            AffineTransformOp shrinkImage = new AffineTransformOp(wheelScalling,null);
            // Draws the steering wheel about the center of the panel with increasing scale factor for larger panel sizes.
            graphics2D.drawImage(theImage, shrinkImage, (int)(xOffset-((200/2)*scaleFactor)),(int)(yOffset-((200/2)*scaleFactor)));          
        }
}  
}
