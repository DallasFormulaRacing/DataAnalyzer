/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 *
 * @author aribdhuka
 */
public class Lap {
        
    protected long start;
    protected long stop;
    protected int lapNumber;
    protected String lapLabel;

    public Lap() {
        start = 0;
        stop = 0;
        lapNumber = -1;
        lapLabel = "";
    }


    public Lap(long start, long stop) {
       this.start = start;
       this.stop = stop;
       lapNumber = -1;
       lapLabel = "";
    }

    public Lap(long start, long stop, int lapNumber) {
        this.start = start;
        this.stop = stop;
        this.lapNumber = lapNumber;
        lapLabel = "";
    }
    
    public Lap(long start, long stop, int lapNumber, String lapLabel) {
        this.start = start;
        this.stop = stop;
        this.lapNumber = lapNumber;
        this.lapLabel = lapLabel;
    }

    public Lap(long start, long stop, CategoricalHashMap dataMap, ArrayList<Lap> lapBreaks) {
       this.start = start;
       this.stop = stop;
       applyToDataset(dataMap, lapBreaks);
    }
    
    //Copy constructor
    public Lap(Lap orig) {
        start = orig.start;
        stop = orig.stop;
        lapNumber = orig.lapNumber;
        lapLabel = orig.lapLabel;
    }

    public static void applyToDataset(CategoricalHashMap dataMap, ArrayList<Lap> lapBreaks) {
        //clear all previous lap data
        for(String tag : dataMap.tags) {
            for(LogObject lo : dataMap.getList(tag)) {
                lo.getLaps().clear();
            }
        }
        //apply new lap data
        for(Lap lap : lapBreaks) {
            //get all the tags
            ArrayList<String> tags = dataMap.tags;
            //for each tag
            for(String tag : tags) {
                //find its base Time,Element because Time will be based off of
                String finding = tag;
                //if it doesn't have time as its domain, its domain will be a double
                double subStart = Double.MIN_VALUE;
                double subStop = Double.MIN_VALUE;
                //have we found it
                boolean found = true;
                //did we have to trackback
                boolean trackBack = false;
                //if the tag is not already a function of time
                while(!finding.contains("Time,")) {
                    //find what its domain is
                    String goTo = finding.substring(0,finding.indexOf(','));
                    //see if there is a Time, with that domain as its range
                    if(tags.contains("Time," + goTo)) {
                        //if so set finding to its tag
                        finding = "Time," + goTo;
                        //store that we found the base tag
                        found = true;
                        //for each element of the base tag
                        for(LogObject los : dataMap.getList(finding)) {
                            //if its a simplelogobject which it should be
                            if(los instanceof SimpleLogObject) {
                                //if the current objects time matches the time the user selected as start
                                if(los.getTime() == lap.start) {
                                    //for this tag, find the value for this time
                                    subStart = ((SimpleLogObject) los).getValue();
                                }
                                //if the current objects time matches the time the user selected as a stop
                                else if(los.getTime() == lap.stop) {
                                    //for this tag, find the value for this time
                                    subStop = ((SimpleLogObject) los).getValue();
                                }
                                //if we set the substart and substop store that we had to trackback
                                if(subStart != Double.MIN_VALUE && subStop != Double.MIN_VALUE) {
                                    trackBack = true;
                                }
                            }

                        }
                    //if the traceback was not a function with domain of time
                    } else {
                        //stop looking
                        break;
                    }
                }
                //if the current tag was not a function of time and we could not find its base function.
                if(!found) {
                    //move onto the next tag
                    continue;
                }

                //for each log object of the current tag
                LinkedList<LogObject> los = dataMap.getList(tag);
                //for each logobject
                for(LogObject lo : los) {
                    //if we had to trackBack to find the Time tag
                    if(trackBack) {
                        //if its a functionoflogobject which it should be
                        if(lo instanceof FunctionOfLogObject) {
                            //if the domain of this log object is within the user defined bounds set its lap counter to the next available counter
                            if(((FunctionOfLogObject) lo).getX() >= subStart && ((FunctionOfLogObject) lo).getX() <= subStop) {
                                lo.addLap(lap.lapNumber);
                            }
                        }
                    //if we didnt trackback, so it was a function of time
                    } else {
                        //if the time is within the bounds
                        if(lo.time >= lap.start && lo.time <= lap.stop) {
                            //set the current objects lap counter to the next available.
                            lo.addLap(lap.lapNumber);
                        }
                    }
                }   
            }
        }
    }

    //getters and setters
    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getStop() {
        return stop;
    }

    public void setStop(long stop) {
        this.stop = stop;
    }

    public int getLapNumber() {
        return lapNumber;
    }

    public void setLapNumber(int lapNumber) {
        this.lapNumber = lapNumber;
    }

    public String getLapLabel() {
        return lapLabel;
    }

    public void setLapLabel(String lapLabel) {
        this.lapLabel = lapLabel;
    }
    
    
        
    /**
     * Turns a list of Laps into lines of their data.
     * @param lapBreaker ArrayList of Laps that will be formatted into a string
     * @return A formatted string of the lap in the lapBreaker
     */
    public static String getStringOfData(ArrayList<Lap> lapBreaker) {
        StringBuilder builder = new StringBuilder();
        for(Lap lap : lapBreaker) {
            builder.append(lap.toString());
            builder.append("\n");
        }
        return builder.toString();
    }
    
    @Override
    public boolean equals(Object eq) {
        if(eq instanceof Lap) {
            if(((Lap) eq).start == this.start && ((Lap) eq).stop == this.stop) {
                return true;
            }
        }
        return false;
    }
        
    @Override
    public String toString() {
        return lapNumber + "(" + start + "," + stop + ")" + lapLabel;
    }
       
}
