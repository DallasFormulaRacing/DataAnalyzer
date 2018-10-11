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
public abstract class LogObject implements Comparable {
    protected long time;
    protected String TAG;
    
    public LogObject() {
        time = -1;
        TAG = "";
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
