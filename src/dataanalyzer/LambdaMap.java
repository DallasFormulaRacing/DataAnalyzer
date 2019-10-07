/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer;

import java.awt.Color;
import java.awt.Component;
import java.util.LinkedList;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.JLabel;


/**
 *
 * @author Peter
 * @author Preston
 * @author Dante
 */
public class LambdaMap extends javax.swing.JFrame {

    //Stores table data and table column/row headers 
    private DefaultTableModel table;
    
    //Contains the information from the char manager
    private CategoricalHashMap dataMap;
    
    //Float 2D arrays that mirror the dataTableModel that store respective quantities
    private double[][] afrTable;
    private double[][] injectorTimingTable;
    
    /**
     * Creates new form LambdaMap
     */
    public LambdaMap() {
        initTableModel(12500, 520, 100, 4);
        initComponents();
    }
    
    public LambdaMap(CategoricalHashMap dataMap){
        this.dataMap = dataMap;
        
        initTableModel(12500, 520, 100, 4);
        
        afrTable = new double[table.getColumnCount()][table.getRowCount()];
        injectorTimingTable = new double[table.getColumnCount()][table.getRowCount()];
        
        //TODO: Update tables
        updateTables();
        //updateInjectorTimingTable();
        
        populateFuelMap();
        
        initComponents();
    }
    
