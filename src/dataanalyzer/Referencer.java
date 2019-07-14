/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer;

/**
 * This class takes any object like String or Integer as a generic type and saves
 * it within an object to keep the reference. Essentially making it mutable.
 * @author aribdhuka
 * @param <T> Variable type that is being saved
 */
public class Referencer<T> {
    T obj;
    
    public Referencer(T obj) {
        this.obj = obj;
    }
    
    public T get() {
        return obj;
    }
    
    public void set(T obj) {
        this.obj = obj;
    }
    
}
