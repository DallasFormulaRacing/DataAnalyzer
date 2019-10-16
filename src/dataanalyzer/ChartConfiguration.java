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
    private static String fileDirectory;
   
    //Holds the data for each chart in the configuration.
    public class ChartLocation{
        public String fileName;
        
        //These are proportions of the dimension and location of the main data analyzer length. These should be >0 and <1.
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
    }
    
    /**
     * Opens an existing chart configuration.
     * @param filename File name of the saved chart configuration.
     */
    public static void openChartConfiguration(String filename) throws Exception{
        ChartLocation[] locations = new ChartLocation[20];
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
            
            //Breaks up each line and creates a new ChartLocation for each line and saves it into locations
            for(int i = 0; i < locationLines.size(); i++){
                String[] tempLocation = new String[5];
                tempLocation = locationLines.get(i).split(" ");
                locations[i].selectedTags = locationLines.get(i).split("~");
                locations[i].x = Float.parseFloat(tempLocation[1]);
                locations[i].y = Float.parseFloat(tempLocation[2]);
                locations[i].width = Float.parseFloat(tempLocation[3]);
                locations[i].height = Float.parseFloat(tempLocation[4]);
            }
            
            //TODO: Figure out how to display the charts onto the screen. 
            
        }else{
            System.err.println("That is not the correct file type");
        }
    }
    
    /**
     * Saves the current chart configuration to the chart configuration directory with the name specified by the user. 
     * @param filename filename of the file to be saved. Specified by the user during the save process. 
     */
    public static void saveChartConfiguration(String filename, ArrayList<ChartAssembly> charts, DataAnalyzer dataAnalyzer, ChartManager chartManager) throws Exception{
        setFileDirectory();
        ChartLocation[] locations = new ChartLocation[20];
        
        //Fills up locations based on the current state of the charts. 
        for(int i = 0; i < charts.size(); i++){
            JInternalFrame chartFrame = charts.get(i).getChartFrame();
            
            float x = (float)chartFrame.getX() / dataAnalyzer.getWidth();
            float y = (float)chartFrame.getY() / dataAnalyzer.getHeight();
            float width = (float)chartFrame.getWidth() / dataAnalyzer.getWidth();
            float height = (float)chartFrame.getHeight() / dataAnalyzer.getHeight();
            
            locations[i].selectedTags = charts.get(i).getSelectedTags();
            locations[i].x = x;
            locations[i].y = y;
            locations[i].width = width;
            locations[i].height = height;
        }
        
        //saves chart to file
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
                    tags += "~";
                }
                
            }
            bw.write(tags + " " + location.x + " " + location.y + " " + location.width + " " + location.height);
            bw.newLine();
        }
     
        bw.close();    
    }
    
    /**
     * Sets the file directory according to the operating system
     */
    public static void setFileDirectory(){
        if(Util.getOS() == "WINDOWS"){
            fileDirectory = "C:" + File.separator + "Program Files" + File.separator + "DataAnalyzer" + File.separator +"Chart Configurations";
        }else if(Util.getOS() == "MAC"){
            fileDirectory = "Applications" + File.separator + "DataAnalyzer" + File.separator +"Chart Configurations";
        }else if(Util.getOS() == "LINUX"){
            fileDirectory = File.separator + "run" + File.separator + "DataAnalyzer" + File.separator +"Chart Configurations";
        }
    }
    
    
}
