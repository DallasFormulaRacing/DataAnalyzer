package dataanalyzer;

import java.util.HashMap;

public class GlobalParameters {

    private static GlobalParameters instance;

    public HashMap<String, Object> parameters;

    public static GlobalParameters getInstance() {
        if (GlobalParameters.instance == null) {
            instance = new GlobalParameters();
        }
        return instance;
    }

    public GlobalParameters() {
        parameters = new HashMap<>();
    }

    public void addParameter(String key, Object value) {
        parameters.put(key, value);
    }

    public Object getParameter(String key) {
        return parameters.get(key);
    }
    
    /**
     * This method sets up all the default values for the application instance.
     */
    public void setDefaults() {
        
    }

}
