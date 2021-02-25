package dataanalyzer;

import java.util.ArrayList;
import javax.swing.*;
import java.io.*;
import java.awt.Dimension;
import com.arib.toast.Toast;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 * This class will hold an array of chart data.
 *
 * @author Nolan Davenport
 */
public class ChartConfiguration {

    private static String fileDirectory;

    /**
     * Opens an existing chart configuration.
     *
     * @param filename File name of the saved chart configuration.
     */
    public static void openChartConfiguration(String filename, DataAnalyzer dataAnalyzer, ChartManager chartManager) throws FileNotFoundException, IOException, ParseException {
        setFileDirectory();
        if (filename.contains(".dfrchartconfig")) {
            File fin = new File(filename);
            FileInputStream fis = new FileInputStream(fin);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            
            
            Object obj = new JSONParser().parse(br);
            JSONArray config = (JSONArray) obj;
            Dimension frameSize = dataAnalyzer.getSize();

            dataAnalyzer.chartManager.clearCharts();
            for(int i = 0; i < config.size(); i++) {
                JSONObject chartInfo = (JSONObject) config.get(i);
                
                //basic chart properties
                int x = (int) ((double) frameSize.width * (double)chartInfo.get("x"));                
                int y = (int) ((double) frameSize.height * (double)chartInfo.get("y"));
                int width = (int) ((double) frameSize.width * (double)chartInfo.get("width"));
                int height = (int) ((double) frameSize.height * (double)chartInfo.get("height"));
                
                //chart data properties
                JSONArray selection = (JSONArray) chartInfo.get("selection");
                LinkedList<DatasetSelection> datasetSelections = new LinkedList<>();
                if (selection.size() > 0) {
                    for(int j = 0; j < selection.size(); j++) {
                        JSONObject datasetselectionjson = (JSONObject) selection.get(j);
                        String datasetName = (String)datasetselectionjson.get("name");
                        Dataset dataset = null;
                        if(datasetName.isEmpty() || dataAnalyzer.chartManager.getDatasets().size() == 1) {
                            dataAnalyzer.chartManager.getMainDataset();
                        } else {
                            dataset = dataAnalyzer.chartManager.getDataset(datasetName);
                        }
                        String tags = (String) datasetselectionjson.get("tags");
                        String laps = (String) datasetselectionjson.get("laps");

                        //convert String of tags into actual list
                        //NEEDS to be comma(space)
                        ArrayList<String> tagsList = new ArrayList<>();
                        if(!tags.equals("[]")) {
                            String[] tagsArray = tags.substring(1, tags.length() - 1).split(", ");
                            for (int k = 0; k < tagsArray.length; k++) {
                                tagsList.add(tagsArray[k]);
                            }
                        }

                        //convert String of laps into Integer list of laps
                        ArrayList<Integer> lapsList = new ArrayList<>();
                        if(!laps.equals("[]")) {
                            String[] lapsArray = laps.substring(1, laps.length() - 1).split(", ");
                            for (int k = 0; k < lapsArray.length; k++) {
                                lapsList.add(Integer.parseInt(lapsArray[k]));
                            }
                        }

                        //create DatasetSelection
                        DatasetSelection ds = new DatasetSelection(dataset, tagsList, lapsList);
                        datasetSelections.add(ds);   
                    }
                }
                
                //setup selection object
                Selection selectionObj = new Selection(datasetSelections);
                
                //setup chart assembly
                ChartAssembly ca = dataAnalyzer.chartManager.addChart();
                ca.chartFrame.setLocation(x, y);
                ca.chartFrame.setSize(width, height);
                ca.selection = selectionObj;
                if(datasetSelections.size() == 0) {
                    ca.showEmptyGraph();
                } else {
                    ca.setChart(ca.selection.getUniqueTags().toArray(new String[ca.selection.getUniqueTags().size()]));
                }
            }
            

        } else {
            System.err.println("That is not the correct file type");
        }
    }

    /**
     * Saves the current chart configuration to the chart configuration
     * directory with the name specified by the user.
     *
     * @param filename filename of the file to be saved. Specified by the user
     * during the save process.
     * @param charts An ArrayList of all the current charts on the screen
     */
    public static void saveChartConfiguration(String filename, ArrayList<ChartAssembly> charts, DataAnalyzer dataAnalyzer, ChartManager chartManager) throws IOException {
        setFileDirectory();
        JSONArray locations = new JSONArray();

        //Fills up locations based on the current state of the charts. 
        for (int i = 0; i < charts.size(); i++) {
            if((charts.get(i).getChartFrame().isVisible())) {
                JInternalFrame chartFrame = charts.get(i).getChartFrame();

                float x = (float) chartFrame.getX() / dataAnalyzer.getWidth();
                float y = (float) chartFrame.getY() / dataAnalyzer.getHeight();
                float width = (float) chartFrame.getWidth() / dataAnalyzer.getWidth();
                float height = (float) chartFrame.getHeight() / dataAnalyzer.getHeight();
                
                JSONObject entry = new JSONObject();
                entry.put("x", x);
                entry.put("y", y);
                entry.put("width", width);
                entry.put("height", height);
                
                ChartAssembly chart = charts.get(i);
                JSONArray selection = new JSONArray();
                for(DatasetSelection ds : chart.selection.getDatasetSelections()) {
                    JSONObject datasetselectionjson = new JSONObject();
                    String datasetName = ds.dataset.name;
                    if(dataAnalyzer.chartManager.getDatasets().size() == 1) {
                        datasetName = "";
                    }
                    datasetselectionjson.put("name", datasetName);
                    datasetselectionjson.put("tags", ds.selectedTags.toString());
                    datasetselectionjson.put("laps", ds.selectedLaps.toString());
                    selection.add(datasetselectionjson);
                }
                entry.put("selection", selection);
                
                locations.add(entry);
            }
        }

        //saves chart to file
        PrintWriter pw = new PrintWriter(fileDirectory + File.separator + filename + ".dfrchartconfig"); 
        pw.write(locations.toJSONString());
        pw.close();
    }

    /**
     * Sets the file directory according to the operating system
     */
    public static void setFileDirectory() {
        String home = System.getProperty("user.home");
        String os = Installer.getOS();
        if (os.equals("Windows")) {
            fileDirectory = home + "\\AppData\\Local\\DataAnalyzer\\ChartConfigurations";
        } else if (os.equals("Mac")) {
            fileDirectory = "/Applications/DataAnalyzer/ChartConfigurations";
        } else if (os.equals("Linux")) {
            fileDirectory = "/Applications/DataAnalyzer/ChartConfigurations";
        }
    }

    //Holds the data for each chart in the configuration.
    private static class ChartLocation {

        public String fileName;

        //These are proportions of the dimension and location of the main data analyzer length. These should be >0 and <1.
        public float x, y;
        public float width, height;

        public String datasetName;     
        ArrayList<String> selectedTags;
        ArrayList<Integer> selectedLaps;

        //Constructs an instance of ChartLocation.
        public ChartLocation(String datasetName, ArrayList<String> selectedTags, ArrayList<Integer> selectedLaps, float x, float y, float width, float height) {
            this.selectedTags = selectedTags;
            this.selectedLaps = selectedLaps;
            this.datasetName = datasetName;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public ChartLocation() {
            this.selectedTags = new ArrayList<>();
            this.x = 0;
            this.y = 0;
            this.width = 0;
            this.height = 0;
        }
    }

}
