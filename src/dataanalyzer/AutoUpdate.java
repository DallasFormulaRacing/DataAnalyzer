/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import junit.framework.Test;

/**
 *
 * @author aribdhuka
 */
public class AutoUpdate {
    
    public static boolean checkForUpdate() throws UnsupportedEncodingException, URISyntaxException, ProtocolException, IOException, MalformedURLException, IndexOutOfBoundsException {
        
        //get current location of file
        String path = Test.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        //get the name of the jar file that we ran
        String decodedPath = URLDecoder.decode(path, "UTF-8");
        
        //pull the filename from the path
        String filename = decodedPath.substring(decodedPath.lastIndexOf('/') + 1);
        //if the file name is this then we ran from netbeans, or different debugger
        //dont try to download in this case
        if(filename.contains("junit"))
            return;
        //get the other half of the path, so we know where to download new file to
        String pathToDownload = decodedPath.substring(0, decodedPath.lastIndexOf('/') + 1);
        //get the current version of the file.
        String currentVersion = filename.substring(0, filename.lastIndexOf('.')).split("DataAnalyzer")[0];

        //get the url for the readme for this project
        URL url = new URL("https://raw.githubusercontent.com/DallasFormulaRacing/DataAnalyzer/master/README.md");
        //connect to this url
        HttpURLConnection c = (HttpURLConnection)url.openConnection();
        //REST GET from this url
        c.setRequestMethod("GET");
        //get the input stream from this connection
        BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
        //will hold each line
        String str;
        //will hold the last line
        String lastLine = "";
        //read the data
        while ((str = in.readLine()) != null)
            //keep updating the read line as the last
            lastLine = str;
        //close the input stream
        in.close();
        
        //get the new filename from the last line (the last line will always be a link to the download)
        String newFileName = lastLine.substring(lastLine.lastIndexOf('/') + 1);
        
        //check if the filenames are the same, if so we are done no need to update
        if(currentVersion.equals(newFileName))
            return false;
        
        /**
         * Copies the data to a file
         */
        //open the url from the last line we caught earlier
        URL website = new URL(lastLine);
        //Open a byte channel to read from
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        //open a file output stream so we can write the file
        FileOutputStream fos = new FileOutputStream(pathToDownload + newFileName);
        //write the new file.
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        
        return true;
    }
    
}
