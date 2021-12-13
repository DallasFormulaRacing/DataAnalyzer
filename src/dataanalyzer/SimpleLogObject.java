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
public class SimpleLogObject extends LogObject implements Valueable{
    
    protected double value;
    
    public SimpleLogObject() {
        super();
        value = 0;
    }
    
    public SimpleLogObject(String TAG, double value, long time) {
        this.TAG = TAG;
        this.value = value;
        this.time = time;
    }

    @Override
    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return time + "," + value;
    }
    
}
