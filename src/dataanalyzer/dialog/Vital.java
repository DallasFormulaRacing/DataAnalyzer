/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer.dialog;

/**
 *
 * @author aribdhuka
 */
public class Vital {
    
    private String channel;
    private boolean lowActive;
    private boolean highActive;
    private VitalType lowType;
    private VitalType highType;
    private double lowValue;
    private double highValue;
    
    /**
     * Default constructor
     */
    public Vital() {
        channel = "";
        lowActive = false;
        highActive = false;
        lowType = VitalType.Info;
        highType = VitalType.Info;
        lowValue = 0;
        highValue = 0;
    }
    
    /**
     * Constructor with attributes provided
     * @param channel
     * @param lowActive
     * @param highActive
     * @param lowType
     * @param highType
     * @param lowValue
     * @param highValue 
     */
    public Vital(String channel, boolean lowActive, boolean highActive, VitalType lowType, VitalType highType, double lowValue, double highValue) {
        //channel,lowactive,highactive,lowtype,hightype,lowvalue,highvalue
        this.channel = channel;
        this.lowActive = lowActive;
        this.highActive = highActive;
        this.lowType = lowType;
        this.highType = highType;
        this.lowValue = lowValue;
        this.highValue = highValue;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public boolean isLowActive() {
        return lowActive;
    }

    public void setLowActive(boolean lowActive) {
        this.lowActive = lowActive;
    }

    public boolean isHighActive() {
        return highActive;
    }

    public void setHighActive(boolean highActive) {
        this.highActive = highActive;
    }

    public VitalType getLowType() {
        return lowType;
    }

    public void setLowType(VitalType lowType) {
        this.lowType = lowType;
    }

    public VitalType getHighType() {
        return highType;
    }

    public void setHighType(VitalType highType) {
        this.highType = highType;
    }

    public double getLowValue() {
        return lowValue;
    }

    public void setLowValue(double lowValue) {
        this.lowValue = lowValue;
    }

    public double getHighValue() {
        return highValue;
    }

    public void setHighValue(double highValue) {
        this.highValue = highValue;
    }
    
    public String toHumanReadable() {
        String readable = "";
        if(lowActive) {
            readable += lowType + " if " + channel + " < " + lowValue + ".";
        }
        if(highActive) {
            readable += highType + " if " + channel + " > " + highValue + ".";
        }
        
        if(readable.isEmpty())
            readable = "Inactive vital for " + channel + ".";
        return readable;
        
    }
    
    public String toString() {
        return "" + channel + ";" + lowActive + ";" + highActive + ";" + lowType + ";" + highType + ";" + lowValue + ";" + highValue;
    }
    
}
