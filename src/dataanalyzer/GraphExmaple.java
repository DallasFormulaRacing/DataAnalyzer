/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dataanalyzer;

/**
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import javax.swing.JPanel;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 *
 * @author aribdhuka & kayla :) <3
 */
public class GraphExmaple extends javax.swing.JFrame{
    
    Panel2 drawingPanel;

    /**
     * Creates new form ForMyIdiotKayla
     */
    public GraphExmaple() {
 
        //create stuffs
        initComponents();
        
        //create drawings
        createDrawing();
        
        
    }
    
    /**
     * Given a time parameter find the corresponding latitude and longitude
     * @param time 
     * @return double[] GPS which has raw [latitude,longitude] from CSV file
     * @throws java.io.FileNotFoundException  
     * @throws java.io.IOException 
     */
    public double[] findSingle(long time) throws FileNotFoundException, IOException{
        
        String currentTag = "";
        String line = "";
        // lat is [0] long is [1]
        double[] GPS = new double [2];
        
        /*find lat and long in csv*/
        
        //open file
        BufferedReader br = new BufferedReader(new FileReader("test.csv"));
        
        while ((line = br.readLine()) != null){
            String[] fields = line.split(",");
            
            //if has tag
            if( fields[0].equals("Time")){
                currentTag = fields[1];
            //if is not equal to end or empty then it is a number
            } else if (!(fields[0].equals("END")) && !(fields[0].isEmpty()) && fields[0] != null && fields[1] != null && !(fields[1].isEmpty())){
                //if current tag is latitude get latitude
                if (currentTag.equals("latitude")){
                    if(Double.parseDouble(fields[0]) == time){
                        GPS[0] = Double.parseDouble(fields[1]);
                    }
                //if current tag is longitude get longitude
                } else if (currentTag.equals("longitude")){
                    if(Double.parseDouble(fields[0]) == time){
                        GPS[1] = Double.parseDouble(fields[1]);
                    }
                }
            }
        }
        
        br.close();
        
        return GPS;
    }
    
    /**
     * Output the raw GPS data from the point corresponding to the given time
     * @param time 
     */
    public void outputSingle(long time){
        try{
            double[] rawData = findSingle(time);
            for( int i=0; i<rawData.length; i++){
                if( i == 0 )
                    System.out.println("Latitude: " + rawData[i]);
                else if ( i == 1 )
                    System.out.println("Longitude: " + rawData[i]);
                else
                    System.out.println("ERROR: Array size > 2");
            }
        }catch(Exception e){
            System.out.println("ERROR: File not found (output Single)");
        }
    }
    
    /**
     * transforms rawData to data that is the default for the panel
     * @param rawData[lat,long]
     * @param numberZero number of zeros used in calculations
     * @param highest
     * @param mostLeft
     * @return arr[x,y]
     */
    public int[] transform(double[] rawData, int numberZero, double highest, double mostLeft){
        int[] arr = new int[]{0,0};
        
        //use raw data from csv for long and then subtract the mostLeft extreme, scale it according to the number of zeros
        arr[0] = doubleIntThree((rawData[1] - mostLeft), numberZero);
        
        //subtract raw data from csv for lat from highest extreme, scale it according to the number of zeros
        arr[1] = doubleIntThree(highest - rawData[0], numberZero);
        
        return arr;
    }
    
    /**
     * transform two integers into a string that is compatible
     * @param x
     * @param y
     * @return 
     */
    public String datatoString(int x, int y){
        String str;
        
        str = (Integer.toString(x) + "," + Integer.toString(y));
        
        return str;
    }
    
    /**
     * find the square to be highlighted
     * @param time
     * @param numberZero: number of zeros to scale with
     * @param highest : extremes[2] .. expected value: -92.12314 (NEGATIVE IS IMPORTANT)
     * @param mostLeft : extremes[0] .. expected value: 43.12314 (POTISTIVE IS IMPORTANT)
     *                  panel will be flipped if extremes are not expected signs
     * @return highlight "x,y"
     */
    public String findHighlight(long time, double[] factor, int numberZero, double highest, double mostLeft){
        String[] highlight = new String[] {""};
        
        try{
            
        //find raw cordinates
        double[] rawData = findSingle(time);
        
        //transform these cordinates to default cordinates
        int[] transformedData = transform(rawData, numberZero, highest, mostLeft);
        
        //convert to string
        String[] transformedDataString;
        transformedDataString = new String[]{datatoString(transformedData[0],transformedData[1])};
        
        //scale based on factors
        highlight = newValues(transformedDataString, factor);
        
        
        }catch(Exception e){
            System.out.println("ERROR: File not found (find highlight)");
        }
        
        return highlight[0];
    }
    
