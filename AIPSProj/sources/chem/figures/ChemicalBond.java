/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chem.figures;

import CH.ifa.draw.figures.LineConnection;
import CH.ifa.draw.framework.Figure;
import chem.figures.persist.BondModel;
import chem.figures.persist.Persistable;
import chem.figures.persist.PersistableFigure;
import chem.tools.ChangeBondEndHandle;
import chem.tools.ChangeBondStartHandle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;

/**
 *
 * @author FallenShard
 */
public class ChemicalBond extends LineConnection implements PersistableFigure
{
    private BondModel m_model = new BondModel();
    
    private ElectronFigure m_start = null;
    private ElectronFigure m_end = null;
    
    public ChemicalBond()
    {
        super();
        setStartDecoration(null);
        setEndDecoration(null);
    }
    
    @Override
    public Vector handles()
    {
        Vector handles = new Vector(fPoints.size());
        handles.addElement(new ChangeBondStartHandle(this));
        handles.addElement(new ChangeBondEndHandle(this));
        return handles;
    }
    
    @Override
    public boolean canConnect(Figure start, Figure end)
    {
        if (start instanceof ElectronFigure && end instanceof ElectronFigure)
        {
            ElectronFigure el1 = (ElectronFigure)start;
            ElectronFigure el2 = (ElectronFigure)end;
            
            AtomFigure p1 = el1.getParent();
            AtomFigure p2 = el2.getParent();
            
            return p1 != p2 && p1.bondsWith(p2) < AtomFigure.MAX_BONDS;
        }
        return false;
    }
    
    @Override
    public void handleConnect(Figure start, Figure end)
    {
        if (start instanceof ElectronFigure && end instanceof ElectronFigure)
        {
            m_start = (ElectronFigure)start;
            m_end = (ElectronFigure)end;
            
            m_start.setCovalentBond(this, end);
            m_end.setCovalentBond(this, start);
            
            getModel();
        }
    }
    
    @Override
    public void handleDisconnect(Figure start, Figure end)
    {
        if (start instanceof ElectronFigure && end instanceof ElectronFigure)
        {
            if (m_start != null)
                m_start.setCovalentBond(null, null);
            m_start = null;
            
            if (m_end != null)
                m_end.setCovalentBond(null, null);
            m_end = null;
        }
    }
    

    @Override
    public BondModel getModel()
    {
        if (m_start != null)
        {
            m_model.setStartAtomX(m_start.getParent().displayBox().x);
            m_model.setStartAtomY(m_start.getParent().displayBox().y);
            m_model.setStartElectronIndex(m_start.getIndex());
        }
        if (m_end != null)
        {
            m_model.setEndAtomX(m_end.getParent().displayBox().x);
            m_model.setEndAtomY(m_end.getParent().displayBox().y);
            m_model.setEndElectronIndex(m_end.getIndex());
        }
        
        return m_model;
    }

    @Override
    public void setModel(Persistable model)
    {
        m_model = (BondModel)model;
    }

    @Override
    public void saveToDatabase(Session session, int documentId)
    {
        getModel();
        
        //m_model.save(session, documentId);
    }
    
    @Override
    public void saveToDatabaseAs(Session session, int documentId)
    {
        getModel();
        
        //m_model.saveAs(session, documentId);
    }
    
    @Override
    public void deleteFromDatabase(Session session)
    {
        
    }

    @Override
    public void appendJson(StringBuilder packedJson, ObjectMapper mapper)
    {
        try 
        {
            getModel();
            packedJson.append(mapper.writeValueAsString(m_model));
            packedJson.append("@B@");
        } 
        catch (JsonProcessingException ex)
        {
            Logger.getLogger(AtomFigure.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
