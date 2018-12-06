/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 *
 * @author aribdhuka
 */
public class VehicleData {
    
    private Map<String, Double> data;
    
    public VehicleData() {
        data = new HashMap<>();
    }
    
    public VehicleData(Map<String, Double> data) {
        this.data = data;
    }
    
    public double get(String key) {
        return data.get(key);
    }
    
    public Set<String> getKeySet() {
        return data.keySet();
    }
    
    public String getStringOfData() {
        //create string builder
        StringBuilder sb = new StringBuilder();
        //get the key set
        Set<String> keySet = getKeySet();
        //for each key
        for(String s : keySet) {
            //save the key
            String key = s;
            //get the value associated with this key
            double val = get(s);
            //create string
            String toAppend = key + " = " + val;
            //append the string created, followed with newline char
            sb.append(toAppend);
            sb.append("\n");
        }
        return sb.toString();
    }
    
    public void clearData() {
        data.clear();
    }
    
    public void applyVehicleData(String dataStr) {
        
        //clean up the string
        String[] lines = dataStr.replace(" ", "").split("\n");
        
        //for each line
        for(String line : lines) {
            //make sure it follows the regex -> some string followed by an equal followed by numbers
            if(!line.matches("^[0-9a-zA-Z]+=[0-9.]+"))
                continue;
            if(line.indexOf('.') != line.lastIndexOf('.'))
                continue;
            //get string from start to '='
            String varName = line.substring(0, line.indexOf('='));
            //get string from '='+1 to end and parse
            double value = Double.parseDouble(line.substring(line.indexOf("=")+1));
            
            //add that into the data 
            data.put(varName, value);
            
        }
    }
    
    private String fixBuffers(String data) {
        String fixedData = "";
        for(int i = 0; i < fixedData.length(); i++) {
            if(data.charAt(i) != ' ') {   
                fixedData += data.charAt(i);
            }
        }
        
        return fixedData;
    }
}
