/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chem.UI;

import chem.figures.persist.DocumentModel;
import java.text.SimpleDateFormat;

/**
 *
 * @author FallenShard
 */
public class DocumentListItem
{
    DocumentModel m_model;
    
    public DocumentListItem(DocumentModel model)
    {
        m_model = model;
    }
    
    public int getDocumentId()
    {
        return m_model.getId();
    }
    
    @Override
    public String toString()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm");
        return String.format("%-20s \t\t%s", m_model.getName(), dateFormat.format(m_model.getTimestamp()));
    }
}
