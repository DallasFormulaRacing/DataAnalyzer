/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer.charts;

import dataanalyzer.data.Dataset;
import java.util.ArrayList;

/**
 *
 * @author aribdhuka
 */

public class DatasetSelection {
    Dataset dataset;
    ArrayList<String> selectedTags;
    ArrayList<Integer> selectedLaps;

    public DatasetSelection(Dataset dataset, ArrayList<String> selectedTags, ArrayList<Integer> selectedLaps) {
        this.dataset = dataset;
        this.selectedTags = selectedTags;
        this.selectedLaps = selectedLaps;
    }

    public Dataset getDataset() {
        return dataset;
    }

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    public ArrayList<String> getSelectedTags() {
        return selectedTags;
    }

    public void setSelectedTags(ArrayList<String> selectedTags) {
        this.selectedTags = selectedTags;
    }

    public ArrayList<Integer> getSelectedLaps() {
        return selectedLaps;
    }

    public void setSelectedLaps(ArrayList<Integer> selectedLaps) {
        this.selectedLaps = selectedLaps;
    }
    
    
}