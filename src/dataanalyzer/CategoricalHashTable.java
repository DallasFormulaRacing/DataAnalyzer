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
public class CategoricalHashTable<Key, Value> {
    
    //Actual table
    LinkedList<Value>[] table;
    
    //constant load factor. Do not exceed
    private final float loadFactor = .75f;
    
    //constructor with base size of 20
    public CategoricalHashTable() {
        table = new LinkedList[20];
    }
    
    //constructor that allows user to define size as long as > 20
    public CategoricalHashTable(int size) {
        table = new LinkedList[Math.max(size, 20)];
    }
    
    //put a new value into the HashMap
    public void put(Key k, Value v) {
        //call private put.
        put(k, v, table);
    }
    
    private void put(Key k, Value v, LinkedList<Value>[] table) {
        int index = k.hashCode() % table.length;
        if(table[index] == null) {
            table[index] = new LinkedList<>();
        }
        
        table[index].add(v);
        checkLoad();
    }
    
    private void put(int i, Value v, LinkedList<Value>[] table) {
        int index = i;
        if(table[index] == null) {
            table[index] = new LinkedList<>();
        }
        
        table[index].add(v);
        checkLoad();
    }
    
    private void checkLoad() {
        int loadCount = 0;
        for(int i = 0; i < table.length; i++) {
            if(table[i] != null)
                loadCount++;
        }
        
        float load = loadCount / table.length;
        if(load > loadFactor)
            rehash();
    }
    
    //extremely expensive function
    private void rehash() {
        LinkedList<Value>[] newTable = new LinkedList[table.length * 2];
        for(int i = 0; i < table.length; i++) {
            if(table[i] == null)
                continue;
            for(Value v : table[i]) {
                put(i, v, newTable);
            }
        }
        
        table = newTable;
    }
}