    /**
     * find left most, right most, up most, down most point in csv
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     * @returns double array of size four like this: [leftmost, rightmost, highest, lowest]
     * these values are not edited and are raw from the csv file
     */
    public static double[] findExtremes() throws FileNotFoundException, IOException {
        double[] extremes = new double[4];
        String currentTag = "";
        String line = "";
        double leftmost = 181;
        double rightmost = -181;
        double highest = -91;
        double lowest = 91;
        extremes[0] = leftmost;
        extremes[1] = rightmost;
        extremes[2] = highest;
        extremes[3] = lowest;
        
        //open file
        BufferedReader br = new BufferedReader(new FileReader("test.csv"));
        
        //transverse through csv file and find extremes
        while((line = br.readLine()) != null){
            String[] fields = line.split(",");
            
            // if line starts with "Time"
            if( fields[0].equals("Time")){
                // change current tag to the field right to time
                currentTag = fields[1];
            //if is not equal to end or empty then it is data/numbers
            } else if (!(fields[0].equals("END")) && !(fields[0].isEmpty()) && fields[0] != null && fields[1] != null && !(fields[1].isEmpty())){
                //if current tag is latitude
                if (currentTag.equals("latitude")){
                    //find highest ( largest # )
                    if (Double.parseDouble(fields[1]) >= highest){
                        //update highest
                        highest = Double.parseDouble(fields[1]);
                    //find lowest (smallest # )
                    } else if (Double.parseDouble(fields[1]) <= lowest) {
                        //update lowest
                        lowest = Double.parseDouble(fields[1]);
                    }
                //if current tag is longitude
                } else if (currentTag.equals("longitude")){
                    //find leftmost ( smallest # )
                    if (Double.parseDouble(fields[1]) <= leftmost){
                        //update leftmost
                        leftmost = Double.parseDouble(fields[1]);
                    //find rightmost ( largest # )
                    } else if (Double.parseDouble(fields[1]) >= rightmost){
                        //update rightmost
                        rightmost = Double.parseDouble(fields[1]);
                    }
                }
            }
        }
        
        //data checking
        
        //leftmost should be between -180 and 180
        if ( leftmost <= 180 && leftmost >= -180 && leftmost < rightmost )
            extremes[0] = leftmost;
        else
            System.out.println("Corrupt Data: leftmost invalid value");
        
        //rightmost should be between -180 and 180
        if ( rightmost <= 180 && rightmost >= -180 && rightmost > leftmost )
            extremes[1] = rightmost;
        else
            System.out.println("Corrupt Data: rightmost invalid value");
        
        //highest should be between -90 and 90
        if ( highest <= 90 && highest >= -90  && highest > lowest )
            extremes[2] = highest;
        else
            System.out.println("Corrupt Data: highest invalid value");
        
        //lowest should be between -90 and 90
        if ( lowest <= 90 && lowest >= -90 && lowest < highest)
            extremes[3] = lowest;
        else
            System.out.println("Corrupt Data: lowest invalid value");
        
        
        return extremes;
    }
    
    /**
     * find default panel size
     * using the extremes array finds the default panel size
     * @param diff[]{difference in lat, difference in long}
     * @param numZero number of zeros (from differences) used in calculations
     * @param extremes array: [leftmost, rightmost, highest, lowest]
     * @return int array: [width, height]
     */
    public static int[] defaultPanel(double[] diff, int numZero){
        int[] dimensions = new int[2];
        dimensions[0] = 0;
        dimensions[1] = 0;
        
        //convert doubles into three digit integers
        dimensions[0] = doubleIntThree(diff[0], numZero);
        dimensions[1] = doubleIntThree(diff[1], numZero);
        
        return dimensions;
    }
    
    /**
     * returns two differences of four numbers
     * @param extremes
     * @return double[] : [ long diff , lat diff ]
     */
    public static double[] findDifference(double[] extremes){
        double[] difference = new double[2];
        difference[0] = 0;
        difference[1] = 0;
        
        //find longitude diff = rightmost - leftmost
        difference[0] = extremes[1] -  extremes[0];
        
        //find latitude diff = highest - lowest
        difference[1] = extremes[2] - extremes[3];
        
        return difference;
    }
    
