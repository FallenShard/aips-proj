/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chem.figures;

import CH.ifa.draw.figures.ChopEllipseConnector;
import CH.ifa.draw.figures.EllipseFigure;
import CH.ifa.draw.framework.Connector;
import CH.ifa.draw.framework.Figure;
import chem.anim.Animatable;
import chem.figures.persist.ElectronModel;
import chem.figures.persist.Persistable;
import chem.figures.persist.PersistableFigure;
import chem.util.Const;
import java.awt.Point;
import org.hibernate.Session;

/**
 *
 * @author FallenShard
 */
public class ElectronFigure extends EllipseFigure implements Animatable, PersistableFigure
{
    // Used for persistence
    ElectronModel m_model = new ElectronModel();
    
    // Parent atom
    AtomFigure m_parent = null;
    
    // Other electron that forms the bond
    ElectronFigure m_otherElectron = null;
    
    // The chemical bond formed
    ChemicalBond m_covalentBond = null;
    
    // Index in the parent's vector, convenience only
    int m_index = -1;
    
    public ElectronFigure(Point center, int radius, AtomFigure parent, int index)
    {   
        setAttribute("FillColor", Const.ELECTRON_FILL);
        
        m_parent = parent;
        m_index = index;
        
        basicDisplayBox(new Point(center.x - radius, center.y - radius), new Point(center.x + radius, center.y + radius));
    }
    
    @Override
    public boolean canConnect()
    {
        return m_otherElectron == null;
    }
    
    public void setCovalentBond(ChemicalBond bond, Figure electron)
    {
        m_covalentBond = bond;
        m_otherElectron = (ElectronFigure)electron;
        
        if (bond == null)
            m_parent.decreaseValence(bond);
        else
            m_parent.increaseValence(bond, ((ElectronFigure)electron).getParent());
    }
    
    public AtomFigure getParent()
    {
        return m_parent;
    }
    
    public Figure getConnectedElectron()
    {
        return m_otherElectron;
    }
    
    public Figure getCovalentBond()
    {
        return m_covalentBond;
    }

    @Override
    public void animationStep(float timeDelta)
    {
        //moveBy((int) (100 * timeDelta + 0.5), 0);
    }
    
    @Override
    public Connector connectorAt(int x, int y)
    {
        return new ChopEllipseConnector(this);
    }

    @Override
    public ElectronModel getModel()
    {
        m_model.setAngle((Double)getAttribute("Angle"));
        m_model.setAtomId(m_parent.getModel().getId());
        m_model.setIndex(m_index);

        return m_model;
    }

    @Override
    public void setModel(Persistable model)
    {
        m_model = (ElectronModel)model;
        setAttribute("Angle", m_model.getAngle());
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
