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
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 *
 * @author Preston Baxter
 */
public class GPSGraphPanel extends JPanel{
    
    private TrackMap tm;
    private String overlayParam;
    private double xCor;
    
    public GPSGraphPanel(){
        tm = new TrackMap();
        tm.readCSV("test.csv");
    }
    
    public GPSGraphPanel(CategoricalHashMap data){
        tm = new TrackMap(data);
    }
    
    public GPSGraphPanel(CategoricalHashMap data, String overlayParam){
        tm = new TrackMap(data);
        setOverlay(overlayParam);
    }
    
    public void setOverlay(String overlayParam){
        this.overlayParam = overlayParam;
        tm.setOverlay(this.overlayParam);
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
        BasicStroke stroke = new BasicStroke(4.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.add(new RenderingHints(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT));
        
        //Enable the beautifying established above
        g2.setColor(Color.BLACK);
        g2.setRenderingHints(rh);
        g2.setStroke(stroke);
        
        //Draw the track map with all of the settings above
        g2.draw(tm);
        if(tm.getOverlay() != null && !(tm.getOverlay().isClear())){
            tm.getOverlay().setXCor(this.xCor);
            tm.getOverlay().paintComponent(g2);
        }
    }
    
    public void setXCor(double xCor){
        this.xCor = xCor;
    }
}

/*
*   TrackMap represents a polygon to be drawn
*   TrackMap Takes GPS data and maps it into the polygon class to be drawn later
*/
class TrackMap extends Polygon{
    protected ArrayList<Point> points;
    protected CategoricalHashMap data;
    private double xMin, xMax, yMin, yMax;
    private Overlay overlay;
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
        LinkedList<LogObject> longlist = null;
        LinkedList<LogObject> latlist = null;
        try{
            longlist = data.getList("Time,Longitude");
            latlist = data.getList("Time,Latitude");
        }catch(Exception e){
            System.out.println("Please make sure Data map has GPS data. It seems it is not there");
            return;
        }
        
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
                return;
            }
            
        }
        
        resize();
    }
    
    public Overlay getOverlay(){
        return overlay;
    }
    
    public void setOverlay(String param){
        overlay = new Overlay(param);
    }
    
    void resize(){
        super.reset();
        for(int i = 0; i<points.size(); i++){
            Point p = points.get(i);
            p.xScaled = (int) (20 + ((p.x - xMin) * (length - 60)) / (xMax - xMin));
            p.yScaled = (int) (20 + ((p.y - yMin) * (width - 60)) / (yMax - yMin));
            
            super.addPoint(p.xScaled, p.yScaled);
        }
    }
    
    
    //A point structure for easy data manipulation
    class Point{
        public double x, y;
        public long time;
        public int xScaled, yScaled;
    }
    
    
    /*
    *   Overlay is a class drawn seperatly of the track map. It takes the points
    *   from the Track Map class and draws lines between them colored based on 
    *   their distance from the minimum value (Low values red, high values green)
    */
    class Overlay extends JComponent{
        private double max, min, xCor = 0;
        private ArrayList<SimpleLogObject> logPoints;
        private LinkedList<LogObject> list;
        private Graphics2D g;
        
        public Overlay(){
            this.max = Integer.MIN_VALUE;
            this.min = Integer.MAX_VALUE;
            this.logPoints = new ArrayList<>();
        }
        
        public Overlay(String param){
            this.max = Integer.MIN_VALUE;
            this.min = Integer.MAX_VALUE;
            this.logPoints = new ArrayList<>();
            
            try{
                list = data.getList(param);
            }catch(Exception e){
                System.out.println("Please make sure Data map has the appropriate data you are trying to overlay. It seems it is not there");
                return;
            }
            processLog();
        }
        
        public void processLog(){
            for(int i = 0; i<list.size(); i++){
                try{
                    SimpleLogObject slo = (SimpleLogObject)list.pop();
                    logPoints.add(slo);
                    list.addLast(slo);
                    
                    max = Math.max(slo.value, max);
                    min = Math.min(slo.value, min);
                }catch(Exception e){
                    System.out.println(e);
                }
            }
        }
        
        public int getCarPoint(double xCor){
            for(int i = 0; i < list.size(); i++){
                if((long) xCor < list.get(i).getTime()){
                    return i;
                }
            }
            return 0;
        }
        
        @Override
        public void paintComponent(Graphics g){
            Graphics2D g2 = (Graphics2D) g;
            
            int x1 = points.get(0).xScaled;
            int y1 = points.get(0).yScaled;
            int x2 = 0, y2 = 0;
            for(int i = 1; i<logPoints.size(); i++){
                x2 = points.get(i).xScaled;
                y2 = points.get(i).yScaled;
                
                /*
                    This is a function that return a float between 0 and 1/3 so 
                    the color of the line will be between red and green on the
                    color wheel (The hue will range form 0 to 120 degrees)
                */
                float hue = (float) ((logPoints.get(i).value - min) / (max - min)) / 3;
                
                g2.setColor(Color.getHSBColor(hue, 1.0f, 1.0f));
                g2.drawLine(x1,y1,x2,y2);
                
                x1 = x2;
                y1 = y2;
            }
            g2.setColor(Color.RED);
            int carPointIndex = getCarPoint(xCor);
            g2.fillOval(points.get(carPointIndex).xScaled-5, points.get(carPointIndex).yScaled-5, 10, 10);
        }
        
        public boolean isClear(){
            return logPoints.size() == 0;
        }
        
        public void setXCor(double xCor){
            this.xCor = xCor;
        }
    }
}
