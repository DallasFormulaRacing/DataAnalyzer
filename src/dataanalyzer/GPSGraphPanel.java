/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.swing.JPanel;

/**
 *
 * @author Preston Baxter
 */
public class GPSGraphPanel extends JPanel{
    
    private TrackMap tm;
    
    public GPSGraphPanel(){
        tm = new TrackMap();
        tm.readCSV("test.csv");
    }
    
    public GPSGraphPanel(CategoricalHashMap data){
        tm = new TrackMap(data);
    }
    
    /*
    *   paintComponent redraws the component whenever a redraw event is triggerd by the super JPanel
    */
    @Override
    public void paintComponent(Graphics g){
        //Convert from basic graphics to Graphics2D 
        Graphics2D g2 = (Graphics2D) g;
        
        //Set the new dimensions of the track map
        Dimension d = this.getSize();
        tm.length = d.width;
        tm.width = d.height;
        tm.resize();
        
        /*
        *   Stuff to make the lines look smoother
        *   CAP_BUTT means the line will be a rectangle
        *   JOIN_ROUND means that the joining of two lines will curve instead of coming to a point
        *   The last two lines enable anti-aliasing and then clean up the anti-aliasing
        */
        BasicStroke stroke = new BasicStroke(2.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.add(new RenderingHints(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT));
        
        //Enable the beautifying established above
        g2.setColor(Color.BLACK);
        g2.setRenderingHints(rh);
        g2.setStroke(stroke);
        
        //Draw the track map with all of the settings above
        g2.draw(tm);
        
    }
    
}

/*
*   TrackMap represents a polygon to be drawn
*   TrackMap Takes GPS data and maps it into the polygon class to be drawn later
*/
class TrackMap extends Polygon{
    private ArrayList<Point> points;
    private CategoricalHashMap data;
    private double xMin, xMax, yMin, yMax;
    int length, width;
    
    public TrackMap(){
        xMin = yMin = Integer.MAX_VALUE;
        xMax = yMax = Integer.MIN_VALUE;
        length = 600;
        width = 400;
        points = null;
    }
    
    public TrackMap(CategoricalHashMap data){
        xMin = yMin = Integer.MAX_VALUE;
        xMax = yMax = Integer.MIN_VALUE;
        length = 600;
        width = 400;
        points = null;
        this.data = data;
        
        readData();
    }
    
    //THIS IS TO NEVER BE ACTUALLY USED IN ITS CURRENT STATER
    //USED FOR TESTING PURPOSES ONLY
    public void readCSV(String path){
        points = new ArrayList<>();
        try{
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line = "";
            boolean isx = true;
            boolean gps = false;
            int pos = 0;

            while((line = br.readLine()) != null){
                String[] row = line.trim().split(",");
                
                if(row.length > 1){
                    if(row[1].equals("longitude")){
                        gps = true;
                        isx = true;
                        pos = 0;
                    }else if(row[1].equals("latitude")){
                        gps = true;
                        isx = false;
                        pos = 0;
                    }else{
                        if(gps){

                            if(isx){
                               Point p = points.get(pos);
                               p.x = Double.parseDouble(row[1]);                             
                               
                               xMax = Math.max(p.x, xMax);
                               xMin = Math.min(p.x, xMin);
                            }else{
                               Point p = new Point();
                               p.y = Double.parseDouble(row[1]);
                               p.time = Long.parseLong(row[0]);
                               points.add(p);
                               
                               yMax = Math.max(p.y, yMax);
                               yMin = Math.min(p.y, yMin);
                            }
                            pos++;
                        }
                    }
                }       
            }
        }catch(Exception e){
            System.out.println(e);
        }
        resize();
    }
    
    private void readData(){
        points = new ArrayList<>();
        //Get the lists
        LinkedList<LogObject> longlist = data.getList("Time,Longitude");
        LinkedList<LogObject> latlist = data.getList("Time,Latitude");
        
        //Loop through Lists
        for(int i = 0; i<longlist.size(); i++){
            //xCordinates
            LogObject longitude = longlist.pop();
            longlist.addLast(longitude);
            
            //yCordinates
            LogObject latitude = latlist.pop();
            latlist.addLast(latitude);
            
            Point p = new Point();
            
            try{
                p.x = ((SimpleLogObject) longitude).value;
                p.y = ((SimpleLogObject) latitude).value;
                p.time = ((SimpleLogObject) longitude).time;
                
                xMax = Math.max(p.x, xMax);
                xMin = Math.min(p.x, xMin);
                
                yMax = Math.max(p.y, yMax);
                yMin = Math.min(p.y, yMin);
            
                points.add(p);
                
            }catch(Exception e){
                System.out.println(e);
            }
            
        }
        
        resize();
    }
    
    void resize(){
        super.reset();
        for(int i = 0; i<points.size(); i++){
            Point p = points.get(i);
            p.xScaled = (int) (20 + ((p.x - xMin) * (length - 60)) / (xMax - xMin));
            p.yScaled = (int) (20 + ((p.y - yMin) * (width - 60)) / (yMax - yMin));
            
            super.addPoint(p.xScaled, p.yScaled);
        }
        System.out.println();
    }
    
    class Point{
        public double x, y;
        public long time;
        public int xScaled, yScaled;
    }
    
}