    /**
     * count num of preceding zeros in number
     * @param number expected values: 0.10, 0.00305, 0.000556 ....
     *               unexpected values: 1.0, 10.0, 100.0 ...
     * @return int numZero: number of preceding zeros in the number
     */
    public static int numberZero(double number){
        int numZero = 0;
        
        //if expected value
        if (number < 1 && number > 0 ){
            String numString = "";
            
            //count # of zero's in numbers until first nonzero digit
            numString = Double.toString(number);
            
            //transverse through numString
            for ( int i = 0; i < numString.length(); i++ ){
                char c = numString.charAt(i);
                
                if ( c == '0' ){
                    //add one to number of zeros
                    numZero++;
                } else if ( c == '.' ){
                    //do nothing
                } else {
                    //exit for loop
                    break;
                }
            }
        //if unexpected value
        } else if ( number < 0 ){
            System.out.println("Error: Invalid Difference, must be positive");
        //number is in 1's place
        } else if ( number >= 1 && number < 10 ){
            numZero = 0;
        //number is in 10's place
        } else if ( number >= 10 && number < 100 ){
            numZero = -1;
        //number is in 100's place from 100-360
        } else if ( number >= 100 && number < 360 ){
            numZero = -2;
        //if number is zero set integer flag to neg 3 
        } else if (number == 0 ){
            numZero = -3;
        // else error
        } else {
            System.out.println("Error: Invalid Difference");
        }
        
        return numZero;
    }
    
    /**
     * convert doubles into three digit integers
     * @param number : expected values: 0.10, 0.00305, 0.000556 ....
     *                 unexpected values: 1.0, 10.0, 100.0 ...
     * @param numZero: number of zeros (influence conversion)
     * @return int dim: double converted into three digit integer
     * TO FIX: make function just for counting the number of zeros for one of the two, then keep it consistent
     */
    public static int doubleIntThree(double number, int numZero){
        int dim = 0;
        
        //if numZero is an expected value convert into three digit number
        if ( numZero >= -2 ){
            dim = (int) Math.ceil(number * Math.pow(10, numZero + 2));
        //if numZero is negative three flag (means value was zero)
        } else if ( numZero == -3 ){
            dim = 0;
        //else numZero is invalid
        }else {
            System.out.println("Error: Number of zeros cannot be less than -3");
        }
        
        return dim;
    }
    
    /**
     * Read CSV and fill array with edited values
     * @param numberZero: number of zeros to scale with
     * @param highest : extremes[2] .. expected value: -92.12314 (NEGATIVE IS IMPORTANT)
     * @param mostLeft : extremes[0] .. expected value: 43.12314 (POTISTIVE IS IMPORTANT)
     *                  panel will be flipped if extremes are not expected signs
     * @return String[] of edited lat and long values to add to the panel
     * @throws java.io.FileNotFoundException
     */
    public static String[] readCSV(int numberZero, double highest, double mostLeft) throws FileNotFoundException, IOException {
        String line = "";
        String currentTag = "";
        LinkedHashMap <String, String> lats = new LinkedHashMap<>();
        LinkedHashMap <String, String> longs = new LinkedHashMap<>();
        ArrayList<String> xys = new ArrayList<>();
        
        //open file
        BufferedReader br = new BufferedReader(new FileReader("test.csv"));
        
        //transverse through data and put lat and long into tree lists
        while ((line = br.readLine()) != null){
            String[] fields = line.split(",");
            
            //if has tag
            if( fields[0].equals("Time")){
                currentTag = fields[1];
            //if is not equal to end or empty then it is a number
            } else if (!(fields[0].equals("END")) && !(fields[0].isEmpty()) && fields[0] != null && fields[1] != null && !(fields[1].isEmpty())){
                //if current tag is latitude
                if (currentTag.equals("latitude")){
                    lats.put(fields[0],fields[1]);
                //if current tag is longitude
                } else if (currentTag.equals("longitude")){
                    longs.put(fields[0],fields[1]);
                }
            }
        }
        
        // put together the two LinkedHashMaps into one ArrayList
        for(String key: lats.keySet()){
            
            //get raw data from csv for long and then subtract the mostLeft extreme, scale it according to the number of zeros
            int xi = doubleIntThree((Double.parseDouble(longs.get(key)) - mostLeft), numberZero);
            //subtract get raw data from csv for lat from highest extreme, scale it according to the number of zeros
            int yi = doubleIntThree(highest - (Double.parseDouble(lats.get(key))), numberZero);
            
            String xs = Integer.toString(xi);
            String ys = Integer.toString(yi);
            
            xys.add(xs + "," + ys);
        }
        
        //create array as big as enteries
        String[] data = new String[xys.size()];
        
        //put ArrayList into String Array
        for(int i=0; i<xys.size(); i++){
            //put into string array
            data[i] = xys.get(i);
        }
        
        br.close();
        return data;
    }
    
