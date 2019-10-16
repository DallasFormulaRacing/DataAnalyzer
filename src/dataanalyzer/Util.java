package dataanalyzer;

/**
 * This class helps with figuring out the system's operating system. 
 * 
 * @author Nolan Davenport
 */
public class Util {    
    private static String os = "";
    
    /**
     * @return The system's operating system
     */
    public static String getOS(){
        String operSys = System.getProperty("os.name").toLowerCase();
        if (operSys.contains("win")) {
            os = "WINDOWS";
        } else if (operSys.contains("nix") || operSys.contains("nux")
                || operSys.contains("aix")) {
            os = "LINUX";
        } else if (operSys.contains("mac")) {
            os = "MAC";
        }
        
        return os;
    }
}