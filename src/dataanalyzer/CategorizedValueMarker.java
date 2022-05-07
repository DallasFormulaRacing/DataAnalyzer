/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer;

import com.arib.categoricalhashtable.CategoricalHashTableInterface;
import org.jfree.chart.plot.ValueMarker;

/**
 * @author aribdhuka
 * Class that holds CategorizedValueMarkers
 * Essentially just ValueMarkers and a String that defines which category they belong to
 * Also holds notes for the marker
 */
public class CategorizedValueMarker implements CategoricalHashTableInterface, Comparable {
    private String TAG;
    private ValueMarker marker;
    private String notes;

    public CategorizedValueMarker() {
        TAG = "";
        marker = null;
        notes = "";
    }

    public CategorizedValueMarker(String TAG, ValueMarker marker) {
        this.TAG = TAG;
        this.marker = marker;
        notes = "";
    }
    
    public CategorizedValueMarker(String TAG, ValueMarker marker, String notes) {
        this.TAG = TAG;
        this.marker = marker;
        this.notes = notes;
    }

    public String getTAG() {
        return TAG;
    }

    public void setTAG(String TAG) {
        this.TAG = TAG;
    }

    public ValueMarker getMarker() {
        return marker;
    }

    public void setMarker(ValueMarker marker) {
        this.marker = marker;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String hashTag() {
        return TAG;
    }
    
    @Override
    public boolean equals(Object o) {
        if(o instanceof CategorizedValueMarker) {
            if(this.TAG.equals(((CategorizedValueMarker) o).TAG) && this.marker.getValue() == ((CategorizedValueMarker) o).getMarker().getValue())
                return true;
        }
        return false;
    }

    @Override
    public int compareTo(Object o) {
        //compare tags
        int tagcomp = this.TAG.compareTo(((CategorizedValueMarker) o).getTAG());
        if(tagcomp == 0) {
            return ((int) this.marker.getValue()) - ((int) ((CategorizedValueMarker) o).getMarker().getValue());
        } else
            return tagcomp;
        
    }
}
