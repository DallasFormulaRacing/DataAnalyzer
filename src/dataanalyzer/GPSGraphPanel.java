/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
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
        TrackMap tm = new TrackMap();
        tm.readCSV("D:\\CodingThings\\DataAnalyzer\\Poly.csv");
        
        Graphics2D g2 = (Graphics2D) g;
        
        g2.setStroke(new BasicStroke(10.0f));
        g2.draw(tm);
        
    }
    
}

class Point{
    public int x, xScaled;
    public int y, yScaled;
}

class TrackMap extends Polygon{
    private ArrayList<Point> points;
    private int xMin, xMax, yMin, yMax;
    private int length, width;
    
    public TrackMap(){
        xMin = yMin = Integer.MAX_VALUE;
        xMax = yMax = Integer.MIN_VALUE;
        length = 600;
        width = 400;
        points = null;
    }
    
    //THIS IS TO NEVER BE ACTUALLY USED IN ITS CURRENT STATER
    //USED FOR TESTING PURPOSES ONLY
    public void readCSV(String path){
        points = new ArrayList<>();
        try{
            Scanner in = new Scanner(new File(path));
            while(in.hasNextLine()){
                Point p = new Point();
                String[] row = in.nextLine().split(",");
                p.x = Integer.parseInt(row[0]);
                p.y = Integer.parseInt(row[1]);
                
                xMax = Math.max(xMax, p.x);
                yMax = Math.max(yMax, p.y);
                
                xMin = Math.min(xMin, p.x);
                yMin = Math.min(yMin, p.y);
                
                points.add(p);
            }
        }catch(Exception e){
            System.out.println(e);
        }
        resize();
    }
    
    private void resize(){
        for(int i = 0; i<points.size(); i++){
            Point p = points.get(i);
            p.xScaled = 20 + ((p.x - xMin) * (length - 60)) / (xMax - xMin);
            p.yScaled = 20 + ((p.y - yMin) * (width - 60)) / (yMax - yMin);
            
            super.addPoint(p.xScaled, p.yScaled);
        }
    }
    
    
}
