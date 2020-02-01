/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer.dialog;

import com.arib.categoricalhashtable.CategoricalHashTable;
import dataanalyzer.CategoricalHashMap;
import dataanalyzer.CategorizedValueMarker;
import dataanalyzer.Dataset;
import dataanalyzer.FunctionOfLogObject;
import dataanalyzer.LogObject;
import dataanalyzer.Selection;
import dataanalyzer.SimpleLogObject;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeMap;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;

/**
 *
 * @author aribdhuka
 */
public class StaticMarkersFrame extends javax.swing.JDialog {
    
    LinkedList<Dataset> datasets;
    Selection selection;
    ArrayList<CategorizedValueMarker> currentMarkers;
    
    public StaticMarkersFrame(LinkedList<Dataset> datasets, Selection selection, java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        currentMarkers = new ArrayList<>();
        this.datasets = datasets;
        this.selection = selection;
        initComponents();
        populateList();
        
        staticMarkersList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                //Determine how many clicks, and if double click: open notes dialog
                //get list
                JList list = (JList)evt.getSource();
                //if two clicks
                if (SwingUtilities.isRightMouseButton(evt)) {
                    //get the list model to get element at index
                    ListModel model = list.getModel();
                    //get indices of items selected
                    int[] selected = list.getSelectedIndices();
                    
                    /*
                    * Okay good. So now what we can do is instead of going through and comparing the strings we are getting from the list model...
                    * we know have a global variable called currentMarkers that should be holding the listview in the exact same order
                    * so we take the selection list and pull the elements from there and compare them to get all the same domains. 
                    */
                    ArrayList<Double> uniqueDomains = new ArrayList<>();
                    //add all unique domains to a list
                    for(int i = 0; i < selected.length; i++) {
                        String curr = ("" + model.getElementAt(selected[i])).substring(1, ("" + model.getElementAt(selected[i])).indexOf(','));
                        double currVal = Double.parseDouble(curr);
                        if(!uniqueDomains.contains(currVal)) {
                            uniqueDomains.add(currVal);
                        }
                    }

                    //get tags currently visible
                    //create same length array of CategorizedValueMarkers
                    ArrayList<CategorizedValueMarker> markers = new ArrayList<>();
                    //for each domain selected
                    for(Double domain : uniqueDomains) {
                        //for every marker
                        for(CategorizedValueMarker marker : currentMarkers) {
                            //if the domain matches, add it to the list of all markers
                            if(!(marker.getNotes().matches("Start Lap[0-9]+") || marker.getNotes().matches("End Lap[0-9]+")) && marker.getMarker().getValue() == domain)
                                markers.add(marker);
                        }
                    }

                    //launch notes dialog
                    if(markers.size() > 0) {
                        new MarkerNotesDialog(StaticMarkersFrame.this, markers.toArray(new CategorizedValueMarker[markers.size()])).setVisible(true);
                        populateList();
                    }
                }
            }
        });
        staticMarkersList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                //depending on the code
                switch(e.getKeyCode()) {
                    //if its backspace, remove the item from the datamap
                    case KeyEvent.VK_DELETE :
                    case KeyEvent.VK_BACK_SPACE : deleteSelected(); break;
                }
            }
        });
    }
    
    /**
     * Populates JList with tags from current tag
     */
    protected void populateList() {
        //array list that holds lap markers
        ArrayList<String> lapMarkers = new ArrayList<>();
        //array list that holds all other markers
        ArrayList<String> otherMarkers = new ArrayList<>();
        
        //holds all the markers from the selection
        ArrayList<CategorizedValueMarker> newLapMarkers = new ArrayList<>();
        ArrayList<CategorizedValueMarker> newOtherMarkers = new ArrayList<>();
        
        
        //get map of markers to values
        TreeMap<CategorizedValueMarker, Double> map = selection.getAllValuedMarkers();
        //for each marker
        for(CategorizedValueMarker v : map.keySet()) {
            //if its a lap marker, show all other markers first
            if(v.getNotes().matches("Start Lap[0-9]+") || v.getNotes().matches("End Lap[0-9]+")) {
                lapMarkers.add("(" + String.format("%.2f", v.getMarker().getValue()) + ", " +
                    String.format("%.2f", map.get(v)) + 
                    ") " + v.getNotes());
                newLapMarkers.add(v);

            } else {
                otherMarkers.add("(" + String.format("%.2f", v.getMarker().getValue()) + ", " +
                    String.format("%.2f", map.get(v)) + 
                    ") " + v.getNotes());
                 newOtherMarkers.add(v);

            }
        }
        
        //hold the current markers;
        currentMarkers.clear();
        currentMarkers.addAll(newOtherMarkers);
        currentMarkers.addAll(newLapMarkers);
        
        //compile into array of string
        ArrayList<String> allMarkers = new ArrayList<>();
        allMarkers.addAll(otherMarkers);
        allMarkers.addAll(lapMarkers);
        String[] finalMarkers = new String[allMarkers.size()]; 
        allMarkers.toArray(finalMarkers);
        
        staticMarkersList.setListData(finalMarkers);
    }
    
    private void deleteSelected() {
        if(staticMarkersList.getSelectedIndex() == -1)
            return;
        //get list and model
        ListModel model = staticMarkersList.getModel();
        int[] selected = staticMarkersList.getSelectedIndices();

        //for each selected item
        for(Integer select : selected) {
            //get the marker at the selected index
            CategorizedValueMarker marker = currentMarkers.get(select);
            //delete it
            if(marker != null) {
                for(Dataset dataset : datasets) {
                    dataset.getStaticMarkers().remove(marker);
                }
            }
        }
        populateList();
    }
    
    /**
     * @param TAG TAG of the dataset
     * @param s String collected from list
     * @return CategorizedValueMarker object that has the same domain marker as the string
     */
    private ArrayList<CategorizedValueMarker> getMarkerFromString(String TAG, String s) {
        //holds all the matching markers
        ArrayList<CategorizedValueMarker> matchingMarkers = new ArrayList<>();
        
        //for all markers
        for(CategorizedValueMarker marker : currentMarkers) {
            if(String.format("%.2f", marker.getMarker().getValue()).equals(s.substring(1, s.indexOf(','))))
                matchingMarkers.add(marker);
        }
        
        return matchingMarkers;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        staticMarkersList = new javax.swing.JList<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        staticMarkersList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(staticMarkersList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList<String> staticMarkersList;
    // End of variables declaration//GEN-END:variables
}
