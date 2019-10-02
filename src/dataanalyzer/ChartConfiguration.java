package dataanalyzer;
import java.util.ArrayList;
import javax.swing.*;
import java.io.*;


/**
 * This class will hold an array of chart data.
 * 
 * @author Nolan Davenport
 */
public class ChartConfiguration {
    private int numCharts;
    private ArrayList<ChartAssembly> charts;
    private ChartManager chartManager;
    private Util util = new Util();    
    private String fileDirectory;
   
    //Holds the data for each chart in the configuration.
    class ChartLocation{
        public String fileName;
        
        //These are proportions of the dimention and location of the main data analyzer length. These should be >0 and <1.
        public float x,y;
        public float width,height;

        public String[] selectedTags;
        
        //Constructs an instance of ChartLocation.
        public ChartLocation(String[] selectedTags, float x, float y, float width, float height){
            this.selectedTags = selectedTags;
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
    public ChartConfiguration(ArrayList<ChartAssembly> charts, DataAnalyzer dataAnalyzer, ChartManager chartManager){
        setFileDirectory();
        this.charts = charts;
        this.chartManager = chartManager;
        numCharts = charts.size();
        for(ChartAssembly chart : charts){
            JInternalFrame chartFrame = chart.getChartFrame();
            
            float x = (float)chartFrame.getX() / dataAnalyzer.getWidth();
            float y = (float)chartFrame.getY() / dataAnalyzer.getHeight();
            float width = (float)chartFrame.getWidth() / dataAnalyzer.getWidth();
            float height = (float)chartFrame.getHeight() / dataAnalyzer.getHeight();
            
            locations.add(new ChartLocation(chart.getSelectedTags(), y, width, height));//TODO: figure out how to get file name of a chart.
        }
    }
    
    /**
     * Opens an existing chart configuration.
     * @param filename File name of the saved chart configuration.
     */
    public ChartConfiguration(String filename) throws Exception{//The file name should be a .dfrchartconfig
        setFileDirectory();
        
        ArrayList<String> locationLines = new ArrayList<String>();
        if(filename.contains(".dfrchartconfig")){
            File fin = new File(fileDirectory + File.separator + filename);
            FileInputStream fis = new FileInputStream(fin);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            
            String line;
            while((line = br.readLine()) != null){
                locationLines.add(line);
            }
            
            br.close();
            //TODO: Convert each line to a location in the ChartLocation array
            
        }else{
            System.err.println("That is not the correct file type");
        }
        //TODO: Talk with people making an installation process to figure out file directories. 
    }
    
    /**
     * Saves the current chart configuration to the chart configuration directory with the name specified by the user. 
     * @param filename filename of the file to be saved. Specified by the user during the save process. 
     */
    public void saveChartConfiguration(String filename) throws Exception{
        File fout = new File(fileDirectory + File.separator + filename + ".dfrchartconfig");
        fout.mkdirs();
        fout.createNewFile();
        
        FileOutputStream fos = new FileOutputStream(fout);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        
        for (ChartLocation location : locations) {
            String tags = "";
            for(int i = 0; i < location.selectedTags.length; i++){

                tags += location.selectedTags[i];
                if(i < location.selectedTags.length - 1){
                    tags += ",";
                }
                
            }
            bw.write("PLACE HOLDER" + " " + location.x + " " + location.y + " " + location.width + " " + location.height);//TODO: Fill in "PLACEHOLDER" 
            bw.newLine();
        }
     
        bw.close();
        //TODO: Figure out format
        
        //Possible format for each location per line
        //[String formatted by showing all data types and separated by commas, "AFRAveraged,TPS,RPM"] [x] [y] [width] [height]
        
        
    }
    
    /**
     * Sets the file directory according to the operating system
     */
    public void setFileDirectory(){
        if(util.os == "WINDOWS"){
            fileDirectory = "C:" + File.separator + "Program Files" + File.separator + "DataAnalyzer" + File.separator +"ChartConfigurations";
        }else if(util.os == "MAC"){
            fileDirectory = "C:" + File.separator + "Applications" + File.separator + "DataAnalyzer" + File.separator +"ChartConfigurations";
        }
    }
    
    
}
