/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer.dialog;

import dataanalyzer.CategoricalHashMap;
import dataanalyzer.FunctionOfLogObject;
import dataanalyzer.LogObject;
import dataanalyzer.SimpleLogObject;
import java.util.ArrayList;
import java.util.List;

/**
 * An independent JFrame that contains three lists: Average, Min, Max.
 * Each list is populated for each lap of each tag.
 * This is to get an overview of the data being shown.
 * @author aribdhuka
 */
public class StatisticsFrame extends javax.swing.JFrame {

    private final CategoricalHashMap dataMap;
    private final String[] tags;
    private final int[] laps;

    public StatisticsFrame(CategoricalHashMap dataMap, String[] tags, int[] laps) {
        initComponents();
        this.dataMap = dataMap;
        this.tags = tags;
        this.laps = laps;
        calculateStatistics();
    }
    
    private void calculateStatistics() {
        //holds the final strings
        ArrayList<String> avgs, mins, maxs;
        avgs = new ArrayList<>();
        mins = new ArrayList<>();
        maxs = new ArrayList<>();
        
        //for each lap if laps is not null
        if(laps != null) {
            for(int lap : laps) {
                //for each tag
                for(String tag : tags) {
                    //get the data list thats showing
                    List<LogObject> data = dataMap.getList(tag);
                    //variables that hold average, min, and max
                    double avg = 0;
                    double min = Double.MAX_VALUE;
                    double max = Double.MIN_VALUE;
                    int countAdded = 0;
                    //for each logobject in the list we got
                    for(LogObject lo : data) {
                        if(!lo.getLaps().contains(lap))
                            continue;
                        //if the LogObject is an instance of a SimpleLogObject
                        if(lo instanceof SimpleLogObject) {
                            //add all the values to average
                            avg += ((SimpleLogObject) lo).getValue();
                            //if the current object is less than the current min, update min
                            if(((SimpleLogObject) lo).getValue() < min)
                                min = ((SimpleLogObject) lo).getValue();
                            //if the current object is greater than the current max, update max
                            if(((SimpleLogObject) lo).getValue() > max)
                                max = ((SimpleLogObject) lo).getValue();
                        }
                        else if(lo instanceof FunctionOfLogObject) {
                            //add all the values to average
                            avg += ((SimpleLogObject) lo).getValue();
                            //if the current object is less than the current min, update min
                            if(((SimpleLogObject) lo).getValue() < min)
                                min = ((SimpleLogObject) lo).getValue();
                            //if the current object is greater than the current max, update max
                            if(((SimpleLogObject) lo).getValue() > max)
                                max = ((SimpleLogObject) lo).getValue();
                        }
                        countAdded++;
                    }
                    //divide average by number of objects we added
                    avg /= countAdded;
                    //append the string
                    avgs.add(tag.substring(tag.indexOf(',')+1) + lap +":" + String.format("%.2f", avg));
                    mins.add(tag.substring(tag.indexOf(',')+1) + lap +":" + String.format("%.2f", min));
                    maxs.add(tag.substring(tag.indexOf(',')+1) + lap +":" + String.format("%.2f", max));
                }
            }
        } else {
            //for each tag
            for(String tag : tags) {
                //get the data list thats showing
                List<LogObject> data = dataMap.getList(tag);
                //variables that hold average, min, and max
                double avg = 0;
                double min = Double.MAX_VALUE;
                double max = Double.MIN_VALUE;
                //for each logobject in the list we got
                for(LogObject lo : data) {
                    //if the LogObject is an instance of a SimpleLogObject
                    if(lo instanceof SimpleLogObject) {
                        //add all the values to average
                        avg += ((SimpleLogObject) lo).getValue();
                        //if the current object is less than the current min, update min
                        if(((SimpleLogObject) lo).getValue() < min)
                            min = ((SimpleLogObject) lo).getValue();
                        //if the current object is greater than the current max, update max
                        if(((SimpleLogObject) lo).getValue() > max)
                            max = ((SimpleLogObject) lo).getValue();
                    }
                }
                //divide average by number of objects we added
                avg /= data.size();
                //append the string
                avgs.add(tag.substring(tag.indexOf(',')+1) + ":" + String.format("%.2f", avg));
                mins.add(tag.substring(tag.indexOf(',')+1) + ":" + String.format("%.2f", min));
                maxs.add(tag.substring(tag.indexOf(',')+1) + ":" + String.format("%.2f", max));
            }
        }
        
        //set the lists
        averageList.setListData(avgs.toArray(new String[avgs.size()]));
        minList.setListData(mins.toArray(new String[mins.size()]));
        maxList.setListData(maxs.toArray(new String[maxs.size()]));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        averageList = new javax.swing.JList<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        maxList = new javax.swing.JList<>();
        jScrollPane3 = new javax.swing.JScrollPane();
        minList = new javax.swing.JList<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Averages");

        averageList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(averageList);

        maxList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(maxList);

        minList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane3.setViewportView(minList);

        jLabel2.setText("Max");

        jLabel3.setText("Min");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList<String> averageList;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JList<String> maxList;
    private javax.swing.JList<String> minList;
    // End of variables declaration//GEN-END:variables
}
