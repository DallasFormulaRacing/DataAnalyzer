/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer.dialog;

import dataanalyzer.Dataset;
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
}