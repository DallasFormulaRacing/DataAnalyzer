/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer;

/**
 * equations used on raw data for graphing
 * @author aribdhuka
 */
public class FunctionOfLogObject extends SimpleLogObject {
    
    double functionOfValue;
    
    public FunctionOfLogObject() {
        functionOfValue = 0;
        value = 0;
        time = 0;
        TAG = "";
    }
    
    public FunctionOfLogObject(String TAG, double value, double functionOfValue, long time) {
        this.time = time;
        this.functionOfValue = functionOfValue;
        this.value = value;
        this.TAG = TAG;
    }
    
    public FunctionOfLogObject(String TAG, double value, double functionOfValue) {
        time = 0;
        this.functionOfValue = functionOfValue;
        this.value = value;
        this.TAG = TAG;
    }
    
    public double getX() {
        return functionOfValue;
    }
    
    public void setX(double functionOfValue) {
        this.functionOfValue = functionOfValue;
    }
    
    public String toString() {
        return functionOfValue + "," + value;
    }
    
}
