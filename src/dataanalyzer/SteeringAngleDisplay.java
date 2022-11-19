/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dataanalyzer;

import dataanalyzer.dialog.TagChooserDialog;
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

/**
 *
 * @author Ofek Shaltiel
 */

public class SteeringAngleDisplay{
    private ChartManager manager;
    private int numSteeringElements;
    private ArrayList<JMenuItem> barMenuItems;
    private Selection[] selections;
    private JTextField textFields[];
    private Color colorSteering[];
    JInternalFrame chartFrame;
    private SteeringPanel panel;
    
    public SteeringAngleDisplay(ChartManager manager){
        this.manager = manager;
        panel = new SteeringPanel();
        chartFrame = new ChartFrame();
        chartFrame.setSize(new Dimension(200, 200));
        chartFrame.setResizable(true);
        chartFrame.setContentPane(panel);
        numSteeringElements = 0;
        addMenuBar();
        chartFrame.setVisible(true);
        panel.repaint();
    }
    
    private void addMenuBar() {     
        barMenuItems = new ArrayList<>();
        JMenuBar frameMenuBar = new JMenuBar();        
        //create data menuitem for user to choose data in this chart.
        JMenu SteeringCount = new JMenu("Number Of Wheels");
        JMenu SteeringInfo = new JMenu("Steering Info");
        JTextField textIntField = new JTextField();
        
        textIntField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = textIntField.getText();
                text = text.replaceAll("[^\\d.]", "");      
                textIntField.setText(text);
                int oldNum = numSteeringElements;
                numSteeringElements = Integer.parseInt(text);
                if (oldNum != numSteeringElements){
                    changeNumSteeringElements(oldNum, SteeringInfo);
                }
                chartFrame.repaint();
            }
        });
        SteeringCount.add(textIntField);
        SteeringCount.setText("# of Steering Components");
        SteeringCount.setVisible(true);
        frameMenuBar.add(SteeringCount);     
        frameMenuBar.add(SteeringInfo);  
        
        chartFrame.setJMenuBar(frameMenuBar);
    }
    
    private void changeNumSteeringElements(int oldNum, JMenu menuBar) {
        
        if (oldNum == 0){
            panel.setLayout(new FlowLayout());
            selections = new Selection[numSteeringElements];
            colorSteering = new Color[numSteeringElements];
            textFields = new JTextField[numSteeringElements];
            for (int x = 0; x < numSteeringElements; x++){
                colorSteering[x] = Color.red;
            }
        } else {
            Selection[] saveSel = new Selection[numSteeringElements];
            Color[] colors = new Color[numSteeringElements];
            JTextField[] fields = new JTextField[numSteeringElements];
            for (int x = 0; (x < numSteeringElements) && (x < selections.length); x++){
                if (selections[x] != null){
                    saveSel[x] = selections[x];
                }
                if (colorSteering[x] != null){
                    colors[x] = colorSteering[x];
                }
                if (textFields[x] != null){
                    fields[x] = textFields[x];
                }
            }
            selections = saveSel;
            colorSteering = colors;
            textFields = fields;
        }
        
        for (int x = oldNum; x < numSteeringElements; x++){
            // make settings for all Steerings
            JMenu data1 = new JMenu(""+(x+1));
            JMenuItem dataPicker = new JMenuItem("Choose Data");
            JMenu colorPicker = new JMenu("Choose Color");
            JLabel lred = new JLabel("R");
            JTextField red = new JTextField();
            red.addActionListener(new SteeringListener(x, 0));
            JLabel lgreen = new JLabel("G");
            JTextField green = new JTextField();
            green.addActionListener(new SteeringListener(x, 1));
            JLabel lblue = new JLabel("B");
            JTextField blue = new JTextField();
            blue.addActionListener(new SteeringListener(x, 2));
            colorPicker.add(lred);
            colorPicker.add(red);
            colorPicker.add(lgreen);
            colorPicker.add(green);
            colorPicker.add(lblue);
            colorPicker.add(blue);
            JMenu labelPicker = new JMenu("Choose Label");
            JTextField labelField = new JTextField();
            
            textFields[x] = labelField;
            
            labelField.addActionListener(new SteeringListener(x));
            
            labelPicker.add(labelField);
            data1.add(dataPicker);
            data1.add(colorPicker);
            data1.add(labelPicker);
            
            dataPicker.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    //TODO: OPEN DIALOG WITH TAGS, CHOOSE TAGS/LAPS, THEN APPLY.
                    Selection selec = new Selection();
                    TagChooserDialog tcd = new TagChooserDialog(manager.getParentFrame(), manager.getDatasets(), selec);
                    tcd.setVisible(true);

                    //wait for the dialog to finish running
                    while(tcd.isRunning()) {
                        try {
                            Thread.currentThread().sleep(100);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(DataAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                    //set the chart with new params
                    if(!selec.getUniqueTags().isEmpty())
                        setSteering(selec, Integer.parseInt(data1.getText())-1);
                    }
                }
            );
            menuBar.add(data1);
            barMenuItems.add(data1);
            
        }
        for (int x = oldNum; x> numSteeringElements; x--){
            menuBar.remove(barMenuItems.get(x-1));
            barMenuItems.remove(barMenuItems.get(x-1));
            //selections[x] = null;
        }
        chartFrame.setSize(numSteeringElements * 70, 200);
        updateOverlay(0);
    }
    
    protected void setSteering(Selection selection, int num) {
        selections[num] = selection;
    }
    
    public void updateOverlay(double xCor) {
        //panel.setSize(chartFrame.getWidth(), chartFrame.getHeight());  
        panel.setNumSteeringElements(numSteeringElements);
        panel.xCor = xCor;
        panel.repaint();
        
    }
    
    public void updateColor(int num, int value, int color){
        int red = colorSteering[num].getRed();
        int green = colorSteering[num].getGreen();
        int blue = colorSteering[num].getBlue();
        
        switch(color){
            case 0:
                colorSteering[num] = new Color(value, green, blue);
                break;
            case 1:
                colorSteering[num] = new Color(red, value, blue);
                break;
            default:
                colorSteering[num] = new Color(red, green, value);
                break;
                
        }
    }

    public class SteeringPanel extends JPanel{
        int numSteeringElements;
        double xCor;
       
        
        public void setNumSteeringElements(int numSteeringElements){this.numSteeringElements = numSteeringElements;}
        @Override
             public void paint(Graphics g){
            //Background setup
            double val = 0;
            Image background = new BufferedImage(panel.getWidth(),panel.getHeight(), 1);
            Graphics bkG = background.getGraphics();
            bkG.setColor(Color.WHITE);
            BufferedImage theImage = null;
            try {
                File steeringWheelFile = new File("../libs/pictures/Steering Wheel Sprite DFR2.png");
                theImage = ImageIO.read(steeringWheelFile);
                 
            } catch (IOException e1) {
                
            }
            //Graphics img2G = theImage.getGraphics();
            Image img = new BufferedImage(panel.getWidth(),panel.getHeight(), 1);
            Graphics imgG = img.getGraphics();
            imgG.setColor(Color.white);
            int xOffset;
            int yOffset;
            int width;
             
            Graphics2D graphics2D = (Graphics2D)g; 
    
            //graphics2D.transform(wheelScalling);
            imgG.fillRect(0, 0, panel.getWidth(), panel.getHeight());
            bkG.fillRect(0, 0, panel.getWidth(), panel.getHeight());
            g.drawImage(background, 0,0, panel);
               for (int x = 0; x < numSteeringElements; x++){
                //setup for position of each Steering element
                xOffset = ((panel.getWidth()-panel.getWidth()/(numSteeringElements+2))/(numSteeringElements))*x+panel.getWidth()/(numSteeringElements+2);
                yOffset = panel.getHeight()/2;
                width = panel.getWidth()/(2*(numSteeringElements+1));
                
               
                imgG.setColor(Color.WHITE);
                
                //Write the text/label input by user
                String text = textFields[x].getText();
                imgG.setColor(Color.black);
                imgG.drawString(text, xOffset, yOffset);
                
                //set the color from the user for the Steering
                imgG.setColor(colorSteering[x]);
                
                //Rotate steering wheel based on time and data
                if (selections != null && 0 < selections.length && selections[x] != null){
                    Selection s = selections[x];  
                  
                    //find way to get yval for xcor
                    XYSeriesCollection col = s.getDataCollection()[0];
                    double upper = col.getRangeUpperBound(true);
                    double lower = col.getRangeLowerBound(true);
                        // Get the y value for the current series.
                    try {
                        if(xCor < 0){
                            val = 0;
                        }else
                          val = DatasetUtilities.findYValue(col, 0, xCor);
                        
                        double angle = val;
                        double diff = (val-lower)/(upper-lower);
                        
                     int imgWidth = 200;
                     int imgHeight = 200;   
                     //rotates the steering wheel about the center of the panel.  
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
           
          //  if(val == 0)
            //  g.drawImage(img, 0, 0, panel);
        }
            
            
}
    
    public class SteeringListener implements ActionListener {

        protected int x;
        protected int col;
        protected boolean isColorListener;
        // 0 = red, 1 = green, 2 = blue
        public SteeringListener(int x, int col){
            this.x = x;
            this.col = col;
            isColorListener = true;
        }
        
        public SteeringListener(int x){
            this.x = x;
            isColorListener = false;
        }
        
        
        
        @Override
        public void actionPerformed(ActionEvent e){
            if (isColorListener){
                try {
                    int value = Integer.parseInt(((JTextField)e.getSource()).getText());
                    updateColor(x, value, col);
                } catch (NumberFormatException ex){}
            }
          panel.repaint();
        }
        
    }
}
