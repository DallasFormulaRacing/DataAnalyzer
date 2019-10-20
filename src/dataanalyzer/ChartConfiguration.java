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
    
    /**
     * Opens an existing chart configuration.
     * @param filename File name of the saved chart configuration.
     */
    public static void openChartConfiguration(String filename) throws FileNotFoundException, IOException{
        ArrayList<ChartLocation> locations = new ArrayList<>();
        setFileDirectory();
        
        ArrayList<String> locationLines = new ArrayList<String>();
        if(filename.contains(".dfrchartconfig")){
            File fin = new File(filename);
            FileInputStream fis = new FileInputStream(fin);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            
            String line;
            while((line = br.readLine()) != null){
                locationLines.add(line);
            }
            
            br.close();
            
            //Breaks up each line and creates a new ChartLocation for each line and saves it into locations
            for(int i = 0; i < locationLines.size(); i++){
                ChartLocation location = new ChartLocation();
                String[] tempLocation = new String[5];
                tempLocation = locationLines.get(i).split(" ");
                if(!tempLocation[0].equals("null")) {
                    location.selectedTags = tempLocation[0].split("~");
                } else {
                    location.selectedTags = new String[1];
                }
                location.x = Float.parseFloat(tempLocation[1]);
                location.y = Float.parseFloat(tempLocation[2]);
                location.width = Float.parseFloat(tempLocation[3]);
                location.height = Float.parseFloat(tempLocation[4]);
                locations.add(location);
            }
            
            //TODO: Figure out how to display the charts onto the screen. 
            
        }else{
            System.err.println("That is not the correct file type");
        }
    }
    
    /**
     * Saves the current chart configuration to the chart configuration directory with the name specified by the user. 
     * @param filename filename of the file to be saved. Specified by the user during the save process. 
     * @param charts An ArrayList of all the current charts on the screen
     */
    public static void saveChartConfiguration(String filename, ArrayList<ChartAssembly> charts, DataAnalyzer dataAnalyzer, ChartManager chartManager) throws IOException {
        setFileDirectory();
        ArrayList<ChartLocation> locations = new ArrayList<>();
        
        //Fills up locations based on the current state of the charts. 
        for(int i = 0; i < charts.size(); i++){
            JInternalFrame chartFrame = charts.get(i).getChartFrame();
            
            float x = (float)chartFrame.getX() / dataAnalyzer.getWidth();
            float y = (float)chartFrame.getY() / dataAnalyzer.getHeight();
            float width = (float)chartFrame.getWidth() / dataAnalyzer.getWidth();
            float height = (float)chartFrame.getHeight() / dataAnalyzer.getHeight();
            
            ChartLocation currLocation = new ChartLocation();
            currLocation.selectedTags = charts.get(i).getSelectedTags();
            currLocation.x = x;
            currLocation.y = y;
            currLocation.width = width;
            currLocation.height = height;
            locations.add(currLocation);
        }
        
        //saves chart to file
        File fout = new File(fileDirectory + File.separator + filename + ".dfrchartconfig");
        
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
        String home = System.getProperty("user.home");
        if(Util.getOS() == "WINDOWS"){
            fileDirectory = home + "\\AppData\\Local\\DataAnalyzer\\ChartConfigurations";
        }else if(Util.getOS() == "MAC"){
            fileDirectory = "/Applications/DataAnalyzer/ChartConfigurations";
        }else if(Util.getOS() == "LINUX"){
            fileDirectory = "/Applications/DataAnalyzer/ChartConfigurations";
        }
    }
    
    //Holds the data for each chart in the configuration.
    private static class ChartLocation{
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
        
        public ChartLocation() {
            this.selectedTags = new String[1];
            this.x = 0;
            this.y = 0;
            this.width = 0;
            this.height = 0;
        }
    }
    
    
}
