/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataanalyzer.readout;

import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
/**
 *
 * @author arib
 */
public class ReadoutPanelListCellRenderer extends DefaultListCellRenderer {
    
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
    {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        Color bg = index % 2 == 0 ? Color.WHITE : Color.LIGHT_GRAY;
        Color fg = Color.BLACK;
        if(isSelected) {
            bg = Color.BLUE;
            fg = Color.WHITE;
        }
        setBackground(bg);
        setForeground(fg);
        
        setOpaque(true); // otherwise, it's transparent
        return this;  // DefaultListCellRenderer derived from JLabel, DefaultListCellRenderer.getListCellRendererComponent returns this as well.
    }
}
