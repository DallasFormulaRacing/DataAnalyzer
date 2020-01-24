/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer;

/**
 *
 * @author aribdhuka
 */
public class DuplicateDatasetNameException extends Exception {
    
    private final String datasetName;
    public DuplicateDatasetNameException(String datasetName) {
        super();
        this.datasetName = datasetName;
    }
    
    public String getDatasetName() {
        return datasetName;
    }
    
}
