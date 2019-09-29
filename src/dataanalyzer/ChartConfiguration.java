/*
 * This file will hold an array of chart data
 */
package dataanalyzer;
import java.util.ArrayList;
import javax.swing.*;
/**
 *
 * @author Nolan Davenport
 */
public class ChartConfiguration {
    private int numCharts;
    private ArrayList<ChartAssembly> charts;
   
    //Holds the data for each chart in the configuration
    class ChartLocation{
        public String fileName;
        
        //These are proportions of the length of the main data analyzer length. These should be >0 and <1
        public float x,y;
        public float width,height;
        
        //Constructs an instance of ChartLocation
        public ChartLocation(String fileName, float x, float y, float width, float height){
            this.fileName = fileName;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    };
    private ArrayList<ChartLocation> locations = new ArrayList<ChartLocation>();
    
    //for creating a new chart configuration
    public ChartConfiguration(ArrayList<ChartAssembly> charts, DataAnalyzer dataAnalyzer){
        this.charts = charts;
        numCharts = charts.size();
        for(ChartAssembly chart : charts){
            JInternalFrame chartFrame = chart.getChartFrame();
            
            float x = chartFrame.getX() / dataAnalyzer.getX();
            float y = chartFrame.getY() / dataAnalyzer.getY();
            float width = chartFrame.getWidth() / dataAnalyzer.getWidth();
            float height = chartFrame.getHeight() / dataAnalyzer.getHeight();
            
            locations.add(new ChartLocation("", x, y, width, height));//TODO: figure out how to get file name of a chart
        }
    }
    
    //For opening an existic chart configuration
    public ChartConfiguration(String filename){//The file name should be a .dfrchartconfig
        //TODO: Talk with people making an installation process to figure out file directories. 
    }
    
    public void saveChartConfiguration(){
        //TODO: Figure out format
        
        //Possible format for each location per line
        //[file name] [x] [y] [width] [height]
        
    }
    
    
}
