/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chem.figures;

import CH.ifa.draw.figures.LineConnection;
import CH.ifa.draw.framework.Figure;
import chem.figures.persist.ChemicalBondModel;
import chem.figures.persist.Persistable;
import chem.figures.persist.PersistableFigure;
import chem.tools.ChangeBondEndHandle;
import chem.tools.ChangeBondStartHandle;
import java.util.Vector;
import org.hibernate.Session;

/**
 *
 * @author FallenShard
 */
public class ChemicalBond extends LineConnection implements PersistableFigure
{
    private ChemicalBondModel m_model = new ChemicalBondModel();
    
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
    public ChemicalBondModel getModel()
    {
        m_model.setStartElectronId(m_start.getModel().getId());
        m_model.setEndElectronId(m_end.getModel().getId());
        return m_model;
    }

    @Override
    public void setModel(Persistable model)
    {
        m_model = (ChemicalBondModel)model;
    }

    @Override
    public void saveToDatabase(Session session, int documentId)
    {
        getModel();
        
        m_model.save(session, documentId);
    }
    
    @Override
    public void saveToDatabaseAs(Session session, int documentId)
    {
        getModel();
        
        m_model.saveAs(session, documentId);
    }
    
    @Override
    public void deleteFromDatabase(Session session)
    {
        
    }
}
