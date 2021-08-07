/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer.shortcuts;

import com.arib.toast.Toast;
import dataanalyzer.ChartConfiguration;
import dataanalyzer.DataAnalyzer;
import dataanalyzer.Dataset;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;
import org.json.simple.parser.ParseException;

/**
 *
 * @author aribdhuka
 */
public class QuickAccessDialog extends javax.swing.JDialog {

    private Map<String, Command> commandMap;
    LinkedList<Dataset> datasets;
    DataAnalyzer da;
    /**
     * Creates new form QuickAccessDialog
     */
    public QuickAccessDialog(DataAnalyzer parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.da = parent;
        this.datasets = parent.getChartManager().getDatasets();
        buildCommandMap();
        quickAccessField.requestFocus();
        JTextComponent editor = (JTextComponent) quickAccessField.getEditor().getEditorComponent();
        editor.setText("");
        Set<KeyStroke> forwardKeys = new HashSet<KeyStroke>(1);
        forwardKeys.add(KeyStroke.getKeyStroke(
            KeyEvent.VK_TAB, InputEvent.CTRL_MASK));
        setFocusTraversalKeys(
            KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardKeys);

        Set<KeyStroke> backwardKeys = new HashSet<KeyStroke>(1);
        backwardKeys.add(KeyStroke.getKeyStroke(
            KeyEvent.VK_TAB, InputEvent.SHIFT_MASK | InputEvent.CTRL_MASK));
        setFocusTraversalKeys(
            KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backwardKeys);
        editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if(e.isActionKey() || e.isAltDown() || e.isControlDown() || e.isMetaDown() || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    return;
                }
                if(e.getKeyCode() == KeyEvent.VK_TAB)
                    editor.setText(quickAccessField.getItemAt(0));
                buildItems(editor.getText() + e.getKeyChar());
                if(!quickAccessField.isPopupVisible())
                    quickAccessField.showPopup();
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    runCommand(editor.getText());
                }
            }
            
        });
    }
    
    private void buildCommandMap() {
        commandMap = new TreeMap<>();
        
        //add chart command
        Command cmd = new AddChartCommand();
        commandMap.put(cmd.commandName, cmd);
        cmd = new SaveFileCommand();
        commandMap.put(cmd.commandName, cmd);
        cmd = new OpenFileCommand();
        commandMap.put(cmd.commandName, cmd);
        cmd = new ChartSetupCommand();
        commandMap.put(cmd.commandName, cmd);
        

    }
    
    private void buildItems(String cmd) {
        
        String[] cmds = cmd.split(" ");
        JTextComponent editor = (JTextComponent) quickAccessField.getEditor().getEditorComponent();
        String txttemp = editor.getText();
        quickAccessField.removeAllItems();
        //if we are still typing initial command, only show commands.
        if(cmds.length == 0 || cmds.length == 1) {
            Set<String> possibleCommands = commandMap.keySet();
            Iterator<String> poss = possibleCommands.iterator();
            while(poss.hasNext()) {
                String posscmd = poss.next();
                if(posscmd.matches(cmd + ".*"))
                    quickAccessField.addItem(posscmd);
            }
        } else {
            Command chosenCommand = commandMap.get(cmds[0]);
            for(String follower : chosenCommand.getFollowers()) {
                if(follower.equals("$channel")) {
                    //for a single dataset no need to define dataset name
                    for(Dataset dataset : datasets) {
                        for(String channel : dataset.getDataMap().getTags()) {
                            //if only one dataset open then specifying dataset name is not required
                            if(datasets.size() == 1) {
                                String toadd = "";
                                for(int i = 0; i < cmds.length - 1; i++) {
                                    toadd += cmds[i];
                                    toadd += " ";
                                }
                                if(channel.matches(cmds[cmds.length - 1] + ".*")) {
                                    toadd += channel;
                                    quickAccessField.addItem(toadd);
                                }
                            }
                            //else if multiple datasets then dataset name must be specified
                            else {
                                String toadd = "";
                                for(int i = 0; i < cmds.length - 1; i++) {
                                    toadd += cmds[i];
                                    toadd += " ";
                                }
                                if((dataset.getName() + channel).matches(cmds[cmds.length - 1] + ".*")) {
                                    toadd += dataset.getName()+"."+channel;
                                    quickAccessField.addItem(toadd);
                                }
                            }
                        }
                    }
                } else if(follower.equals("$chartsetupname")) {
                    getChartSetupNames(cmd);
                }
            }
        }
        editor.setText(txttemp);
        
        
    }
    
    private void getChartSetupNames(String cmd) {
        
        String[] cmds = cmd.split(" ");
        //get current OS
        String OS = System.getProperty("os.name");
        
        //for Windows
        if (OS.startsWith("Windows")) {
            String home = System.getProperty("user.home");
            //check for files in this folder
            final File folder = new File(home + "\\AppData\\Local\\DataAnalyzer\\ChartConfigurations\\");
            //for each object in this directory
            for (final File fileEntry : folder.listFiles()) {
                //check if its a file
                if (fileEntry.isFile()) {
                    //get the extension
                    String filename = fileEntry.getAbsolutePath().substring(fileEntry.getAbsolutePath().lastIndexOf(File.separator), fileEntry.getAbsolutePath().lastIndexOf('.')).substring(1);
                    String extension = fileEntry.getAbsolutePath().substring(fileEntry.getAbsolutePath().lastIndexOf('.'));
                    //if its the right extension
                    if(extension.equals(".dfrchartconfig")) {
                        if(filename.matches(cmds[cmds.length - 1] + ".*")) {
                            //add menu item
                            String toadd = "";
                            for(int i = 0; i < cmds.length - 1; i++) {
                                toadd += cmds[i];
                                toadd += " ";
                            }
                            toadd += filename;
                            quickAccessField.addItem(toadd);
                        }
                    }
                }
            }
        } else if (OS.startsWith("Linux") || OS.startsWith("Mac")) {
            char sep = '/';
            //check for files in this folder
            final File folder = new File(sep+"Applications"+sep+"DataAnalyzer"+sep+"ChartConfigurations");
            //for each object in this directory
            for (final File fileEntry : folder.listFiles()) {
                //check if its a file
                if (fileEntry.isFile()) {
                    //get the extension
                    String filename = fileEntry.getAbsolutePath().substring(fileEntry.getAbsolutePath().lastIndexOf(sep), fileEntry.getAbsolutePath().lastIndexOf('.')).substring(1);
                    String extension = fileEntry.getAbsolutePath().substring(fileEntry.getAbsolutePath().lastIndexOf('.'));
                    //if its the right extension
                    if(extension.equals(".dfrchartconfig")) {
                        //add menu item
                        if(filename.matches(cmds[cmds.length - 1] + ".*")) {
                            String toadd = "";
                            for(int i = 0; i < cmds.length - 1; i++) {
                                toadd += cmds[i];
                                toadd += " ";
                            }
                            toadd += filename;
                            quickAccessField.addItem(toadd);
                        }
                    }
                }
            }
        }
    }
    
    private void runCommand(String cmd) {
        //get commands from quick access field
        String[] argv = cmd.split(" ");
        int argc = argv.length;
        
        Command command = commandMap.get(argv[0]);
        if(command != null) {
            command.doAction(argv, da);
//            JTextComponent editor = (JTextComponent) quickAccessField.getEditor().getEditorComponent();
//            quickAccessField.removeAllItems();
//            editor.setText("");
//            this.setVisible(false);
            this.dispose();
            //TODO:UPdATE so its not disposing just hiding each time
        }
        else {
            Toast.makeToast(da, "Error processing command! Couldnt find specified command!", Toast.DURATION_MEDIUM);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        quickAccessField = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        quickAccessField.setEditable(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(quickAccessField, 0, 528, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(quickAccessField, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> quickAccessField;
    // End of variables declaration//GEN-END:variables
}
