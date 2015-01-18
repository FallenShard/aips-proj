/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chem.UI;

import chem.figures.persist.DocumentModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author FallenShard
 */
public class DocTableModel extends AbstractTableModel
{
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm");
    List<DocumentModel> m_documents = new ArrayList<>();
    String[] m_columnNames = { "Name", "Date Modified" };
    
    public DocTableModel(List<DocumentModel> documents)
    {
        m_documents = documents;
    }

    @Override
    public int getRowCount()
    {
        return m_documents.size();
    }

    @Override
    public int getColumnCount()
    {
        return 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        if (columnIndex == 0)
            return m_documents.get(rowIndex).getName();
        else
            return dateFormat.format(m_documents.get(rowIndex).getTimestamp());
    }
    
    @Override
    public String getColumnName(int col)
    {
        return m_columnNames[col];
    }
    
    @Override
    public boolean isCellEditable(int row, int col)
    { 
        return false;
    }
}
