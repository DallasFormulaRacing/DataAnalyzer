/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer.shortcuts;

import dataanalyzer.DataAnalyzer;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author aribdhuka
 */
public class OpenFileCommand extends Command {
    
    public OpenFileCommand() {
        super("openfile", new ArrayList<String>(Arrays.asList(new String[] {"path"})));
    }

    @Override
    public void doAction(String[] cmd, DataAnalyzer da) {
        da.openFile(null);
    }
    
}
