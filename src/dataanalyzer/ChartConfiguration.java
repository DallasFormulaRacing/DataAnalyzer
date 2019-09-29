package dataanalyzer;
import java.util.ArrayList;
import javax.swing.*;

/**
 * This class will hold an array of chart data.
 * 
 * @author Nolan Davenport
 */
public class ChartConfiguration {
    private int numCharts;
    private ArrayList<ChartAssembly> charts;
   
    //Holds the data for each chart in the configuration.
    class ChartLocation{
        public String fileName;
        
        //These are proportions of the dimention and location of the main data analyzer length. These should be >0 and <1.
        public float x,y;
        public float width,height;
        
        //Constructs an instance of ChartLocation.
        public ChartLocation(String fileName, float x, float y, float width, float height){
            this.fileName = fileName;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    };
    private ArrayList<ChartLocation> locations = new ArrayList<ChartLocation>();
    
    /**
     * Creates a new chart configuration based on the current state of the charts on screen.
     * @param charts ArrayList of charts that are going to be saved in a chart configuration.
     * @param dataAnalyzer The instantiated object of the DataAnalyzer class. Used to get the dimensions of the window. 
     */
    public ChartConfiguration(ArrayList<ChartAssembly> charts, DataAnalyzer dataAnalyzer){
        this.charts = charts;
        numCharts = charts.size();
        for(ChartAssembly chart : charts){
            JInternalFrame chartFrame = chart.getChartFrame();
            
            float x = (float)chartFrame.getX() / dataAnalyzer.getWidth();
            float y = (float)chartFrame.getY() / dataAnalyzer.getHeight();
            float width = (float)chartFrame.getWidth() / dataAnalyzer.getWidth();
            float height = (float)chartFrame.getHeight() / dataAnalyzer.getHeight();
            
            locations.add(new ChartLocation("", x, y, width, height));//TODO: figure out how to get file name of a chart.
        }
    }
    
    /**
     * Opens an existing chart configuration.
     * @param filename File name of the saved chart configuration.
     */
    public ChartConfiguration(String filename){//The file name should be a .dfrchartconfig
        //TODO: Talk with people making an installation process to figure out file directories. 
    }
    
    /**
     * Saves the current chart configuration to the chart configuration directory with the name specified by the user. 
     * @param filename filename of the file to be saved. Specified by the user during the save process. 
     */
    public void saveChartConfiguration(String filename){
        //TODO: Figure out format
        
        //Possible format for each location per line
        //[file name] [x] [y] [width] [height]
        
    }
    
    
}
