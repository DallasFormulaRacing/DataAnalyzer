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

/**
 *
 * @author djcma
 */

/*
TODO:
    - add labels to pedals
    - color picker
    - add dropdown for each pedal for color and for label text
    - fix removing pedals
*/
public class PedalDisplay{
    private ChartManager manager;
    private int numBars;
    private ArrayList<JMenuItem> barMenuItems;
    private Selection[] selections;
    private JTextField textFields[];
    private Color colorPedal[];
    JInternalFrame chartFrame;
    private PedalPanel panel;
    
    public PedalDisplay(ChartManager manager){
        this.manager = manager;
        panel = new PedalPanel();
        chartFrame = new ChartFrame();
        chartFrame.setSize(new Dimension(200, 200));
        chartFrame.setResizable(true);
        chartFrame.setContentPane(panel);
        numBars = 0;
        addMenuBar();
        chartFrame.setVisible(true);
        panel.repaint();
    }
    
    private void addMenuBar() {     
        barMenuItems = new ArrayList<>();
        JMenuBar frameMenuBar = new JMenuBar();        
        //create data menuitem for user to choose data in this chart.
        JMenu pedalCount = new JMenu("Number Of Pedals");
        JMenu pedalInfo = new JMenu("Pedal Info");
        JTextField textIntField = new JTextField();
        
        textIntField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = textIntField.getText();
                text = text.replaceAll("[^\\d.]", "");      
                textIntField.setText(text);
                int oldNum = numBars;
                numBars = Integer.parseInt(text);
                if (oldNum != numBars){
                    changeNumBars(oldNum, pedalInfo);
                }
                chartFrame.repaint();
            }
        });
        pedalCount.add(textIntField);
        pedalCount.setText("# of Pedals");
        pedalCount.setVisible(true);
        frameMenuBar.add(pedalCount);     
        frameMenuBar.add(pedalInfo);  
        
        chartFrame.setJMenuBar(frameMenuBar);
    }
    
    private void changeNumBars(int oldNum, JMenu menuBar) {
        
        if (oldNum == 0){
            panel.setLayout(new FlowLayout());
            selections = new Selection[numBars];
            colorPedal = new Color[numBars];
            textFields = new JTextField[numBars];
            for (int x = 0; x < numBars; x++){
                colorPedal[x] = Color.red;
            }
        } else {
            Selection[] saveSel = new Selection[numBars];
            Color[] colors = new Color[numBars];
            JTextField[] fields = new JTextField[numBars];
            for (int x = 0; (x < numBars) && (x < selections.length); x++){
                if (selections[x] != null){
                    saveSel[x] = selections[x];
                }
                if (colorPedal[x] != null){
                    colors[x] = colorPedal[x];
                }
                if (textFields[x] != null){
                    fields[x] = textFields[x];
                }
            }
            selections = saveSel;
            colorPedal = colors;
            textFields = fields;
        }
        
        for (int x = oldNum; x < numBars; x++){
            // make settings for all pedals
            JMenu data1 = new JMenu(""+(x+1));
            JMenuItem dataPicker = new JMenuItem("Choose Data");
            JMenu colorPicker = new JMenu("Choose Color");
            JLabel lred = new JLabel("R");
            JTextField red = new JTextField();
            red.addActionListener(new PedalListener(x, 0));
            JLabel lgreen = new JLabel("G");
            JTextField green = new JTextField();
            green.addActionListener(new PedalListener(x, 1));
            JLabel lblue = new JLabel("B");
            JTextField blue = new JTextField();
            blue.addActionListener(new PedalListener(x, 2));
            colorPicker.add(lred);
            colorPicker.add(red);
            colorPicker.add(lgreen);
            colorPicker.add(green);
            colorPicker.add(lblue);
            colorPicker.add(blue);
            JMenu labelPicker = new JMenu("Choose Label");
            JTextField labelField = new JTextField();
            
            textFields[x] = labelField;
            
            labelField.addActionListener(new PedalListener(x));
            
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
                        setPedal(selec, Integer.parseInt(data1.getText())-1);
                    }
                }
            );
            menuBar.add(data1);
            barMenuItems.add(data1);
            
        }
        for (int x = oldNum; x> numBars; x--){
            menuBar.remove(barMenuItems.get(x-1));
            barMenuItems.remove(barMenuItems.get(x-1));
            //selections[x] = null;
        }
        chartFrame.setSize(numBars * 70, 200);
        updateOverlay(0);
    }
    
    protected void setPedal(Selection selection, int num) {
        selections[num] = selection;
    }
    
    public void updateOverlay(double xCor) {
        //panel.setSize(chartFrame.getWidth(), chartFrame.getHeight());  
        panel.setNumBars(numBars);
        panel.xCor = xCor;
        panel.repaint();
        
    }
    
    public void updateColor(int num, int value, int color){
        int red = colorPedal[num].getRed();
        int green = colorPedal[num].getGreen();
        int blue = colorPedal[num].getBlue();
        
        switch(color){
            case 0:
                colorPedal[num] = new Color(value, green, blue);
                break;
            case 1:
                colorPedal[num] = new Color(red, value, blue);
                break;
            default:
                colorPedal[num] = new Color(red, green, value);
                break;
                
        }
    }
    
    public class PedalPanel extends JPanel{
        int numBars;
        double xCor;
            
        public void setNumBars(int numBars){this.numBars = numBars;}
        @Override
        public void paint(Graphics g){
            //Background setup
            Image img = new BufferedImage(panel.getWidth(), panel.getHeight(), 1);
            Graphics imgG = img.getGraphics();
            imgG.setColor(Color.GRAY);
            imgG.fillRect(0, 0, panel.getWidth(), panel.getHeight());
            for (int x = 0; x < numBars; x++){
                //setup for rectangle of each pedal
                int xOffset = ((panel.getWidth()-panel.getWidth()/(numBars+2))/(numBars))*x+panel.getWidth()/(numBars+2);
                int yOffset = panel.getHeight()/5;
                int width = panel.getWidth()/(2*(numBars+1));
                imgG.setColor(Color.WHITE);
                imgG.fillRect(xOffset, yOffset, width, 3*panel.getHeight()/5);
                
                
                //Write the text/label input by user
                String text = textFields[x].getText();
                imgG.setColor(Color.black);
                imgG.drawString(text, xOffset, yOffset);
                
                //set the color from the user for the pedal
                imgG.setColor(colorPedal[x]);
                
                //Fill rect based on time and data
                if (selections != null && x < selections.length && selections[x] != null){
                    Selection s = selections[x];

                    //find way to get yval for xcor
                    XYSeriesCollection col = s.getDataCollection()[0];
                    double upper = col.getRangeUpperBound(true);
                    double lower = col.getRangeLowerBound(true);
                        // Get the y value for the current series.
                    try {
                        double val = DatasetUtilities.findYValue(col, 0, xCor);
                        // Add the value to the list
                        double yCor = val;
                        double diff = (val-lower)/(upper-lower);
                        imgG.fillRect(xOffset, 
                                panel.getHeight()/5+(int)Math.ceil((3*panel.getHeight()/5)*(1-diff)), /*3*panel.getHeight()/5*/
                                width, 
                                (int)Math.floor((3*panel.getHeight()/5)*diff));
                    } catch(IllegalArgumentException e) {

                    }
                }
            }
            g.drawImage(img, 0, 0, panel);
        }
    }
    
    public class PedalListener implements ActionListener {

        protected int x;
        protected int col;
        protected boolean isColorListener;
        // 0 = red, 1 = green, 2 = blue
        public PedalListener(int x, int col){
            this.x = x;
            this.col = col;
            isColorListener = true;
        }
        
        public PedalListener(int x){
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
