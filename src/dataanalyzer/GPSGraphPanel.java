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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
        tm.readCSV("test.csv");
        
        Graphics2D g2 = (Graphics2D) g;
        
        //g2.setStroke(new BasicStroke(10.0f));
        g2.draw(tm);
        
    }
    
}

class Point{
    public double x, y;
    public int xScaled, yScaled;
}

class TrackMap extends Polygon{
    private ArrayList<Point> points;
    private double xMin, xMax, yMin, yMax;
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
    
    private void resize(){
        for(int i = 0; i<points.size(); i++){
            Point p = points.get(i);
            p.xScaled = (int) (20 + ((p.x - xMin) * (length - 60)) / (xMax - xMin));
            p.yScaled = (int) (20 + ((p.y - yMin) * (width - 60)) / (yMax - yMin));
            
            super.addPoint(p.xScaled, p.yScaled);
        }
        System.out.println();
    }
    
    
}