    /**
     * create the track drawing on a panel
     */
    public void createDrawing(){

        try{
            //read from csv
            int numbZero;
            double[] ex = findExtremes(); 
            double[] factors = new double[]{1,1};
            double[] difference = findDifference(ex);
            //int time = 2507; //highlighted time value
            int time = 105505;
            
            // find number of zero to scale graph with
            // if number of zeros in lat difference is more than number of zeros in long difference
            // use long number of zeros to scale graph
            if (numberZero(difference[0]) >= numberZero(difference[1]) ){
                numbZero = numberZero(difference[1]);
            } else {
                numbZero = numberZero(difference[0]);
            }
            
            //get the default dimensions
            int[] dim = defaultPanel(difference, numbZero);
            
            // get data
            String[] data = readCSV(numbZero, ex[2], ex[0]);
            
            //draw track
            drawingPanel = new Panel2(data,dim[0],dim[1]);
            //highlight
            drawingPanel.updateHLParameters(time, factors, numbZero, ex[2], ex[0]);
            //output lat and long cordinates for the time
            outputSingle(time);
            drawingPanel.repaint();
            drawingPanel.addComponentListener(new ComponentListener() {
                boolean isEdge = false;
                int pixSiz = 1;
                int first = 0; 
                //if component is resized
                @Override
                public void componentResized(ComponentEvent e) {
                    if (isEdge == false){ 
                        if ( first != 0 )
                            drawingPanel.addEdge(-pixSiz);
                    
                    //find factors to multiply components with based on the change
                    double[] factor = findFactors(drawingPanel,dim);
                    String[] updatedData = newValues(data,factor);
                    drawingPanel.updateValues(updatedData);
                    
                    //find new pixel Size and update Pixel Size
                    pixSiz = getPixelSize(factor,drawingPanel.getDefaultPixelSize());
                    drawingPanel.updatePixelSize(pixSiz);
                    
                    //update the highlight cord too
                    //update factor for highlight based on movement
                    drawingPanel.updateHLParameters(time, factor, numbZero, ex[2], ex[0]);
                    
                    //update track
                    //drawingPanel.repaint();
                    //resize
                    isEdge = true;
                    } else if (isEdge == true){ 
                        drawingPanel.addEdge(pixSiz);
                        first = 1; 
                        isEdge = false;
                    }
                }

                @Override
                public void componentMoved(ComponentEvent e) {
                    System.out.println("Component Moved");
                }

                @Override
                public void componentShown(ComponentEvent e) {
                    System.out.println("Component Shown");
                }

                @Override
                public void componentHidden(ComponentEvent e) {
                    System.out.println("Component Hidden");
                }
            });
            this.setContentPane(drawingPanel);
            pack();
            
        } catch(Exception e){
            System.out.println("ERROR: File not found (create drawing)");
            return;
        } 
        
    }
    
    /**
     * finds what factor the JPanel changed by
     * @param drawPanel
     * @param orgDim
     * @return fac[widthFactor, heightFactor]
     */
    public double[] findFactors(JPanel drawPanel, int[] orgDim){
        double widthFactor = 0;
        double heightFactor = 0;
        double[] fac = new double[2];
        
        //compare with original dimensions
        widthFactor = findRatio(drawPanel.getWidth(), orgDim[0]);
        heightFactor = findRatio(drawPanel.getHeight(), orgDim[1]);
        
        //assign value to array
        fac[0] = widthFactor;
        fac[1] = heightFactor;
        
        return fac;
    }
    