    /**
     * Initializes the classes DefaultTableModel attribute with row and column headers based on parameters
     * @param columnLimit The last/largest value in the column header sequence
     * @param columnInterval The interval value each column header is incremented by
     * @param rowLimit The last/largest value in the row header sequence
     * @param rowInterval The interval value each row header is incremented by
     */
    public void initTableModel(int columnLimit, int columnInterval, int rowLimit, int rowInterval) {
        //Stores table models column and row size
        int colSize;
        int rowSize;

        //Determines the row and column size of the table
        if (columnLimit % columnInterval == 0) {
            colSize = columnLimit / columnInterval + 1;
        }
        else {
            colSize = columnLimit / columnInterval + 2;
        }
        if (rowLimit % rowInterval == 0) {
            rowSize = rowLimit / rowInterval;
        }
        else {
            rowSize = rowLimit / rowInterval + 1;
        }

        //Creates 2D array for table data (first column contains row headers, not table data)
        Object[][] dataTable = new Object[rowSize][colSize];

        //Creates 2D array for column headers
        Object columnHeader[] = new Object[colSize];

        //Initializes all table data to zero
        for (int row = 0; row < rowSize; row++) {
            for (int col = 1; col < colSize; col++) {
                dataTable[row][col] = 0;
            }
        }

        //Initializes row headers based on parameter values
        int i = 1;
        while (rowInterval * i < rowLimit) {
            dataTable[i - 1][0] = (rowInterval * i) + "%";
            i++;
        }
        dataTable[rowSize - 1][0] = rowLimit + "%";

        //Initializes column headers based on parameter values
        int j = 1;
        columnHeader[0] = "";
        while (columnInterval * j < columnLimit) {
            columnHeader[j] = columnInterval * j;
            j++;
        }
        columnHeader[colSize - 1] = columnLimit;

        //Sets table equal to a new DefaultTableModel created from dataTable and columnHeader
        table = new DefaultTableModel(dataTable, columnHeader) {
            //Makes the first column uneditable and the rest editable
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                if (columnIndex == 0) {
                    return false;
                }
                else {
                    return true;
                }
            }
        };
    }
    
    //squeezes a large range into a defined range
    private int squeeze(double value, int min, int max, int floor, int ceil){
        return (int)Math.floor(((ceil-floor)*(value - min)*1.0)/(max-min) + min);
    }
    
    /**
     * Runs through the RPM, TPS, Lambda, and FuelOpenTime to update the afrTable
     * and injectorTimingTable to the averaged value across the data set for that
     * rpm and tps
     */
    private void updateTables(){
        LinkedList<LogObject> list = dataMap.getList("Time,RPM");
        LinkedList<LogObject> list2 = dataMap.getList("Time,TPS");
        LinkedList<LogObject> list3 = dataMap.getList("Time,Lambda");
        LinkedList<LogObject> list4 = dataMap.getList("Time,FuelOpenTime");
        
        
        int[][] avg = new int[table.getColumnCount()][table.getRowCount()];
        
        for(int i = 0; i<list.size(); i++){
            double rpm = 0, tps = 0, lambda = 0, injectorTime = 0;
            
            //to make sure the LogObject has a value
            try{
                LogObject rpmObj = list.pop();
                list.addLast(rpmObj);
                rpm = ((SimpleLogObject)rpmObj).value;
                
                LogObject tpsObj = list2.pop();
                list2.addLast(tpsObj);
                tps = ((SimpleLogObject)tpsObj).value;
                
                LogObject lambdaObj = list3.pop();
                list3.addLast(lambdaObj);
                lambda = ((SimpleLogObject)lambdaObj).value;
                
                LogObject injectorObj = list4.pop();
                list4.addLast(injectorObj);
                injectorTime = ((SimpleLogObject)injectorObj).value;
            }catch(Exception e){
                System.out.println(e);
            }
            
            //Finds which column the data should go into
            int column = squeeze(rpm, 0,12500, 0,25);
            int row = squeeze(tps, 0, 100, 0,24);
            
            //adds the respective value to its slot and increments how many values
            //in that particular slot
            afrTable[column][row] += lambda;
            injectorTimingTable[column][row] += injectorTime;
            avg[column][row] += 1;
        }
        
        //Averages out each slot of the tables
        for(int y = 0; y<table.getColumnCount()-1; y++){
            for(int x = 0; x<table.getRowCount(); x++){
                if(avg[y][x] != 0){
                    afrTable[y][x] = afrTable[y][x] / avg[y][x];
                    injectorTimingTable[y][x] = injectorTimingTable[y][x] / avg[y][x];
                }
            }
        }
    }
   
    //populates each cell of the fuel map
    private void populateFuelMap(){
        for(int y = 0; y<table.getColumnCount()-1; y++){
                for(int x = 0; x<table.getRowCount(); x++){
                    table.setValueAt((afrTable[y][x] * 2)+10, x, y+1);
                }
            }
    }
    
    //colors each cell of fuel map red if value is 1.5 away from desired value
    public void highlightCells(desiredValue) {
        int maxLim = (desiredValue + 1.5);
        int minLim = (desiredValue - 1.5);
        for (int x = 0; x < table.getRowCount(); x++) {
            for (int y = 0; y < table.getColumnCount()-1; y++) {
                int cellValue = table.getValueAt(x,y);
                if (cellValue > maxLim) {
                    table(x,y).setBackground(new java.awt.Color(255, 0, 0));
                }
                else if (cellValue < minLim) {
                    table(x,y).setBackground(new java.awt.Color(255, 0, 0));
                }
                else  {
                    table(x,y).setBackground(new java.awt.Color(0, 0, 0));
                }
            }
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

        jScrollPane1 = new javax.swing.JScrollPane();
        //Custom initialization of jTable
        jTable1 = new javax.swing.JTable()
        {
            //Overrides prepareRenderer so that the jTable can be formated
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col)
            {
                //JComponent component = (JComponent) super.prepareRenderer(renderer, row, col);

                if (col == 0)
                {
                    return this.getTableHeader().getDefaultRenderer()
                    .getTableCellRendererComponent(this, this.getValueAt(row, col), false, false, row, col);
                } else
                {
                    return super.prepareRenderer(renderer, row, col);
                }
            }
        };
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1350, 675));
        setPreferredSize(new java.awt.Dimension(1350, 675));

        //Renders the row headers
        final JTableHeader header = jTable1.getTableHeader();
        header.setDefaultRenderer(new HeaderRenderer(jTable1));

        //Sets the jTable model to table
        jTable1.setModel(table);
        //Centers all of the cells in the data table
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );
        for(int i = 0; i < jTable1.getModel().getColumnCount(); i++){
            jTable1.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        jTable1.setShowVerticalLines(true);
        jTable1.setShowHorizontalLines(true);
        jTable1.setGridColor(Color.GRAY);
        jTable1.setCellSelectionEnabled(true);
        jTable1.setRowHeight(22);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTable1);

        jLabel1.setText("RPM");

        jLabel3.setText("TPS");

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 999, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 578, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel3)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
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
            java.util.logging.Logger.getLogger(LambdaMap.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LambdaMap.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LambdaMap.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LambdaMap.class.getName()).log(java.util.logging.Level.SEVERE, null, ex); 
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LambdaMap().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}


/**
 * Table header renderer class for row headers (since row headers aren't natively supported by jTable)
 */
class HeaderRenderer implements TableCellRenderer {

    DefaultTableCellRenderer renderer;

    public HeaderRenderer(JTable jTable1) {
        renderer = (DefaultTableCellRenderer) jTable1.getTableHeader().getDefaultRenderer();
        renderer.setHorizontalAlignment(JLabel.CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        return renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
    }
}
