/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer;

import java.util.ArrayList;

/**
 * 
 * @author aribdhuka
 */
public abstract class LogObject implements Comparable {
    protected long time;
    protected String TAG;
    protected ArrayList<Integer> laps;
    
    public LogObject() {
        time = -1;
        TAG = "";
        laps = new ArrayList<>();
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
    
    public String getTAG() {
        return TAG;
    }

    public void setTAG(String TAG) {
        this.TAG = TAG;
    }

    public ArrayList<Integer> getLaps() {
        return laps;
    }

    public void addLap(int lap) {
        laps.add(lap);
    }
    
    //if we do ever sort, keep the list in order of TAGS, then time.
    @Override
    public int compareTo(Object o) {
        LogObject other = (LogObject) o;
        if(this.TAG.equalsIgnoreCase(other.TAG)) {
            if(other.time > this.time)
                return 1;
            else
                return -1;
        } else {
            return this.TAG.compareTo(other.TAG);
        }
            
    }
    
   
    
}
