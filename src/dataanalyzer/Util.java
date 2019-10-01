package dataanalyzer;

/**
 * Once instantiated, it can be used to figure out the operating system, whenever that is needed. 
 * Just create an instance of this class and whenever you need the operating system, use: util.os
 * Example: if(util.os == "WINDOWS"){//DO SOMETHING}
 * 
 * @author Nolan Davenport
 */
public class Util {    
    public String os = "";
    public Util(){
        String operSys = System.getProperty("os.name").toLowerCase();
        if (operSys.contains("win")) {
            os = "WINDOWS";
        } else if (operSys.contains("nix") || operSys.contains("nux")
                || operSys.contains("aix")) {
            os = "LINUX";
        } else if (operSys.contains("mac")) {
            os = "MAC";
        }
    }
}