/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * This class takes objects and puts them into a hashtable that organizes data into categories.
 * Each class will provide a way to categorize the element. This category is used to calculate which index the item will go on.
 * On collision: If the object we are trying to put has the same category as the value we collided with, separate chain it to that index.
 * If the categories do not match, linearly probe until we find an empty spot or the category index we are supposed to be at.
 * The purpose is to help datasets that have large element counts for few categories.
 * It allows direct access to elements of a category instead of having to iterate through every element and pick out the elements with the desired property.
 * The only excess travel it needs to do to get a list of all the elements of a certain category is navigate the array if the category was placed at a different index than its hash due to collisions.
 * Simply, in worst case it only needs to navigate all of the categories instead of all the elements.
 * 
 * @author aribdhuka
 * @param <Value> A class that implements the CategoricalHashTableInterface
 * this interface makes the class have a method available that provides this class with what value to categorize this class with, also a toString for prettier outputs
 */
public class CategoricalHashTable<Value extends CategoricalHashTableInterface> {
    
    //Actual table
    LinkedList<Value>[] table;
    
    //constant load factor. Do not exceed
    private final float loadFactor = .75f;
    
    //holds list of tags that have been entered into the CategoricalHashTable
    ArrayList<String> tags;
    
    /**
     * Constructor that defaults the HashTable size to 20 and initiates the list of tags
     */
    public CategoricalHashTable() {
        table = new LinkedList[20];
        tags = new ArrayList<>();
    }
    
    /**
     * Constructor that allows the user to define the size of the table and initiates the list of tags
     * @param size Table size
     */
    public CategoricalHashTable(int size) {
        //sets the table to the user defined size unless its less than 20
        table = new LinkedList[Math.max(size, 20)];
        tags = new ArrayList<>();
    }
    
    /**
     * Adds a object to the HashTable
     * @param v generic object the user is trying to add to the HashTable
     */
    public void put(Value v) {
        //call private put.
        put(v, table);
    }
    
    /**
     * Internal method to allow default constructors
     * @param v generic object user is trying to add to the table
     * @param table which table to add it to, allows method to be used by rehash function
     */
    private void put(Value v, LinkedList<Value>[] table) {
        //get the hashcode of the category
        int index = Math.abs(v.hashTag().hashCode()) % table.length;
        //if the table at current index is null create a new linked list here
        while(true) {
            //if the current index doesn't even have a created link list its empty and we can put this category here
            if(table[index] == null) {
                table[index] = new LinkedList<>();
                tags.add(v.hashTag());
                break;
            }
            //if the linked list here is empty we can put the category here
            if(table[index].isEmpty()) {
                tags.add(v.hashTag());
                break;
            }
            //if the linked list here has elements of the same category as curr element, the element belongs here
            if(table[index].getFirst().hashTag().equals(v.hashTag()))
                break;
            
            //else if the current index could not hold our element
            //linearly probe to the next index
            index++;
            index %= table.length;
        
        }
       
        //add the element to the linkedlist at this index.
        table[index].add(v);
        //check the load factor
        checkLoad();
    }
    
    /**
     * Checks how filled the table is to keep insert times low
     * Overfilled HashTables can run into problems of iterating through many indices before finding a valid place to add the object
     */
    private void checkLoad() {
        //holds number of elements in table
        int loadCount = 0;
        //for each element of the table
        for (LinkedList<Value> chain : table) {
            //if its not empty or null, the spot it filled so increase load count
            if (chain != null && !chain.isEmpty()) {
                loadCount++;
            }
        }
        
        //calculate the load factor by calculating percent of table filled
        float load = loadCount / table.length;
        //if the current load exceeds the max load factor we have set for the table, rehash
        if(load > loadFactor)
            rehash();
    }
    
    /**
     * Creates new table twice the size and inserts elements into that table.
     * Now there are more open spots for the put to find, load factor is lowered again
     */
    private void rehash() {
        //create new table twice the size
        LinkedList<Value>[] newTable = new LinkedList[table.length * 2];
        //for every index of current table
        for (LinkedList<Value> chain : table) {
            //if the current index is null, skip it
            if (chain == null) {
                continue;
            }
            //for each value of the separate chain 
            for (Value v : chain) {
                //put it into the new table
                put(v, newTable);
            }
        }
        
        //set the current table to the new table
        table = newTable;
    }
    
    /**
     * Lets the user know if the table is empty
     * empty is defined as the table containing 0 Value objects
     * So a table with all its indices instantiated with linked lists, but the linked lists being empty, is empty.
     * @return 
     */
    public boolean isEmpty() {
        //for each table entry
        for (LinkedList<Value> table1 : table) {
            //if it contains something, the whole dataset is not empty return false
            if (table1 != null && !table1.isEmpty()) {
                return false;
            }
        }
        //else return true
        return true;
    }
    
    /**
     * gets all the elements from the HashTable that match the category given
     * @param TAG Category value. Defines which category of elements the user wants
     * @return LinkedList of objects that match the category provided by user
     */
    public LinkedList<Value> getList(String TAG) {
        //for each linkedlist in the hash table
        for (LinkedList<Value> chain : table) {
            //if its null or empty skip
            if (chain == null || chain.isEmpty())
                continue;
            //if we find a linked list that matches the tag
            if(chain.getFirst().hashTag().equals(TAG))
                //return the whole linked list
                return chain;
        }
        
        //if we didnt find it return null
        return null;
    }
    
    /**
     * toString method
     * @return return a string that has the tag followed by all its elements for all the tags
     */
    @Override
    public String toString() {
        String returnable = "";
        for (String tag : tags) {
            LinkedList<Value> elements = getList(tag);
            returnable += tag + "\n";
            for(Value v : elements) {
                returnable += v.toString() + "\n";
            }
        }
        return returnable;
    }
    
    
}
