/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dataanalyzer;

import dataanalyzer.dialog.TagChooserDialog;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author djcma
 */
public class PedalDisplay{
    private ChartManager manager;
    private int numBars;
    private ArrayList<JMenuItem> barMenuItems;
    private Selection[] selections;
    JInternalFrame chartFrame;
    PedalPanel panel;
    
    
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
                    changeNumBars(oldNum, frameMenuBar);
                }
                if (numBars > 0){
                    pedalCount.setText("# of Pedals  Choose Data:");
                } else {
                    pedalCount.setText("# of Pedals");
                }
                chartFrame.repaint();
            }
        });
        pedalCount.add(textIntField);
        pedalCount.setText("# of Pedals");
        pedalCount.setVisible(true);
        frameMenuBar.add(pedalCount);     
        
        chartFrame.setJMenuBar(frameMenuBar);
    }
    
    private void changeNumBars(int oldNum, JMenuBar menuBar) {
        selections = new Selection[numBars];
        for (int x = oldNum; x < numBars; x++){
            JMenuItem data1 = new JMenuItem(""+(x+1));
            data1.addActionListener(new ActionListener() {
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
            });
            menuBar.add(data1);
            barMenuItems.add(data1);
            
        }
        if (oldNum > numBars){
            
            /*
            for (int x = oldNum; x> numBars; x--){
                menuBar.remove(barMenuItems.get(x-1));
                barMenuItems.remove(barMenuItems.get(x-1));
                selections[x] = null;
            }*/
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
        //panel.paintG(g);
        
    }
    
    public class PedalPanel extends JPanel{
        int numBars;
        double xCor;
            
        public void setNumBars(int numBars){this.numBars = numBars;}
        @Override
        public void paint(Graphics g){
            Image img = new BufferedImage(panel.getWidth(), panel.getHeight(), 1);
            Graphics imgG = img.getGraphics();
            imgG.setColor(Color.GRAY);
            imgG.fillRect(0, 0, panel.getWidth(), panel.getHeight());
            for (int x = 0; x < numBars; x++){
                imgG.setColor(Color.WHITE);
                imgG.fillRect((panel.getWidth()/(numBars))*x+panel.getWidth()/(2*(numBars+1)), panel.getHeight()/5, panel.getWidth()/(2*(numBars+1)), 3*panel.getHeight()/5);
                imgG.setColor(Color.RED);
                double yCor = 0.0;
                if (x < selections.length && selections[x] != null){
                    Selection s = selections[x];

                    //find way to get yval for xcor
                    XYSeriesCollection col = s.getDataCollection()[0];
                    double upper = col.getRangeUpperBound(true);
                    double lower = col.getRangeLowerBound(true);
                        // Get the y value for the current series.
                    try {
                        double val = DatasetUtilities.findYValue(col, 0, xCor);
                        // Add the value to the list
                        yCor = val;
                        double diff = (val-lower)/(upper-lower);
                        imgG.fillRect((panel.getWidth()/(numBars))*x+panel.getWidth()/(2*(numBars+1)), 
                                panel.getHeight()/5+(int)Math.ceil((3*panel.getHeight()/5)*(1-diff)), /*3*panel.getHeight()/5*/
                                panel.getWidth()/(2*(numBars+1)), 
                                (int)Math.floor((3*panel.getHeight()/5)*diff));
                    } catch(IllegalArgumentException e) {

                    }
                }
            }
            g.drawImage(img, 0, 0, panel);
        }
    }
}
