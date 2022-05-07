/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer;

/**
 * ReturnCode is a work around to allow java to accept integers as addresses
 * It saves an integer in an object for an object to be passed around by reference
 * @author aribdhuka
 */
public class ReturnCode {
    
    //value held
    int code;
    
    //default constructor
    public ReturnCode() {
        code = 0;
    }
    
    //constructor that allows start value to be something other than 0
    public ReturnCode(int startValue) {
        code = startValue;
    }

    //getters and setters
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }   
}
