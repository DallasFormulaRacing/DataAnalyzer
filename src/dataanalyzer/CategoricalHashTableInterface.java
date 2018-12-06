package dataanalyzer;

/**
 *
 * @author aribdhuka
 * This interface must be implemented by classes that want to be elements of CategoricalHashTable
 */
public interface CategoricalHashTableInterface {
        
    /**
     * hashTag() is similar to hashCode() in a way that it makes sure that the class provides a way for the hashmap to categorize the element
     * @return 
     */
    public String hashTag();
    
    /**
     * toString() exists so that the hashmap ensures proper output of elements during its own toString();
     * @return 
     */
    @Override
    public String toString();
    
}
