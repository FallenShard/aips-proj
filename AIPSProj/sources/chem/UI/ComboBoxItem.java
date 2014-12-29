/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chem.UI;

import chem.figures.persist.DocumentModel;
import java.util.Date;

/**
 *
 * @author Mefi
 */
public class ComboBoxItem
{
    private String m_name;
    private Date m_timestamp;
    private int m_documentId;
    
    public ComboBoxItem(String name, Date timestamp, int docId)
    {
        m_name = name;
        m_timestamp = timestamp;
        m_documentId = docId;
    }
    
    public ComboBoxItem(DocumentModel documentModel)
    {
        m_name = documentModel.getName();
        m_timestamp = documentModel.getTimestamp();
        m_documentId = documentModel.getId();
    }
    
    @Override
    public String toString()
    {
        return String.format("\t%-20s %s" , m_name, m_timestamp.toString());
    }
    
    public String getName() { return m_name; }
    public Date getTimestamp() { return m_timestamp; }
    public int getDocId() { return m_documentId; }
}
