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
    
    //ArrayList of listeners
    private ArrayList<HashTableTagSizeChangedListener> listeners;
    
    //Actual table
    private LinkedList<Value>[] table;
    
    //constant load factor. Do not exceed
    private final float loadFactor = .75f;
    
    //holds list of tags that have been entered into the CategoricalHashTable
    private ArrayList<String> tags;
    
    /**
     * Constructor that defaults the HashTable size to 20 and initiates the list of tags
     */
    public CategoricalHashTable() {
        table = new LinkedList[20];
        tags = new ArrayList<>();
        listeners = new ArrayList<>();
    }
    
    /**
     * Constructor that allows the user to define the size of the table and initiates the list of tags
     * @param size Table size
     */
    public CategoricalHashTable(int size) {
        //sets the table to the user defined size unless its less than 20
        table = new LinkedList[Math.max(size, 20)];
        tags = new ArrayList<>();
        listeners = new ArrayList<>();
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
                broadcastSizeChange();
                break;
            }
            //if the linked list here is empty we can put the category here
            if(table[index].isEmpty()) {
                tags.add(v.hashTag());
                broadcastSizeChange();
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
     * Removes a given value from the table. 
     * Iterates through the table to find the tag
     * Iterates through the list to find the element: requires Value's equals() to be overriden
     * removes and return the element
     * @param v given a value from the user
     * @return Value removed, null if not found
     */
    public Value remove(Value v) {
        //get orignal hash code for the value's tag
        int origIndex = Math.abs(v.hashTag().hashCode()) % table.length;
        //if this is correct position
        if(table[origIndex] != null && !table[origIndex].isEmpty() && table[origIndex].getFirst().hashTag().equals(v.hashTag())) {
            //ask linked list to remove element
            //linked list should iterate through and call .equals() to match v with the correct element and remove
            boolean removed = table[origIndex].remove(v);
            //if an element was removed
            if(removed) {
                //if the table list is now empty
                if(table[origIndex].isEmpty()) {
                    //remove the tag
                    tags.remove(v.hashTag());
                    //broadcast tag size changed
                    broadcastSizeChange();
                }
                //return element
                return v;
            }
        } else {
            //linear prob
            //move to next index
            int index = (origIndex + 1) % table.length;
            while(true) {
                //if this is correct position
                if(table[index] != null && !table[index].isEmpty() && table[index].getFirst().hashTag().equals(v.hashTag())) {
                    //ask linked list to check and remove element
                    boolean removed = table[index].remove(v);
                    //if we removed an element
                    if(removed) {
                        //if linked list is now empty
                        if(table[index].isEmpty()) {
                            //remove the tag
                            tags.remove(v.hashTag());
                            //broadcast tag size changed
                            broadcastSizeChange();
                        }
                        //return element
                        return v;
                    }
                    //if not removed return null
                    return null;
                }
                //if we end up at same index position as original break, tag doesnt exist
                else if(index == origIndex)
                    break;
                //else move to next table position
                else
                    index = (index + 1) % table.length;

            }
        }
        //tag not found return null
        return null;
    }
    
    /**
     * Removes a given value from the table. 
     * Iterates through the table to find the tag
     * clears list and return a clone
     * @param tag tag to remove
     * @return list of values removed, null if not found
     */
    public LinkedList<Value> remove(String tag) {
        //get original index from tags hash code
        int origIndex = Math.abs(tag.hashCode()) % table.length;
        //if this is the correct index
        if(table[origIndex] != null && !table[origIndex].isEmpty() && table[origIndex].getFirst().hashTag().equals(tag)) {
            //get a clone of the list
            LinkedList<Value> toReturn = (LinkedList<Value>) table[origIndex].clone();
            //clear the table
            table[origIndex].clear();
            //remove the tag
            tags.remove(tag);
            //broadcast the tag size was changed
            broadcastSizeChange();
            //return the list
            return toReturn;
        } else {
            //linear prob
            //move to next index
            int index = (origIndex + 1) % table.length;
            while(true) {
                //if this is correct position
                if(table[index] != null && !table[index].isEmpty() && table[index].getFirst().hashTag().equals(tag)) {
                    //get a clone of the list
                    LinkedList<Value> toReturn = (LinkedList<Value>) table[origIndex].clone();
                    //clear the table
                    table[origIndex].clear();
                    //remove the tag
                    tags.remove(tag);
                    //broadcast the tag size was changed
                    broadcastSizeChange();
                    //return the list
                    return toReturn;
                }
                //else if we ended up back at original position, tag doesn't exist
                else if(index == origIndex)
                    break;
                //else move to next index
                else
                    index = (index + 1) % table.length;

            }
            //return null, tag didn't exist
            return null;
        }
    }
    
    /**
     * Gets a value from the table given a similar value. Requires Value's equals() to be override
     * Iterates through the table to find the tag
     * Iterates through the list to find the element: may require Value's equals() to be overriden
     * @param v given a value object
     * @return Value object from the table, null if not found
     */
    public Value get(Value v) {
        int origIndex = Math.abs(v.hashTag().hashCode()) % table.length;
        if(table[origIndex] != null && !table[origIndex].isEmpty() && table[origIndex].getFirst().hashTag().equals(v.hashTag())) {
            for(Value val : table[origIndex]) {
                if(val.equals(v))
                    return val;
            }
            return null;
        } else {
            //linear prob
            int index = (origIndex + 1) % table.length;
            while(true) {
                if(table[index] != null && !table[index].isEmpty() && table[index].getFirst().hashTag().equals(v.hashTag())) {
                    for(Value val : table[origIndex]) {
                        if(val.equals(v))
                            return val;
                    }
                    return null;
                }
                else if(index == origIndex)
                    break;
                else
                    index = (index + 1) % table.length;

            }
            return null;
        }
        
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
     * Get all tags. Similar use as getKeySet()
     * @return tags ArrayList
     */
    public ArrayList<String> getTags() {
        return tags;
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
     * Iterates through the list of listeners and calls the sizeUpdate() method
     */
    private void broadcastSizeChange() {
        //for each listener
        for(HashTableTagSizeChangedListener listener : listeners) {
            //call implemented method
            listener.sizeUpdate();
        }
    }
    
    /**
     * Adds a listener to the class
     * @param e listener to add
     */
    public void addTagSizeChangeListener(HashTableTagSizeChangedListener e) {
        listeners.add(e);
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
    
    public interface HashTableTagSizeChangedListener {
        void sizeUpdate();
    }
    
}