    /**
     * find ratio between new and old dimensions
     * @param newDim
     * @param oldDim
     * @return ratio
     */
    public double findRatio(double newDim, double oldDim){
        return newDim / oldDim;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 710, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 563, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>                        

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws FileNotFoundException, IOException{
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GraphExmaple.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GraphExmaple.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GraphExmaple.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GraphExmaple.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GraphExmaple().setVisible(true);
            }
        });
        
    }
    
    /**
     * update rectangles width and height by their specific factor
     * @param data
     * @param factor [widthFactor, heightFactor]
     * @return 
     */
    public String[] newValues(String[] data, double[] factor){
        String[] newData = new String[data.length];
       for(int i=0; i<newData.length; i++){
            String s = data[i];
            String[] split = s.split(",");
                
            //convert to int... multiply it by scale...
            int width = (int)(Integer.parseInt(split[0]) * factor[0]);
            int height = (int)(Integer.parseInt(split[1]) * factor[1]);
                
            //update string
            newData[i] = Integer.toString(width) + "," + Integer.toString(height);
        }
        
        return newData;
    }

    /**
     * get new pixel size
     * @param factors[]{x factor,y factor}
     * @param defaultPix
     * @return newPix
     */
    public int getPixelSize(double[] factors, int defaultPix){
        //default pixel size is two, change this according to the smallest factor
        //if default changes change the value of pix
        int newPix;
        if(factors[0] < factors[1]){
            newPix = (int)(defaultPix * factors[0]);
        }else{
            newPix = (int)(defaultPix * factors[1]);
        }
        
        //won't go lower than default pixel size
        if(newPix < defaultPix){
            newPix = defaultPix;
        }
        
        return newPix;
    }
    // Variables declaration - do not modify                     
    // End of variables declaration                   

    
    
    class Panel2 extends JPanel{
        
        Color shapeColor = Color.BLACK;
        String[] values;
        //default size... if you change this value here change it in the getPixelSize function as well
        private int pixelSize = 5;
        private int defaultPix = 5;
        private long time = 0;
        private double[] fact = new double[]{1,1};
        private int numbZero = 0;
        private double highest = 0;
        private double mostLeft = 0;
        
        
        public Panel2() {
            // set a preferred size for the custom panel.
            setPreferredSize(new Dimension(600,400));
        }
        
        /**
         * Draws panel with values array...
         * creates panel of size (width + pixelSize) x (height + pixelSize)
         * adding the pixelSize to make sure we can see the pixels if they're on the edge
         * @param values
         * @param width
         * @param height 
         */
        public Panel2(String[] values, int width, int height) {
            setPreferredSize(new Dimension(width + pixelSize,height + pixelSize));
            
            this.values = values;
        }
        
        /**
         * update values
         * @param newValues 
         */
        public void updateValues(String[] newValues){
            this.values = newValues;
        }
        
        /**
         * update highlight parameters 
         * @param tim
         * @param fac
         * @param numbZer
         * @param high
         * @param mLeft 
         */
        public void updateHLParameters(long tim, double[] fac, int numbZer, double high, double mLeft){
            this.time = tim;
            this.fact = fac;
            this.numbZero = numbZer;
            this.highest = high;
            this.mostLeft = mLeft;
        }
        
        /**
         * update pixel size
         * @param newSize 
         */
        public void updatePixelSize(int newSize){
            pixelSize = newSize;
        }
        
        /**
         * get default (original) pixel size
         * @return 
         */
        public int getDefaultPixelSize(){
            return defaultPix;
        }
        
        /**
         * adds edge so pixels are shown
         */
        public void addEdge(int pixSiz){
            setSize(new Dimension((this.getWidth() + pixSiz),(this.getHeight() + pixSiz)));
        }

        @Override
        public void paintComponent(Graphics g){
            try{
                //find highlighted x and y cordinates and store into hl
                String hl = findHighlight(time, fact, numbZero, highest, mostLeft);
                
                super.paintComponent(g);
                
                for(String s : values) {
                    String[] split = s.split(",");
                    
                    //highlight rectangle
                    if( s == null ? hl == null : s.equals(hl) ){
                        g.setColor(Color.RED);
                        
                        //edit size
                        g.fillRect(Integer.parseInt(split[0]), Integer.parseInt(split[1]), pixelSize+3 , pixelSize+3);
                        
                    } else{
                        g.setColor(Color.BLACK);
                        
                        //edit size
                        g.fillRect(Integer.parseInt(split[0]), Integer.parseInt(split[1]), pixelSize, pixelSize);
                    }
                    
                }
            }catch(Exception e){
                System.out.println("ERROR: File not found (paint component)");
                return;
            } 
             
        }
        
    }
    
}
