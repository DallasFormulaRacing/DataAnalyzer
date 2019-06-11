/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer.dialog;

import com.arib.categoricalhashtable.CategoricalHashTable;
import dataanalyzer.CategoricalHashMap;
import dataanalyzer.CategorizedValueMarker;
import dataanalyzer.FunctionOfLogObject;
import dataanalyzer.LogObject;
import dataanalyzer.SimpleLogObject;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;

/**
 *
 * @author aribdhuka
 */
public class StaticMarkersFrame extends javax.swing.JFrame {
    
    private CategoricalHashMap dataMap;
    private String[] tags;
    private CategoricalHashTable<CategorizedValueMarker> staticMarkers;

    public StaticMarkersFrame(CategoricalHashMap dataMap, String[] tags, CategoricalHashTable<CategorizedValueMarker> staticMarkers) {
        this.dataMap = dataMap;
        this.tags = tags;
        this.staticMarkers = staticMarkers;
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
                    //holds how many different domains are in selected
                    int domainCount = 0;
                    //check each element, if its a duplicate
                    for(int i = 0; i < selected.length; i++) {
                        boolean wasBefore = false;
                        //check every element before
                        for(int j = i - 1; j > 0; j--) {
                            //if prev element and curr element match: fail
                            String prev = ("" + model.getElementAt(selected[j])).substring(1, ("" + model.getElementAt(selected[j])).indexOf(','));
                            String curr = ("" + model.getElementAt(selected[i])).substring(1, ("" + model.getElementAt(selected[i])).indexOf(','));
                            if(prev.equals(curr)) {
                                wasBefore = true;
                                break;
                            }
                        }

                        //if we didnt find the same element before, 
                        if(!wasBefore)
                            domainCount++;
                    }

                    //
                    int[] selectedDomains = new int[domainCount];
                    int k = 0;
                    //get only 
                    for(int i = 0; i < selected.length; i++) {
                        boolean wasBefore = false;
                        //check every element before
                        for(int j = i - 1; j > 0; j--) {
                            //if prev element and curr element match: fail
                            //if prev element and curr element match: fail
                            String prev = ("" + model.getElementAt(selected[j])).substring(1, ("" + model.getElementAt(selected[j])).indexOf(','));
                            String curr = ("" + model.getElementAt(selected[i])).substring(1, ("" + model.getElementAt(selected[i])).indexOf(','));
                            if(prev.equals(curr)) {
                                wasBefore = true;
                                break;
                            }
                        }

                        //if we didnt find the same element before, 
                        if(!wasBefore) {
                            selectedDomains[k] = selected[i];
                            k++;
                        }
                    }
                    //get tags currently visible
                    //create same length array of CategorizedValueMarkers
                    ArrayList<CategorizedValueMarker> markers = new ArrayList<>();
                    //for each domain selected
                    for(int i = 0; i < selectedDomains.length; i++) {
                        //get the corresponding CategorizedValueMarker
                        for(String tag : tags) {
                            CategorizedValueMarker currMarker = getMarkerFromString(tag, "" + model.getElementAt(selectedDomains[i]));
                            if(currMarker != null && !(currMarker.getNotes().matches("Start Lap[0-9]+") || currMarker.getNotes().matches("End Lap[0-9]+"))) {
                                markers.add(currMarker);
                            }
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
        
        //get markers for each tag
        for(String tag : tags) {
            LinkedList<LogObject> data = dataMap.getList(tag);
            LinkedList<CategorizedValueMarker> markers = staticMarkers.getList(tag);
            for(CategorizedValueMarker v : markers) {
                //if its a lap marker, show all other markers first
                if(v.getNotes().matches("Start Lap[0-9]+") || v.getNotes().matches("End Lap[0-9]+")) {
                    lapMarkers.add("(" + String.format("%.2f", v.getMarker().getValue()) + ", " +
                        String.format("%.2f", getValueAt(v.getMarker().getValue(), data)) + 
                        ") " + v.getNotes());
                } else {
                    otherMarkers.add("(" + String.format("%.2f", v.getMarker().getValue()) + ", " +
                        String.format("%.2f", getValueAt(v.getMarker().getValue(), data)) + 
                        ") " + v.getNotes());
                }
            }
        }
        
        //compile into array of string
        ArrayList<String> allMarkers = new ArrayList<>();
        allMarkers.addAll(otherMarkers);
        allMarkers.addAll(lapMarkers);
        String[] finalMarkers = new String[allMarkers.size()]; 
        allMarkers.toArray(finalMarkers);
        
        staticMarkersList.setListData(finalMarkers);
    }
    
    private void deleteSelected() {
        //get list and model
        ListModel model = staticMarkersList.getModel();
        for(String tag : tags) {
            staticMarkers.remove(getMarkerFromString(tag, "" +
                    model.getElementAt(staticMarkersList.getSelectedIndex())));
        }
    }
    
    /**
     * Returns the value at a given point.
     * Handles children to return accurate values for simple and functionof objects
     * @param x domain value
     * @param data list of data
     * @return corresponding y value for given x
     */
    private double getValueAt(double x, LinkedList<LogObject> data) {
        double lastDiff = Double.MAX_VALUE;
        double valueToReturn = Double.NaN;
        //for each object, check if this is correct object, if so: return value
        for(LogObject lo : data) {
            if(lo instanceof SimpleLogObject) {
                if(lo.getTime() == getRoundedTime(x)) {
                    return ((SimpleLogObject) lo).getValue();
                }
            } else if(lo instanceof FunctionOfLogObject) {
                //for function of we can't round to the nearest time
                //so we have to find the closest value
                if(Math.abs(((FunctionOfLogObject) lo).getX()-x) < lastDiff) { 
                    valueToReturn = ((FunctionOfLogObject) lo).getValue();
                } else {
                    return valueToReturn;
                }
            }
        }
        
        //return NaN for not value found
        return valueToReturn;
    }
    
     /**
     * 
     * @param TAG TAG of the dataset
     * @param s String collected from list
     * @return CategorizedValueMarker object that has the same domain marker as the string
     */
    private CategorizedValueMarker getMarkerFromString(String TAG, String s) {
        for(CategorizedValueMarker marker : staticMarkers.getList(TAG)) {
            if(String.format("%.2f", marker.getMarker().getValue()).equals(s.substring(1, s.indexOf(','))))
                return marker;
        }
        return null;
    }
    
    private long getRoundedTime(double val) {
        //time to return if its not already a function of time
        long time = -1;
        //get the tag of the first chart
        String TAG = tags[0];
        //if its a function of time, find nearest 50ms point
        if(TAG.contains("Time,")) {
            //get mod of value
            double mod = val % 50;
            //if value is less than 25 round down
            if(mod < 25) {
                return (long) (val - mod);
            //else round up
            } else {
                return (long) (val + (50 - mod));
            }
        //find base function
        } else {
            //Round to nearest domain value for the tag we are looking at
            String finding = TAG;
            //holds the closest value, holds value and object
            double closestVal = Double.MAX_VALUE;
            //for each logobject of the current tag
            for(LogObject lo : dataMap.getList(finding)) {
                //if its a functionoflogobject which it should be
                if(lo instanceof FunctionOfLogObject) {
                    //calculate the difference between this objects domain and the value the user clicked
                    if(Math.abs(((FunctionOfLogObject) lo).getX() - val) < closestVal) {
                        //if its closer, save this as closest
                        closestVal = Math.abs(((FunctionOfLogObject) lo).getX() - val);
                    }
                }
            }
            //find what its domain is
            String goTo = finding.substring(0,finding.indexOf(','));
            //see if there is a Time, with that domain as its range
            if(dataMap.getTags().contains("Time," + goTo)) {
                //get the tag for the function of time
                String toSearch = "Time," + goTo;
                //for each logobject of the base function
                for(LogObject lo : dataMap.getList(toSearch)) {
                    if(lo instanceof SimpleLogObject) {
                        if(((SimpleLogObject) lo).getValue() == closestVal) {
                            return lo.getTime();
                        }
                    }
                }
            }
        }
        //return what we stored as time
        //this value is either -1 for no value found or the time realted to the other domain the user clicked
        return time;
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
